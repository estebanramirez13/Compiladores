import java.util.*;

public class ItemLR1 {
    public String lhs;
    public List<String> rhs;
    public int dot;
    public Set<String> lookahead;
    
    public ItemLR1(String lhs, List<String> rhs, int dot, Set<String> lookahead) {
        this.lhs = lhs;
        this.rhs = new ArrayList<>(rhs);
        this.dot = dot;
        this.lookahead = new HashSet<>(lookahead);
    }
    
    public ItemLR1(String lhs, String rhsStr, int dot, Set<String> lookahead) {
        this.lhs = lhs;
        this.rhs = rhsStr.equals("ε") ? new ArrayList<>() : Arrays.asList(rhsStr.split(" "));
        this.dot = dot;
        this.lookahead = new HashSet<>(lookahead);
    }
    
    @Override
    public String toString() {
        String before = String.join(" ", rhs.subList(0, Math.min(dot, rhs.size())));
        String after = dot < rhs.size() ? String.join(" ", rhs.subList(dot, rhs.size())) : "";
        List<String> laList = new ArrayList<>(lookahead);
        Collections.sort(laList);
        String la = String.join("/", laList);
        return String.format("%s -> %s . %s , { %s }", lhs, before, after, la).trim().replaceAll("\\s+", " ");
    }

    public String toCoreString() {
        String before = String.join(" ", rhs.subList(0, Math.min(dot, rhs.size())));
        String after = dot < rhs.size() ? String.join(" ", rhs.subList(dot, rhs.size())) : "";
        return String.format("%s -> %s . %s", lhs, before, after).trim().replaceAll("\\s+", " ");
    }

    public String nextSymbol() {
        if (dot >= rhs.size()) return null;
        return rhs.get(dot);
    }
    
    public ItemLR1 advance() {
        return new ItemLR1(lhs, rhs, dot + 1, lookahead);
    }
}

