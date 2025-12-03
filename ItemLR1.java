import java.util.*;

public class ItemLR1 {
    public String lhs;
    public List<String> rhs;
    public int dot;
    public Set<String> lookahead;

    public ItemLR1(String lhs, List<String> rhs, int dot, Set<String> lookahead) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.dot = dot;
        this.lookahead = new HashSet<>(lookahead);
    }

    public String toString() {
        String before = String.join(" ", rhs.subList(0, dot));
        String after = String.join(" ", rhs.subList(dot, rhs.size()));
        String la = String.join("/", new TreeSet<>(lookahead));
        return lhs + " -> " + before + " . " + after + " , { " + la + " }";
    }

    public String toCoreString() {
        String before = String.join(" ", rhs.subList(0, dot));
        String after = String.join(" ", rhs.subList(dot, rhs.size()));
        return lhs + " -> " + before + " . " + after;
    }

    public String nextSymbol() {
        if (dot >= rhs.size()) return null;
        return rhs.get(dot);
    }
}
