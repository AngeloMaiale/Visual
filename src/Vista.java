import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class Vista extends JFrame {
    private JTextField txtNombreTabla;
    private JButton btnCargar;
    private JTable tabla;
    private DefaultTableModel modeloVacio;
    public Vista() {
        setTitle("Sistema MVC - PostgreSQL");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));
        JPanel panelSup = new JPanel(new FlowLayout());
        panelSup.add(new JLabel("Tabla a consultar:"));
        txtNombreTabla = new JTextField(15);
        txtNombreTabla.setText("materias");
        panelSup.add(txtNombreTabla);
        btnCargar = new JButton("Consultar");
        panelSup.add(btnCargar);
        add(panelSup, BorderLayout.NORTH);
        modeloVacio = new DefaultTableModel();
        tabla = new JTable(modeloVacio);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }
    public String getNombreTabla() {
        return txtNombreTabla.getText();
    }
    public void actualizarTabla(DefaultTableModel nuevoModelo) {
        tabla.setModel(nuevoModelo);
    }
    public void addBotonListener(ActionListener listenForBtn) {
        btnCargar.addActionListener(listenForBtn);
    }
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
