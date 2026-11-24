package cafeteria.creacional;

import cafeteria.modelo.Pedido;
import cafeteria.persistencia.PedidoDAO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SINGLETON: unico registro de pedidos de toda la cafeteria.
 * Al crearse carga los pedidos guardados en la base de datos, y cada pedido nuevo
 * se guarda en ella a traves del DAO.
 */
public class RegistroPedidos {
    private static RegistroPedidos instancia;
    private final List<Pedido> pedidos = new ArrayList<>();
    private final PedidoDAO dao = new PedidoDAO();

    private RegistroPedidos() {
        pedidos.addAll(dao.cargarTodos());
    }

    public static synchronized RegistroPedidos getInstancia() {
        if (instancia == null) instancia = new RegistroPedidos();
        return instancia;
    }

    public void registrar(Pedido p) {
        // El numero del pedido lo da la base de datos. Si no hay conexion, se usa el siguiente numero.
        int id = dao.guardar(p);
        p.setId(id > 0 ? id : siguienteNumero());
        pedidos.add(p);
    }

    private int siguienteNumero() {
        return pedidos.isEmpty() ? 1 : pedidos.get(pedidos.size() - 1).getId() + 1;
    }

    public List<Pedido> getPedidos() { return Collections.unmodifiableList(pedidos); }

    public Pedido buscar(int id) {
        for (Pedido p : pedidos) if (p.getId() == id) return p;
        return null;
    }

    public Pedido getUltimo() {
        return pedidos.isEmpty() ? null : pedidos.get(pedidos.size() - 1);
    }

    public double getTotalVentas() {
        return pedidos.stream().mapToDouble(Pedido::getTotal).sum();
    }
}
