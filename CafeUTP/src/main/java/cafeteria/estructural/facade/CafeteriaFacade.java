package cafeteria.estructural.facade;

import cafeteria.comportamiento.observer.Cocina;
import cafeteria.comportamiento.observer.ObservadorPedido;
import cafeteria.comportamiento.observer.PantallaCliente;
import cafeteria.creacional.BebidaFactory;
import cafeteria.creacional.ExtraFactory;
import cafeteria.creacional.PedidoBuilder;
import cafeteria.creacional.RegistroPedidos;
import cafeteria.estructural.adapter.MetodoPago;
import cafeteria.estructural.adapter.PagoEfectivo;
import cafeteria.estructural.adapter.YapeAdapter;
import cafeteria.estructural.proxy.ReporteVentasProxy;
import cafeteria.modelo.Bebida;
import cafeteria.modelo.Pedido;
import cafeteria.persistencia.ConexionBD;
import cafeteria.persistencia.PedidoDAO;
import java.util.ArrayList;
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
    // Otros observadores que se pueden sumar sin tocar Pedido (por ejemplo, la tabla de la ventana)
    private final List<ObservadorPedido> otrosObservadores = new ArrayList<>();
    // El DAO tambien es observador: guarda en la base de datos cada cambio de estado
    private final ObservadorPedido baseDatos = new PedidoDAO();

    public CafeteriaFacade() {
        // Los pedidos que se cargaron de la base de datos tambien deben avisar a cocina, pantalla y BD
        for (Pedido p : registro.getPedidos()) suscribirObservadores(p);
    }

    /** Suma un observador nuevo (por ejemplo, la tabla de la ventana) a los pedidos actuales y futuros. */
    public void agregarObservador(ObservadorPedido observador) {
        otrosObservadores.add(observador);
        for (Pedido p : registro.getPedidos()) p.suscribir(observador);
    }

    public boolean baseDatosConectada() { return ConexionBD.disponible(); }

    public String errorBaseDatos() { return ConexionBD.getError(); }

    private void suscribirObservadores(Pedido pedido) {
        pedido.suscribir(cocina);
        pedido.suscribir(pantalla);
        pedido.suscribir(baseDatos);
        for (ObservadorPedido o : otrosObservadores) pedido.suscribir(o);
    }

    public Bebida prepararBebida(int tipo, List<Integer> extras) {
        // Aca se usa el patron FACTORY: crea la bebida base sin hacer new Cafe()
        Bebida bebida = BebidaFactory.crear(tipo);
        // Aca se usa el patron DECORATOR: cada extra envuelve a la bebida anterior
        for (int extra : extras) bebida = ExtraFactory.agregar(bebida, extra);
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
        pedido.setMetodoPago(pago.getNombre());
        // Aca se usa el patron OBSERVER: cocina, pantalla y la base de datos se suscriben al pedido
        suscribirObservadores(pedido);
        // SINGLETON: se guarda en el registro unico (y este lo guarda en la base de datos)
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
