package cafeteria.comportamiento.state;

import cafeteria.modelo.Pedido;

public class EnPreparacion implements EstadoPedido {
    @Override public void siguiente(Pedido pedido) { pedido.setEstado(new Listo()); }
    @Override public String getNombre() { return "EN PREPARACION"; }
}
