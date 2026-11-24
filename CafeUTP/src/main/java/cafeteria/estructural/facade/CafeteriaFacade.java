package cafeteria.estructural.facade;

import cafeteria.comportamiento.observer.Cocina;
import cafeteria.comportamiento.observer.ObservadorPedido;
import cafeteria.comportamiento.observer.PantallaCliente;
import cafeteria.creacional.BebidaFactory;
import cafeteria.creacional.PedidoBuilder;
import cafeteria.creacional.RegistroPedidos;
import cafeteria.estructural.adapter.MetodoPago;
import cafeteria.estructural.adapter.PagoEfectivo;
import cafeteria.estructural.adapter.YapeAdapter;
import cafeteria.estructural.decorator.ConCanela;
import cafeteria.estructural.decorator.ConCrema;
import cafeteria.estructural.decorator.ConLeche;
import cafeteria.estructural.proxy.ReporteVentasProxy;
import cafeteria.modelo.Bebida;
import cafeteria.modelo.Pedido;
import java.util.List;

/**
 * FACADE: una sola puerta de entrada para el menu. Oculta la Factory,
 * los Decorators, el Adapter, el Singleton, el Observer y el Proxy.
 * Tambien cumple el rol de Controlador (GRASP).
 */
public class CafeteriaFacade {
    // Aca se usa el patron SINGLETON: siempre el mismo registro de pedidos
    private final RegistroPedidos registro = RegistroPedidos.getInstancia();
    // Observadores del patron OBSERVER
    private final ObservadorPedido cocina = new Cocina();
    private final ObservadorPedido pantalla = new PantallaCliente();

    public Bebida prepararBebida(int tipo, List<Integer> extras) {
        // Aca se usa el patron FACTORY: crea la bebida base sin hacer new Cafe()
        Bebida bebida = BebidaFactory.crear(tipo);
        // Aca se usa el patron DECORATOR: cada extra envuelve a la bebida anterior
        for (int extra : extras) {
            switch (extra) {
                case 1: bebida = new ConLeche(bebida); break;
                case 2: bebida = new ConCrema(bebida); break;
                case 3: bebida = new ConCanela(bebida); break;
                default: break;
            }
        }
        return bebida;
    }

    public MetodoPago crearPago(int opcion, String celular) {
        // Aca se usa el patron ADAPTER: Yape se cobra a traves de YapeAdapter
        return opcion == 2 ? new YapeAdapter(celular) : new PagoEfectivo();
    }

    /** Cobra y registra el pedido. Devuelve null si el pago fue rechazado. */
    public Pedido confirmarPedido(PedidoBuilder builder, MetodoPago pago) {
        // Aca se usa el patron BUILDER: build() entrega el pedido ya armado
        return cobrarYRegistrar(builder.build(), pago);
    }

    public Pedido repetirUltimoPedido(MetodoPago pago) {
        Pedido ultimo = registro.getUltimo();
        if (ultimo == null) return null;
        // Aca se usa el patron PROTOTYPE: se clona el ultimo pedido
        return cobrarYRegistrar(ultimo.clone(), pago);
    }

    private Pedido cobrarYRegistrar(Pedido pedido, MetodoPago pago) {
        if (!pago.pagar(pedido.getTotal())) return null;
        // Aca se usa el patron OBSERVER: cocina y pantalla se suscriben al pedido
        pedido.suscribir(cocina);
        pedido.suscribir(pantalla);
        registro.registrar(pedido);
        return pedido;
    }

    public boolean avanzarPedido(int id) {
        Pedido p = registro.buscar(id);
        if (p == null) return false;
        // Aca se usa el patron STATE: el estado actual decide cual es el siguiente
        p.avanzar();
        return true;
    }

    public List<Pedido> listarPedidos() { return registro.getPedidos(); }

    public Pedido ultimoPedido() { return registro.getUltimo(); }

    public void verReporte(String clave) {
        // Aca se usa el patron PROXY: se pide el reporte al proxy, no al reporte real
        new ReporteVentasProxy(clave).mostrar();
    }
}
