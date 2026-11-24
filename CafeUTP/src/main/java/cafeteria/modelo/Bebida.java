package cafeteria.modelo;

/**
 * Interfaz base de todas las bebidas.
 * - En el patron FACTORY es el "producto" que devuelve BebidaFactory.
 * - En el patron DECORATOR es el "componente" que envuelven los extras.
 */
public interface Bebida {
    String getDescripcion();
    double getPrecio();
}
