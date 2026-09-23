import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * O algoritmo A*.
 *
 * Ideia geral: temos uma lista de pontos "na fila" para visitar (a fronteira).
 * A cada rodada, tiramos da fila o ponto com menor f, onde
 *   g = quanto já andamos desde a origem até ele
 *   h = quanto a heurística acha que ainda falta até o destino
 *   f = g + h
 * Depois olhamos as ruas que saem desse ponto e colocamos os vizinhos na fila.
 * Quando o ponto tirado da fila é o destino, terminamos.
 */
public class BuscaAEstrela {

    private final Mapa mapa;

    public BuscaAEstrela(Mapa mapa) {
        this.mapa = mapa;
    }

    /** Um item da fila: o ponto e os valores de g e h com que ele entrou. */
    private static class ItemDaFila {
        final Ponto ponto;
        final int g;
        final double h;

        ItemDaFila(Ponto ponto, int g, double h) {
            this.ponto = ponto;
            this.g = g;
            this.h = h;
        }

        double f() {
            return g + h;
        }
    }

    public Resultado buscar(Ponto origem, Ponto destino, Heuristica heuristica) {
        // A fila sempre entrega primeiro o menor f.
        // Se empatar, fica o de menor h (mais perto do destino) e depois a ordem alfabética,
        // assim o resultado é sempre o mesmo toda vez que rodar.
        PriorityQueue<ItemDaFila> fila = new PriorityQueue<>((a, b) -> {
            if (a.f() != b.f()) return Double.compare(a.f(), b.f());
            if (a.h != b.h) return Double.compare(a.h, b.h);
            return a.ponto.getNome().compareTo(b.ponto.getNome());
        });

        Map<Ponto, Integer> melhorG = new HashMap<>();   // menor distância achada até cada ponto
        Map<Ponto, Ponto> veioDe = new HashMap<>();      // de qual ponto chegamos em cada um
        Set<Ponto> jaVisitados = new HashSet<>();        // pontos que já saíram da fila
        List<Etapa> etapas = new ArrayList<>();

        melhorG.put(origem, 0);
        fila.add(new ItemDaFila(origem, 0, heuristica.estimar(origem, destino)));

        while (!fila.isEmpty()) {
            ItemDaFila atual = fila.poll();

            // O mesmo ponto pode estar na fila mais de uma vez (com valores antigos).
            // Se ele já foi visitado, é só ignorar.
            if (jaVisitados.contains(atual.ponto)) {
                continue;
            }
            jaVisitados.add(atual.ponto);

            Etapa etapa = new Etapa(atual.ponto);
            etapas.add(etapa);
            etapa.getLinhas().add(String.format("Visitando %s: g=%d, h=%s, f=%s",
                    atual.ponto, atual.g, numero(atual.h), numero(atual.f())));

            if (atual.ponto == destino) {
                List<Ponto> caminho = montarCaminho(veioDe, destino);
                return new Resultado(caminho, melhorG, etapas);
            }

            for (Rua rua : mapa.ruasSaindoDe(atual.ponto)) {
                Ponto vizinho = rua.getDestino();
                if (jaVisitados.contains(vizinho)) {
                    continue;
                }
                etapa.getRuasTestadas().add(rua);

                int novoG = atual.g + rua.getCusto();
                Integer gAntigo = melhorG.get(vizinho);

                // Só vale a pena se for a primeira vez ou se achamos um jeito mais curto
                if (gAntigo == null || novoG < gAntigo) {
                    melhorG.put(vizinho, novoG);
                    veioDe.put(vizinho, atual.ponto);
                    double h = heuristica.estimar(vizinho, destino);
                    fila.add(new ItemDaFila(vizinho, novoG, h));
                    etapa.getLinhas().add(String.format("    %s entra na fila: g=%d, h=%s, f=%s",
                            vizinho, novoG, numero(h), numero(novoG + h)));
                }
            }
        }

        return new Resultado(new ArrayList<>(), melhorG, etapas);
    }

    /** Volta do destino até a origem seguindo o "veio de", e depois inverte a lista. */
    private List<Ponto> montarCaminho(Map<Ponto, Ponto> veioDe, Ponto destino) {
        List<Ponto> caminho = new ArrayList<>();
        Ponto p = destino;
        while (p != null) {
            caminho.add(p);
            p = veioDe.get(p);
        }
        Collections.reverse(caminho);
        return caminho;
    }

    private static String numero(double valor) {
        if (valor == Math.floor(valor)) {
            return String.valueOf((int) valor);
        }
        return String.format("%.2f", valor);
    }
}
