package cafeteria.creacional;

import cafeteria.modelo.Bebida;
import cafeteria.modelo.Cafe;
import cafeteria.modelo.Jugo;
import cafeteria.modelo.Te;

/** FACTORY: centraliza la creacion de bebidas; el resto del sistema no hace "new Cafe()". */
public class BebidaFactory {
    public static Bebida crear(int tipo) {
        switch (tipo) {
            case 1: return new Cafe();
            case 2: return new Te();
            case 3: return new Jugo();
            default: throw new IllegalArgumentException("Bebida no valida: " + tipo);
        }
    }
}
