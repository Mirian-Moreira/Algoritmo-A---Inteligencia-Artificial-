import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tudo o que a busca descobriu: o caminho escolhido, quanto custou até cada
 * ponto e as etapas da busca (pontos visitados e ruas testadas, em ordem).
 */
public class Resultado {

    private final List<Ponto> caminho;              // vazio se não achou caminho
    private final Map<Ponto, Integer> custoAcumulado; // g de cada ponto visitado
    private final List<Etapa> etapas;

    public Resultado(List<Ponto> caminho, Map<Ponto, Integer> custoAcumulado,
                     List<Etapa> etapas) {
        this.caminho = caminho;
        this.custoAcumulado = custoAcumulado;
        this.etapas = etapas;
    }

    public boolean achouCaminho() {
        return !caminho.isEmpty();
    }

    public List<Ponto> getCaminho() {
        return caminho;
    }

    public List<Etapa> getEtapas() {
        return etapas;
    }

    public List<Rua> getRuasTestadas() {
        List<Rua> todas = new ArrayList<>();
        for (Etapa etapa : etapas) {
            todas.addAll(etapa.getRuasTestadas());
        }
        return todas;
    }

    public int getCustoTotal() {
        if (!achouCaminho()) {
            return 0;
        }
        return custoAcumulado.get(caminho.get(caminho.size() - 1));
    }

    /**
     * Monta o texto no formato pedido no enunciado:
     * A → B(1) → C(4) → D(5) → E(7)
     * O primeiro ponto vai sem número, os outros com a distância somada até ali.
     */
    public String comoTexto() {
        if (!achouCaminho()) {
            return "Não existe caminho entre esses pontos.";
        }
        StringBuilder texto = new StringBuilder(caminho.get(0).getNome());
        for (int i = 1; i < caminho.size(); i++) {
            Ponto p = caminho.get(i);
            texto.append(" → ").append(p.getNome())
                 .append("(").append(custoAcumulado.get(p)).append(")");
        }
        return texto.toString();
    }
}
