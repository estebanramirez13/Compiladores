import java.util.*;

public class LR1Parser {
    public static LR1Result buildLR1Table(Map<String, List<String>> grammar, String startSymbol) {
        String augmentedStart = startSymbol.endsWith("'") ? startSymbol : startSymbol + "'";
        Map<String, List<String>> newGrammar = new LinkedHashMap<>(grammar);
        if (!newGrammar.containsKey(augmentedStart)) newGrammar.put(augmentedStart, Arrays.asList(startSymbol.replace("'", "")));
        Map<String, Set<String>> firstSets = GrammarUtils.calculateFirstSets(newGrammar);
        ItemLR1 startItem = new ItemLR1(augmentedStart, Arrays.asList(startSymbol.replace("'", "")), 0, new HashSet<>(Arrays.asList("$")));
        List<ItemLR1> I0 = closureLR1(Arrays.asList(startItem), newGrammar, firstSets);
        List<List<ItemLR1>> states = new ArrayList<>();
        states.add(I0);
        Map<String, Integer> transitions = new HashMap<>();
        boolean changed = true;
        int safety = 0;
        while (changed && safety++ < 500) {
            changed = false;
            int nStates = states.size();
            for (int i = 0; i < nStates; i++) {
                List<ItemLR1> state = states.get(i);
                Set<String> symbols = new HashSet<>();
                for (ItemLR1 it : state) {
                    String s = it.nextSymbol();
                    if (s != null) symbols.add(s);
                }
                for (String sym : symbols) {
                    List<ItemLR1> targetState = goToLR1(state, sym, newGrammar, firstSets);
                    if (targetState.isEmpty()) continue;
                    String targetSign = getStateSignature(targetState);
                    int targetIdx = -1;
                    for (int k = 0; k < states.size(); k++) {
                        if (getStateSignature(states.get(k)).equals(targetSign)) {
                            targetIdx = k;
                            break;
                        }
                    }
                    if (targetIdx == -1) {
                        states.add(targetState);
                        targetIdx = states.size() - 1;
                        changed = true;
                    }
                    transitions.put(i + "-" + sym, targetIdx);
                }
            }
        }
        Map<Integer, Map<String, String>> table = new HashMap<>();
        Set<String> terminals = new HashSet<>();
        Set<String> nonTerminals = new HashSet<>();
        for (String nt : newGrammar.keySet()) {
            if (!nt.equals(augmentedStart)) nonTerminals.add(nt);
            for (String r : newGrammar.get(nt)) {
                for (String s : r.split(" ")) {
                    if (!s.equals("ε") && !newGrammar.containsKey(s)) terminals.add(s);
                }
            }
        }
        terminals.add("$");
        for (int i = 0; i < states.size(); i++) table.put(i, new HashMap<>());
        for (int i = 0; i < states.size(); i++) {
            for (String key : transitions.keySet()) {
                String[] parts = key.split("-");
                int src = Integer.parseInt(parts[0]);
                String sym = parts[1];
                int dst = transitions.get(key);
                if (src == i) {
                    if (nonTerminals.contains(sym)) table.get(i).put(sym, "ir_a " + dst);
                    else table.get(i).put(sym, "d" + dst);
                }
            }
            for (ItemLR1 item : states.get(i)) {
                if (item.dot == item.rhs.size()) {
                    if (item.lhs.equals(augmentedStart)) {
                        if (item.lookahead.contains("$")) table.get(i).put("$", "Aceptar");
                    } else {
                        for (String la : item.lookahead) {
                            String prodStr = item.lhs + " -> " + (item.rhs.isEmpty() ? "ε" : String.join(" ", item.rhs));
                            if (!table.get(i).containsKey(la)) table.get(i).put(la, "r: " + prodStr);
                        }
                    }
                }
            }
        }
        List<String> transitionList = new ArrayList<>();
        for (String key : transitions.keySet()) {
            String[] parts = key.split("-");
            int src = Integer.parseInt(parts[0]);
            String sym = parts[1];
            int dst = transitions.get(key);
            transitionList.add("I" + src + " --(" + sym + ")--> I" + dst);
        }
        return new LR1Result(states, table, transitionList, terminals, nonTerminals);
    }

    private static String getStateSignature(List<ItemLR1> state) {
        List<String> items = new ArrayList<>();
        for (ItemLR1 it : state) items.add(it.toString());
        Collections.sort(items);
        return String.join("|", items);
    }

    public static List<ItemLR1> closureLR1(List<ItemLR1> items, Map<String, List<String>> grammar, Map<String, Set<String>> firstSets) {
        Map<String, ItemLR1> closureMap = new HashMap<>();
        for (ItemLR1 item : items) {
            String core = item.toCoreString();
            if (closureMap.containsKey(core)) {
                closureMap.get(core).lookahead.addAll(item.lookahead);
            } else {
                closureMap.put(core, new ItemLR1(item.lhs, item.rhs, item.dot, item.lookahead));
            }
        }
        boolean changed = true;
        int safety = 0;
        while (changed && safety++ < 2000) {
            changed = false;
            List<ItemLR1> currentItems = new ArrayList<>(closureMap.values());
            for (ItemLR1 item : currentItems) {
                String B = item.nextSymbol();
                if (B != null && grammar.containsKey(B)) {
                    List<String> beta = item.rhs.subList(item.dot + 1, item.rhs.size());
                    Set<String> fBeta = GrammarUtils.firstOfSeq(beta, firstSets);
                    Set<String> newLookaheads = new HashSet<>();
                    for (String t : fBeta) if (!t.equals("ε")) newLookaheads.add(t);
                    if (fBeta.contains("ε")) newLookaheads.addAll(item.lookahead);
                    if (newLookaheads.isEmpty()) continue;
                    for (String rule : grammar.get(B)) {
                        List<String> rhs = rule.equals("ε") ? new ArrayList<>() : Arrays.asList(rule.split(" "));
                        ItemLR1 tempItem = new ItemLR1(B, rhs, 0, new HashSet<>());
                        String coreStr = tempItem.toCoreString();
                        ItemLR1 existingItem = closureMap.get(coreStr);
                        if (existingItem == null) {
                            closureMap.put(coreStr, new ItemLR1(B, rhs, 0, newLookaheads));
                            changed = true;
                        } else {
                            int sizeBefore = existingItem.lookahead.size();
                            existingItem.lookahead.addAll(newLookaheads);
                            if (existingItem.lookahead.size() > sizeBefore) changed = true;
                        }
                    }
                }
            }
        }
        return new ArrayList<>(closureMap.values());
    }

    public static List<ItemLR1> goToLR1(List<ItemLR1> items, String symbol, Map<String, List<String>> grammar, Map<String, Set<String>> firstSets) {
        List<ItemLR1> nextItems = new ArrayList<>();
        for (ItemLR1 item : items) {
            if (symbol.equals(item.nextSymbol())) {
                nextItems.add(new ItemLR1(item.lhs, item.rhs, item.dot + 1, item.lookahead));
            }
        }
        return nextItems.isEmpty() ? new ArrayList<>() : closureLR1(nextItems, grammar, firstSets);
    }
}
