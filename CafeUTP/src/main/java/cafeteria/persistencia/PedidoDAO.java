package cafeteria.persistencia;

import cafeteria.comportamiento.observer.ObservadorPedido;
import cafeteria.comportamiento.state.EnPreparacion;
import cafeteria.comportamiento.state.Entregado;
import cafeteria.comportamiento.state.EstadoPedido;
import cafeteria.comportamiento.state.Listo;
import cafeteria.comportamiento.state.Registrado;
import cafeteria.creacional.BebidaFactory;
import cafeteria.creacional.ExtraFactory;
import cafeteria.modelo.Bebida;
import cafeteria.modelo.Pedido;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO (Data Access Object): es la unica clase que escribe SQL para los pedidos.
 * El resto del sistema no sabe que existe una base de datos.
 * Tambien es un observador del patron OBSERVER: cuando un pedido cambia de estado,
 * le avisa y aqui se actualiza la columna "estado" en la base de datos.
 * Si no hay conexion, no hace nada y el programa sigue funcionando en memoria.
 */
public class PedidoDAO implements ObservadorPedido {

    private interface Operacion<T> {
        T ejecutar(Connection c) throws SQLException;
    }

    /** Ejecuta la operacion; si la conexion se habia caido, reconecta y lo intenta una vez mas. */
    private static <T> T conReintento(Operacion<T> op) throws SQLException {
        try {
            return op.ejecutar(ConexionBD.obtener());
        } catch (SQLException primerError) {
            ConexionBD.descartar();
            return op.ejecutar(ConexionBD.obtener());
        }
    }

    // ------------------------------------------------------------------ guardar
    /** Guarda el pedido con sus bebidas y extras. Devuelve el numero que le dio la base de datos, o -1. */
    public int guardar(Pedido pedido) {
        if (!ConexionBD.disponible()) return -1;
        try {
            int id = conReintento(c -> guardarEnTransaccion(c, pedido));
            System.out.println("[BD] Pedido #" + id + " guardado en la base de datos.");
            return id;
        } catch (SQLException e) {
            System.out.println("[BD] No se pudo guardar el pedido: " + e.getMessage());
            return -1;
        }
    }

