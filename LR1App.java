import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class LR1App {
    private static final Color PRIMARY_COLOR = new Color(37, 99, 235);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color WARNING_COLOR = new Color(217, 119, 6);
    private static final Color BG_COLOR = new Color(241, 245, 249);
    private static final Color SURFACE_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(203, 213, 225);
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
    
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("LR(1) Parser - Solver Compiladores");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.getContentPane().setBackground(BG_COLOR);
        frame.setLayout(new BorderLayout(10, 10));
        
        // Panel superior: entrada de gramática
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BG_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        
        // Panel de gramática
        JPanel grammarPanel = createStyledPanel("1. Gramática (formato: S -> A B)");
        JTextArea grammarArea = new JTextArea("S -> C C\nC -> c C | d", 6, 60);
        grammarArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        grammarArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane grammarScroll = new JScrollPane(grammarArea);
        grammarPanel.add(grammarScroll, BorderLayout.CENTER);
        
        // Panel de controles
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(SURFACE_COLOR);
        
        JLabel startLabel = new JLabel("Símbolo inicial:");
        startLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        controlPanel.add(startLabel);
        
        JTextField startSymbolField = new JTextField("S", 10);
        startSymbolField.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        startSymbolField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        controlPanel.add(startSymbolField);
        
        JButton runButton = createStyledButton("Construir LR(1)", PRIMARY_COLOR);
        controlPanel.add(runButton);
        
        grammarPanel.add(controlPanel, BorderLayout.SOUTH);
        topPanel.add(grammarPanel, BorderLayout.CENTER);
        
        // Panel inferior: salida con pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG_COLOR);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        // Pestaña: Colección Canónica
        JPanel collectionPanel = createStyledPanel("Colección Canónica LR(1) (Items con Lookahead)");
        JTextArea collectionArea = createStyledTextArea();
        collectionPanel.add(new JScrollPane(collectionArea), BorderLayout.CENTER);
        
        // Pestaña: Tabla LR(1)
        JPanel tablePanel = createStyledPanel("Tabla LR(1)");
        JEditorPane tableArea = new JEditorPane();
        tableArea.setContentType("text/html");
        tableArea.setEditable(false);
        tableArea.setBackground(new Color(248, 250, 252));
        tablePanel.add(new JScrollPane(tableArea), BorderLayout.CENTER);
        
        // Pestaña: Grafo
        JPanel graphPanel = createStyledPanel("AFN LR(1) (Grafo de Transiciones)");
        LR1GraphPanel graphCanvas = new LR1GraphPanel();
        JScrollPane graphScroll = new JScrollPane(graphCanvas);
        graphScroll.getViewport().setBackground(Color.WHITE);
        graphPanel.add(graphScroll, BorderLayout.CENTER);
        
        // Pestaña: Reconocer Cadena
        JPanel parsePanel = new JPanel(new BorderLayout(10, 10));
        parsePanel.setBackground(BG_COLOR);
        parsePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel parseTopPanel = createStyledPanel("2. Análisis de Cadena (Trace / Mangos)");
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        inputPanel.setBackground(SURFACE_COLOR);
        
        JLabel inputLabel = new JLabel("Cadena de entrada (tokens separados por espacio):");
        inputLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        inputPanel.add(inputLabel);
        
        JTextField inputStringField = new JTextField(30);
        inputStringField.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        inputStringField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        inputPanel.add(inputStringField);
        
        JButton parseButton = createStyledButton("Reconocer Cadena", SUCCESS_COLOR);
        inputPanel.add(parseButton);
        
        parseTopPanel.add(inputPanel, BorderLayout.CENTER);
        
        JTextArea parseResultArea = createStyledTextArea();
        JScrollPane parseResultScroll = new JScrollPane(parseResultArea);
        
        parsePanel.add(parseTopPanel, BorderLayout.NORTH);
        parsePanel.add(parseResultScroll, BorderLayout.CENTER);
        
        tabbedPane.addTab("Colección Canónica", collectionPanel);
        tabbedPane.addTab("Tabla LR(1)", tablePanel);
        tabbedPane.addTab("Grafo de Transiciones", graphPanel);
        tabbedPane.addTab("Reconocer Cadena", parsePanel);
        
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(tabbedPane, BorderLayout.CENTER);
        
        // Variable para almacenar el último resultado
        final LR1Parser.LR1Result[] lastResult = new LR1Parser.LR1Result[1];
        @SuppressWarnings("unchecked")
        final Map<String, java.util.List<String>>[] lastGrammar = new Map[1];
        
        // Lógica del análisis
        Runnable runAnalysis = () -> {
            try {
                String grammarText = grammarArea.getText().trim();
                String startSymbol = startSymbolField.getText().trim();
                
                if (grammarText.isEmpty() || startSymbol.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, 
                        "Por favor ingrese una gramática y un símbolo inicial.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                GrammarParser.ParsedGrammar parsed = GrammarParser.parseGrammar(grammarText);
                Map<String, java.util.List<String>> grammar = parsed.grammar;
                
                LR1Parser.LR1Result result = LR1Parser.buildLR1Table(grammar, startSymbol);
                
                lastResult[0] = result;
                lastGrammar[0] = grammar;
                
                collectionArea.setText(result.formatCanonicalCollection());
                tableArea.setText(result.formatTable());
                graphCanvas.setResult(result);
                
                tabbedPane.setSelectedIndex(0);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, 
                    "Error al procesar la gramática:\n" + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        };
        
        // Lógica de reconocimiento de cadena
        Runnable runParse = () -> {
            try {
                if (lastResult[0] == null || lastGrammar[0] == null) {
                    JOptionPane.showMessageDialog(frame, 
                        "Primero debe construir la tabla LR(1).", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String inputString = inputStringField.getText().trim();
                if (inputString.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, 
                        "Por favor ingrese una cadena a reconocer.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                LRParserTrace.ParseResult parseResult = LRParserTrace.runParser(
                    lastResult[0].table, 
                    lastGrammar[0], 
                    inputString
                );
                
                parseResultArea.setText(parseResult.formatTrace());
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, 
                    "Error al reconocer la cadena:\n" + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        };
        
        // Eventos
        runButton.addActionListener(e -> runAnalysis.run());
        startSymbolField.addActionListener(e -> runAnalysis.run());
        
        parseButton.addActionListener(e -> runParse.run());
        inputStringField.addActionListener(e -> runParse.run());
        
        grammarArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    runAnalysis.run();
                }
            }
        });
        
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private static JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(SURFACE_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        if (title != null && !title.isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            titleLabel.setForeground(new Color(51, 65, 85));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            panel.add(titleLabel, BorderLayout.NORTH);
        }
        
        return panel;
    }
    
    private static JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("JetBrains Mono", Font.PLAIN, 11));
        area.setBackground(new Color(248, 250, 252));
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        area.setLineWrap(false);
        return area;
    }
    
    private static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
}
