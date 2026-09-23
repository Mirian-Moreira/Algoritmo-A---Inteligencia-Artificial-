import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.Timer;

/**
 * Mostra a busca acontecendo aos poucos, um "tick" de cada vez.
 *
 * A busca em si já terminou (o resultado está pronto); aqui a gente só vai
 * revelando o que aconteceu, na mesma ordem:
 *   1. visitar um ponto ............................. 1 tick
 *   2. as ruas testadas a partir dele vão crescendo . 1 tick por unidade
 *      (todas juntas, então dura o tamanho da maior)
 *   3. repete 1 e 2 até o destino
 *   4. o caminho final em azul vai sendo desenhado .. 1 tick por unidade
 */
public class Animacao {

    private enum Fase { VISITANDO, TESTANDO_RUAS, DESENHANDO_CAMINHO, TERMINOU }

    private final Resultado resultado;
    private final Runnable redesenhar;
    private final Consumer<String> escreverLinha;
    private final Runnable aoTerminar;
    private Timer relogio;

    private Fase fase = Fase.VISITANDO;
    private int numeroDaEtapa = 0;
    private int unidadesDaEtapa = 0;

    private final Set<Ponto> pontosVisitados = new HashSet<>();
    private final Map<Rua, Integer> ruasDesenhadas = new HashMap<>(); // rua -> quantas unidades já apareceram
    private Ponto pontoAtual;
    private int unidadesDoCaminho = 0;

    public Animacao(Resultado resultado, Runnable redesenhar,
                    Consumer<String> escreverLinha, Runnable aoTerminar) {
        this.resultado = resultado;
        this.redesenhar = redesenhar;
        this.escreverLinha = escreverLinha;
        this.aoTerminar = aoTerminar;
    }

    public void iniciar(int milissegundos) {
        relogio = new Timer(milissegundos, e -> {
            proximoTick();
            redesenhar.run();
        });
        relogio.start();
    }

    /** Modo instantâneo: pula direto para o final. */
    public void pularParaOFim() {
        parar();
        while (fase != Fase.TERMINOU) {
            proximoTick();
        }
        redesenhar.run();
    }

    public void parar() {
        if (relogio != null) {
            relogio.stop();
        }
    }

    private void proximoTick() {
        List<Etapa> etapas = resultado.getEtapas();

        switch (fase) {
            case VISITANDO -> {
                Etapa etapa = etapas.get(numeroDaEtapa);
                pontoAtual = etapa.getPonto();
                pontosVisitados.add(pontoAtual);
                for (String linha : etapa.getLinhas()) {
                    escreverLinha.accept(linha);
                }
                unidadesDaEtapa = 0;
                if (etapa.getRuasTestadas().isEmpty()) {
                    irParaProximaEtapa();
                } else {
                    fase = Fase.TESTANDO_RUAS;
                }
            }
            case TESTANDO_RUAS -> {
                Etapa etapa = etapas.get(numeroDaEtapa);
                unidadesDaEtapa++;
                for (Rua rua : etapa.getRuasTestadas()) {
                    ruasDesenhadas.put(rua, Math.min(unidadesDaEtapa, rua.getCusto()));
                }
                if (unidadesDaEtapa >= etapa.maiorCusto()) {
                    irParaProximaEtapa();
                }
            }
            case DESENHANDO_CAMINHO -> {
                unidadesDoCaminho++;
                if (unidadesDoCaminho >= resultado.getCustoTotal()) {
                    terminar();
                }
            }
            case TERMINOU -> parar();
        }
    }

    private void irParaProximaEtapa() {
        numeroDaEtapa++;
        if (numeroDaEtapa < resultado.getEtapas().size()) {
            fase = Fase.VISITANDO;
        } else if (resultado.achouCaminho() && resultado.getCustoTotal() > 0) {
            fase = Fase.DESENHANDO_CAMINHO;
        } else {
            terminar();
        }
    }

    private void terminar() {
        fase = Fase.TERMINOU;
        pontoAtual = null;
        parar();
        aoTerminar.run();
    }

    public boolean foiVisitado(Ponto p) {
        return pontosVisitados.contains(p);
    }

    public Ponto getPontoAtual() {
        return pontoAtual;
    }

    public Map<Rua, Integer> getRuasDesenhadas() {
        return ruasDesenhadas;
    }

    public List<Ponto> getCaminho() {
        return resultado.getCaminho();
    }

    public int getUnidadesDoCaminho() {
        return unidadesDoCaminho;
    }
}
