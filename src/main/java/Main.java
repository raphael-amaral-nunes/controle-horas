public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.createTables();
        MenuPrincipal menu = new MenuPrincipal();
        menu.executar();
    }
}
