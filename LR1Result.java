import java.util.*;

public class LR1Result {
    public List<List<ItemLR1>> canonicalCollection;
    public Map<Integer, Map<String, String>> table;
    public List<String> transitions;
    public Set<String> terminals;
    public Set<String> nonTerminals;

    public LR1Result(List<List<ItemLR1>> cc, Map<Integer, Map<String, String>> t, List<String> tr, Set<String> te, Set<String> nt) {
        canonicalCollection = cc;
        table = t;
        transitions = tr;
        terminals = te;
        nonTerminals = nt;
    }

    public String formatCanonicalCollection() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < canonicalCollection.size(); i++) {
            sb.append("I").append(i).append(":\n");
            for (ItemLR1 item : canonicalCollection.get(i)) {
                sb.append("  ").append(item.toString()).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String formatTable() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < table.size(); i++) {
            sb.append("Estado ").append(i).append(": ");
            for (String t : terminals) {
                String act = table.get(i).getOrDefault(t, "");
                if (!act.isEmpty()) sb.append(t).append("=").append(act).append(" ");
            }
            for (String nt : nonTerminals) {
                String act = table.get(i).getOrDefault(nt, "");
                if (!act.isEmpty()) sb.append(nt).append("=").append(act).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
