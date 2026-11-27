import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

// Desenho e animações de cópias visuais. Não altera os algoritmos de busca.
public class PainelArvore extends JPanel implements Scrollable {
    private NoVisual raiz;
    private List<NoVisual> caminho = Collections.emptyList();
    private final Map<NoVisual, Rectangle> pos = new IdentityHashMap<>();
    private final Map<String, Rectangle> anteriores = new HashMap<>();
    private final Map<String, NoVisual> nosAnteriores = new HashMap<>();
    private int largura=400, altura=220;
    private static final int GAP=12, PASSO=62, CELULA=53;
    private long inicio;
    private float fase=1;
    private final javax.swing.Timer timer;
    public PainelArvore(){setOpaque(false);timer=new javax.swing.Timer(16,e->{fase=Math.min(1,(System.nanoTime()-inicio)/duracao());repaint();if(fase>=1)((javax.swing.Timer)e.getSource()).stop();});}
    private float duracao(){return Math.max(420,caminho.size()*155+160)*1_000_000f;}
    private void animar(){fase=0;inicio=System.nanoTime();timer.restart();repaint();}
    public void repetirBusca(){if(!caminho.isEmpty()){anteriores.clear();nosAnteriores.clear();animar();}}
    private String id(NoVisual n){return Arrays.toString(n.chaves);}
    public void atualizar(NoVisual nova,List<NoVisual> caminho){
        anteriores.clear();nosAnteriores.clear();
        if(raiz!=null && getWidth()>0){pos.clear();localizar(raiz,Math.max(20,(getWidth()-medir(raiz))/2),22);for(Map.Entry<NoVisual,Rectangle> e:pos.entrySet()){anteriores.put(id(e.getKey()),e.getValue());nosAnteriores.put(id(e.getKey()),e.getKey());}}
        this.raiz=nova;this.caminho=caminho;
        largura=raiz==null?400:medir(raiz)+40;
        altura=raiz==null?220:raiz.altura()*PASSO+68;
        setPreferredSize(new Dimension(largura,altura));revalidate();animar();
    }
    private int tamanho(NoVisual n){return n.binario?Math.max(40,n.texto.length()*7+7):n.chaves.length*CELULA;}
    private int medir(NoVisual n){if(n==null)return 18;int s=0;for(NoVisual f:n.filhos)s+=medir(f)+GAP;return Math.max(tamanho(n),Math.max(0,s-GAP));}
    private void localizar(NoVisual n,int x,int y){if(n==null)return;int w=medir(n),nw=tamanho(n);pos.put(n,new Rectangle(x+(w-nw)/2,y,nw,30));int total=0;for(NoVisual f:n.filhos)total+=medir(f)+GAP;int fx=x+(w-Math.max(0,total-GAP))/2;for(NoVisual f:n.filhos){localizar(f,fx,y+PASSO);fx+=medir(f)+GAP;}}
    private float movimento(){float t=Math.min(1,fase*duracao()/420_000_000f);return 1-(1-t)*(1-t)*(1-t);}
    private Rectangle interpolar(NoVisual n){Rectangle r=pos.get(n),a=anteriores.get(id(n));if(a==null||fase>=1)return r;float t=movimento();return new Rectangle(Math.round(a.x+(r.x-a.x)*t),Math.round(a.y+(r.y-a.y)*t),r.width,r.height);}
    private int indice(NoVisual n){for(int i=0;i<caminho.size();i++)if(Arrays.equals(caminho.get(i).chaves,n.chaves))return i;return -1;}
    private boolean visitado(NoVisual n){int i=indice(n);return i>=0 && (fase>=1||fase*duracao()/1_000_000f>=i*155);}
    private void arestas(Graphics2D g,NoVisual n){Rectangle a=interpolar(n);for(NoVisual f:n.filhos)if(f!=null){Rectangle b=interpolar(f);boolean destaque=visitado(n)&&visitado(f);g.setColor(destaque?new Color(25,106,135):new Color(53,78,87,95));g.setStroke(new BasicStroke(destaque?2f:1f));g.draw(new Line2D.Float(a.x+a.width/2f,a.y+a.height,b.x+b.width/2f,b.y));arestas(g,f);}}
    private void desenharNo(Graphics2D gg,NoVisual n,Rectangle r,float opacidade,boolean busca){Graphics2D g=(Graphics2D)gg.create();g.setComposite(AlphaComposite.SrcOver.derive(Math.max(0,Math.min(1,opacidade))));
        if(busca){g.setColor(new Color(226,244,248,150));g.fill(new RoundRectangle2D.Float(r.x-5,r.y-4,r.width+10,r.height+8,12,12));g.setColor(new Color(25,106,135));g.setStroke(new BasicStroke(1.7f));g.draw(new RoundRectangle2D.Float(r.x-5,r.y-4,r.width+10,r.height+8,12,12));}
        if(n.binario){Color cor=n.preto?new Color(40,56,64,195):new Color(137,66,75,190);g.setColor(cor);g.fill(new RoundRectangle2D.Float(r.x,r.y,r.width,r.height,7,7));g.setFont(Interface.fonte(14));g.setColor(Color.WHITE);centro(g,n.texto,r.x+r.width/2,r.y+20);}
        else{g.setColor(new Color(236,245,247,95));g.fill(new RoundRectangle2D.Float(r.x,r.y,r.width,r.height,8,8));g.setColor(new Color(61,89,99,130));g.setStroke(new BasicStroke(1));g.draw(new RoundRectangle2D.Float(r.x,r.y,r.width,r.height,8,8));g.setFont(Interface.fonte(15));for(int i=0;i<n.chaves.length;i++){if(i>0){g.setColor(new Color(61,89,99,85));g.drawLine(r.x+i*CELULA,r.y+6,r.x+i*CELULA,r.y+r.height-6);}g.setColor(new Color(33,53,62));centro(g,String.valueOf(n.chaves[i]),r.x+i*CELULA+CELULA/2,r.y+20);}}
        g.dispose();}
    private void nos(Graphics2D g,NoVisual n){float alpha=anteriores.containsKey(id(n))||fase>=1?1:Math.max(.12f,movimento());desenharNo(g,n,interpolar(n),alpha,visitado(n));for(NoVisual f:n.filhos)if(f!=null)nos(g,f);}
    private void centro(Graphics2D g,String s,int x,int y){g.drawString(s,x-g.getFontMetrics().stringWidth(s)/2,y);}
    protected void paintComponent(Graphics gg){super.paintComponent(gg);Graphics2D g=(Graphics2D)gg.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        pos.clear();if(raiz!=null){localizar(raiz,Math.max(20,(getWidth()-medir(raiz))/2),22);arestas(g,raiz);nos(g,raiz);}else{g.setFont(Interface.fonte(19));g.setColor(new Color(52,73,82));centro(g,"árvore vazia",getWidth()/2,90);}
        if(fase<1){Set<String> atuais=new HashSet<>();for(NoVisual n:pos.keySet())atuais.add(id(n));for(Map.Entry<String,NoVisual> e:nosAnteriores.entrySet())if(!atuais.contains(e.getKey()))desenharNo(g,e.getValue(),anteriores.get(e.getKey()),1-movimento(),false);}
        g.dispose();}
    public Dimension getPreferredScrollableViewportSize(){return new Dimension(900,300);}
    public int getScrollableUnitIncrement(Rectangle r,int o,int d){return 24;}
    public int getScrollableBlockIncrement(Rectangle r,int o,int d){return o==SwingConstants.HORIZONTAL?r.width-24:r.height-24;}
    public boolean getScrollableTracksViewportWidth(){return getParent()!=null&&getParent().getWidth()>=largura;}
    public boolean getScrollableTracksViewportHeight(){return getParent()!=null&&getParent().getHeight()>=altura;}
}
