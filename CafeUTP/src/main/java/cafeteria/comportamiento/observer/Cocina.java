package cafeteria.comportamiento.observer;

import cafeteria.modelo.Pedido;

public class Cocina implements ObservadorPedido {
    @Override
    public void actualizar(Pedido pedido) {
        System.out.printf("  [Cocina]   Pedido #%d ahora esta: %s%n", pedido.getId(), pedido.getEstado());
    }
}
