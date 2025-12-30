import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS registro (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "data TEXT NOT NULL, " +
                "hora_entrada TEXT, " +
                "hora_saida_intervalo TEXT, " +
                "hora_volta_intervalo TEXT, " +
                "hora_saida TEXT);";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela: " + e.getMessage());
        }
    }
}
