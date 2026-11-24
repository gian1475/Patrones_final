package cafeteria.comportamiento.state;

import cafeteria.modelo.Pedido;

/** Estado concreto del patron STATE: el pedido se esta preparando; el siguiente es LISTO. */
public class EnPreparacion implements EstadoPedido {
    @Override public void siguiente(Pedido pedido) { pedido.setEstado(new Listo()); }
    @Override public String getNombre() { return "EN PREPARACION"; }
}
