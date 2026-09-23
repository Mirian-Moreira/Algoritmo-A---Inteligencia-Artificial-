import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.swing.JPanel;

/**
 * A área onde o mapa é desenhado.
 * O usuário clica em um ponto para ser a origem e em outro para ser o destino.
 */
public class PainelMapa extends JPanel {

    private static final int TAMANHO_QUADRADO = 90; // tamanho de 1 unidade na tela, em pixels
    private static final int MARGEM = 60;
    private static final int COLUNAS = 7;
    private static final int LINHAS = 5;
    private static final int RAIO_PONTO = 8;

    private static final Color COR_GRADE = new Color(220, 220, 220);
    private static final Color COR_RUA = new Color(220, 50, 50);
    private static final Color COR_TESTADA = new Color(255, 200, 0);
    private static final Color COR_CAMINHO = new Color(30, 90, 230);
    private static final Color COR_ORIGEM = new Color(40, 170, 70);
    private static final Color COR_DESTINO = new Color(200, 30, 30);
    private static final Color COR_NOME = new Color(40, 40, 140);
    private static final Color COR_VISITADO = new Color(255, 140, 0);

    private final Mapa mapa;
    private Ponto origem;
    private Ponto destino;
    private Animacao animacao;

    private BiConsumer<Ponto, Ponto> quandoEscolher;

