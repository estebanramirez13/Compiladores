import java.util.*;

// Implementa el algoritmo LR(1) completo
public class LR1Parser {
    
    // Calcula el closure de un conjunto de items
    // Closure expande items agregando todas las producciones relacionadas
    public static List<ItemLR1> closure(List<ItemLR1> items, Map<String, List<String>> grammar, 
                                         Map<String, Set<String>> firstSets) {
        // Usar Map para combinar items con mismo nucleo
        Map<String, ItemLR1> closureMap = new LinkedHashMap<>();
        
        // Agregar items iniciales (semilla)
        for (ItemLR1 item : items) {
            String core = item.toCoreString();
            if (closureMap.containsKey(core)) {
                // Si ya existe, combinar lookaheads
                ItemLR1 existing = closureMap.get(core);
                existing.lookahead.addAll(item.lookahead);
            } else {
                closureMap.put(core, new ItemLR1(item.lhs, item.rhs, item.dot, item.lookahead));
            }
        }
        
        boolean changed = true;
        int safety = 0;  // contador para evitar loops infinitos
        
        while (changed && safety++ < 2000) {
            changed = false;
            List<ItemLR1> currentItems = new ArrayList<>(closureMap.values());
            
            for (ItemLR1 item : currentItems) {
                String B = item.nextSymbol();
                
                // Si hay un no-terminal despues del punto
                if (B != null && grammar.containsKey(B)) {
                    // Calcular beta (simbolos despues de B)
                    List<String> beta = item.rhs.subList(Math.min(item.dot + 1, item.rhs.size()), item.rhs.size());
                    Set<String> fBeta = FirstSetsCalculator.firstOfSequence(beta, firstSets);
                    Set<String> newLookaheads = new HashSet<>();
                    
                    // Agregar FIRST(beta) sin epsilon
                    for (String t : fBeta) {
                        if (!t.equals("ε")) {
                            newLookaheads.add(t);
                        }
                    }
                    
                    // Si beta puede derivar epsilon, agregar lookahead original
                    if (fBeta.contains("ε")) {
                        newLookaheads.addAll(item.lookahead);
                    }
                    
                    if (newLookaheads.isEmpty()) continue;
                    
                    // Para cada produccion B -> gamma
                    for (String rule : grammar.get(B)) {
                        List<String> rhs = rule.equals("ε") ? new ArrayList<>() : Arrays.asList(rule.split(" "));
                        ItemLR1 tempItem = new ItemLR1(B, rhs, 0, new HashSet<>());
                        String coreStr = tempItem.toCoreString();
                        
                        ItemLR1 existingItem = closureMap.get(coreStr);
                        
                        if (existingItem == null) {
                            // Crear nuevo item
                            closureMap.put(coreStr, new ItemLR1(B, rhs, 0, newLookaheads));
                            changed = true;
                        } else {
                            // Combinar lookaheads con item existente
                            int sizeBefore = existingItem.lookahead.size();
                            existingItem.lookahead.addAll(newLookaheads);
                            if (existingItem.lookahead.size() > sizeBefore) {
                                changed = true;
                            }
                        }
                    }
                }
            }
        }
        
        return new ArrayList<>(closureMap.values());
    }
    
    // Funcion GOTO - calcula el siguiente estado al leer un simbolo
    public static List<ItemLR1> goTo(List<ItemLR1> items, String symbol, Map<String, List<String>> grammar,
                                      Map<String, Set<String>> firstSets) {
        List<ItemLR1> nextItems = new ArrayList<>();
        
        // Avanzar el punto en todos los items donde el siguiente simbolo coincida
        for (ItemLR1 item : items) {
            if (symbol.equals(item.nextSymbol())) {
                nextItems.add(item.advance());
            }
        }
        
        if (nextItems.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Aplicar closure al resultado
        return closure(nextItems, grammar, firstSets);
    }
    
    // Clase para encapsular los resultados del analisis LR(1)
    public static class LR1Result {
        public List<List<ItemLR1>> states;           // Coleccion canonica
        public Map<String, String> transitions;      // Transiciones entre estados
        public Map<Integer, Map<String, String>> table;  // Tabla de parsing
        public List<String> terminals;
        public List<String> nonTerminals;
        
        // Formatea la coleccion canonica para mostrar
        public String formatCanonicalCollection() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < states.size(); i++) {
                sb.append("I").append(i).append(" = {\n");
                for (ItemLR1 item : states.get(i)) {
                    sb.append("  ").append(item.toString()).append("\n");
                }
                sb.append("}\n\n");
            }
            return sb.toString();
        }
        
        // Formatea las transiciones de forma legible
        public String formatTransitions() {
            List<String> transList = new ArrayList<>();
            for (Map.Entry<String, String> entry : transitions.entrySet()) {
                String[] parts = entry.getKey().split("-");
                transList.add(String.format("I%s --(%s)--> I%s", parts[0], parts[1], entry.getValue()));
            }
            Collections.sort(transList);
            return String.join("\n", transList);
        }
        
