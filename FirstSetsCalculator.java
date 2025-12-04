import java.util.*;

// Calcula los conjuntos FIRST de una gramatica
public class FirstSetsCalculator {
    // FIRST(X) = conjunto de terminales que pueden aparecer al inicio de X
    public static Map<String, Set<String>> calculateFirstSets(Map<String, List<String>> grammar) {
        Map<String, Set<String>> firstSets = new HashMap<>();
        
        // Inicializar conjuntos vacios
        for (String nt : grammar.keySet()) {
            firstSets.put(nt, new HashSet<>());
        }
        
        boolean changed = true;
        int safety = 0;  // evitar loops infinitos
        
        // Iterar hasta que no haya cambios (punto fijo)
        while (changed && safety++ < 1000) {
            changed = false;
            
            for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
                String lhs = entry.getKey();
                List<String> productions = entry.getValue();
                
                for (String prod : productions) {
                    String[] symbols = prod.split(" ");
                    
                    // Caso especial: produccion epsilon
                    if (symbols.length == 1 && symbols[0].equals("ε")) {
                        if (firstSets.get(lhs).add("ε")) {
                            changed = true;
                        }
                        continue;
                    }
                    
                    // Procesar cada simbolo de la produccion
                    boolean allEpsilon = true;
                    for (String symbol : symbols) {
                        if (grammar.containsKey(symbol)) {
                            // Simbolo es no-terminal
                            Set<String> firstOfSymbol = firstSets.get(symbol);
                            boolean hasEpsilon = false;
                            
                            for (String t : firstOfSymbol) {
                                if (t.equals("ε")) {
                                    hasEpsilon = true;
                                } else {
                                    if (firstSets.get(lhs).add(t)) {
                                        changed = true;
                                    }
                                }
                            }
                            
                            // Si no deriva epsilon, parar aqui
                            if (!hasEpsilon) {
                                allEpsilon = false;
                                break;
                            }
                        } else {
                            // Simbolo es terminal - agregarlo directamente
                            if (firstSets.get(lhs).add(symbol)) {
                                changed = true;
                            }
                            allEpsilon = false;
                            break;
                        }
                    }
                    
                    // Si todos derivan epsilon, agregar epsilon al FIRST
                    if (allEpsilon) {
                        if (firstSets.get(lhs).add("ε")) {
                            changed = true;
                        }
                    }
                }
            }
        }
        
        return firstSets;
    }
    
    // Calcula FIRST de una secuencia de simbolos (para lookaheads)
    public static Set<String> firstOfSequence(List<String> sequence, Map<String, Set<String>> firstSets) {
        Set<String> result = new HashSet<>();
        boolean allEpsilon = true;
        
        for (String sym : sequence) {
            if (!firstSets.containsKey(sym)) {
                // Es terminal - agregarlo y terminar
                result.add(sym);
                allEpsilon = false;
                break;
            }
            
            // Es no terminal - agregar su FIRST (menos epsilon)
            boolean hasEpsilon = false;
            for (String t : firstSets.get(sym)) {
                if (t.equals("ε")) {
                    hasEpsilon = true;
                } else {
                    result.add(t);
                }
            }
            
            // Si no tiene epsilon, no seguir con los demas simbolos
            if (!hasEpsilon) {
                allEpsilon = false;
                break;
            }
        }
        
        // Si toda la secuencia puede derivar epsilon
        if (allEpsilon) {
            result.add("ε");
        }
        
        return result;
    }
}
