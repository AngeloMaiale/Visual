import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.*;
public class Modelo {
    private String url, user, pass;
    public void configurarConexion(String host, String puerto, String db, String user, String pass) {
        this.url = "jdbc:postgresql://" + host + ":" + puerto + "/" + db;
        this.user = user;
        this.pass = pass;
    }
    public Connection getConexion() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
    public DefaultTableModel obtenerDatosTabla(String nombreTabla) throws SQLException {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != getColumnCount() - 1;
            }
        };
        try (Connection conn = getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + nombreTabla + " ORDER BY 1 ASC")) {
            ResultSetMetaData metaData = rs.getMetaData();
            int colCount = metaData.getColumnCount();
            for (int i = 1; i <= colCount; i++) modelo.addColumn(metaData.getColumnName(i));
            modelo.addColumn("ACCIONES");
            while (rs.next()) {
                Vector<Object> fila = new Vector<>();
                for (int i = 1; i <= colCount; i++) fila.add(rs.getObject(i));
                fila.add("Eliminar ❌");
                modelo.addRow(fila);
            }
        }
        return modelo;
    }
    public void insertarRegistro(String tabla, Map<String, String> datos) throws SQLException {
        if (datos == null || datos.isEmpty()) return;

        StringBuilder columnasSql = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (String col : datos.keySet()) {
            columnasSql.append("\"").append(col).append("\",");
            placeholders.append("?,");
        }
        columnasSql.deleteCharAt(columnasSql.length() - 1);
        placeholders.deleteCharAt(placeholders.length() - 1);
        String sql = "INSERT INTO \"" + tabla + "\" (" + columnasSql + ") VALUES (" + placeholders + ")";
        try (Connection conn = getConexion();
             Statement checkStmt = conn.createStatement();
             ResultSet rs = checkStmt.executeQuery("SELECT * FROM \"" + tabla + "\" WHERE 1=0");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int i = 1;
            for (Map.Entry<String, String> entrada : datos.entrySet()) {
                String nombreCol = entrada.getKey();
                String valorTexto = entrada.getValue();
                int tipoSql = Types.VARCHAR;
                for (int j = 1; j <= metaData.getColumnCount(); j++) {
                    if (metaData.getColumnName(j).equalsIgnoreCase(nombreCol)) {
                        tipoSql = metaData.getColumnType(j);
                        break;
                    }
                }
                if (valorTexto == null || valorTexto.trim().isEmpty()) {
                    pstmt.setNull(i++, tipoSql);
                } else {
                    pstmt.setObject(i++, valorTexto.trim(), tipoSql);
                }
            }
            pstmt.executeUpdate();
        }
    }
    public void actualizarCelda(String tabla, String colId, Object idVal, String colCambiar, Object nuevoVal) throws SQLException {
        String sql = "UPDATE \"" + tabla + "\" SET \"" + colCambiar + "\" = ? WHERE \"" + colId + "\" = ?";
        try (Connection conn = getConexion();
             Statement checkStmt = conn.createStatement();
             ResultSet rs = checkStmt.executeQuery("SELECT \"" + colCambiar + "\" FROM \"" + tabla + "\" WHERE 1=0");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int tipoSql = metaData.getColumnType(1);
            if (nuevoVal == null || nuevoVal.toString().trim().isEmpty()) {
                pstmt.setNull(1, tipoSql);
            } else {
                pstmt.setObject(1, nuevoVal.toString().trim(), tipoSql);
            }
            pstmt.setObject(2, idVal);
            pstmt.executeUpdate();
            System.out.println("✅ Actualización exitosa en " + tabla + " (Col: " + colCambiar + ")");
        }
    }
    public void eliminarRegistro(String tabla, String pkNombre, Object pkValor) throws SQLException {
        String sql = "DELETE FROM \"" + tabla + "\" WHERE \"" + pkNombre + "\" = ?";
        try (Connection conn = getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, pkValor);
            pstmt.executeUpdate();
        }
    }
}