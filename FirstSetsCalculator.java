import java.util.*;

public class FirstSetsCalculator {
    public static Map<String, Set<String>> calculateFirstSets(Map<String, List<String>> grammar) {
        Map<String, Set<String>> firstSets = new HashMap<>();
        
        // Inicializar conjuntos FIRST para todos los no terminales
        for (String nt : grammar.keySet()) {
            firstSets.put(nt, new HashSet<>());
        }
        
        boolean changed = true;
        int safety = 0;
        
        while (changed && safety++ < 1000) {
            changed = false;
            
            for (Map.Entry<String, List<String>> entry : grammar.entrySet()) {
                String lhs = entry.getKey();
                List<String> productions = entry.getValue();
                
                for (String prod : productions) {
                    String[] symbols = prod.split(" ");
                    
                    if (symbols.length == 1 && symbols[0].equals("ε")) {
                        if (firstSets.get(lhs).add("ε")) {
                            changed = true;
                        }
                        continue;
                    }
                    
                    boolean allEpsilon = true;
                    for (String symbol : symbols) {
                        if (grammar.containsKey(symbol)) {
                            // Es no terminal
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
                            
                            if (!hasEpsilon) {
                                allEpsilon = false;
                                break;
                            }
                        } else {
                            // Es terminal
                            if (firstSets.get(lhs).add(symbol)) {
                                changed = true;
                            }
                            allEpsilon = false;
                            break;
                        }
                    }
                    
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
    
    public static Set<String> firstOfSequence(List<String> sequence, Map<String, Set<String>> firstSets) {
        Set<String> result = new HashSet<>();
        boolean allEpsilon = true;
        
        for (String sym : sequence) {
            if (!firstSets.containsKey(sym)) {
                // Es terminal
                result.add(sym);
                allEpsilon = false;
                break;
            }
            
            // Es no terminal
            boolean hasEpsilon = false;
            for (String t : firstSets.get(sym)) {
                if (t.equals("ε")) {
                    hasEpsilon = true;
                } else {
                    result.add(t);
                }
            }
            
            if (!hasEpsilon) {
                allEpsilon = false;
                break;
            }
        }
        
        if (allEpsilon) {
            result.add("ε");
        }
        
        return result;
    }
}
