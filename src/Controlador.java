import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
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
            try {
                List<String> tablas = modelo.obtenerNombresTablas();
                vista.llenarComboTablas(tablas);
                inicializarEventos();
                if (!tablas.isEmpty()) {
                    cargarDatos();
                }
                vista.setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al conectar: " + e.getMessage());
                System.exit(0);
            }
        } else { System.exit(0); }
    }
    private void inicializarEventos() {
        vista.addCargarListener(e -> cargarDatos());
        vista.addCambioTablaListener(e -> cargarDatos());
        vista.addAgregarListener(e -> {
            Map<String, String> datos = vista.mostrarFormularioRegistro();
            if (datos != null) {
                try {
                    modelo.insertarRegistro(vista.getTablaSeleccionada(), datos);
                    cargarDatos();
                } catch (SQLException ex) { manejarErrorSql(ex); }
            }
        });
        vista.getTabla().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int col = vista.getTabla().getColumnCount() - 1;
                int fila = vista.getTabla().rowAtPoint(e.getPoint());
                if (fila != -1 && vista.getTabla().columnAtPoint(e.getPoint()) == col) {
                    Object id = vista.getTabla().getValueAt(fila, 0);
                    if (JOptionPane.showConfirmDialog(null, "¿Eliminar Registro " + id + "?") == JOptionPane.YES_OPTION) {
                        try {
                            modelo.eliminarRegistro(vista.getTablaSeleccionada(), vista.getTabla().getColumnName(0), id);
                            cargarDatos();
                        } catch (SQLException ex) { manejarErrorSql(ex); }
                    }
                }
            }
        });
    }
    private void cargarDatos() {
        String tabla = vista.getTablaSeleccionada();
        if (tabla == null) return;
        try {
            var modelTabla = modelo.obtenerDatosTabla(tabla);
            modelTabla.addTableModelListener(e -> {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int r = e.getFirstRow(), c = e.getColumn();
                    if (r < 0 || c < 0) return;
                    Object id = vista.getTabla().getValueAt(r, 0);
                    try {
                        modelo.actualizarCelda(tabla, vista.getTabla().getColumnName(0), id, vista.getTabla().getColumnName(c), vista.getTabla().getValueAt(r, c));
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
        JOptionPane.showMessageDialog(null, "❌ Error Base de Datos:\n" + ex.getMessage());
    }
}