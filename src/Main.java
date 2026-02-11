import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Modelo modelo = new Modelo();
            Vista vista = new Vista();
            new Controlador(vista, modelo);
            vista.setVisible(true);
        });
    }
}