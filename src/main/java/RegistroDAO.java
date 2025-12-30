import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RegistroDAO {

    public boolean inserir(Registro registro) {
        String sql = "INSERT INTO registro (data, hora_entrada, hora_saida_intervalo, " +
                "hora_volta_intervalo, hora_saida) VALUES (?,?,?,?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, registro.getData().toString());
            stmt.setString(2, registro.getHoraEntrada().toString());
            stmt.setString(3, registro.getHoraSaidaIntervalo() != null ?
                    registro.getHoraSaidaIntervalo().toString() : null);
            stmt.setString(4, registro.getHoraVoltaIntervalo() != null ?
                    registro.getHoraVoltaIntervalo().toString() : null);
            stmt.setString(5, registro.getHoraSaida() != null ?
                    registro.getHoraSaida().toString() : null);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir registro: " + e.getMessage());
            return false;
        }
    }

    public List<Registro> buscarPorMes(int mes, int ano) {
        List<Registro> registros = new ArrayList<>();
        String sql = "SELECT * FROM registro WHERE strftime('%m', data) = ? " +
                "AND strftime('%Y', data) = ? ORDER BY data";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, String.format("%02d", mes));
            stmt.setString(2, String.valueOf(ano));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                registros.add(criarRegistroDoResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar registros: " + e.getMessage());
        }

        return registros;
    }

    public Registro buscarPorData(LocalDate data) {
        String sql = "SELECT * FROM registro WHERE data = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, data.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return criarRegistroDoResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar registro: " + e.getMessage());
        }

        return null;
    }

    private Registro criarRegistroDoResultSet(ResultSet rs) throws SQLException {
        return new Registro(
                rs.getInt("id"),
                LocalDate.parse(rs.getString("data")),
                LocalTime.parse(rs.getString("hora_entrada")),
                rs.getString("hora_saida_intervalo") != null ?
                        LocalTime.parse(rs.getString("hora_saida_intervalo")) : null,
                rs.getString("hora_volta_intervalo") != null ?
                        LocalTime.parse(rs.getString("hora_volta_intervalo")) : null,
                rs.getString("hora_saida") != null ?
                        LocalTime.parse(rs.getString("hora_saida")) : null
        );
    }
}
