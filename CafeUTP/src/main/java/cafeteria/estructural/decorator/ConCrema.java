package cafeteria.estructural.decorator;

import cafeteria.modelo.Bebida;

/** Decorador concreto del patron DECORATOR: envuelve una bebida, agrega "crema" y suma S/ 1.50. */
public class ConCrema extends ExtraDecorator {
    public ConCrema(Bebida bebida) { super(bebida); }

    @Override public String getDescripcion() { return bebida.getDescripcion() + " + crema"; }
    @Override public double getPrecio() { return bebida.getPrecio() + 1.50; }
}
