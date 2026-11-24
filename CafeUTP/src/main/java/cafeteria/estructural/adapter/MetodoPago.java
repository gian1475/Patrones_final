package cafeteria.estructural.adapter;

/** Interfaz que espera nuestro sistema para cobrar. */
public interface MetodoPago {
    boolean pagar(double monto);
    String getNombre();
}
