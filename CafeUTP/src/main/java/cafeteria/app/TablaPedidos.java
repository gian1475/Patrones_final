package cafeteria.app;

import cafeteria.comportamiento.observer.ObservadorPedido;
import cafeteria.modelo.Bebida;
import cafeteria.modelo.Pedido;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.table.AbstractTableModel;

/**
 * Tabla de pedidos en vivo de la ventana principal.
 * Es un TERCER observador del patron OBSERVER (junto a Cocina y PantallaCliente):
 * cada vez que un pedido cambia de estado, el pedido le avisa y la fila se actualiza sola.
 * Se agrego sin modificar la clase Pedido.
 */
class TablaPedidos extends AbstractTableModel implements ObservadorPedido {
    // Sin columna de dinero: los montos y el total vendido son solo para el dueno (Reporte con Proxy)
    private static final String[] COLUMNAS = {"#", "Cliente", "Bebidas", "Estado"};
    private final List<Pedido> pedidos;   // vista de solo lectura del registro (Singleton)

    TablaPedidos(List<Pedido> pedidos) { this.pedidos = pedidos; }

    @Override
    public void actualizar(Pedido pedido) {
        int fila = pedidos.indexOf(pedido);
        if (fila >= 0) fireTableRowsUpdated(fila, fila);
    }

    /** Se llama cuando se registra un pedido nuevo, para que aparezca su fila. */
    void pedidoNuevo() {
        int ultima = pedidos.size() - 1;
        if (ultima >= 0) fireTableRowsInserted(ultima, ultima);
    }

    Pedido getPedido(int fila) { return pedidos.get(fila); }

    @Override public int getRowCount() { return pedidos.size(); }
    @Override public int getColumnCount() { return COLUMNAS.length; }
    @Override public String getColumnName(int columna) { return COLUMNAS[columna]; }

    @Override
    public Object getValueAt(int fila, int columna) {
        Pedido p = pedidos.get(fila);
        switch (columna) {
            case 0: return p.getId();
            case 1: return p.getCliente();
            case 2: return p.getBebidas().stream().map(Bebida::getDescripcion).collect(Collectors.joining(", "));
            default: return p.getEstado();
        }
    }
}
