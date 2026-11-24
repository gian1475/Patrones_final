package cafeteria.estructural.adapter;

/**
 * Simula una API externa de billetera digital.
 * Su interfaz es INCOMPATIBLE con MetodoPago: pide celular y monto en centimos
 * y responde un texto en lugar de un boolean.
 */
public class ApiYape {
    public String cobrar(String celular, int montoEnCentimos) {
        if (celular == null || !celular.matches("9\\d{8}")) return "RECHAZADO";
        System.out.printf("[ApiYape] Cobro de %d centimos al celular %s%n", montoEnCentimos, celular);
        return "APROBADO";
    }
}
