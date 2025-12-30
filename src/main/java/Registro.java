import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

public class Registro {
    private int id;
    private LocalDate data;
    private LocalTime horaEntrada;
    private LocalTime horaSaidaIntervalo;
    private LocalTime horaVoltaIntervalo;
    private LocalTime horaSaida;

    // Construtor completo
    public Registro(int id, LocalDate data, LocalTime horaEntrada,
                    LocalTime horaSaidaIntervalo, LocalTime horaVoltaIntervalo,
                    LocalTime horaSaida) {
        this.id = id;
        this.data = data;
        this.horaEntrada = horaEntrada;
        this.horaSaidaIntervalo = horaSaidaIntervalo;
        this.horaVoltaIntervalo = horaVoltaIntervalo;
        this.horaSaida = horaSaida;
    }

    // Construtor sem ID (para inserção)
    public Registro(LocalDate data, LocalTime horaEntrada,
                    LocalTime horaSaidaIntervalo, LocalTime horaVoltaIntervalo,
                    LocalTime horaSaida) {
        this(0, data, horaEntrada, horaSaidaIntervalo, horaVoltaIntervalo, horaSaida);
    }

    // Calcula horas trabalhadas (descontando intervalo)
    public Duration calcularHorasTrabalhadas() {
        if (horaEntrada == null || horaSaida == null) return Duration.ZERO;

        Duration total = Duration.between(horaEntrada, horaSaida);
        Duration intervalo = calcularTempoIntervalo();

        return total.minus(intervalo);
    }

    // Calcula tempo de intervalo
    public Duration calcularTempoIntervalo() {
        if (horaSaidaIntervalo == null || horaVoltaIntervalo == null)
            return Duration.ZERO;

        return Duration.between(horaSaidaIntervalo, horaVoltaIntervalo);
    }

    // Formata duração para exibição (ex: "7h 45min")
    public static String formatarDuracao(Duration duration) {
        long horas = duration.toHours();
        long minutos = duration.toMinutesPart();
        return String.format("%dh %02dmin", horas, minutos);
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalTime horaEntrada) { this.horaEntrada = horaEntrada; }

    public LocalTime getHoraSaidaIntervalo() { return horaSaidaIntervalo; }
    public void setHoraSaidaIntervalo(LocalTime horaSaidaIntervalo) {
        this.horaSaidaIntervalo = horaSaidaIntervalo;
    }

    public LocalTime getHoraVoltaIntervalo() { return horaVoltaIntervalo; }
    public void setHoraVoltaIntervalo(LocalTime horaVoltaIntervalo) {
        this.horaVoltaIntervalo = horaVoltaIntervalo;
    }

    public LocalTime getHoraSaida() { return horaSaida; }
    public void setHoraSaida(LocalTime horaSaida) { this.horaSaida = horaSaida; }
}
