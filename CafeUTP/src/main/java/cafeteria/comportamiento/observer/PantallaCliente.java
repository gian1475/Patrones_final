package cafeteria.comportamiento.observer;

import cafeteria.modelo.Pedido;

public class PantallaCliente implements ObservadorPedido {
    @Override
    public void actualizar(Pedido pedido) {
        System.out.printf("  [Pantalla] %s, tu pedido #%d esta: %s%n",
                pedido.getCliente(), pedido.getId(), pedido.getEstado());
    }
}
