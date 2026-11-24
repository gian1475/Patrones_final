package cafeteria.comportamiento.state;

import cafeteria.modelo.Pedido;

public class Registrado implements EstadoPedido {
    @Override public void siguiente(Pedido pedido) { pedido.setEstado(new EnPreparacion()); }
    @Override public String getNombre() { return "REGISTRADO"; }
}
