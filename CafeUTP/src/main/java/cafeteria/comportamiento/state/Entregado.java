package cafeteria.comportamiento.state;

import cafeteria.modelo.Pedido;

/** Estado concreto del patron STATE: estado final; ya no hay siguiente estado. */
public class Entregado implements EstadoPedido {
    @Override
    public void siguiente(Pedido pedido) {
        System.out.println("El pedido #" + pedido.getId() + " ya fue entregado.");
    }

    @Override public String getNombre() { return "ENTREGADO"; }
}
