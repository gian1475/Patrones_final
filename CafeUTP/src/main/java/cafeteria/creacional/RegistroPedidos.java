package cafeteria.creacional;

import cafeteria.modelo.Pedido;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** SINGLETON: unico registro de pedidos de toda la cafeteria. */
public class RegistroPedidos {
    private static RegistroPedidos instancia;
    private final List<Pedido> pedidos = new ArrayList<>();

    private RegistroPedidos() { }

    public static synchronized RegistroPedidos getInstancia() {
        if (instancia == null) instancia = new RegistroPedidos();
        return instancia;
    }

    public void registrar(Pedido p) {
        p.setId(pedidos.size() + 1);
        pedidos.add(p);
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
