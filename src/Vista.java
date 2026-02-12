import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Vista extends JFrame {
    private JTextField txtHost, txtPuerto, txtDB, txtUser;
    private JPasswordField txtPass;
    private JComboBox<String> cboTablas;
    private JButton btnCargar, btnAgregar;
    private JTable tabla;
    private final Color FONDO = new Color(30, 30, 30);
    private final Color PANEL = new Color(45, 45, 45);
    private final Color TEXTO = new Color(230, 230, 230);
    private final Color ACENTO = new Color(70, 130, 180);
    public Vista() {
        setTitle("Gestor de Base de Datos - Dark Mode");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(FONDO);
        JPanel panelSup = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panelSup.setBackground(PANEL);
        panelSup.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel lblTabla = new JLabel("Seleccionar Tabla:");
        lblTabla.setForeground(TEXTO);
        lblTabla.setFont(new Font("SansSerif", Font.BOLD, 13));
        cboTablas = new JComboBox<>();
        cboTablas.setPreferredSize(new Dimension(200, 30));
        btnCargar = new JButton("🔄 Refrescar");
        estilizarBoton(btnCargar);
        btnAgregar = new JButton("➕ Nueva Fila");
        estilizarBoton(btnAgregar);
        panelSup.add(lblTabla);
        panelSup.add(cboTablas);
        panelSup.add(btnCargar);
        panelSup.add(btnAgregar);
        add(panelSup, BorderLayout.NORTH);
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(FONDO);
        panelCentral.setBorder(new EmptyBorder(25, 25, 25, 25)); // Margen elegante
        tabla = new JTable();
        estilizarTabla(tabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        panelCentral.add(scroll, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }
    private void estilizarBoton(JButton b) {
        b.setBackground(ACENTO);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
    }
    private void estilizarTabla(JTable t) {
        t.setBackground(PANEL);
        t.setForeground(TEXTO);
        t.setGridColor(new Color(60, 60, 60));
        t.setSelectionBackground(ACENTO);
        t.setSelectionForeground(Color.WHITE);
        t.setRowHeight(30);
        t.setShowVerticalLines(false);
        t.getTableHeader().setBackground(new Color(20, 20, 20));
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setPreferredSize(new Dimension(0, 35));
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        t.setDefaultRenderer(Object.class, centerRenderer);
    }
    public boolean mostrarLogin() {
        // Aplicamos colores al diálogo de login
        UIManager.put("OptionPane.background", FONDO);
        UIManager.put("Panel.background", FONDO);
        UIManager.put("Label.foreground", TEXTO);
        JPanel p = new JPanel(new GridLayout(5, 2, 10, 10));
        p.setBackground(FONDO);
        txtHost = new JTextField("localhost");
        txtPuerto = new JTextField("5432");
        txtDB = new JTextField(""); // Nombre de tu DB
        txtUser = new JTextField("postgres");
        txtPass = new JPasswordField("");
        JTextField[] campos = {txtHost, txtPuerto, txtDB, txtUser, txtPass};
        for(JTextField f : campos) {
            f.setBackground(PANEL);
            f.setForeground(Color.WHITE);
            f.setCaretColor(Color.WHITE);
            f.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        }
        p.add(new JLabel("Host:")); p.add(txtHost);
        p.add(new JLabel("Puerto:")); p.add(txtPuerto);
        p.add(new JLabel("Base de Datos:")); p.add(txtDB);
        p.add(new JLabel("Usuario:")); p.add(txtUser);
        p.add(new JLabel("Contraseña:")); p.add(txtPass);
        int result = JOptionPane.showConfirmDialog(null, p, "Conexión PostgreSQL",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }
    public String[] getCredenciales() {
        return new String[]{
                txtHost.getText(),
                txtPuerto.getText(),
                txtDB.getText(),
                txtUser.getText(),
                new String(txtPass.getPassword())
        };
    }
    public void llenarComboTablas(List<String> tablas) {
        cboTablas.removeAllItems();
        for (String t : tablas) cboTablas.addItem(t);
    }
    public String getTablaSeleccionada() {
        return (String) cboTablas.getSelectedItem();
    }
    public JTable getTabla() {
        return tabla;
    }
    public Map<String, String> mostrarFormularioRegistro() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 15, 15));
        panel.setBackground(FONDO);
        Map<String, JTextField> campos = new LinkedHashMap<>();
        for (int i = 1; i < tabla.getColumnCount() - 1; i++) {
            String col = tabla.getColumnName(i);
            JLabel label = new JLabel(col + ":");
            label.setForeground(TEXTO);
            JTextField txt = new JTextField();
            txt.setBackground(PANEL);
            txt.setForeground(Color.WHITE);
            txt.setCaretColor(Color.WHITE);
            txt.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(80, 80, 80)),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            panel.add(label);
            panel.add(txt);
            campos.put(col, txt);
        }
        UIManager.put("OptionPane.background", FONDO);
        UIManager.put("Panel.background", FONDO);
        int result = JOptionPane.showConfirmDialog(this, panel, "Nueva Entrada",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            Map<String, String> valores = new LinkedHashMap<>();
            campos.forEach((k, v) -> valores.put(k, v.getText()));
            return valores;
        }
        return null;
    }
    public void mostrarError(String mensaje) {
        UIManager.put("OptionPane.background", FONDO);
        UIManager.put("Panel.background", FONDO);
        UIManager.put("OptionPane.messageForeground", new Color(255, 100, 100));
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    public void addCargarListener(ActionListener l) { btnCargar.addActionListener(l); }
    public void addAgregarListener(ActionListener l) { btnAgregar.addActionListener(l); }
    public void addCambioTablaListener(ActionListener l) { cboTablas.addActionListener(l); }
}