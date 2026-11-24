package cafeteria.modelo;

/** Producto concreto del patron FACTORY (lo crea BebidaFactory) y componente concreto del patron DECORATOR. */
public class Cafe implements Bebida {
    @Override public String getDescripcion() { return "Cafe"; }
    @Override public double getPrecio() { return 5.00; }
}
