import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Map;

public class Controlador {
    private Vista vista;
    private Modelo modelo;
    public Controlador(Vista vista, Modelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        if (vista.mostrarLogin()) {
            String[] c = vista.getCredenciales();
            modelo.configurarConexion(c[0], c[1], c[2], c[3], c[4]);
            inicializarEventos();
            cargarDatos();
            vista.setVisible(true);
        } else { System.exit(0); }
    }
    private void inicializarEventos() {
        vista.addCargarListener(e -> cargarDatos());

        vista.addAgregarListener(e -> {
            Map<String, String> datos = vista.mostrarFormularioRegistro();
            if (datos != null) {
                try {
                    modelo.insertarRegistro(vista.getNombreTabla(), datos);
                    cargarDatos();
                } catch (SQLException ex) {
                    manejarErrorSql(ex);
                }
            }
        });
        vista.getTabla().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int col = vista.getTabla().getColumnCount() - 1;
                int fila = vista.getTabla().rowAtPoint(e.getPoint());
                if (fila != -1 && vista.getTabla().columnAtPoint(e.getPoint()) == col) {
                    Object id = vista.getTabla().getValueAt(fila, 0);
                    if (JOptionPane.showConfirmDialog(null, "¿Eliminar ID " + id + "?") == JOptionPane.YES_OPTION) {
                        try {
                            modelo.eliminarRegistro(vista.getNombreTabla(), vista.getTabla().getColumnName(0), id);
                            cargarDatos();
                        } catch (SQLException ex) { manejarErrorSql(ex); }
                    }
                }
            }
        });
    }

    private void cargarDatos() {
        try {
            var modelTabla = modelo.obtenerDatosTabla(vista.getNombreTabla());
            modelTabla.addTableModelListener(e -> {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int r = e.getFirstRow(), c = e.getColumn();
                    Object id = vista.getTabla().getValueAt(r, 0);
                    try {
                        modelo.actualizarCelda(vista.getNombreTabla(), vista.getTabla().getColumnName(0), id, vista.getTabla().getColumnName(c), vista.getTabla().getValueAt(r, c));
                    } catch (SQLException ex) {
                        manejarErrorSql(ex);
                        cargarDatos();
                    }
                }
            });
            vista.getTabla().setModel(modelTabla);
        } catch (SQLException ex) { manejarErrorSql(ex); }
    }
    private void manejarErrorSql(SQLException ex) {
        String msg = ex.getMessage();
        if (msg.contains("value too long")) {
            JOptionPane.showMessageDialog(null, "❌ Error: El texto es demasiado largo para esta columna.");
        } else if (msg.contains("invalid input syntax")) {
            JOptionPane.showMessageDialog(null, "❌ Error: Formato de dato incorrecto (ej. letras en campo numérico).");
        } else {
            JOptionPane.showMessageDialog(null, "❌ Error de base de datos:\n" + msg);
        }
    }
}