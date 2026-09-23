/**
 * Um ponto (esquina) do mapa, como A, B, C...
 * A posição x e y é contada em quadradinhos da grade, começando no ponto A
 * (canto de cima, à esquerda). O x cresce para a direita e o y para baixo.
 */
public class Ponto {

    private final String nome;
    private final int x;
    private final int y;

    public Ponto(String nome, int x, int y) {
        this.nome = nome;
        this.x = x;
        this.y = y;
    }

    public String getNome() {
        return nome;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return nome;
    }
}
