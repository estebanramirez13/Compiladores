import java.util.*;

// Representa un item LR(1): [A -> α.β, lookahead]
public class ItemLR1 {
    public String lhs;              // Lado izquierdo de la produccion
    public List<String> rhs;        // Lado derecho (simbolos)
    public int dot;                 // Posicion del punto en la regla
    public Set<String> lookahead;   // Conjunto de lookaheads
    
    // Constructor principal - copia las colecciones para evitar problemas
    public ItemLR1(String lhs, List<String> rhs, int dot, Set<String> lookahead) {
        this.lhs = lhs;
        this.rhs = new ArrayList<>(rhs);
        this.dot = dot;
        this.lookahead = new HashSet<>(lookahead);
    }
    
    // Constructor alternativo que acepta String para rhs
    public ItemLR1(String lhs, String rhsStr, int dot, Set<String> lookahead) {
        this.lhs = lhs;
        // Manejo especial para epsilon
        this.rhs = rhsStr.equals("ε") ? new ArrayList<>() : Arrays.asList(rhsStr.split(" "));
        this.dot = dot;
        this.lookahead = new HashSet<>(lookahead);
    }
    
    // Formato para mostrar: [A -> α.β, {lookaheads}]
    @Override
    public String toString() {
        String before = String.join(" ", rhs.subList(0, Math.min(dot, rhs.size())));
        String after = dot < rhs.size() ? String.join(" ", rhs.subList(dot, rhs.size())) : "";
        // Ordenar lookaheads para que se vea mas consistente
        List<String> laList = new ArrayList<>(lookahead);
        Collections.sort(laList);
        String la = String.join("/", laList);
        return String.format("%s -> %s . %s , { %s }", lhs, before, after, la).trim().replaceAll("\\s+", " ");
    }

    // Version sin lookaheads - para comparar nucleos de items
    public String toCoreString() {
        String before = String.join(" ", rhs.subList(0, Math.min(dot, rhs.size())));
        String after = dot < rhs.size() ? String.join(" ", rhs.subList(dot, rhs.size())) : "";
        return String.format("%s -> %s . %s", lhs, before, after).trim().replaceAll("\\s+", " ");
    }

    // Devuelve el simbolo inmediatamente despues del punto
    public String nextSymbol() {
        if (dot >= rhs.size()) return null;
        return rhs.get(dot);
    }
    
    // Crea un nuevo item con el punto avanzado una posicion
    public ItemLR1 advance() {
        return new ItemLR1(lhs, rhs, dot + 1, lookahead);
    }
}

