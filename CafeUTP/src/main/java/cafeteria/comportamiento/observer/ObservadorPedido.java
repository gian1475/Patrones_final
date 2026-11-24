package cafeteria.comportamiento.observer;

import cafeteria.modelo.Pedido;

/** OBSERVER: quien quiera enterarse de los cambios de un pedido implementa esto. */
public interface ObservadorPedido {
    void actualizar(Pedido pedido);
}