        // Genera tabla HTML con estilos para la interfaz
        public String formatTable() {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head><style>");
            sb.append("body { font-family: 'Monospaced'; font-size: 11px; }");
            sb.append("table { border-collapse: collapse; width: 100%; }");
            sb.append("th, td { border: 1px solid #cbd5e1; padding: 8px; text-align: center; }");
            sb.append("th { background-color: #e2e8f0; color: #334155; font-weight: bold; }");
            sb.append(".shift { color: #059669; font-weight: bold; }");
            sb.append(".reduce { color: #d97706; font-weight: bold; }");
            sb.append(".accept { background-color: #dcfce7; color: #15803d; font-weight: bold; }");
            sb.append(".goto { color: #2563eb; }");
            sb.append("</style></head><body>");
            sb.append("<table>");
            
            // Encabezados de columnas
            sb.append("<tr><th>Estado</th>");
            for (String t : terminals) {
                sb.append("<th>").append(t).append("</th>");
            }
            for (String nt : nonTerminals) {
                sb.append("<th>").append(nt).append("</th>");
            }
            sb.append("</tr>");
            
            // Filas de la tabla (una por estado)
            for (int i = 0; i < states.size(); i++) {
                sb.append("<tr><td><b>").append(i).append("</b></td>");
                
                Map<String, String> row = table.getOrDefault(i, new HashMap<>());
                
                // Acciones para terminales
                for (String t : terminals) {
                    String action = row.getOrDefault(t, "");
                    String cssClass = "";
                    String display = action;
                    
                    if (action.startsWith("d")) {
                        cssClass = "shift";
                        display = "d" + action.substring(1);
                    } else if (action.startsWith("r:")) {
                        cssClass = "reduce";
                        display = "r" + action.substring(2).split("->")[0].trim();
                    } else if (action.equals("Aceptar")) {
                        cssClass = "accept";
                        display = "acept";
                    }
                    
                    sb.append("<td class='").append(cssClass).append("'>").append(display).append("</td>");
                }
                
                // Gotos para no-terminales
                for (String nt : nonTerminals) {
                    String action = row.getOrDefault(nt, "");
                    String display = action.startsWith("ir_a ") ? action.substring(5) : action;
                    sb.append("<td class='goto'>").append(display).append("</td>");
                }
                
                sb.append("</tr>");
            }
            
            sb.append("</table></body></html>");
            return sb.toString();
        }
    }
    
    // Construye la tabla LR(1) completa
    public static LR1Result buildLR1Table(Map<String, List<String>> grammar, String startSymbol) {
        // Gramatica aumentada con S' -> S
        String augmentedStart = startSymbol + "'";
        Map<String, List<String>> newGrammar = new LinkedHashMap<>(grammar);
        if (!newGrammar.containsKey(augmentedStart)) {
            newGrammar.put(augmentedStart, Arrays.asList(startSymbol));
        }
        
        Map<String, Set<String>> firstSets = FirstSetsCalculator.calculateFirstSets(newGrammar);
        
        // Crear estado inicial I0 con [S' -> .S, $]
        Set<String> initialLookahead = new HashSet<>();
        initialLookahead.add("$");
        ItemLR1 startItem = new ItemLR1(augmentedStart, Arrays.asList(startSymbol), 0, initialLookahead);
        List<ItemLR1> I0 = closure(Arrays.asList(startItem), newGrammar, firstSets);
        
        List<List<ItemLR1>> states = new ArrayList<>();
        states.add(I0);
        
        Map<String, String> transitions = new LinkedHashMap<>();
        
        // Generar todos los estados usando goto
        boolean changed = true;
        int safety = 0;
        
        while (changed && safety++ < 500) {
            changed = false;
            int statesCount = states.size();
            
            for (int i = 0; i < statesCount; i++) {
                List<ItemLR1> state = states.get(i);
                Set<String> symbols = new HashSet<>();
                
                // Recolectar todos los simbolos que aparecen despues del punto
                for (ItemLR1 it : state) {
                    String s = it.nextSymbol();
                    if (s != null) symbols.add(s);
                }
                
                // Calcular goto para cada simbolo
                for (String sym : symbols) {
                    List<ItemLR1> targetState = goTo(state, sym, newGrammar, firstSets);
                    if (targetState.isEmpty()) continue;
                    
                    // Verificar si este estado ya existe
                    int targetIdx = -1;
                    String targetSign = getStateSignature(targetState);
                    
                    for (int k = 0; k < states.size(); k++) {
                        String existingSign = getStateSignature(states.get(k));
                        if (existingSign.equals(targetSign)) {
                            targetIdx = k;
                            break;
                        }
                    }
                    
                    // Agregar nuevo estado si no existe
                    if (targetIdx == -1) {
                        states.add(targetState);
                        targetIdx = states.size() - 1;
                        changed = true;
                    }
                    
                    // Guardar transicion
                    transitions.put(i + "-" + sym, String.valueOf(targetIdx));
                }
            }
        }
        
        // Identificar terminales y no-terminales de la gramatica
        Set<String> terminals = new HashSet<>();
        Set<String> nonTerminals = new HashSet<>();
        
        for (Map.Entry<String, List<String>> entry : newGrammar.entrySet()) {
            String nt = entry.getKey();
            if (!nt.equals(augmentedStart)) {
                nonTerminals.add(nt);
            }
            
            for (String rule : entry.getValue()) {
                String[] symbols = rule.split(" ");
                for (String s : symbols) {
                    if (!s.equals("ε") && !newGrammar.containsKey(s)) {
                        terminals.add(s);
                    }
                }
            }
        }
        terminals.add("$");  // simbolo de fin
        
        // Inicializar tabla vacia
        Map<Integer, Map<String, String>> table = new HashMap<>();
        for (int i = 0; i < states.size(); i++) {
            table.put(i, new HashMap<>());
        }
        
        // Agregar acciones shift y goto basadas en las transiciones
        for (Map.Entry<String, String> entry : transitions.entrySet()) {
            String[] parts = entry.getKey().split("-");
            int src = Integer.parseInt(parts[0]);
            String sym = parts[1];
            int dst = Integer.parseInt(entry.getValue());
            
            if (nonTerminals.contains(sym)) {
                // Es no-terminal -> GOTO
                table.get(src).put(sym, "ir_a " + dst);
            } else {
                // Es terminal -> SHIFT
                table.get(src).put(sym, "d" + dst);
            }
        }
        
        // Agregar acciones reduce y accept para items completos
        for (int i = 0; i < states.size(); i++) {
            for (ItemLR1 item : states.get(i)) {
                // Item completo (punto al final)
                if (item.dot == item.rhs.size()) {
                    if (item.lhs.equals(augmentedStart)) {
                        // Estado de aceptacion
                        if (item.lookahead.contains("$")) {
                            table.get(i).put("$", "Aceptar");
                        }
                    } else {
                        // Reduccion normal
                        String rhsStr = item.rhs.isEmpty() ? "ε" : String.join(" ", item.rhs);
                        String prodStr = item.lhs + " -> " + rhsStr;
                        
                        for (String la : item.lookahead) {
                            // Solo agregar si no hay conflicto
                            if (!table.get(i).containsKey(la)) {
                                table.get(i).put(la, "r: " + prodStr);
                            }
                        }
                    }
                }
            }
        }
        
        // Empaquetar resultado
        LR1Result result = new LR1Result();
        result.states = states;
        result.transitions = transitions;
        result.table = table;
        
        List<String> terminalsList = new ArrayList<>(terminals);
        List<String> nonTerminalsList = new ArrayList<>(nonTerminals);
        result.terminals = terminalsList;
        result.nonTerminals = nonTerminalsList;
        
        return result;
    }
    
    // Genera firma unica para comparar estados (con lookaheads)
    private static String getStateSignature(List<ItemLR1> state) {
        List<String> signatures = new ArrayList<>();
        for (ItemLR1 item : state) {
            signatures.add(item.toString());
        }
        Collections.sort(signatures);
        return String.join("|", signatures);
    }
    
    // Genera codigo Mermaid para visualizar el grafo del automata
    public static String generateMermaidGraph(LR1Result result) {
        StringBuilder sb = new StringBuilder();
        sb.append("```mermaid\n");
        sb.append("graph TD\n");
        
        // Definir nodos con sus items
        for (int i = 0; i < result.states.size(); i++) {
            sb.append("    I").append(i).append("[\"<b>I").append(i).append("</b>");
            
            List<ItemLR1> items = result.states.get(i);
            int count = 0;
            for (ItemLR1 item : items) {
                if (count++ >= 5) {  // limitar items mostrados
                    sb.append("<br/>...");
                    break;
                }
                String itemStr = item.toString()
                    .replace("->", "→")
                    .replace(".", "•");
                sb.append("<br/>").append(itemStr);
            }
            
            sb.append("\"]\n");
        }
        
        sb.append("\n");
        
        // Definir transiciones (flechas)
        for (Map.Entry<String, String> entry : result.transitions.entrySet()) {
            String[] parts = entry.getKey().split("-");
            String src = parts[0];
            String symbol = parts[1];
            String dst = entry.getValue();
            
            sb.append("    I").append(src)
              .append(" -->|").append(symbol).append("| I")
              .append(dst).append("\n");
        }
        
        sb.append("\n");
        
        // Aplicar estilos segun tipo de estado
        sb.append("    style I0 fill:#e3f2fd\n");  // estado inicial
        
        for (int i = 0; i < result.states.size(); i++) {
            boolean isAccept = false;
            boolean hasComplete = false;
            
            for (ItemLR1 item : result.states.get(i)) {
                if (item.lhs.endsWith("'") && item.dot == item.rhs.size()) {
                    isAccept = true;
                    break;
                }
                if (item.dot == item.rhs.size()) {
                    hasComplete = true;
                }
            }
            
            if (isAccept) {
                sb.append("    style I").append(i).append(" fill:#c8e6c9\n");  // verde
            } else if (hasComplete) {
                sb.append("    style I").append(i).append(" fill:#ffccbc\n");  // naranja
            } else if (i > 0) {
                sb.append("    style I").append(i).append(" fill:#fff9c4\n");  // amarillo
            }
        }
        
        sb.append("```\n");
        return sb.toString();
    }
}
