import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class LR1DesktopApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame("LR(1) Parser Desktop");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JTextArea grammarArea = new JTextArea("E -> E + T\nE -> T\nT -> T * F\nT -> F\nF -> ( E )\nF -> id", 10, 60);
        JTextField startSymbolField = new JTextField("E", 10);
        JButton runButton = new JButton("Construir LR(1)");
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Gramática (formato: S -> A B):"), BorderLayout.NORTH);
        topPanel.add(new JScrollPane(grammarArea), BorderLayout.CENTER);
        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startPanel.add(new JLabel("Símbolo inicial:"));
        startPanel.add(startSymbolField);
        topPanel.add(startPanel, BorderLayout.SOUTH);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(runButton, BorderLayout.CENTER);
        frame.add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        runButton.addActionListener(e -> {
            String grammarText = grammarArea.getText().trim();
            String startSymbol = startSymbolField.getText().trim();
            Map<String, List<String>> grammar = new LinkedHashMap<>();
            for (String line : grammarText.split("\n")) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("->");
                if (parts.length != 2) continue;
                String lhs = parts[0].trim();
                String rhs = parts[1].trim();
                grammar.computeIfAbsent(lhs, k -> new ArrayList<>()).add(rhs);
            }
            LR1Result result = LR1Parser.buildLR1Table(grammar, startSymbol);
            StringBuilder sb = new StringBuilder();
            sb.append("Colección canónica LR(1):\n\n").append(result.formatCanonicalCollection()).append("\n");
            sb.append("Tabla LR(1):\n\n").append(result.formatTable()).append("\n");
            sb.append("Transiciones:\n");
            for (String tr : result.transitions) sb.append(tr).append("\n");
            outputArea.setText(sb.toString());
        });

        frame.setVisible(true);
    }
}
