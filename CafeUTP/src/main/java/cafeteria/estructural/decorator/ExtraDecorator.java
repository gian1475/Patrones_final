package cafeteria.estructural.decorator;

import cafeteria.modelo.Bebida;

/** DECORATOR: envuelve una bebida para agregarle extras sin crear subclases. */
public abstract class ExtraDecorator implements Bebida {
    protected final Bebida bebida;

    protected ExtraDecorator(Bebida bebida) { this.bebida = bebida; }
}
