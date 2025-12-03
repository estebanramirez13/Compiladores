import java.util.*;

public class GrammarUtils {
    public static Map<String, Set<String>> calculateFirstSets(Map<String, List<String>> grammar) {
        Map<String, Set<String>> firstSets = new HashMap<>();
        for (String nt : grammar.keySet()) firstSets.put(nt, new HashSet<>());
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String nt : grammar.keySet()) {
                for (String rule : grammar.get(nt)) {
                    String[] symbols = rule.equals("ε") ? new String[]{} : rule.split(" ");
                    for (int i = 0; i < symbols.length; i++) {
                        String sym = symbols[i];
                        if (!grammar.containsKey(sym)) {
                            if (firstSets.get(nt).add(sym)) changed = true;
                            break;
                        } else {
                            for (String t : firstSets.get(sym)) {
                                if (!t.equals("ε") && firstSets.get(nt).add(t)) changed = true;
                            }
                            if (!firstSets.get(sym).contains("ε")) break;
                        }
                        if (i == symbols.length - 1) {
                            if (firstSets.get(nt).add("ε")) changed = true;
                        }
                    }
                }
            }
        }
        return firstSets;
    }

    public static Set<String> firstOfSeq(List<String> sequence, Map<String, Set<String>> firstSets) {
        Set<String> result = new HashSet<>();
        boolean allEpsilon = true;
        for (String sym : sequence) {
            if (!firstSets.containsKey(sym)) {
                result.add(sym);
                allEpsilon = false;
                break;
            }
            boolean hasEpsilon = false;
            for (String t : firstSets.get(sym)) {
                if (t.equals("ε")) hasEpsilon = true;
                else result.add(t);
            }
            if (!hasEpsilon) {
                allEpsilon = false;
                break;
            }
        }
        if (allEpsilon) result.add("ε");
        return result;
    }
}
