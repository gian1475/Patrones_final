package cafeteria.estructural.adapter;

/** ADAPTER: convierte la interfaz de ApiYape en la interfaz MetodoPago. */
public class YapeAdapter implements MetodoPago {
    private final ApiYape api = new ApiYape();
    private final String celular;

    public YapeAdapter(String celular) { this.celular = celular; }

    @Override
    public boolean pagar(double monto) {
        int centimos = (int) Math.round(monto * 100);
        return "APROBADO".equals(api.cobrar(celular, centimos));
    }

    @Override public String getNombre() { return "Yape"; }
}
