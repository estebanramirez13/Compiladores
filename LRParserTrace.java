import java.util.*;

public class LRParserTrace {
    
    public static class ParseStep {
        public int step;
        public String stack;
        public String input;
        public String action;
        
        public ParseStep(int step, String stack, String input, String action) {
            this.step = step;
            this.stack = stack;
            this.input = input;
            this.action = action;
        }
        
        @Override
        public String toString() {
            return String.format("%-6d %-25s %-25s %s", step, stack, input, action);
        }
    }
    
    public static class ParseResult {
        public List<ParseStep> trace;
        public boolean success;
        public String message;
        public List<String> decisionLog;
        
        public ParseResult(List<ParseStep> trace, boolean success, String message, List<String> decisionLog) {
            this.trace = trace;
            this.success = success;
            this.message = message;
            this.decisionLog = decisionLog;
        }
        
        public String formatTrace() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-6s %-25s %-25s %s\n", "Paso", "Pila", "Entrada", "Acción"));
            sb.append("-".repeat(80)).append("\n");
            for (ParseStep step : trace) {
                sb.append(step.toString()).append("\n");
            }
            sb.append("\n").append(message).append("\n");
            return sb.toString();
        }
    }
    
    public static ParseResult runParser(Map<Integer, Map<String, String>> table, 
                                         Map<String, List<String>> grammar,
                                         String inputStr) {
        Stack<Integer> stack = new Stack<>();
        Stack<String> symbolsStack = new Stack<>();
        stack.push(0);
        symbolsStack.push("$");
        
        String[] tokens = inputStr.trim().split("\\s+");
        List<String> input = new ArrayList<>(Arrays.asList(tokens));
        input.add("$");
        
        int cursor = 0;
        List<ParseStep> trace = new ArrayList<>();
        List<String> decisionLog = new ArrayList<>();
        boolean success = false;
        String message = "";
        
        int steps = 0;
        final int MAX_STEPS = 500;
        
        while (steps < MAX_STEPS) {
            steps++;
            int state = stack.peek();
            String token = input.get(cursor);
            
            Map<String, String> row = table.get(state);
            String action = (row != null) ? row.get(token) : null;
            
            // Registrar paso
            String stackStr = String.join("", symbolsStack);
            String inputStr2 = String.join("", input.subList(cursor, input.size()));
            String actionStr = formatAction(action);
            
            trace.add(new ParseStep(steps, stackStr, inputStr2, actionStr));
            
            if (action == null) {
                message = String.format("Error de sintaxis: El estado %d no espera el símbolo '%s'.", state, token);
                decisionLog.add(String.format("[Paso %d] Estado %d vs Entrada '%s' -> Error. No hay acción definida.", steps, state, token));
                break;
            }
            
            if (action.startsWith("d")) {
                // Desplazar
                int nextState = Integer.parseInt(action.substring(1));
                decisionLog.add(String.format("[Paso %d] Estado %d vs Entrada '%s' -> Desplazar a estado %d.", steps, state, token, nextState));
                stack.push(nextState);
                symbolsStack.push(token);
                cursor++;
                
            } else if (action.startsWith("r:")) {
                // Reducir
                String prodStr = action.substring(3).trim();
                String[] parts = prodStr.split("->");
                String lhs = parts[0].trim();
                String rhsStr = parts[1].trim();
                int len = rhsStr.equals("ε") ? 0 : rhsStr.split(" ").length;
                
                decisionLog.add(String.format("[Paso %d] Estado %d vs Entrada '%s' -> Reducir por %s (%d elementos).", steps, state, token, prodStr, len));
                
                // Pop len elementos
                for (int i = 0; i < len; i++) {
                    stack.pop();
                    symbolsStack.pop();
                }
                
                int topState = stack.peek();
                Map<String, String> topRow = table.get(topState);
                String gotoAction = (topRow != null) ? topRow.get(lhs) : null;
                
                if (gotoAction == null || !gotoAction.startsWith("ir_a")) {
                    message = String.format("Error fatal: No hay transición GOTO definida para [%d, %s] tras reducción.", topState, lhs);
                    break;
                }
                
                int nextState = Integer.parseInt(gotoAction.substring(5).trim());
                stack.push(nextState);
                symbolsStack.push(lhs);
                decisionLog.add(String.format("   -> GOTO(Estado %d, %s) = Estado %d.", topState, lhs, nextState));
                
            } else if (action.equals("Aceptar")) {
                decisionLog.add(String.format("[Paso %d] Estado %d vs Entrada '%s' -> ACEPTAR.", steps, state, token));
                success = true;
                message = "Cadena aceptada correctamente.";
                break;
            }
        }
        
        if (steps >= MAX_STEPS) {
            message = "Análisis detenido: Se excedió el límite de pasos (posible ciclo infinito).";
        }
        
        return new ParseResult(trace, success, message, decisionLog);
    }
    
    private static String formatAction(String action) {
        if (action == null) return "Error";
        if (action.startsWith("d")) return "desplazar";
        if (action.startsWith("r:")) return "reducir " + action.substring(3);
        if (action.equals("Aceptar")) return "aceptar";
        return action;
    }
}
