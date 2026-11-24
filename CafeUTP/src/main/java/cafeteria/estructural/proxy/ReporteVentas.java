package cafeteria.estructural.proxy;

import cafeteria.creacional.RegistroPedidos;
import cafeteria.modelo.Bebida;
import cafeteria.modelo.Pedido;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sujeto real (RealSubject) del patron PROXY: muestra el resumen de dinero del negocio
 * (total vendido, ticket promedio, ventas por bebida y cobro de cada pedido).
 * Es informacion solo para el dueno: unicamente se llega a ella a traves del proxy.
 */
public class ReporteVentas implements Reporte {
    @Override
    public void mostrar() {
        RegistroPedidos registro = RegistroPedidos.getInstancia();
        List<Pedido> pedidos = registro.getPedidos();
        System.out.println("============ REPORTE DE VENTAS ============");
        if (pedidos.isEmpty()) {
            System.out.println("No hay ventas registradas.");
            return;
        }

        int bebidasVendidas = pedidos.stream().mapToInt(p -> p.getBebidas().size()).sum();
        double total = registro.getTotalVentas();
        System.out.printf("Pedidos registrados : %d%n", pedidos.size());
        System.out.printf("Bebidas vendidas    : %d%n", bebidasVendidas);
        System.out.printf("TOTAL VENDIDO       : S/ %.2f%n", total);
        System.out.printf("Ticket promedio     : S/ %.2f%n", total / pedidos.size());

        // Ventas por bebida base (Cafe, Te, Jugo), sumando lo que se cobro con extras
        Map<String, double[]> porBebida = new LinkedHashMap<>();   // nombre -> {cantidad, monto}
        for (Pedido p : pedidos) {
            for (Bebida b : p.getBebidas()) {
                String base = b.getDescripcion().split(" \\+ ")[0];
                double[] datos = porBebida.computeIfAbsent(base, k -> new double[2]);
                datos[0]++;
                datos[1] += b.getPrecio();
            }
        }
        System.out.println();
        System.out.println("Ventas por bebida:");
        porBebida.forEach((nombre, d) ->
                System.out.printf("  %-6s %3d und.        S/ %7.2f%n", nombre, (int) d[0], d[1]));

        System.out.println();
        System.out.println("Cobro por pedido:");
        for (Pedido p : pedidos) {
            System.out.printf("  #%-3d %-15s     S/ %7.2f%n", p.getId(), p.getCliente(), p.getTotal());
        }
    }
}
