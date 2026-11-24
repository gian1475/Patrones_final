package cafeteria.creacional;

import cafeteria.modelo.Bebida;
import cafeteria.modelo.Pedido;
import java.util.ArrayList;
import java.util.List;

/** BUILDER: arma el pedido paso a paso y lo entrega listo con build(). */
public class PedidoBuilder {
    private String cliente = "Cliente";
    private final List<Bebida> bebidas = new ArrayList<>();

    public PedidoBuilder cliente(String cliente) {
        if (cliente != null && !cliente.isBlank()) this.cliente = cliente;
        return this;
    }

    public PedidoBuilder agregar(Bebida bebida) {
        bebidas.add(bebida);
        return this;
    }

    public boolean estaVacio() { return bebidas.isEmpty(); }

    public Pedido build() {
        if (bebidas.isEmpty()) throw new IllegalStateException("El pedido no tiene bebidas");
        Pedido p = new Pedido(cliente);
        bebidas.forEach(p::agregarBebida);
        return p;
    }
}
