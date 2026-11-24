package cafeteria.comportamiento.state;

import cafeteria.modelo.Pedido;

public class Listo implements EstadoPedido {
    @Override public void siguiente(Pedido pedido) { pedido.setEstado(new Entregado()); }
    @Override public String getNombre() { return "LISTO"; }
}
