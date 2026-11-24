package cafeteria.app;

import javax.swing.SwingUtilities;

/** Punto de entrada: abre la ventana principal del sistema (formulario). */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
