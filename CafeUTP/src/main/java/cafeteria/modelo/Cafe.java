package cafeteria.modelo;

public class Cafe implements Bebida {
    @Override public String getDescripcion() { return "Cafe"; }
    @Override public double getPrecio() { return 5.00; }
}
