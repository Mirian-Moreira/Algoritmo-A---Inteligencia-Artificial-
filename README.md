# Busca A* – Mapa da cidade virtual

Trabalho Prático I de Inteligência Artificial (IFBA). O programa encontra a rota entre dois pontos do mapa usando o algoritmo A*.

## Como rodar

É preciso ter o Java 17 ou mais novo. Dentro da pasta `astar`:

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out JanelaPrincipal
```

## Como usar

1. Escolha a heurística no topo: Manhattan, Euclidiana ou Chebyshev.
2. Clique em um ponto do mapa para marcar a origem (fica verde).
3. Clique em outro ponto para marcar o destino (fica vermelho). A busca começa na hora.
4. A busca aparece aos poucos no mapa, um passo por vez:
   - visitar um ponto leva 1 passo. O ponto fica **laranja** e o que está sendo visitado ganha um anel;
   - as ruas testadas a partir dele crescem em **amarelo**, 1 unidade por passo;
   - no final, o caminho escolhido é desenhado em **azul**, também 1 unidade por passo.
5. O passo a passo vai aparecendo na caixa de texto. No fim, aparece o trajeto no formato `A → B(1) → C(4) → D(5) → E(7)` e a distância total.
6. Se trocar a heurística, a busca é refeita com os mesmos pontos. Um novo clique começa outra escolha.

### Velocidade

- **Tempo por passo (s)**: quanto dura cada passo da animação. O padrão é `0,2`, e aceita vírgula ou ponto.
- **Instantâneo**: marcado, mostra o resultado final direto, sem animação.

## Arquivos

| Arquivo | O que faz |
|---------|-----------|
| `Ponto.java` | Um ponto do mapa (nome e posição na grade) |
| `Rua.java` | Um trecho de rua de um ponto a outro, com a distância |
| `Mapa.java` | Cria todos os pontos e ruas da Figura 1, com as mãos de direção |
| `Heuristica.java` | As três formas de estimar quanto falta até o destino |
| `BuscaAEstrela.java` | O algoritmo A* |
| `Etapa.java` | Uma rodada da busca: ponto visitado e ruas testadas a partir dele |
| `Resultado.java` | Guarda o que a busca encontrou e monta o texto do trajeto |
| `Animacao.java` | Mostra a busca aos poucos na tela, um passo por vez |
| `PainelMapa.java` | Desenha o mapa e trata os cliques |
| `JanelaPrincipal.java` | A janela do programa (tem o `main`) |
