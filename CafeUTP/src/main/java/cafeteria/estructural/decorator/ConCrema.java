package cafeteria.estructural.decorator;

import cafeteria.modelo.Bebida;

public class ConCrema extends ExtraDecorator {
    public ConCrema(Bebida bebida) { super(bebida); }

    @Override public String getDescripcion() { return bebida.getDescripcion() + " + crema"; }
    @Override public double getPrecio() { return bebida.getPrecio() + 1.50; }
}
