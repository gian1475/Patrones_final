package cafeteria.modelo;

import cafeteria.comportamiento.observer.ObservadorPedido;
import cafeteria.comportamiento.state.EstadoPedido;
import cafeteria.comportamiento.state.Registrado;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido de un cliente.
 * - PROTOTYPE: se puede clonar para "repetir el ultimo pedido".
 * - STATE: delega el cambio de estado a un objeto EstadoPedido.
 * - OBSERVER: es el sujeto que avisa a cocina y pantalla.
 */
public class Pedido implements Cloneable {
    private int id;   // lo asigna RegistroPedidos al registrar
    private final String cliente;
    private String metodoPago = "";   // "Efectivo" o "Yape"; se guarda en la base de datos
    private List<Bebida> bebidas = new ArrayList<>();
    private EstadoPedido estado = new Registrado();
    private List<ObservadorPedido> observadores = new ArrayList<>();

    public Pedido(String cliente) {
        this.cliente = cliente;
    }

    public void setId(int id) { this.id = id; }

    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getMetodoPago() { return metodoPago; }

    public void agregarBebida(Bebida b) { bebidas.add(b); }

    /** Experto en informacion (GRASP): el pedido conoce sus bebidas y calcula su total. */
    public double getTotal() {
        return bebidas.stream().mapToDouble(Bebida::getPrecio).sum();
    }

    // ----- Observer -----
    public void suscribir(ObservadorPedido o) { observadores.add(o); }

    private void notificar() {
        for (ObservadorPedido o : observadores) o.actualizar(this);
    }

    // ----- State -----
    public void setEstado(EstadoPedido nuevo) {
        this.estado = nuevo;
        notificar();
    }

    public void avanzar() { estado.siguiente(this); }

    public String getEstado() { return estado.getNombre(); }

    // ----- Prototype -----
    @Override
    public Pedido clone() {
        try {
            Pedido copia = (Pedido) super.clone();
            copia.id = 0;
            copia.bebidas = new ArrayList<>(this.bebidas);
            copia.observadores = new ArrayList<>();
            copia.estado = new Registrado();
            return copia;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public int getId() { return id; }
    public String getCliente() { return cliente; }
    public List<Bebida> getBebidas() { return bebidas; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Pedido #%d - %s [%s]%n", id, cliente, getEstado()));
        for (Bebida b : bebidas) {
            sb.append(String.format("   - %-32s S/ %6.2f%n", b.getDescripcion(), b.getPrecio()));
        }
        sb.append(String.format("   %-34s S/ %6.2f", "TOTAL", getTotal()));
        return sb.toString();
    }
}
