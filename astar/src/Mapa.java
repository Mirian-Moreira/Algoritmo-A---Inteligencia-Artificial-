import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * O mapa da cidade da Figura 1: os pontos e as ruas que ligam cada um.
 * Os dados foram tirados do anexo "Mapa como grafo" do enunciado.
 */
public class Mapa {

    private final Map<String, Ponto> pontos = new LinkedHashMap<>();

    // Para cada ponto, a lista de ruas que saem dele
    private final Map<Ponto, List<Rua>> saidas = new LinkedHashMap<>();

    private final List<Rua> todasAsRuas = new ArrayList<>();

    public Mapa() {
        criarPontos();
        criarRuas();
    }

    private void criarPontos() {
        novoPonto("A", 0, 0);
        novoPonto("B", 1, 0);
        novoPonto("C", 4, 0);
        novoPonto("D", 5, 0);
        novoPonto("E", 7, 0);

        novoPonto("F", 0, 2);
        novoPonto("G", 1, 2);
        novoPonto("H", 4, 2);
        novoPonto("I", 7, 2);

        novoPonto("J", 1, 3);
        novoPonto("K", 3, 3);
        novoPonto("L", 4, 3);
        novoPonto("M", 5, 3);
        novoPonto("N", 7, 3);

        novoPonto("O", 4, 4);
        novoPonto("P", 7, 4);

        novoPonto("Q", 0, 5);
        novoPonto("R", 1, 5);
        novoPonto("S", 3, 5);
        novoPonto("T", 4, 5);
        novoPonto("U", 7, 5);
    }

    private void criarRuas() {
        maoDupla("A", "B", 1);
        maoDupla("B", "C", 3);
        maoDupla("C", "D", 1);
        maoDupla("D", "E", 2);
        maoDupla("A", "F", 2);
        maoDupla("F", "Q", 3);
        maoDupla("F", "G", 1);
        maoDupla("G", "H", 3);
        maoDupla("D", "M", 3);
        maoDupla("K", "S", 2);
        maoDupla("T", "U", 3);
        maoDupla("G", "J", 1);

        // Ruas de mão única: só dá para ir do primeiro para o segundo
        maoUnica("B", "G", 2);
        maoUnica("J", "R", 2);
        maoUnica("H", "C", 2);
        maoUnica("L", "H", 1);
        maoUnica("O", "L", 1);
        maoUnica("T", "O", 1);
        maoUnica("E", "I", 2);
        maoUnica("I", "N", 1);
        maoUnica("N", "P", 1);
        maoUnica("P", "U", 1);
        maoUnica("N", "M", 2);
        maoUnica("M", "L", 1);
        maoUnica("L", "K", 1);
        maoUnica("K", "J", 2);
        maoUnica("P", "O", 3);
        maoUnica("Q", "R", 1);
        maoUnica("R", "S", 2);
        maoUnica("S", "T", 1);
    }

    private void novoPonto(String nome, int x, int y) {
        Ponto p = new Ponto(nome, x, y);
        pontos.put(nome, p);
        saidas.put(p, new ArrayList<>());
    }

    private void maoDupla(String a, String b, int custo) {
        adicionarRua(pontos.get(a), pontos.get(b), custo, false);
        adicionarRua(pontos.get(b), pontos.get(a), custo, false);
    }

    private void maoUnica(String de, String para, int custo) {
        adicionarRua(pontos.get(de), pontos.get(para), custo, true);
    }

    private void adicionarRua(Ponto de, Ponto para, int custo, boolean maoUnica) {
        Rua rua = new Rua(de, para, custo, maoUnica);
        saidas.get(de).add(rua);
        todasAsRuas.add(rua);
    }

    public Ponto getPonto(String nome) {
        return pontos.get(nome);
    }

    public Collection<Ponto> getPontos() {
        return pontos.values();
    }

    /** Ruas que dá para pegar saindo deste ponto (já respeitando a mão). */
    public List<Rua> ruasSaindoDe(Ponto p) {
        return saidas.get(p);
    }

    public List<Rua> getTodasAsRuas() {
        return todasAsRuas;
    }
}
