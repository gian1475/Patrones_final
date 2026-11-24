package cafeteria.persistencia;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Conexion con la base de datos PostgreSQL (Neon).
 * - Lee la cadena de conexion DATABASE_URL del archivo .env (o de una variable de entorno).
 * - Mantiene UNA conexion abierta y la reabre si se cae (Neon la corta tras un rato sin uso).
 * - La primera vez, si las tablas no existen, ejecuta el script bd/cafeutp.sql.
 */
public final class ConexionBD {
    private static String url;
    private static final Properties credenciales = new Properties();
    private static Connection conexion;
    private static Boolean disponible;
    private static String error = "";

    private ConexionBD() { }

    /** Intenta conectarse una sola vez; despues recuerda el resultado. */
    public static synchronized boolean disponible() {
        if (disponible == null) {
            try {
                leerConfiguracion();
                crearTablasSiFaltan(obtener());
                disponible = true;
            } catch (Exception e) {
                disponible = false;
                error = e.getMessage();
            }
        }
        return disponible;
    }

    public static String getError() { return error; }

    static synchronized Connection obtener() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, credenciales);
        }
        return conexion;
    }

    /** Cierra la conexion actual para que la proxima operacion abra una nueva. */
    static synchronized void descartar() {
        try {
            if (conexion != null) conexion.close();
        } catch (SQLException ignorada) {
            // ya estaba cerrada
        }
        conexion = null;
    }

    // ------------------------------------------------------------------ configuracion
    private static void leerConfiguracion() throws IOException {
        String cadena = System.getenv("DATABASE_URL");
        if (cadena == null || cadena.isBlank()) {
            Path env = buscarArchivo(".env");
            if (env == null) throw new IOException("No se encontró el archivo .env con DATABASE_URL");
            for (String linea : Files.readAllLines(env, StandardCharsets.UTF_8)) {
                linea = linea.trim();
                if (linea.startsWith("DATABASE_URL=")) cadena = linea.substring("DATABASE_URL=".length()).trim();
            }
            if (cadena == null || cadena.isBlank()) throw new IOException("El archivo .env no tiene DATABASE_URL");
        }
        convertirCadena(cadena.replace("\"", ""));
    }

    /**
     * Neon da la cadena como postgresql://usuario:clave@host/base?sslmode=require
     * y Java (JDBC) la necesita como jdbc:postgresql://host/base?sslmode=require + usuario y clave aparte.
     */
    private static void convertirCadena(String cadena) {
        URI uri = URI.create(cadena.replaceFirst("^postgres(ql)?://", "http://"));
        String[] usuarioClave = uri.getRawUserInfo().split(":", 2);
        credenciales.setProperty("user", decodificar(usuarioClave[0]));
        credenciales.setProperty("password", decodificar(usuarioClave[1]));
        credenciales.setProperty("sslmode", "require");
        credenciales.setProperty("connectTimeout", "15");
        credenciales.setProperty("prepareThreshold", "0");   // necesario con el pooler de Neon
        String puerto = uri.getPort() > 0 ? ":" + uri.getPort() : "";
        url = "jdbc:postgresql://" + uri.getHost() + puerto + uri.getPath();
    }

    private static String decodificar(String texto) {
        return java.net.URLDecoder.decode(texto, StandardCharsets.UTF_8);
    }

    /**
     * Busca un archivo del proyecto (por ejemplo ".env" o "bd/cafeutp.sql") en la carpeta desde donde
     * se ejecuta el programa y en las carpetas de arriba de donde estan las clases compiladas.
     */
    static Path buscarArchivo(String nombre) {
        List<Path> carpetas = new ArrayList<>();
        Path actual = Paths.get("").toAbsolutePath();
        carpetas.add(actual);
        carpetas.add(actual.resolve("CafeUTP"));
        carpetas.add(actual.resolve("Patrones_final").resolve("CafeUTP"));
        try {
            Path clases = Paths.get(ConexionBD.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            for (Path p = clases; p != null && carpetas.size() < 8; p = p.getParent()) carpetas.add(p);
        } catch (Exception ignorada) {
            // no se pudo saber donde estan las clases; se usan solo las otras carpetas
        }
        for (Path carpeta : carpetas) {
            Path archivo = carpeta.resolve(nombre);
            if (Files.isRegularFile(archivo)) return archivo;
        }
        return null;
    }

    // ------------------------------------------------------------------ script de tablas
    private static void crearTablasSiFaltan(Connection c) throws SQLException, IOException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT to_regclass('public.pedido') IS NOT NULL")) {
            rs.next();
            if (rs.getBoolean(1)) return;   // las tablas ya existen
        }
        Path script = buscarArchivo("bd/cafeutp.sql");
        if (script == null) throw new IOException("Faltan las tablas y no se encontró el script bd/cafeutp.sql");

        StringBuilder sinComentarios = new StringBuilder();
        for (String linea : Files.readAllLines(script, StandardCharsets.UTF_8)) {
            int comentario = linea.indexOf("--");
            sinComentarios.append(comentario >= 0 ? linea.substring(0, comentario) : linea).append('\n');
        }
        try (Statement st = c.createStatement()) {
            for (String sentencia : sinComentarios.toString().split(";")) {
                if (!sentencia.isBlank()) st.execute(sentencia);
            }
        }
    }
}
