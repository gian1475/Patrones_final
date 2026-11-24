package cafeteria.estructural.adapter;

/** Interfaz objetivo (Target) del patron ADAPTER: es la forma de cobrar que entiende nuestro sistema. */
public interface MetodoPago {
    boolean pagar(double monto);
    String getNombre();
}
