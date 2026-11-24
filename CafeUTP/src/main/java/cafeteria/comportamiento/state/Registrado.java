package cafeteria.comportamiento.state;

import cafeteria.modelo.Pedido;

/** Estado concreto del patron STATE: primer estado del pedido; el siguiente es EN PREPARACION. */
public class Registrado implements EstadoPedido {
    @Override public void siguiente(Pedido pedido) { pedido.setEstado(new EnPreparacion()); }
    @Override public String getNombre() { return "REGISTRADO"; }
}
