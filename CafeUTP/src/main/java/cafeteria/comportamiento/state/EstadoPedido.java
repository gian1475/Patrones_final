package cafeteria.comportamiento.state;

import cafeteria.modelo.Pedido;

/** STATE: cada estado sabe cual es el siguiente. */
public interface EstadoPedido {
    void siguiente(Pedido pedido);
    String getNombre();
}
