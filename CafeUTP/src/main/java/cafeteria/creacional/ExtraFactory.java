package cafeteria.creacional;

import cafeteria.estructural.decorator.ConCanela;
import cafeteria.estructural.decorator.ConCrema;
import cafeteria.estructural.decorator.ConLeche;
import cafeteria.modelo.Bebida;

/**
 * FACTORY de extras: es el unico lugar que sabe que extra corresponde a cada codigo.
 * Devuelve la bebida envuelta en el decorador correspondiente (patron DECORATOR).
 * La usan la Facade (al preparar una bebida) y el DAO (al cargarla de la base de datos).
 */
public class ExtraFactory {
    public static Bebida agregar(Bebida bebida, int extra) {
        switch (extra) {
            case 1: return new ConLeche(bebida);
            case 2: return new ConCrema(bebida);
            case 3: return new ConCanela(bebida);
            default: throw new IllegalArgumentException("Extra no valido: " + extra);
        }
    }
}
