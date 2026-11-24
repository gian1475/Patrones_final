package cafeteria.app;

import cafeteria.creacional.PedidoBuilder;
import cafeteria.estructural.adapter.MetodoPago;
import cafeteria.estructural.facade.CafeteriaFacade;
import cafeteria.modelo.Bebida;
import cafeteria.modelo.Pedido;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Menu de consola. Es el "cliente" del patron FACADE: solo habla con CafeteriaFacade
 * y no conoce la fabrica, los decoradores, los pagos ni el registro.
 */
public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final CafeteriaFacade cafeteria = new CafeteriaFacade();

    public static void main(String[] args) {
        int opcion;
        do {
            System.out.println();
            System.out.println("========== CafeUTP ==========");
            System.out.println("1. Nuevo pedido");
            System.out.println("2. Avanzar estado de un pedido");
            System.out.println("3. Repetir ultimo pedido");
            System.out.println("4. Reporte de ventas (admin)");
            System.out.println("0. Salir");
            opcion = leerEntero("Opcion: ");
            switch (opcion) {
                case 1: nuevoPedido(); break;
                case 2: avanzarPedido(); break;
                case 3: repetirPedido(); break;
                case 4: cafeteria.verReporte(leerTexto("Clave de administrador: ")); break;
                case 0: System.out.println("Gracias por usar CafeUTP."); break;
                default: System.out.println("Opcion no valida.");
            }
        } while (opcion != 0);
    }

    private static void nuevoPedido() {
        PedidoBuilder builder = new PedidoBuilder().cliente(leerTexto("Nombre del cliente: "));
        while (true) {
            System.out.println("Bebidas: 1. Cafe (S/5.00)  2. Te (S/4.00)  3. Jugo (S/7.00)  0. Terminar");
            int tipo = leerEntero("Bebida: ");
            if (tipo == 0) break;
            if (tipo < 1 || tipo > 3) { System.out.println("Bebida no valida."); continue; }

            List<Integer> extras = new ArrayList<>();
            while (true) {
                System.out.println("  Extras: 1. Leche (+1.00)  2. Crema (+1.50)  3. Canela (+0.50)  0. Ninguno mas");
                int extra = leerEntero("  Extra: ");
                if (extra == 0) break;
                if (extra >= 1 && extra <= 3) extras.add(extra);
                else System.out.println("  Extra no valido.");
            }
            Bebida bebida = cafeteria.prepararBebida(tipo, extras);
            builder.agregar(bebida);
            System.out.printf("  Agregado: %s  S/ %.2f%n", bebida.getDescripcion(), bebida.getPrecio());
        }
        if (builder.estaVacio()) {
            System.out.println("Pedido cancelado: no se agregaron bebidas.");
            return;
        }
        Pedido pedido = cafeteria.confirmarPedido(builder, pedirMetodoPago());
        mostrarResultado(pedido);
    }

    private static void avanzarPedido() {
        List<Pedido> pedidos = cafeteria.listarPedidos();
        if (pedidos.isEmpty()) { System.out.println("No hay pedidos."); return; }
        for (Pedido p : pedidos) System.out.printf("  #%d %s -> %s%n", p.getId(), p.getCliente(), p.getEstado());
        if (!cafeteria.avanzarPedido(leerEntero("Numero de pedido: "))) System.out.println("Pedido no encontrado.");
    }

    private static void repetirPedido() {
        Pedido ultimo = cafeteria.ultimoPedido();
        if (ultimo == null) { System.out.println("Aun no hay pedidos para repetir."); return; }
        System.out.println("Se repetira:");
        System.out.println(ultimo);
        mostrarResultado(cafeteria.repetirUltimoPedido(pedirMetodoPago()));
    }

    private static MetodoPago pedirMetodoPago() {
        int op = leerEntero("Pago: 1. Efectivo  2. Yape -> ");
        String celular = op == 2 ? leerTexto("Celular (9 digitos): ") : null;
        return cafeteria.crearPago(op, celular);
    }

    private static void mostrarResultado(Pedido pedido) {
        if (pedido == null) {
            System.out.println("Pago rechazado. El pedido no se registro.");
        } else {
            System.out.println("Pedido registrado con exito:");
            System.out.println(pedido);
        }
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            String linea = leerTexto(mensaje);
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero valido.");
            }
        }
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!sc.hasNextLine()) System.exit(0);
        return sc.nextLine().trim();
    }
}