    public PainelMapa(Mapa mapa) {
        this.mapa = mapa;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(COLUNAS * TAMANHO_QUADRADO + 2 * MARGEM,
                                       LINHAS * TAMANHO_QUADRADO + 2 * MARGEM));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cliqueNoMapa(e.getX(), e.getY());
            }
        });
    }

    public void aoEscolherPontos(BiConsumer<Ponto, Ponto> acao) {
        this.quandoEscolher = acao;
    }

    public void mostrarAnimacao(Animacao nova) {
        if (animacao != null) {
            animacao.parar();
        }
        animacao = nova;
        repaint();
    }

    public void limpar() {
        if (animacao != null) {
            animacao.parar();
        }
        origem = null;
        destino = null;
        animacao = null;
        repaint();
    }

    public Ponto getOrigem() {
        return origem;
    }

    public Ponto getDestino() {
        return destino;
    }

    private void cliqueNoMapa(int xTela, int yTela) {
        Ponto clicado = pontoPerto(xTela, yTela);
        if (clicado == null) {
            return;
        }

        if (origem == null || destino != null) {
            limpar();
            origem = clicado;
        } else if (clicado != origem) {
            destino = clicado;
            if (quandoEscolher != null) {
                quandoEscolher.accept(origem, destino);
            }
        }
        repaint();
    }

    private Ponto pontoPerto(int xTela, int yTela) {
        for (Ponto p : mapa.getPontos()) {
            int dx = telaX(p) - xTela;
            int dy = telaY(p) - yTela;
            if (dx * dx + dy * dy <= 18 * 18) {
                return p;
            }
        }
        return null;
    }

    // Convertem a posição na grade para a posição na tela
    private int telaX(Ponto p) {
        return MARGEM + p.getX() * TAMANHO_QUADRADO;
    }

    private int telaY(Ponto p) {
        return MARGEM + p.getY() * TAMANHO_QUADRADO;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        desenharGrade(g2);
        desenharRuas(g2);

        if (animacao != null) {
            desenharRuasTestadas(g2);
            desenharCaminho(g2);
        }

        desenharSetas(g2);
        desenharPontos(g2);
    }

    private void desenharGrade(Graphics2D g2) {
        g2.setColor(COR_GRADE);
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i <= COLUNAS; i++) {
            int x = MARGEM + i * TAMANHO_QUADRADO;
            g2.drawLine(x, MARGEM, x, MARGEM + LINHAS * TAMANHO_QUADRADO);
        }
        for (int i = 0; i <= LINHAS; i++) {
            int y = MARGEM + i * TAMANHO_QUADRADO;
            g2.drawLine(MARGEM, y, MARGEM + COLUNAS * TAMANHO_QUADRADO, y);
        }
    }

    private void desenharRuas(Graphics2D g2) {
        g2.setColor(COR_RUA);
        g2.setStroke(new BasicStroke(3));
        for (Rua rua : mapa.getTodasAsRuas()) {
            linha(g2, rua);
        }
    }

    /** As ruas testadas que ficarem no caminho final são cobertas depois pelo azul. */
    private void desenharRuasTestadas(Graphics2D g2) {
        g2.setColor(COR_TESTADA);
        g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (Map.Entry<Rua, Integer> item : animacao.getRuasDesenhadas().entrySet()) {
            Rua rua = item.getKey();
            double parte = item.getValue() / (double) rua.getCusto();
            pedacoDeLinha(g2, rua.getOrigem(), rua.getDestino(), parte);
        }
    }

    private void desenharCaminho(Graphics2D g2) {
        g2.setColor(COR_CAMINHO);
        g2.setStroke(new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        List<Ponto> caminho = animacao.getCaminho();
        int faltaDesenhar = animacao.getUnidadesDoCaminho();

        for (int i = 0; i + 1 < caminho.size() && faltaDesenhar > 0; i++) {
            Ponto a = caminho.get(i);
            Ponto b = caminho.get(i + 1);
            int tamanho = Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
            int agora = Math.min(faltaDesenhar, tamanho);
            pedacoDeLinha(g2, a, b, agora / (double) tamanho);
            faltaDesenhar -= agora;
        }
    }

    /** Uma setinha no meio de cada rua de mão única, apontando para onde se pode ir. */
    private void desenharSetas(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        for (Rua rua : mapa.getTodasAsRuas()) {
            if (!rua.isMaoUnica()) {
                continue;
            }
            int x1 = telaX(rua.getOrigem());
            int y1 = telaY(rua.getOrigem());
            int x2 = telaX(rua.getDestino());
            int y2 = telaY(rua.getDestino());
            int meioX = (x1 + x2) / 2;
            int meioY = (y1 + y2) / 2;

            int dirX = Integer.signum(x2 - x1);
            int dirY = Integer.signum(y2 - y1);

            // Triângulo: a ponta vai para frente e as duas abas para os lados
            Polygon seta = new Polygon();
            seta.addPoint(meioX + dirX * 8, meioY + dirY * 8);
            seta.addPoint(meioX - dirX * 6 - dirY * 6, meioY - dirY * 6 + dirX * 6);
            seta.addPoint(meioX - dirX * 6 + dirY * 6, meioY - dirY * 6 - dirX * 6);
            g2.fillPolygon(seta);
        }
    }

    private void desenharPontos(Graphics2D g2) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 20));
        for (Ponto p : mapa.getPontos()) {
            int x = telaX(p);
            int y = telaY(p);

            Color cor = Color.DARK_GRAY;
            if (animacao != null && animacao.foiVisitado(p)) cor = COR_VISITADO;
            if (p == origem) cor = COR_ORIGEM;
            if (p == destino) cor = COR_DESTINO;

            g2.setColor(cor);
            g2.fillOval(x - RAIO_PONTO, y - RAIO_PONTO, RAIO_PONTO * 2, RAIO_PONTO * 2);

            if (animacao != null && p == animacao.getPontoAtual()) {
                g2.setColor(COR_VISITADO);
                g2.setStroke(new BasicStroke(3));
                int r = RAIO_PONTO + 6;
                g2.drawOval(x - r, y - r, r * 2, r * 2);
            }

            g2.setColor(COR_NOME);
            g2.drawString(p.getNome(), x + 8, y - 10);
        }
    }

    private void linha(Graphics2D g2, Rua rua) {
        pedacoDeLinha(g2, rua.getOrigem(), rua.getDestino(), 1.0);
    }

    /** Desenha só uma parte da linha de a até b (parte = 0.5 desenha metade, 1.0 inteira). */
    private void pedacoDeLinha(Graphics2D g2, Ponto a, Ponto b, double parte) {
        int x1 = telaX(a);
        int y1 = telaY(a);
        int x2 = x1 + (int) Math.round((telaX(b) - x1) * parte);
        int y2 = y1 + (int) Math.round((telaY(b) - y1) * parte);
        g2.drawLine(x1, y1, x2, y2);
    }
}
