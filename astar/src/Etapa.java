import java.util.ArrayList;
import java.util.List;

/**
 * Uma rodada da busca: o ponto que saiu da fila para ser visitado
 * e as ruas que foram testadas a partir dele.
 * Serve para a animação conseguir mostrar a busca acontecendo aos poucos.
 */
public class Etapa {

    private final Ponto ponto;
    private final List<Rua> ruasTestadas = new ArrayList<>();
    private final List<String> linhas = new ArrayList<>();

    public Etapa(Ponto ponto) {
        this.ponto = ponto;
    }

    public Ponto getPonto() {
        return ponto;
    }

    public List<Rua> getRuasTestadas() {
        return ruasTestadas;
    }

    public List<String> getLinhas() {
        return linhas;
    }

    public int maiorCusto() {
        int maior = 0;
        for (Rua rua : ruasTestadas) {
            maior = Math.max(maior, rua.getCusto());
        }
        return maior;
    }
}
