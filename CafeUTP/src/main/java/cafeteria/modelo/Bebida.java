package cafeteria.modelo;

/** Componente base de todas las bebidas (lo usan Factory y Decorator). */
public interface Bebida {
    String getDescripcion();
    double getPrecio();
}
