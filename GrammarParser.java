import java.util.*;

// Parser para convertir texto de gramática en estructuras de datos
public class GrammarParser {
    // Clase auxiliar para retornar gramática parseada
    public static class ParsedGrammar {
        public Map<String, List<String>> grammar;
        public List<String> nonTerminalOrder;  // mantener orden de aparicion
        
        public ParsedGrammar(Map<String, List<String>> grammar, List<String> nonTerminalOrder) {
            this.grammar = grammar;
            this.nonTerminalOrder = nonTerminalOrder;
        }
    }
    
    // Lee el texto y convierte en Map de producciones
    public static ParsedGrammar parseGrammar(String text) {
        Map<String, List<String>> grammar = new LinkedHashMap<>();
        List<String> nonTerminalOrder = new ArrayList<>();
        String[] lines = text.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            // Separar lado izquierdo y derecho por ->
            String[] parts = line.split("->");
            if (parts.length != 2) continue;
            
            String lhs = parts[0].trim();
            String rhs = parts[1].trim();
            
            // Primera vez que vemos este no-terminal
            if (!grammar.containsKey(lhs)) {
                grammar.put(lhs, new ArrayList<>());
                nonTerminalOrder.add(lhs);
            }
            
            // Separar alternativas por |
            String[] productions = rhs.split("\\|");
            for (String prod : productions) {
                // Agregar espacios alrededor de simbolos especiales
                String processed = prod
                    .replaceAll("([\\{\\}\\[\\]\\(\\)\\;,])", " $1 ")
                    .trim()
                    .replaceAll("\\s+", " ");
                    
                // Produccion vacia se representa como epsilon
                if (processed.isEmpty()) {
                    processed = "ε";
                }
                grammar.get(lhs).add(processed);
            }
        }
        
        return new ParsedGrammar(grammar, nonTerminalOrder);
    }
    
    // Extrae todos los terminales del texto de la gramatica
    public static List<String> extractTerminals(String text, Map<String, List<String>> grammar) {
        List<String> terminals = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        Set<String> nonTerminals = grammar.keySet();
        
        String[] lines = text.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            String[] parts = line.split("->");
            if (parts.length != 2) continue;
            
            String rhs = parts[1].trim();
            String[] productions = rhs.split("\\|");
            
            for (String prod : productions) {
                String[] tokens = prod
                    .replaceAll("([\\{\\}\\[\\]\\(\\)\\;,])", " $1 ")
                    .trim()
                    .split("\\s+");
                    
                // Un token es terminal si no está en los no-terminales
                for (String token : tokens) {
                    if (!token.isEmpty() && !token.equals("ε") && 
                        !nonTerminals.contains(token) && !seen.contains(token)) {
                        terminals.add(token);
                        seen.add(token);
                    }
                }
            }
        }
        
        // Agregar símbolo de fin de entrada
        terminals.add("$");
        return terminals;
    }
}
