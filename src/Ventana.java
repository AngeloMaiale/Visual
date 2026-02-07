import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Vector;

public class Ventana extends JFrame {

    // Configuración de conexión (Ajusta si es necesario)
    private static final String URL = "jdbc:postgresql://localhost:5432/Prueba";
    private static final String USER = "postgres";
    private static final String PASS = "123456";

    // Componentes globales
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtTabla; // Ahora es global para leerlo desde el botón

    public Ventana() {
        setTitle("Visor Universal de Tablas PostgreSQL");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));

        // 1. Panel Superior (Buscador)
        add(crearPanelSuperior(), BorderLayout.NORTH);

        // 2. Tabla Central (Inicialmente vacía)
        tabla = new JTable();
        modeloTabla = new DefaultTableModel();
        tabla.setModel(modeloTabla);
        tabla.setRowHeight(25);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Scroll para la tabla
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JLabel lblInstruccion = new JLabel("Nombre de la Tabla:");
        lblInstruccion.setFont(new Font("SansSerif", Font.BOLD, 14));

        txtTabla = new JTextField(15);
        txtTabla.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtTabla.setText("materias"); // Valor por defecto

        JButton btnCargar = new JButton("Cargar Tabla");
        btnCargar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCargar.setBackground(new Color(70, 130, 180)); // Azul acero
        btnCargar.setForeground(Color.WHITE);

        // Acción del botón: Leer el texto y llamar a la función
        btnCargar.addActionListener((ActionEvent e) -> {
            String nombreTabla = txtTabla.getText().trim();
            if (!nombreTabla.isEmpty()) {
                cargarTablaDinamica(nombreTabla);
            }
        });

        panel.add(lblInstruccion);
        panel.add(txtTabla);
        panel.add(btnCargar);

        return panel;
    }

    private void cargarTablaDinamica(String nombreTabla) {
        // OJO: En JDBC los nombres de tabla no pueden ir con '?' (parámetros).
        // Se deben concatenar, pero esto tiene riesgo de inyección SQL si es público.
        // Para uso local/educativo está bien.
        String sql = "SELECT * FROM " + nombreTabla;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // --- PASO 1: Reconstruir las Columnas ---
            ResultSetMetaData metaData = rs.getMetaData();
            int numeroColumnas = metaData.getColumnCount();

            // Vector es una lista antigua que usa Swing para los nombres de columna
            Vector<String> nombresColumnas = new Vector<>();

            for (int i = 1; i <= numeroColumnas; i++) {
                nombresColumnas.add(metaData.getColumnName(i));
            }

            // Reiniciamos el modelo con las nuevas columnas
            modeloTabla.setColumnIdentifiers(nombresColumnas);
            modeloTabla.setRowCount(0); // Borrar filas viejas

            // --- PASO 2: Llenar los Datos ---
            while (rs.next()) {
                Vector<Object> fila = new Vector<>();
                for (int i = 1; i <= numeroColumnas; i++) {
                    fila.add(rs.getObject(i));
                }
                modeloTabla.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error: No se pudo cargar la tabla '" + nombreTabla + "'.\n" + e.getMessage(),
                    "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Ventana().setVisible(true);
        });
    }
}