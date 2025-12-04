import java.util.*;

public class GrammarParser {
    public static class ParsedGrammar {
        public Map<String, List<String>> grammar;
        public List<String> nonTerminalOrder;
        
        public ParsedGrammar(Map<String, List<String>> grammar, List<String> nonTerminalOrder) {
            this.grammar = grammar;
            this.nonTerminalOrder = nonTerminalOrder;
        }
    }
    
    public static ParsedGrammar parseGrammar(String text) {
        Map<String, List<String>> grammar = new LinkedHashMap<>();
        List<String> nonTerminalOrder = new ArrayList<>();
        String[] lines = text.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            String[] parts = line.split("->");
            if (parts.length != 2) continue;
            
            String lhs = parts[0].trim();
            String rhs = parts[1].trim();
            
            if (!grammar.containsKey(lhs)) {
                grammar.put(lhs, new ArrayList<>());
                nonTerminalOrder.add(lhs);
            }
            
            String[] productions = rhs.split("\\|");
            for (String prod : productions) {
                String processed = prod
                    .replaceAll("([\\{\\}\\[\\]\\(\\)\\;,])", " $1 ")
                    .trim()
                    .replaceAll("\\s+", " ");
                    
                if (processed.isEmpty()) {
                    processed = "ε";
                }
                grammar.get(lhs).add(processed);
            }
        }
        
        return new ParsedGrammar(grammar, nonTerminalOrder);
    }
    
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
                    
                for (String token : tokens) {
                    if (!token.isEmpty() && !token.equals("ε") && 
                        !nonTerminals.contains(token) && !seen.contains(token)) {
                        terminals.add(token);
                        seen.add(token);
                    }
                }
            }
        }
        
        terminals.add("$");
        return terminals;
    }
}
