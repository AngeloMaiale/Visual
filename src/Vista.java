import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;

public class Vista extends JFrame {
    private JTextField txtHost, txtPuerto, txtDB, txtUser, txtTabla;
    private JPasswordField txtPass;
    private JButton btnCargar, btnAgregar;
    private JTable tabla;
    public Vista() {
        setTitle("CRUD Dinámico MVC - PostgreSQL");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        JPanel panelSup = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        txtTabla = new JTextField("materias", 12);
        btnCargar = new JButton("🔄 Refrescar");
        btnAgregar = new JButton("➕ Nueva Fila");
        panelSup.add(new JLabel("Tabla:"));
        panelSup.add(txtTabla);
        panelSup.add(btnCargar);
        panelSup.add(btnAgregar);
        add(panelSup, BorderLayout.NORTH);
        tabla = new JTable();
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }
    public Map<String, String> mostrarFormularioRegistro() {
        Map<String, JTextField> campos = new LinkedHashMap<>();
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        for (int i = 0; i < tabla.getColumnCount() - 1; i++) {
            String col = tabla.getColumnName(i);
            JTextField txt = new JTextField();
            panel.add(new JLabel(col + ":"));
            panel.add(txt);
            campos.put(col, txt);
        }
        int result = JOptionPane.showConfirmDialog(this, panel, "Ingresar Datos", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Map<String, String> valores = new LinkedHashMap<>();
            campos.forEach((k, v) -> valores.put(k, v.getText()));
            return valores;
        }
        return null;
    }
    public boolean mostrarLogin() {
        JPanel p = new JPanel(new GridLayout(5, 2));
        txtHost = new JTextField("localhost");
        txtPuerto = new JTextField("5432");
        txtDB = new JTextField("Prueba");
        txtUser = new JTextField("postgres");
        txtPass = new JPasswordField("123456");
        p.add(new JLabel("Host:")); p.add(txtHost);
        p.add(new JLabel("Port:")); p.add(txtPuerto);
        p.add(new JLabel("DB:")); p.add(txtDB);
        p.add(new JLabel("User:")); p.add(txtUser);
        p.add(new JLabel("Pass:")); p.add(txtPass);
        return JOptionPane.showConfirmDialog(null, p, "Login", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
    }
    public String[] getCredenciales() {
        return new String[]{txtHost.getText(), txtPuerto.getText(), txtDB.getText(), txtUser.getText(), new String(txtPass.getPassword())};
    }
    public JTable getTabla() { return tabla; }
    public String getNombreTabla() { return txtTabla.getText(); }
    public void addCargarListener(ActionListener l) { btnCargar.addActionListener(l); }
    public void addAgregarListener(ActionListener l) { btnAgregar.addActionListener(l); }
}