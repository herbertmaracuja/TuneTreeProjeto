import java.util.ArrayList;
import java.util.List;

public class RubroNegra {
    private static final boolean VERMELHO = true, PRETO = false;
    private static class No {
        int codigo;
        boolean cor;
        No esq, dir, pai;
        No(int codigo, boolean cor) { this.codigo = codigo; this.cor = cor; }
    }
    // NIL representa todas as folhas vazias, sempre pretas.
    private final No nil = new No(0, PRETO);
    private No raiz = nil;
    private int tamanho;
    private final List<String> eventos = new ArrayList<>();

    public RubroNegra() { nil.esq = nil.dir = nil.pai = nil; }
    public int tamanho() { return tamanho; }
    public List<String> eventos() { return new ArrayList<>(eventos); }
    private No localizar(int codigo) {
        No atual = raiz;
        while (atual != nil && atual.codigo != codigo)
            atual = codigo < atual.codigo ? atual.esq : atual.dir;
        return atual;
    }
    public boolean buscar(int codigo) { return localizar(codigo) != nil; }
    public NoVisual visual() { return visual(raiz); }
    private NoVisual visual(No n) {
        if (n == nil) return null;
        NoVisual v = new NoVisual(n.codigo + (n.cor ? " V" : " P"), !n.cor, true, n.codigo);
        if (n.esq != nil || n.dir != nil) { v.filhos.add(visual(n.esq)); v.filhos.add(visual(n.dir)); }
        return v;
    }
    public List<NoVisual> caminhoBusca(int codigo) {
        List<NoVisual> caminho = new ArrayList<>();
        No n = raiz;
        while (n != nil) {
            caminho.add(new NoVisual("", !n.cor, true, n.codigo));
            if (codigo == n.codigo) break;
            n = codigo < n.codigo ? n.esq : n.dir;
        }
        return caminho;
    }
    public boolean inserir(int codigo) {
        eventos.clear();
        No pai = nil, atual = raiz;
        while (atual != nil) {
            if (codigo == atual.codigo) return false;
            pai = atual;
            atual = codigo < atual.codigo ? atual.esq : atual.dir;
        }
        No novo = new No(codigo, VERMELHO);
        novo.esq = novo.dir = nil;
        novo.pai = pai;
        if (pai == nil) raiz = novo;
        else if (codigo < pai.codigo) pai.esq = novo;
        else pai.dir = novo;
        tamanho++;
        corrigirInsercao(novo);
        return true;
    }
    private void girarEsquerda(No x) {
        eventos.add("Rotacao a esquerda em " + x.codigo);
        No y = x.dir;
        x.dir = y.esq;
        if (y.esq != nil) y.esq.pai = x;
        y.pai = x.pai;
        if (x.pai == nil) raiz = y;
        else if (x == x.pai.esq) x.pai.esq = y;
        else x.pai.dir = y;
        y.esq = x;
        x.pai = y;
    }
    private void girarDireita(No x) {
        eventos.add("Rotacao a direita em " + x.codigo);
        No y = x.esq;
        x.esq = y.dir;
        if (y.dir != nil) y.dir.pai = x;
        y.pai = x.pai;
        if (x.pai == nil) raiz = y;
        else if (x == x.pai.dir) x.pai.dir = y;
        else x.pai.esq = y;
        y.dir = x;
        x.pai = y;
    }
    private void pintar(No n, boolean cor) {
        if (n.cor != cor) eventos.add("Recolorir " + n.codigo + " -> " + (cor ? "V" : "P"));
        n.cor = cor;
    }
    private void corrigirInsercao(No z) {
        while (z.pai.cor == VERMELHO) {
            No avo = z.pai.pai;
            if (z.pai == avo.esq) {
                No tio = avo.dir;
                if (tio.cor == VERMELHO) {
                    pintar(z.pai, PRETO); pintar(tio, PRETO); pintar(avo, VERMELHO);
                    z = avo;
                } else {
                    if (z == z.pai.dir) { z = z.pai; girarEsquerda(z); }
                    pintar(z.pai, PRETO); pintar(z.pai.pai, VERMELHO);
                    girarDireita(z.pai.pai);
                }
            } else {
                No tio = avo.esq;
                if (tio.cor == VERMELHO) {
                    pintar(z.pai, PRETO); pintar(tio, PRETO); pintar(avo, VERMELHO);
                    z = avo;
                } else {
                    if (z == z.pai.esq) { z = z.pai; girarDireita(z); }
                    pintar(z.pai, PRETO); pintar(z.pai.pai, VERMELHO);
                    girarEsquerda(z.pai.pai);
                }
            }
        }
        pintar(raiz, PRETO);
    }
    private void substituir(No u, No v) {
        if (u.pai == nil) raiz = v;
        else if (u == u.pai.esq) u.pai.esq = v;
        else u.pai.dir = v;
        v.pai = u.pai;
    }
    private No minimo(No n) {
        while (n.esq != nil) n = n.esq;
        return n;
    }
    public boolean excluir(int codigo) {
        eventos.clear();
        No z = localizar(codigo);
        if (z == nil) return false;
        No y = z, x;
        boolean corOriginal = y.cor;
        if (z.esq == nil) {
            x = z.dir; substituir(z, z.dir);
        } else if (z.dir == nil) {
            x = z.esq; substituir(z, z.esq);
        } else {
            y = minimo(z.dir);
            eventos.add("Sucessor de " + z.codigo + ": " + y.codigo);
            corOriginal = y.cor;
            x = y.dir;
            if (y.pai == z) x.pai = y;
            else {
                substituir(y, y.dir);
                y.dir = z.dir; y.dir.pai = y;
            }
            substituir(z, y);
            y.esq = z.esq; y.esq.pai = y;
            pintar(y, z.cor);
        }
        tamanho--;
        if (corOriginal == PRETO) corrigirExclusao(x);
        nil.pai = nil;
        return true;
    }
    private void corrigirExclusao(No x) {
        while (x != raiz && x.cor == PRETO) {
            if (x == x.pai.esq) {
                No irmao = x.pai.dir;
                if (irmao.cor == VERMELHO) {
                    pintar(irmao, PRETO); pintar(x.pai, VERMELHO);
                    girarEsquerda(x.pai); irmao = x.pai.dir;
                }
                if (irmao.esq.cor == PRETO && irmao.dir.cor == PRETO) {
                    pintar(irmao, VERMELHO); x = x.pai;
                } else {
                    if (irmao.dir.cor == PRETO) {
                        pintar(irmao.esq, PRETO); pintar(irmao, VERMELHO);
                        girarDireita(irmao); irmao = x.pai.dir;
                    }
                    pintar(irmao, x.pai.cor); pintar(x.pai, PRETO); pintar(irmao.dir, PRETO);
                    girarEsquerda(x.pai); x = raiz;
                }
            } else {
                No irmao = x.pai.esq;
                if (irmao.cor == VERMELHO) {
                    pintar(irmao, PRETO); pintar(x.pai, VERMELHO);
                    girarDireita(x.pai); irmao = x.pai.esq;
                }
                if (irmao.dir.cor == PRETO && irmao.esq.cor == PRETO) {
                    pintar(irmao, VERMELHO); x = x.pai;
                } else {
                    if (irmao.esq.cor == PRETO) {
                        pintar(irmao.dir, PRETO); pintar(irmao, VERMELHO);
                        girarEsquerda(irmao); irmao = x.pai.esq;
                    }
                    pintar(irmao, x.pai.cor); pintar(x.pai, PRETO); pintar(irmao.esq, PRETO);
                    girarDireita(x.pai); x = raiz;
                }
            }
        }
        pintar(x, PRETO);
    }
    public List<Integer> emOrdem() {
        List<Integer> lista = new ArrayList<>(); emOrdem(raiz, lista); return lista;
    }
    private void emOrdem(No n, List<Integer> lista) {
        if (n == nil) return;
        emOrdem(n.esq, lista); lista.add(n.codigo); emOrdem(n.dir, lista);
    }
    public void mostrar() {
        if (raiz == nil) System.out.println("(vazia)");
        else mostrar(raiz, "", "Raiz: ");
    }
    private void mostrar(No n, String recuo, String lado) {
        if (n == nil) return;
        System.out.println(recuo + lado + n.codigo + "(" + (n.cor ? "V" : "P") + ")");
        mostrar(n.esq, recuo + "    ", "E: "); mostrar(n.dir, recuo + "    ", "D: ");
    }
    public boolean valida() {
        return nil.cor == PRETO && raiz.cor == PRETO && raiz.pai == nil &&
            verificar(raiz, Long.MIN_VALUE, Long.MAX_VALUE) > 0 && emOrdem().size() == tamanho;
    }
    private int verificar(No n, long min, long max) {
        if (n == nil) return 1;
        if (n.codigo <= min || n.codigo >= max) return -1;
        if ((n.esq != nil && n.esq.pai != n) || (n.dir != nil && n.dir.pai != n)) return -1;
        if (n.cor && (n.esq.cor || n.dir.cor)) return -1;
        int e = verificar(n.esq, min, n.codigo), d = verificar(n.dir, n.codigo, max);
        return e < 0 || e != d ? -1 : e + (n.cor == PRETO ? 1 : 0);
    }
}
