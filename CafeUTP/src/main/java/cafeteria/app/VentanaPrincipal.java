package cafeteria.app;

import cafeteria.estructural.adapter.MetodoPago;
import cafeteria.estructural.facade.CafeteriaFacade;
import cafeteria.modelo.Pedido;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Ventana principal (formulario). Es el "cliente" del patron FACADE: solo habla con
 * CafeteriaFacade y no conoce la fabrica, los decoradores, los pagos ni el registro.
 */
public class VentanaPrincipal extends JFrame {
    static final Color VERDE = new Color(46, 125, 50);
    static final Color AZUL = new Color(21, 101, 192);
    static final Color MORADO = new Color(106, 27, 154);
    static final Color ROJO = new Color(198, 40, 40);
    static final Color NARANJA = new Color(230, 81, 0);
    static final Color GRIS = new Color(97, 97, 97);
    static final Color CAFE = new Color(93, 64, 55);
    static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 13);

    private final CafeteriaFacade cafeteria = new CafeteriaFacade();
    private final TablaPedidos modeloTabla = new TablaPedidos(cafeteria.listarPedidos());
    private final JTable tabla = new JTable(modeloTabla);
    private final JTextArea actividad = new JTextArea(6, 46);

    public VentanaPrincipal() {
        super("CafeUTP - Sistema de pedidos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        ToolTipManager.sharedInstance().setInitialDelay(300);
        ToolTipManager.sharedInstance().setDismissDelay(20000);

        // OBSERVER: la tabla se suscribe como un observador mas de cada pedido
        cafeteria.agregarObservador(modeloTabla);

        JLabel titulo = new JLabel("CaféUTP", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(CAFE);
        JLabel subtitulo = new JLabel("Sistema de pedidos de la cafetería  ·  pasa el mouse sobre un botón para ver sus patrones",
                SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel estadoBD = new JLabel("", SwingConstants.CENTER);
        estadoBD.setFont(new Font("Segoe UI", Font.BOLD, 13));
        if (cafeteria.baseDatosConectada()) {
            estadoBD.setText("● Base de datos conectada (PostgreSQL en Neon)");
            estadoBD.setForeground(VERDE);
        } else {
            estadoBD.setText("● Sin conexión a la base de datos: los pedidos no se guardarán");
            estadoBD.setForeground(ROJO);
            estadoBD.setToolTipText("Motivo: " + cafeteria.errorBaseDatos());
        }
        JPanel cabecera = new JPanel(new GridLayout(3, 1));
        cabecera.add(titulo);
        cabecera.add(subtitulo);
        cabecera.add(estadoBD);

        JPanel botones = new JPanel(new GridLayout(1, 4, 10, 0));
        botones.add(conAyuda(boton("Nuevo pedido", VERDE, e -> new DialogoNuevoPedido(this, cafeteria).setVisible(true)),
                "Arma y cobra un pedido nuevo.",
                "Factory (crea la bebida) · Decorator (extras) · Builder (arma el pedido)",
                "Adapter (pago con Yape) · Observer (avisos) · Singleton (lo guarda)"));
        botones.add(conAyuda(boton("Avanzar estado", AZUL, e -> avanzarPedido()),
                "Avanza el pedido elegido en la tabla.",
                "State (el estado decide el siguiente) · Observer (avisa a cocina, pantalla y tabla)",
                "Singleton (busca el pedido)"));
        botones.add(conAyuda(boton("Repetir último", MORADO, e -> repetirPedido()),
                "Vuelve a pedir lo mismo que el último pedido.",
                "Prototype (clona el pedido) · Singleton (da el último y guarda la copia)",
                "Adapter (pago) · Observer (avisos)"));
        botones.add(conAyuda(boton("Reporte de ventas", ROJO, e -> verReporte()),
                "Solo el dueño (clave admin123): total vendido, ventas por bebida y cobro de cada pedido.",
                "Proxy (revisa la clave antes de mostrar el reporte)",
                "Singleton (de ahí salen las ventas)"));

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pie.add(boton("Salir", GRIS, e -> System.exit(0)));

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.add(botones, BorderLayout.NORTH);
        centro.add(crearPanelTabla(), BorderLayout.CENTER);
        centro.add(crearPanelActividad(), BorderLayout.SOUTH);

        JPanel raiz = new JPanel(new BorderLayout(0, 12));
        raiz.setBorder(BorderFactory.createEmptyBorder(14, 18, 10, 18));
        raiz.add(cabecera, BorderLayout.NORTH);
        raiz.add(centro, BorderLayout.CENTER);
        raiz.add(pie, BorderLayout.SOUTH);
        setContentPane(raiz);

        redirigirConsola();
        pack();
        setLocationRelativeTo(null);
    }

    private JScrollPane crearPanelTabla() {
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setFillsViewportHeight(true);
        tabla.setPreferredScrollableViewportSize(new Dimension(680, 170));

        int[] anchos = {35, 120, 385, 140};
        for (int i = 0; i < anchos.length; i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel, boolean foco, int f, int c) {
                JLabel celda = (JLabel) super.getTableCellRendererComponent(t, valor, sel, foco, f, c);
                celda.setHorizontalAlignment(SwingConstants.CENTER);
                celda.setFont(celda.getFont().deriveFont(Font.BOLD));
                if (!sel) {
                    celda.setForeground(Color.WHITE);
                    celda.setBackground(colorEstado(String.valueOf(valor)));
                }
                return celda;
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Pedidos (se actualiza sola: es un observador más)"));
        return scroll;
    }

    private JScrollPane crearPanelActividad() {
        // Solo para ver: no se puede escribir ni poner el cursor
        actividad.setEditable(false);
        actividad.setFocusable(false);
        actividad.setBackground(new Color(245, 245, 245));
        actividad.setFont(MONO);
        JScrollPane scroll = new JScrollPane(actividad);
        scroll.setBorder(BorderFactory.createTitledBorder("Actividad (cocina, pantalla del cliente y pagos)"));
        return scroll;
    }

    private static Color colorEstado(String estado) {
        switch (estado) {
            case "EN PREPARACION": return NARANJA;
            case "LISTO": return VERDE;
            case "ENTREGADO": return AZUL;
            default: return GRIS;   // REGISTRADO
        }
    }

    /** Boton plano de color, usado en todas las ventanas. */
    static JButton boton(String texto, Color color, ActionListener accion) {
        JButton b = new JButton(texto);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(Math.max(120, b.getPreferredSize().width + 30), 44));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(accion);
        return b;
    }

    /** Texto de ayuda que aparece al pasar el mouse: que hace el boton y que patrones usa. */
    private static JButton conAyuda(JButton b, String queHace, String... patrones) {
        b.setToolTipText("<html><b>" + queHace + "</b><br>Patrones: " + String.join("<br>", patrones) + "</html>");
        return b;
    }

    // ----------------------------------------------------------------- Avanzar estado
    private void avanzarPedido() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Aún no hay pedidos.", "Avanzar estado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Primero haz clic en un pedido de la tabla.", "Avanzar estado",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // STATE: el estado actual del pedido decide cual es el siguiente.
        // OBSERVER: al cambiar, el pedido avisa a cocina, pantalla y a la tabla.
        cafeteria.avanzarPedido(modeloTabla.getPedido(fila).getId());
    }

    // ----------------------------------------------------------------- Repetir pedido
    private void repetirPedido() {
        Pedido ultimo = cafeteria.ultimoPedido();
        if (ultimo == null) {
            JOptionPane.showMessageDialog(this, "Aún no hay pedidos para repetir.", "Repetir pedido", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int r = JOptionPane.showConfirmDialog(this, new Object[]{"Se repetirá este pedido:", textoFijo(ultimo.toString())},
                "Repetir último pedido", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;
        MetodoPago pago = pedirMetodoPago(this);
        if (pago != null) mostrarResultado(this, cafeteria.repetirUltimoPedido(pago));
    }

    // ----------------------------------------------------------------- Reporte (Proxy)
    private void verReporte() {
        JPasswordField clave = new JPasswordField(14);
        int r = JOptionPane.showConfirmDialog(this, new Object[]{"Clave de administrador:", clave},
                "Reporte de ventas", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;
        // PROXY: el reporte se pide al proxy, que revisa la clave antes de mostrarlo
        String salida = capturarSalida(() -> cafeteria.verReporte(new String(clave.getPassword()))).trim();
        if (salida.startsWith("Acceso denegado")) {
            JOptionPane.showMessageDialog(this, salida, "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        } else {
            JTextArea area = textoFijo(salida);
            area.setOpaque(true);
            JScrollPane scroll = new JScrollPane(area);
            Dimension tam = area.getPreferredSize();
            scroll.setPreferredSize(new Dimension(tam.width + 30, Math.min(tam.height + 10, 420)));
            JOptionPane.showMessageDialog(this, scroll, "Reporte de ventas", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ----------------------------------------------------------------- Utilidades compartidas
    /** Pide efectivo o Yape. Devuelve null si el usuario cancela. */
    MetodoPago pedirMetodoPago(Component padre) {
        JComboBox<String> tipo = new JComboBox<>(new String[]{"Efectivo", "Yape"});
        JTextField celular = new JTextField(12);
        celular.setEnabled(false);
        tipo.addActionListener(e -> celular.setEnabled(tipo.getSelectedIndex() == 1));
        while (true) {
            int r = JOptionPane.showConfirmDialog(padre,
                    new Object[]{"Método de pago:", tipo, "Celular Yape (9 dígitos, empieza con 9):", celular},
                    "Pago", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r != JOptionPane.OK_OPTION) return null;
            int opcion = tipo.getSelectedIndex() + 1;
            String numero = celular.getText().trim();
            if (opcion == 2 && !numero.matches("9\\d{8}")) {
                JOptionPane.showMessageDialog(padre, "El celular de Yape debe tener 9 dígitos y empezar con 9.",
                        "Celular no válido", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            // ADAPTER: si es Yape, la Facade devuelve un YapeAdapter
            return cafeteria.crearPago(opcion, opcion == 2 ? numero : null);
        }
    }

    void mostrarResultado(Component padre, Pedido pedido) {
        if (pedido == null) {
            JOptionPane.showMessageDialog(padre, "Pago rechazado. El pedido no se registró.", "Pago rechazado",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        modeloTabla.pedidoNuevo();
        int fila = modeloTabla.getRowCount() - 1;
        tabla.setRowSelectionInterval(fila, fila);
        JOptionPane.showMessageDialog(padre, new Object[]{"Pedido registrado con éxito:", textoFijo(pedido.toString())},
                "Pedido registrado", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Area de texto de solo lectura con letra de ancho fijo, para que las columnas se alineen. */
    static JTextArea textoFijo(String texto) {
        JTextArea area = new JTextArea(texto);
        area.setFont(MONO);
        area.setEditable(false);
        area.setOpaque(false);
        return area;
    }

    /**
     * Los patrones Observer, Adapter y State escriben sus avisos con System.out.
     * En vez de mostrarlos en una consola, los mandamos al cuadro "Actividad" de la ventana.
     */
    private void redirigirConsola() {
        OutputStream haciaVentana = new OutputStream() {
            @Override public void write(int b) { write(new byte[]{(byte) b}, 0, 1); }

            @Override public void write(byte[] b, int off, int len) {
                String texto = new String(b, off, len, StandardCharsets.UTF_8);
                SwingUtilities.invokeLater(() -> {
                    actividad.append(texto);
                    actividad.setCaretPosition(actividad.getDocument().getLength());
                });
            }
        };
        System.setOut(new PrintStream(haciaVentana, true, StandardCharsets.UTF_8));
    }

    /** Ejecuta una accion y devuelve lo que imprimio, en vez de mandarlo al cuadro de actividad. */
    private static String capturarSalida(Runnable accion) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            accion.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }
}
