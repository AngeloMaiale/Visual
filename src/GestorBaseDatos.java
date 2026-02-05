import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class GestorBaseDatos {
    private static final String URL = "jdbc:postgresql://localhost:5432/Prueba";
    private static final String USER = "postgres";
    private static final String PASS = "123456";

    public static void main(String[] args) {
        String consulta = """
                          SELECT * FROM usuarios LIMIT 10
                          """;

        System.out.println("--- Iniciando consulta ---");
        ejecutarQuery(consulta);
    }

    public static void ejecutarQuery(String sql) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            ResultSetMetaData metaData = rs.getMetaData();
            int numeroColumnas = metaData.getColumnCount();
            System.out.println("\n--------------------------------------------------");
            for (int i = 1; i <= numeroColumnas; i++) {
                System.out.printf("%-20s", metaData.getColumnName(i));
            }
            System.out.println("\n--------------------------------------------------");
            while (rs.next()) {
                for (int i = 1; i <= numeroColumnas; i++) {
                    String valor = String.valueOf(rs.getObject(i));
                    if (valor.length() > 18) valor = valor.substring(0, 15) + "...";
                    System.out.printf("%-20s", valor);
                }
                System.out.println();
            }
            System.out.println("--------------------------------------------------\n");

        } catch (SQLException e) {
            System.err.println("❌ Error de conexión o consulta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}