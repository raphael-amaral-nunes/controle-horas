import java.sql.*;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:controle-horas.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
