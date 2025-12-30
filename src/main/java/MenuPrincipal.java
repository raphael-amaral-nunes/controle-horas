import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class MenuPrincipal {
    private final Scanner scanner;
    private final RegistroDAO registroDAO;
    private final DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter formatadorHora = DateTimeFormatter.ofPattern("HH:mm");

    public MenuPrincipal() {
        this.scanner = new Scanner(System.in);
        this.registroDAO = new RegistroDAO();
    }

    public void executar() {
        boolean continuar = true;

        while (continuar) {
            TerminalUtils.limpar();
            exibirCabecalho();
            exibirOpcoes();

            int opcao = lerOpcao();
            continuar = processarOpcao(opcao);
        }

        scanner.close();
        System.out.println("\nPrograma encerrado.\n");
    }

    private void exibirCabecalho() {
        System.out.println("=======================================");
        System.out.println("     Controle de Dias Trabalhados     ");
        System.out.println("=======================================\n");
    }

    private void exibirOpcoes() {
        System.out.println("1 - Registrar dia trabalhado");
        System.out.println("2 - Verificar histórico");
        System.out.println("0 - Sair\n");
        System.out.print("Escolha uma opção: ");
    }

    private int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean processarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                registrarDiaTrabalhado();
                break;
            case 2:
                verificarHistorico();
                break;
            case 0:
                System.out.println("\nEncerrando programa...");
                return false;
            default:
                System.out.println("\nOpção inválida!");
                aguardarEnter();
        }
        return true;
    }

    private void registrarDiaTrabalhado() {
        TerminalUtils.limpar();
        System.out.println("=== REGISTRAR DIA TRABALHADO ===\n");

        // Data
        LocalDate data = lerData();
        if (data == null) {
            System.out.println("\nData inválida!");
            aguardarEnter();
            return;
        }

        // Verificar se já existe registro para esta data
        if (registroDAO.buscarPorData(data) != null) {
            System.out.println("\nJá existe um registro para esta data!");
            aguardarEnter();
            return;
        }

        // Horários
        System.out.print("Hora de entrada (HH:mm): ");
        LocalTime horaEntrada = lerHora();
        if (horaEntrada == null) {
            System.out.println("\nHorário inválido!");
            aguardarEnter();
            return;
        }

        System.out.print("Hora de saída para intervalo (HH:mm): ");
        LocalTime horaSaidaIntervalo = lerHora();

        System.out.print("Hora de volta do intervalo (HH:mm): ");
        LocalTime horaVoltaIntervalo = lerHora();

        System.out.print("Hora de saída (HH:mm): ");
        LocalTime horaSaida = lerHora();

        // Criar e salvar registro
        Registro registro = new Registro(data, horaEntrada, horaSaidaIntervalo,
                horaVoltaIntervalo, horaSaida);

        if (registroDAO.inserir(registro)) {
            System.out.println("\n✓ Registro salvo com sucesso!");
            System.out.println("\nTotal de horas trabalhadas: " +
                    Registro.formatarDuracao(registro.calcularHorasTrabalhadas()));
            System.out.println("Tempo de intervalo: " +
                    Registro.formatarDuracao(registro.calcularTempoIntervalo()));
        } else {
            System.out.println("\n✗ Erro ao salvar registro!");
        }

        aguardarEnter();
    }

    private LocalDate lerData() {
        System.out.print("Data (DD/MM/AAAA) [Enter para hoje]: ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return LocalDate.now();
        }

        try {
            return LocalDate.parse(input, formatadorData);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private LocalTime lerHora() {
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return null;
        }

        try {
            return LocalTime.parse(input, formatadorHora);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void verificarHistorico() {
        YearMonth mesAtual = YearMonth.now();
        boolean voltarMenu = false;

        while (!voltarMenu) {
            TerminalUtils.limpar();
            System.out.println("=== HISTÓRICO ===");
            System.out.println("Mês: " + mesAtual.getMonth().getValue() + "/" + mesAtual.getYear() + "\n");

            List<Registro> registros = registroDAO.buscarPorMes(
                    mesAtual.getMonthValue(), mesAtual.getYear());

            if (registros.isEmpty()) {
                System.out.println("Nenhum registro encontrado para este mês.\n");
            } else {
                exibirListaRegistros(registros);
            }

            System.out.println("\n1 - Ver detalhes de um dia");
            System.out.println("2 - Mudar mês/ano");
            System.out.println("0 - Voltar ao menu principal\n");
            System.out.print("Escolha uma opção: ");

            int opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    if (!registros.isEmpty()) {
                        verDetalhesRegistro(registros);
                    }
                    break;
                case 2:
                    mesAtual = selecionarMesAno();
                    break;
                case 0:
                    voltarMenu = true;
                    break;
            }
        }
    }

    private void exibirListaRegistros(List<Registro> registros) {
        System.out.printf("%-12s | %-20s%n", "Data", "Horas Trabalhadas");
        System.out.println("-----------------------------------");

        for (Registro registro : registros) {
            String dataFormatada = registro.getData().format(formatadorData);
            String horasTrabalhadas = Registro.formatarDuracao(
                    registro.calcularHorasTrabalhadas());

            System.out.printf("%-12s | %-20s%n", dataFormatada, horasTrabalhadas);
        }
    }

    private void verDetalhesRegistro(List<Registro> registros) {
        System.out.print("\nDigite a data do registro (DD/MM/AAAA): ");
        LocalDate data = lerData();

        if (data == null) {
            System.out.println("\nData inválida!");
            aguardarEnter();
            return;
        }

        Registro registro = registroDAO.buscarPorData(data);

        if (registro == null) {
            System.out.println("\nNenhum registro encontrado para esta data!");
            aguardarEnter();
            return;
        }

        TerminalUtils.limpar();
        System.out.println("=== DETALHES DO REGISTRO ===");
        System.out.println("Data: " + registro.getData().format(formatadorData));
        System.out.println("\nHorários:");
        System.out.println("  Entrada: " + registro.getHoraEntrada().format(formatadorHora));

        if (registro.getHoraSaidaIntervalo() != null) {
            System.out.println("  Saída intervalo: " +
                    registro.getHoraSaidaIntervalo().format(formatadorHora));
        }

        if (registro.getHoraVoltaIntervalo() != null) {
            System.out.println("  Volta intervalo: " +
                    registro.getHoraVoltaIntervalo().format(formatadorHora));
        }

        if (registro.getHoraSaida() != null) {
            System.out.println("  Saída: " + registro.getHoraSaida().format(formatadorHora));
        }

        System.out.println("\nResumo:");
        System.out.println("  Horas trabalhadas: " +
                Registro.formatarDuracao(registro.calcularHorasTrabalhadas()));
        System.out.println("  Tempo de intervalo: " +
                Registro.formatarDuracao(registro.calcularTempoIntervalo()));

        aguardarEnter();
    }

    private YearMonth selecionarMesAno() {
        System.out.print("\nMês (1-12): ");
        int mes = lerOpcao();

        System.out.print("Ano: ");
        int ano = lerOpcao();

        try {
            return YearMonth.of(ano, mes);
        } catch (Exception e) {
            System.out.println("\nMês/Ano inválido! Usando mês atual.");
            aguardarEnter();
            return YearMonth.now();
        }
    }

    private void aguardarEnter() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}
