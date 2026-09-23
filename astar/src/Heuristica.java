/**
 * As três formas de "chutar" quanto falta de um ponto até o destino.
 * Esse chute é o h(n) da fórmula f(n) = g(n) + h(n).
 * Importante: o carro continua andando só pelas ruas; aqui muda apenas a estimativa.
 */
public enum Heuristica {

    MANHATTAN("Manhattan") {
        @Override
        public double estimar(Ponto a, Ponto b) {
            return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
        }
    },

    EUCLIDIANA("Euclidiana") {
        @Override
        public double estimar(Ponto a, Ponto b) {
            int dx = a.getX() - b.getX();
            int dy = a.getY() - b.getY();
            return Math.sqrt(dx * dx + dy * dy);
        }
    },

    CHEBYSHEV("Chebyshev") {
        @Override
        public double estimar(Ponto a, Ponto b) {
            return Math.max(Math.abs(a.getX() - b.getX()), Math.abs(a.getY() - b.getY()));
        }
    };

    private final String nomeNaTela;

    Heuristica(String nomeNaTela) {
        this.nomeNaTela = nomeNaTela;
    }

    public abstract double estimar(Ponto a, Ponto b);

    @Override
    public String toString() {
        return nomeNaTela;
    }
}
