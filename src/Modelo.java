import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

public class Modelo {
    private final String URL = "jdbc:postgresql://localhost:5432/Prueba";
    private final String USER = "postgres";
    private final String PASS = "123456";
    public DefaultTableModel obtenerDatosTabla(String nombreTabla) throws SQLException {
        DefaultTableModel modelo = new DefaultTableModel();
        if (nombreTabla == null || nombreTabla.trim().isEmpty()) {
            throw new SQLException("El nombre de la tabla no puede estar vacío.");
        }
        String sql = "SELECT * FROM " + nombreTabla;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int colCount = metaData.getColumnCount();
            Vector<String> columnas = new Vector<>();
            for (int i = 1; i <= colCount; i++) {
                columnas.add(metaData.getColumnName(i));
            }
            modelo.setColumnIdentifiers(columnas);
            while (rs.next()) {
                Vector<Object> fila = new Vector<>();
                for (int i = 1; i <= colCount; i++) {
                    fila.add(rs.getObject(i));
                }
                modelo.addRow(fila);
            }
        }
        return modelo;
    }
}