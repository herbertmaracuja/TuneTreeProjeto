import java.util.ArrayList;
import java.util.List;

// Cópia apenas para desenho; não participa das operações das árvores.
public class NoVisual {
    final String texto;
    final boolean preto;
    final boolean binario;
    final List<NoVisual> filhos = new ArrayList<>();
    final int[] chaves;
    NoVisual(String texto, boolean preto, boolean binario, int... chaves) {
        this.texto = texto; this.preto = preto; this.binario = binario; this.chaves = chaves;
    }
    public int altura() {
        int h = -1;
        for (NoVisual f : filhos) if (f != null) h = Math.max(h, f.altura());
        return h + 1;
    }
}
