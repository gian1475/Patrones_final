package cafeteria.estructural.proxy;

import cafeteria.creacional.RegistroPedidos;
import cafeteria.modelo.Pedido;

/** Sujeto real (RealSubject) del patron PROXY: muestra todas las ventas. Solo se llega a el a traves del proxy. */
public class ReporteVentas implements Reporte {
    @Override
    public void mostrar() {
        RegistroPedidos registro = RegistroPedidos.getInstancia();
        System.out.println("========= REPORTE DE VENTAS =========");
        if (registro.getPedidos().isEmpty()) System.out.println("No hay ventas registradas.");
        for (Pedido p : registro.getPedidos()) {
            System.out.printf("#%-3d %-15s %-15s S/ %6.2f%n",
                    p.getId(), p.getCliente(), p.getEstado(), p.getTotal());
        }
        System.out.printf("TOTAL VENDIDO: S/ %.2f%n", registro.getTotalVentas());
    }
}
