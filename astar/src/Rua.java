/**
 * Um trecho de rua que liga um ponto a outro, sempre no sentido origem -> destino.
 * Rua de mão dupla vira dois trechos, um para cada lado.
 */
public class Rua {

    private final Ponto origem;
    private final Ponto destino;
    private final int custo;       // distância em quadradinhos
    private final boolean maoUnica;

    public Rua(Ponto origem, Ponto destino, int custo, boolean maoUnica) {
        this.origem = origem;
        this.destino = destino;
        this.custo = custo;
        this.maoUnica = maoUnica;
    }

    public Ponto getOrigem() {
        return origem;
    }

    public Ponto getDestino() {
        return destino;
    }

    public int getCusto() {
        return custo;
    }

    public boolean isMaoUnica() {
        return maoUnica;
    }
}
