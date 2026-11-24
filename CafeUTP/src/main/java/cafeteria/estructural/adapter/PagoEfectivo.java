package cafeteria.estructural.adapter;

/** Pago normal del patron ADAPTER: ya cumple la interfaz MetodoPago, asi que no necesita adaptador. */
public class PagoEfectivo implements MetodoPago {
    @Override
    public boolean pagar(double monto) {
        System.out.printf("Pago en efectivo de S/ %.2f recibido.%n", monto);
        return true;
    }

    @Override public String getNombre() { return "Efectivo"; }
}
