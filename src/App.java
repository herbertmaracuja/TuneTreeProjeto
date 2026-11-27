import java.util.List;
import java.util.Scanner;

public class App {
    private static final int[] INICIAIS = {40,20,60,10,30,50,70,5,15,25,35,45,55,65,75,3,7,12,17,22,27,32,37,42,47,52,57,62,67,72,77};
    private final RubroNegra rb = new RubroNegra();
    private final Arvore234 a234 = new Arvore234();
    private void inserir(int codigo) {
        boolean a = rb.inserir(codigo), b = a234.inserir(codigo);
        if (a != b) throw new IllegalStateException("Indices divergentes");
        System.out.println(a ? "Musica " + codigo + " cadastrada." : "Codigo repetido: cadastro recusado.");
        eventos();
    }
    private void excluir(int codigo) {
        boolean a = rb.excluir(codigo), b = a234.excluir(codigo);
        if (a != b) throw new IllegalStateException("Indices divergentes");
        System.out.println(a ? "Musica " + codigo + " excluida." : "Codigo inexistente.");
        eventos();
    }
    private void eventos() {
        System.out.println("Rubro-Negra: " + descrever(rb.eventos()));
        System.out.println("2-3-4: " + descrever(a234.eventos()));
        conferir();
    }
    private String descrever(List<String> lista) {
        return lista.isEmpty() ? "sem ajuste estrutural/de cor" : String.join("; ", lista);
    }
    private void conferir() {
        if (!rb.valida() || !a234.valida() || !rb.emOrdem().equals(a234.emOrdem()))
            throw new IllegalStateException("Falha de integridade");
        System.out.println("Integridade OK | " + rb.tamanho() + " musicas em cada indice.");
    }
    private void mostrar() {
        System.out.println("\n--- RUBRO-NEGRA (V = vermelho, P = preto) ---"); rb.mostrar();
        System.out.println("\n--- ARVORE 2-3-4 ---"); a234.mostrar();
        System.out.println("\nCodigos ordenados: " + rb.emOrdem()); conferir();
    }
    private void inicializar() {
        for (int codigo : INICIAIS) { rb.inserir(codigo); a234.inserir(codigo); }
    }
    private void buscar(int codigo) {
        System.out.println("Rubro-Negra: " + (rb.buscar(codigo) ? "encontrada" : "nao encontrada"));
        System.out.println("2-3-4: " + (a234.buscar(codigo) ? "encontrada" : "nao encontrada"));
    }
    public static void main(String[] args) {
        App app = new App(); app.inicializar();
        System.out.println("=== TUNETREE - INDICE DE MUSICAS ==="); app.conferir();
        if (args.length > 0 && args[0].equals("demo")) {
            app.mostrar(); app.buscar(35); app.buscar(999);
            app.inserir(80); app.inserir(85); app.inserir(90); app.inserir(35);
            app.excluir(3); app.excluir(5); app.excluir(7); app.excluir(10);
            app.mostrar(); return;
        }
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n1 - Cadastrar | 2 - Excluir | 3 - Buscar | 4 - Mostrar arvores | 0 - Sair");
                if (!sc.hasNextLine()) break;
                String opcao = sc.nextLine().trim();
                if (opcao.equals("0")) break;
                if (opcao.equals("4")) { app.mostrar(); continue; }
                if (!opcao.equals("1") && !opcao.equals("2") && !opcao.equals("3")) {
                    System.out.println("Opcao invalida."); continue;
                }
                System.out.print("Codigo inteiro positivo: ");
                if (!sc.hasNextLine()) break;
                try {
                    int codigo = Integer.parseInt(sc.nextLine().trim());
                    if (codigo <= 0) { System.out.println("Codigo deve ser positivo."); continue; }
                    switch (opcao) {
                        case "1": app.inserir(codigo); break;
                        case "2": app.excluir(codigo); break;
                        case "3": app.buscar(codigo); break;
                    }
                } catch (NumberFormatException e) { System.out.println("Codigo invalido."); }
            }
        }
    }
}
