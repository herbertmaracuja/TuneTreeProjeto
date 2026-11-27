import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TesteArvores {
    public static void main(String[] args) {
        int operacoes = 0;
        for (int s = 0; s < 30; s++) {
            RubroNegra rb = new RubroNegra(); Arvore234 a = new Arvore234();
            Set<Integer> esperado = new HashSet<>(); Random r = new Random(s);
            for (int i = 0; i < 1500; i++) {
                int x = r.nextInt(201) - 100;
                int op = r.nextInt(3);
                boolean e, b, c;
                if (op == 0) { e = esperado.add(x); b = rb.inserir(x); c = a.inserir(x); }
                else if (op == 1) { e = esperado.remove(x); b = rb.excluir(x); c = a.excluir(x); }
                else { e = esperado.contains(x); b = rb.buscar(x); c = a.buscar(x); }
                if (e != b || e != c) throw new AssertionError("Resultado divergente: " + s + "/" + i);
                verificar(rb, a, esperado); operacoes++;
            }
            List<Integer> resto = new ArrayList<>(esperado);
            Collections.shuffle(resto, r);
            for (int x : resto) {
                esperado.remove(x);
                if (!rb.excluir(x) || !a.excluir(x)) throw new AssertionError("Falha ao esvaziar");
                verificar(rb, a, esperado); operacoes++;
            }
        }
        System.out.println("OK: " + operacoes + " operacoes verificadas, incluindo esvaziamento completo.");
    }
    private static void verificar(RubroNegra rb, Arvore234 a, Set<Integer> esperado) {
        List<Integer> lista = new ArrayList<>(esperado); Collections.sort(lista);
        if (!rb.valida() || !a.valida() || !rb.emOrdem().equals(lista) || !a.emOrdem().equals(lista))
            throw new AssertionError("Invariantes ou conteudo incorretos");
    }
}
