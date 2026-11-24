package cafeteria.comportamiento.state;

import cafeteria.modelo.Pedido;

public class Entregado implements EstadoPedido {
    @Override
    public void siguiente(Pedido pedido) {
        System.out.println("El pedido #" + pedido.getId() + " ya fue entregado.");
    }

    @Override public String getNombre() { return "ENTREGADO"; }
}
