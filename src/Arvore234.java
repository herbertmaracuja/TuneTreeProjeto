import java.util.ArrayList;
import java.util.List;

public class Arvore234 {
    // Cada no guarda 1 a 3 chaves e, se interno, 2 a 4 filhos.
    private static class No {
        int n;
        int[] chaves = new int[3];
        No[] filhos = new No[4];
        boolean folha;
        No(boolean folha) { this.folha = folha; }
    }
    private No raiz = new No(true);
    private int tamanho;
    private final List<String> eventos = new ArrayList<>();
    public int tamanho() { return tamanho; }
    public List<String> eventos() { return new ArrayList<>(eventos); }
    private int posicao(No no, int codigo) {
        int i = 0;
        while (i < no.n && codigo > no.chaves[i]) i++;
        return i;
    }
    public boolean buscar(int codigo) {
        No atual = raiz;
        while (true) {
            int i = posicao(atual, codigo);
            if (i < atual.n && atual.chaves[i] == codigo) return true;
            if (atual.folha) return false;
            atual = atual.filhos[i];
        }
    }
    public NoVisual visual() { return raiz.n == 0 ? null : visual(raiz); }
    private NoVisual visual(No n) {
        NoVisual v = new NoVisual(rotulo(n), false, false, java.util.Arrays.copyOf(n.chaves, n.n));
        if (!n.folha) for (int i = 0; i <= n.n; i++) v.filhos.add(visual(n.filhos[i]));
        return v;
    }
    public List<NoVisual> caminhoBusca(int codigo) {
        List<NoVisual> caminho = new ArrayList<>();
        if (raiz.n == 0) return caminho;
        No n = raiz;
        while (true) {
            caminho.add(new NoVisual("", false, false, java.util.Arrays.copyOf(n.chaves, n.n)));
            int i = posicao(n, codigo);
            if ((i < n.n && n.chaves[i] == codigo) || n.folha) return caminho;
            n = n.filhos[i];
        }
    }
    public boolean inserir(int codigo) {
        eventos.clear();
        if (buscar(codigo)) return false;
        if (raiz.n == 3) {
            No nova = new No(false); nova.filhos[0] = raiz;
            dividir(nova, 0); raiz = nova;
        }
        inserirNaoCheio(raiz, codigo); tamanho++;
        return true;
    }
    private void dividir(No pai, int i) {
        No cheio = pai.filhos[i], direita = new No(cheio.folha);
        int meio = cheio.chaves[1];
        eventos.add("Divisao " + rotulo(cheio) + ": promove " + meio);
        direita.chaves[0] = cheio.chaves[2]; direita.n = 1;
        if (!cheio.folha) {
            direita.filhos[0] = cheio.filhos[2]; direita.filhos[1] = cheio.filhos[3];
            cheio.filhos[2] = cheio.filhos[3] = null;
        }
        cheio.n = 1;
        for (int j = pai.n; j > i; j--) pai.filhos[j+1] = pai.filhos[j];
        pai.filhos[i+1] = direita;
        for (int j = pai.n-1; j >= i; j--) pai.chaves[j+1] = pai.chaves[j];
        pai.chaves[i] = meio; pai.n++;
    }
    private void inserirNaoCheio(No no, int codigo) {
        int i = posicao(no, codigo);
        if (no.folha) {
            for (int j = no.n-1; j >= i; j--) no.chaves[j+1] = no.chaves[j];
            no.chaves[i] = codigo; no.n++;
        } else {
            if (no.filhos[i].n == 3) {
                dividir(no, i);
                if (codigo > no.chaves[i]) i++;
            }
            inserirNaoCheio(no.filhos[i], codigo);
        }
    }
    public boolean excluir(int codigo) {
        eventos.clear();
        if (!buscar(codigo)) return false;
        remover(raiz, codigo); tamanho--;
        if (raiz.n == 0 && !raiz.folha) {
            raiz = raiz.filhos[0]; eventos.add("Raiz reduzida: altura diminuiu");
        }
        return true;
    }
    private void remover(No no, int codigo) {
        int i = posicao(no, codigo);
        if (i < no.n && no.chaves[i] == codigo) {
            if (no.folha) {
                for (int j = i; j < no.n-1; j++) no.chaves[j] = no.chaves[j+1];
                no.n--;
            } else if (no.filhos[i].n >= 2) {
                int anterior = maior(no.filhos[i]);
                eventos.add("Substitui " + codigo + " pelo predecessor " + anterior);
                no.chaves[i] = anterior; remover(no.filhos[i], anterior);
            } else if (no.filhos[i+1].n >= 2) {
                int seguinte = menor(no.filhos[i+1]);
                eventos.add("Substitui " + codigo + " pelo sucessor " + seguinte);
                no.chaves[i] = seguinte; remover(no.filhos[i+1], seguinte);
            } else {
                unir(no, i); remover(no.filhos[i], codigo);
            }
        } else if (!no.folha) {
            // Antes de descer, garante que o filho tenha pelo menos duas chaves.
            if (no.filhos[i].n == 1) {
                if (i > 0 && no.filhos[i-1].n >= 2) emprestarEsquerda(no, i);
                else if (i < no.n && no.filhos[i+1].n >= 2) emprestarDireita(no, i);
                else if (i < no.n) unir(no, i);
                else { unir(no, i-1); i--; }
            }
            remover(no.filhos[i], codigo);
        }
    }
    private int maior(No no) {
        while (!no.folha) no = no.filhos[no.n];
        return no.chaves[no.n-1];
    }
    private int menor(No no) {
        while (!no.folha) no = no.filhos[0];
        return no.chaves[0];
    }
    private void emprestarEsquerda(No pai, int i) {
        No filho = pai.filhos[i], irmao = pai.filhos[i-1];
        eventos.add("Emprestimo do irmao esquerdo via pai " + pai.chaves[i-1]);
        for (int j = filho.n-1; j >= 0; j--) filho.chaves[j+1] = filho.chaves[j];
        if (!filho.folha) {
            for (int j = filho.n; j >= 0; j--) filho.filhos[j+1] = filho.filhos[j];
            filho.filhos[0] = irmao.filhos[irmao.n]; irmao.filhos[irmao.n] = null;
        }
        filho.chaves[0] = pai.chaves[i-1];
        pai.chaves[i-1] = irmao.chaves[irmao.n-1];
        filho.n++; irmao.n--;
    }
    private void emprestarDireita(No pai, int i) {
        No filho = pai.filhos[i], irmao = pai.filhos[i+1];
        eventos.add("Emprestimo do irmao direito via pai " + pai.chaves[i]);
        filho.chaves[filho.n] = pai.chaves[i];
        if (!filho.folha) filho.filhos[filho.n+1] = irmao.filhos[0];
        pai.chaves[i] = irmao.chaves[0];
        for (int j = 0; j < irmao.n-1; j++) irmao.chaves[j] = irmao.chaves[j+1];
        if (!irmao.folha) {
            for (int j = 0; j < irmao.n; j++) irmao.filhos[j] = irmao.filhos[j+1];
            irmao.filhos[irmao.n] = null;
        }
        filho.n++; irmao.n--;
    }
    private void unir(No pai, int i) {
        No e = pai.filhos[i], d = pai.filhos[i+1];
        eventos.add("Fusao " + rotulo(e) + " + " + pai.chaves[i] + " + " + rotulo(d));
        int antiga = e.n;
        e.chaves[antiga] = pai.chaves[i];
        for (int j = 0; j < d.n; j++) e.chaves[antiga+1+j] = d.chaves[j];
        if (!e.folha) for (int j = 0; j <= d.n; j++) e.filhos[antiga+1+j] = d.filhos[j];
        e.n += d.n + 1;
        for (int j = i; j < pai.n-1; j++) pai.chaves[j] = pai.chaves[j+1];
        for (int j = i+1; j < pai.n; j++) pai.filhos[j] = pai.filhos[j+1];
        pai.filhos[pai.n] = null; pai.n--;
    }
    private String rotulo(No no) {
        String s = "[";
        for (int i = 0; i < no.n; i++) s += (i == 0 ? "" : " | ") + no.chaves[i];
        return s + "]";
    }
    public void mostrar() { mostrar(raiz, "", "Raiz: "); }
    private void mostrar(No no, String recuo, String lado) {
        System.out.println(recuo + lado + rotulo(no));
        if (!no.folha) for (int i = 0; i <= no.n; i++) mostrar(no.filhos[i], recuo + "    ", "F"+i+": ");
    }
    public List<Integer> emOrdem() {
        List<Integer> lista = new ArrayList<>(); emOrdem(raiz, lista); return lista;
    }
    private void emOrdem(No no, List<Integer> lista) {
        for (int i = 0; i < no.n; i++) {
            if (!no.folha) emOrdem(no.filhos[i], lista);
            lista.add(no.chaves[i]);
        }
        if (!no.folha) emOrdem(no.filhos[no.n], lista);
    }
    public boolean valida() {
        return verificar(raiz, Long.MIN_VALUE, Long.MAX_VALUE, true) >= 0 && emOrdem().size() == tamanho;
    }
    private int verificar(No no, long min, long max, boolean eRaiz) {
        if (no == null || no.n > 3 || no.n < 0 || (no.n == 0 && (!eRaiz || !no.folha))) return -1;
        for (int i = 0; i < no.n; i++) {
            if (no.chaves[i] <= min || no.chaves[i] >= max || (i > 0 && no.chaves[i] <= no.chaves[i-1])) return -1;
        }
        if (no.folha) return 0;
        int altura = -1;
        for (int i = 0; i <= no.n; i++) {
            int h = verificar(no.filhos[i], i == 0 ? min : no.chaves[i-1], i == no.n ? max : no.chaves[i], false);
            if (h < 0 || (altura >= 0 && h != altura)) return -1;
            altura = h;
        }
        return altura + 1;
    }
}
