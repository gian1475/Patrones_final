package cafeteria.estructural.decorator;

import cafeteria.modelo.Bebida;

/** Decorador concreto del patron DECORATOR: envuelve una bebida, agrega "leche" y suma S/ 1.00. */
public class ConLeche extends ExtraDecorator {
    public ConLeche(Bebida bebida) { super(bebida); }

    @Override public String getDescripcion() { return bebida.getDescripcion() + " + leche"; }
    @Override public double getPrecio() { return bebida.getPrecio() + 1.00; }
}
