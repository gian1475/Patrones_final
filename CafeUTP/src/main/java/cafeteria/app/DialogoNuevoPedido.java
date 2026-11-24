package cafeteria.app;

import cafeteria.creacional.PedidoBuilder;
import cafeteria.estructural.adapter.MetodoPago;
import cafeteria.estructural.facade.CafeteriaFacade;
import cafeteria.modelo.Bebida;
import cafeteria.modelo.Pedido;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/** Formulario para armar un pedido: cliente, bebidas con extras y pago. */
class DialogoNuevoPedido extends JDialog {
    private final VentanaPrincipal ventana;
    private final CafeteriaFacade cafeteria;
    private final List<Bebida> bebidas = new ArrayList<>();

    private final JTextField cliente = new JTextField(18);
    private final JComboBox<String> bebida =
            new JComboBox<>(new String[]{"Café  (S/ 5.00)", "Té  (S/ 4.00)", "Jugo  (S/ 7.00)"});
    private final JCheckBox leche = new JCheckBox("Leche (+1.00)");
    private final JCheckBox crema = new JCheckBox("Crema (+1.50)");
    private final JCheckBox canela = new JCheckBox("Canela (+0.50)");
    private final DefaultListModel<String> agregadas = new DefaultListModel<>();
    private final JList<String> lista = new JList<>(agregadas);
    private final JLabel total = new JLabel("Total: S/ 0.00");

    DialogoNuevoPedido(VentanaPrincipal ventana, CafeteriaFacade cafeteria) {
        super(ventana, "Nuevo pedido", true);
        this.ventana = ventana;
        this.cafeteria = cafeteria;

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.add(fila(new JLabel("Cliente:"), cliente));
        formulario.add(fila(new JLabel("Bebida:"), bebida));
        formulario.add(fila(new JLabel("Extras:"), leche, crema, canela));
        formulario.add(fila(
                VentanaPrincipal.boton("Agregar bebida", VentanaPrincipal.AZUL, e -> agregarBebida()),
                VentanaPrincipal.boton("Quitar bebida", VentanaPrincipal.NARANJA, e -> quitarBebida())));

        lista.setFont(VentanaPrincipal.MONO);
        lista.setVisibleRowCount(6);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createTitledBorder("Bebidas del pedido (elige una y presiona \"Quitar\" para sacarla)"));

        total.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JPanel pie = new JPanel(new BorderLayout());
        pie.add(total, BorderLayout.WEST);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.add(VentanaPrincipal.boton("Confirmar y pagar", VentanaPrincipal.VERDE, e -> confirmar()));
        acciones.add(VentanaPrincipal.boton("Cancelar", VentanaPrincipal.GRIS, e -> dispose()));
        pie.add(acciones, BorderLayout.EAST);

        JPanel raiz = new JPanel(new BorderLayout(0, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(12, 14, 10, 14));
        raiz.add(formulario, BorderLayout.NORTH);
        raiz.add(scroll, BorderLayout.CENTER);
        raiz.add(pie, BorderLayout.SOUTH);
        setContentPane(raiz);
        pack();
        setLocationRelativeTo(ventana);
    }

    private static JPanel fila(Component... componentes) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        for (Component c : componentes) p.add(c);
        return p;
    }

    private void agregarBebida() {
        List<Integer> extras = new ArrayList<>();
        if (leche.isSelected()) extras.add(1);
        if (crema.isSelected()) extras.add(2);
        if (canela.isSelected()) extras.add(3);

        // La Facade usa FACTORY (crea la bebida) y DECORATOR (le pone los extras)
        Bebida b = cafeteria.prepararBebida(bebida.getSelectedIndex() + 1, extras);
        bebidas.add(b);
        agregadas.addElement(String.format("%-30s S/ %6.2f", b.getDescripcion(), b.getPrecio()));
        actualizarTotal();
        leche.setSelected(false);
        crema.setSelected(false);
        canela.setSelected(false);
    }

    private void quitarBebida() {
        if (bebidas.isEmpty()) return;
        int i = lista.getSelectedIndex();
        if (i < 0) {
            JOptionPane.showMessageDialog(this, "Primero elige en la lista la bebida que quieres quitar.",
                    "Quitar bebida", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        bebidas.remove(i);
        agregadas.remove(i);
        actualizarTotal();
    }

    private void actualizarTotal() {
        double suma = bebidas.stream().mapToDouble(Bebida::getPrecio).sum();
        total.setText(String.format("Total: S/ %.2f", suma));
    }

    private void confirmar() {
        if (bebidas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agrega al menos una bebida.", "Pedido vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        MetodoPago pago = ventana.pedirMetodoPago(this);
        if (pago == null) return;

        // BUILDER: el pedido se arma paso a paso (cliente y cada bebida) y se entrega con build()
        PedidoBuilder builder = new PedidoBuilder().cliente(cliente.getText().trim());
        for (Bebida b : bebidas) builder.agregar(b);

        Pedido pedido = cafeteria.confirmarPedido(builder, pago);
        ventana.mostrarResultado(this, pedido);
        if (pedido != null) dispose();
    }
}
