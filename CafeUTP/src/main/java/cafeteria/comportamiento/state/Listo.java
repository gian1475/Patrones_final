package cafeteria.comportamiento.state;

import cafeteria.modelo.Pedido;

/** Estado concreto del patron STATE: el pedido esta listo para recoger; el siguiente es ENTREGADO. */
public class Listo implements EstadoPedido {
    @Override public void siguiente(Pedido pedido) { pedido.setEstado(new Entregado()); }
    @Override public String getNombre() { return "LISTO"; }
}
