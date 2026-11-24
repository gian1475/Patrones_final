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
    private final RegistroPedidos registro = RegistroPedidos.getInstancia();
    private final ObservadorPedido cocina = new Cocina();
    private final ObservadorPedido pantalla = new PantallaCliente();

    public Bebida prepararBebida(int tipo, List<Integer> extras) {
        Bebida bebida = BebidaFactory.crear(tipo);
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
        return opcion == 2 ? new YapeAdapter(celular) : new PagoEfectivo();
    }

    /** Cobra y registra el pedido. Devuelve null si el pago fue rechazado. */
    public Pedido confirmarPedido(PedidoBuilder builder, MetodoPago pago) {
        return cobrarYRegistrar(builder.build(), pago);
    }

    /** Usa Prototype: clona el ultimo pedido y lo registra como uno nuevo. */
    public Pedido repetirUltimoPedido(MetodoPago pago) {
        Pedido ultimo = registro.getUltimo();
        if (ultimo == null) return null;
        return cobrarYRegistrar(ultimo.clone(), pago);
    }

    private Pedido cobrarYRegistrar(Pedido pedido, MetodoPago pago) {
        if (!pago.pagar(pedido.getTotal())) return null;
        pedido.suscribir(cocina);
        pedido.suscribir(pantalla);
        registro.registrar(pedido);
        return pedido;
    }

    public boolean avanzarPedido(int id) {
        Pedido p = registro.buscar(id);
        if (p == null) return false;
        p.avanzar();
        return true;
    }

    public List<Pedido> listarPedidos() { return registro.getPedidos(); }

    public Pedido ultimoPedido() { return registro.getUltimo(); }

    public void verReporte(String clave) { new ReporteVentasProxy(clave).mostrar(); }
}
