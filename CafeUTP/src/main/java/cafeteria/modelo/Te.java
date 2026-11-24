package cafeteria.modelo;

/** Producto concreto del patron FACTORY (lo crea BebidaFactory) y componente concreto del patron DECORATOR. */
public class Te implements Bebida {
    @Override public String getDescripcion() { return "Te"; }
    @Override public double getPrecio() { return 4.00; }
}
