package cafeteria.estructural.proxy;

/**
 * PROXY de proteccion: se pone delante del ReporteVentas y solo deja
 * verlo si la clave de administrador es correcta. Ademas crea el reporte
 * real solo cuando hace falta (carga perezosa).
 */
public class ReporteVentasProxy implements Reporte {
    private static final String CLAVE_ADMIN = "admin123";
    private final String claveIngresada;
    private ReporteVentas reporteReal;

    public ReporteVentasProxy(String claveIngresada) { this.claveIngresada = claveIngresada; }

    @Override
    public void mostrar() {
        if (!CLAVE_ADMIN.equals(claveIngresada)) {
            System.out.println("Acceso denegado: solo el administrador puede ver el reporte.");
            return;
        }
        if (reporteReal == null) reporteReal = new ReporteVentas();
        reporteReal.mostrar();
    }
}
