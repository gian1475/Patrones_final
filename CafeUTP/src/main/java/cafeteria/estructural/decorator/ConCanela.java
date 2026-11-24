package cafeteria.estructural.decorator;

import cafeteria.modelo.Bebida;

/** Decorador concreto del patron DECORATOR: envuelve una bebida, agrega "canela" y suma S/ 0.50. */
public class ConCanela extends ExtraDecorator {
    public ConCanela(Bebida bebida) { super(bebida); }

    @Override public String getDescripcion() { return bebida.getDescripcion() + " + canela"; }
    @Override public double getPrecio() { return bebida.getPrecio() + 0.50; }
}
