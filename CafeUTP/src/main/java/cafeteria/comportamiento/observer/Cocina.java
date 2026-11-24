package cafeteria.comportamiento.observer;

import cafeteria.modelo.Pedido;

/** Observador concreto del patron OBSERVER: la cocina recibe un aviso cada vez que un pedido cambia de estado. */
public class Cocina implements ObservadorPedido {
    @Override
    public void actualizar(Pedido pedido) {
        System.out.printf("  [Cocina]   Pedido #%d ahora esta: %s%n", pedido.getId(), pedido.getEstado());
    }
}
