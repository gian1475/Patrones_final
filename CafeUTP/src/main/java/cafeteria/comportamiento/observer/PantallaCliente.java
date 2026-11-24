package cafeteria.comportamiento.observer;

import cafeteria.modelo.Pedido;

/** Observador concreto del patron OBSERVER: la pantalla avisa al cliente cada vez que su pedido cambia de estado. */
public class PantallaCliente implements ObservadorPedido {
    @Override
    public void actualizar(Pedido pedido) {
        System.out.printf("  [Pantalla] %s, tu pedido #%d esta: %s%n",
                pedido.getCliente(), pedido.getId(), pedido.getEstado());
    }
}
