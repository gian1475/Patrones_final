package cafeteria.modelo;

/** Producto concreto del patron FACTORY (lo crea BebidaFactory) y componente concreto del patron DECORATOR. */
public class Jugo implements Bebida {
    @Override public String getDescripcion() { return "Jugo"; }
    @Override public double getPrecio() { return 7.00; }
}
