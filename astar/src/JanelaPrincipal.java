import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class JanelaPrincipal extends JFrame {

    private static final double TEMPO_PADRAO = 0.2; // segundos por tick

    private final Mapa mapa = new Mapa();
    private final BuscaAEstrela busca = new BuscaAEstrela(mapa);
    private final PainelMapa painelMapa = new PainelMapa(mapa);
    private final JComboBox<Heuristica> escolhaHeuristica = new JComboBox<>(Heuristica.values());
    private final JCheckBox caixaInstantaneo = new JCheckBox("Instantâneo");
    private final JTextField campoTempo = new JTextField("0,2", 4);
    private final JTextArea areaTexto = new JTextArea();

    public JanelaPrincipal() {
        super("Busca A* - Mapa da cidade virtual");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(montarBarraDeCima(), BorderLayout.NORTH);
        add(painelMapa, BorderLayout.CENTER);
        add(montarAreaDeTexto(), BorderLayout.SOUTH);

        painelMapa.aoEscolherPontos((origem, destino) -> rodarBusca());

        escolhaHeuristica.addActionListener(e -> {
            if (painelMapa.getOrigem() != null && painelMapa.getDestino() != null) {
                rodarBusca();
            }
        });

        caixaInstantaneo.addActionListener(e -> campoTempo.setEnabled(!caixaInstantaneo.isSelected()));

        mostrarInstrucoes();
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel montarBarraDeCima() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(new JLabel("Heurística:"));
        barra.add(escolhaHeuristica);

        barra.add(new JLabel("   Tempo por passo (s):"));
        barra.add(campoTempo);
        barra.add(caixaInstantaneo);

        JButton botaoLimpar = new JButton("Limpar");
        botaoLimpar.addActionListener(e -> {
            painelMapa.limpar();
            mostrarInstrucoes();
        });
        barra.add(botaoLimpar);
        return barra;
    }

    private JPanel montarAreaDeTexto() {
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane rolagem = new JScrollPane(areaTexto);
        rolagem.setPreferredSize(new Dimension(0, 220));

        JPanel parteDeBaixo = new JPanel(new BorderLayout());
        parteDeBaixo.add(new JLabel("  Laranja = ponto visitado   |   Amarelo = ruas testadas"
                + "   |   Azul = caminho escolhido"), BorderLayout.NORTH);
        parteDeBaixo.add(rolagem, BorderLayout.CENTER);
        return parteDeBaixo;
    }

    private void mostrarInstrucoes() {
        areaTexto.setText("Clique em um ponto do mapa para escolher a origem "
                + "e depois em outro para escolher o destino.");
    }

    private int lerTempoEmMilissegundos() {
        String digitado = campoTempo.getText().trim().replace(',', '.');
        try {
            double segundos = Double.parseDouble(digitado);
            if (segundos > 0) {
                return (int) Math.round(segundos * 1000);
            }
        } catch (NumberFormatException e) {
            // cai no aviso abaixo
        }
        JOptionPane.showMessageDialog(this,
                "Tempo inválido. Use um número maior que zero, por exemplo 0,2.\n"
                        + "Vou usar " + TEMPO_PADRAO + " segundos.");
        campoTempo.setText("0,2");
        return (int) (TEMPO_PADRAO * 1000);
    }

    private void rodarBusca() {
        Ponto origem = painelMapa.getOrigem();
        Ponto destino = painelMapa.getDestino();
        Heuristica heuristica = (Heuristica) escolhaHeuristica.getSelectedItem();

        // A busca é rápida; quem deixa ela "devagar" na tela é a animação
        Resultado resultado = busca.buscar(origem, destino, heuristica);

        areaTexto.setText("Heurística: " + heuristica + "\n\nPasso a passo da busca:\n");

        Animacao animacao = new Animacao(
                resultado,
                painelMapa::repaint,
                linha -> areaTexto.append(linha + "\n"),
                () -> areaTexto.append(resumoFinal(resultado)));
        painelMapa.mostrarAnimacao(animacao);

        if (caixaInstantaneo.isSelected()) {
            animacao.pularParaOFim();
        } else {
            animacao.iniciar(lerTempoEmMilissegundos());
        }
    }

    private String resumoFinal(Resultado resultado) {
        StringBuilder texto = new StringBuilder("\nTrajeto: ").append(resultado.comoTexto()).append("\n");
        if (resultado.achouCaminho()) {
            texto.append("Distância total: ").append(resultado.getCustoTotal()).append(" U\n");
        }
        return texto.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JanelaPrincipal().setVisible(true));
    }
}
