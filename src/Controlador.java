import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class Controlador {
    private Vista vista;
    private Modelo modelo;
    public Controlador(Vista vista, Modelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        this.vista.addBotonListener(new CargarListener());
    }
    class CargarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String nombreTabla = vista.getNombreTabla();
            try {
                DefaultTableModel datos = modelo.obtenerDatosTabla(nombreTabla);
                vista.actualizarTabla(datos);
            } catch (SQLException ex) {
                vista.mostrarError("Error al cargar la BD: " + ex.getMessage());
            }
        }
    }
}