    private static int guardarEnTransaccion(Connection c, Pedido pedido) throws SQLException {
        c.setAutoCommit(false);
        try {
            int idPedido;
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO pedido (cliente, estado, metodo_pago, total) VALUES (?, ?, ?, ?) RETURNING id_pedido")) {
                ps.setString(1, pedido.getCliente());
                ps.setString(2, pedido.getEstado());
                ps.setString(3, pedido.getMetodoPago());
                ps.setDouble(4, pedido.getTotal());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    idPedido = rs.getInt(1);
                }
            }
            for (Bebida b : pedido.getBebidas()) {
                // La descripcion es "Cafe + leche + canela": la primera parte es la bebida y el resto, los extras
                String[] partes = b.getDescripcion().split(" \\+ ");
                int idDetalle;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO detalle_pedido (id_pedido, id_bebida, descripcion, precio) "
                        + "VALUES (?, (SELECT id_bebida FROM bebida WHERE lower(nombre) = lower(?)), ?, ?) RETURNING id_detalle")) {
                    ps.setInt(1, idPedido);
                    ps.setString(2, partes[0]);
                    ps.setString(3, b.getDescripcion());
                    ps.setDouble(4, b.getPrecio());
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        idDetalle = rs.getInt(1);
                    }
                }
                for (int i = 1; i < partes.length; i++) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "INSERT INTO detalle_extra (id_detalle, id_extra) "
                            + "SELECT ?, id_extra FROM extra WHERE lower(nombre) = lower(?) ON CONFLICT DO NOTHING")) {
                        ps.setInt(1, idDetalle);
                        ps.setString(2, partes[i]);
                        ps.executeUpdate();
                    }
                }
            }
            c.commit();
            return idPedido;
        } catch (SQLException e) {
            try { c.rollback(); } catch (SQLException ignorada) { /* la conexion ya no sirve */ }
            throw e;
        } finally {
            try { c.setAutoCommit(true); } catch (SQLException ignorada) { /* la conexion ya no sirve */ }
        }
    }

    // ------------------------------------------------------------------ actualizar estado (Observer)
    @Override
    public void actualizar(Pedido pedido) {
        if (!ConexionBD.disponible() || pedido.getId() <= 0) return;
        try {
            conReintento(c -> {
                try (PreparedStatement ps = c.prepareStatement("UPDATE pedido SET estado = ? WHERE id_pedido = ?")) {
                    ps.setString(1, pedido.getEstado());
                    ps.setInt(2, pedido.getId());
                    return ps.executeUpdate();
                }
            });
            System.out.println("[BD] Estado del pedido #" + pedido.getId() + " guardado: " + pedido.getEstado());
        } catch (SQLException e) {
            System.out.println("[BD] No se pudo guardar el estado: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ cargar
    /** Carga todos los pedidos guardados. Cada bebida se vuelve a armar con FACTORY + DECORATOR. */
    public List<Pedido> cargarTodos() {
        if (!ConexionBD.disponible()) return new ArrayList<>();
        try {
            return conReintento(PedidoDAO::cargar);
        } catch (SQLException e) {
            System.out.println("[BD] No se pudieron cargar los pedidos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static List<Pedido> cargar(Connection c) throws SQLException {
        Map<Integer, Pedido> pedidos = new LinkedHashMap<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id_pedido, cliente, estado, metodo_pago FROM pedido ORDER BY id_pedido");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Pedido p = new Pedido(rs.getString("cliente"));
                p.setId(rs.getInt("id_pedido"));
                p.setMetodoPago(rs.getString("metodo_pago"));
                p.setEstado(estadoDesdeNombre(rs.getString("estado")));
                pedidos.put(p.getId(), p);
            }
        }
        // Una fila por cada (bebida, extra). Los extras vienen ordenados igual que en el formulario.
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT d.id_pedido, d.id_detalle, d.id_bebida, e.id_extra FROM detalle_pedido d "
                + "LEFT JOIN detalle_extra e ON e.id_detalle = d.id_detalle "
                + "ORDER BY d.id_pedido, d.id_detalle, e.id_extra");
             ResultSet rs = ps.executeQuery()) {
            int detalleActual = -1;
            int idPedido = -1;
            int idBebida = 0;
            List<Integer> extras = new ArrayList<>();
            while (rs.next()) {
                int idDetalle = rs.getInt("id_detalle");
                if (idDetalle != detalleActual) {
                    if (detalleActual != -1) agregarBebida(pedidos.get(idPedido), idBebida, extras);
                    detalleActual = idDetalle;
                    idPedido = rs.getInt("id_pedido");
                    idBebida = rs.getInt("id_bebida");
                    extras = new ArrayList<>();
                }
                int idExtra = rs.getInt("id_extra");
                if (!rs.wasNull()) extras.add(idExtra);
            }
            if (detalleActual != -1) agregarBebida(pedidos.get(idPedido), idBebida, extras);
        }
        return new ArrayList<>(pedidos.values());
    }

    private static void agregarBebida(Pedido pedido, int idBebida, List<Integer> extras) {
        if (pedido == null) return;
        Bebida bebida = BebidaFactory.crear(idBebida);                            // FACTORY
        for (int extra : extras) bebida = ExtraFactory.agregar(bebida, extra);    // DECORATOR
        pedido.agregarBebida(bebida);
    }

    private static EstadoPedido estadoDesdeNombre(String nombre) {
        switch (nombre) {
            case "EN PREPARACION": return new EnPreparacion();
            case "LISTO": return new Listo();
            case "ENTREGADO": return new Entregado();
            default: return new Registrado();
        }
    }
}
