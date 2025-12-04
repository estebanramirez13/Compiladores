import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class LR1GraphPanel extends JPanel {
    private LR1Parser.LR1Result result;
    private Map<Integer, Point> nodePositions;
    private static final int NODE_WIDTH = 200;
    private static final int NODE_HEIGHT = 80;
    private static final Color NODE_COLOR = new Color(255, 255, 255);
    private static final Color NODE_BORDER = new Color(51, 51, 51);
    private static final Color EDGE_COLOR = new Color(100, 116, 139);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private int canvasWidth = 1200;
    private int canvasHeight = 800;
    
    public LR1GraphPanel() {
        setBackground(Color.WHITE);
        nodePositions = new HashMap<>();
    }
    
    public void setResult(LR1Parser.LR1Result result) {
        this.result = result;
        calculateNodePositions();
        setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        revalidate();
        repaint();
    }
    
    private void calculateNodePositions() {
        if (result == null || result.states.isEmpty()) return;
        
        nodePositions.clear();
        int numStates = result.states.size();
        
        // Layout mejorado: organizar en niveles según profundidad desde I0
        Map<Integer, Integer> levels = calculateNodeLevels();
        Map<Integer, List<Integer>> nodesByLevel = new HashMap<>();
        
        // Agrupar nodos por nivel
        for (int i = 0; i < numStates; i++) {
            int level = levels.getOrDefault(i, 0);
            nodesByLevel.computeIfAbsent(level, k -> new ArrayList<>()).add(i);
        }
        
        int maxLevel = nodesByLevel.keySet().stream().max(Integer::compare).orElse(0);
        int xSpacing = NODE_WIDTH + 120;
        int ySpacing = NODE_HEIGHT + 100;
        
        canvasWidth = Math.max(1400, (maxLevel + 1) * xSpacing + 200);
        
        // Calcular altura necesaria según el nivel con más nodos
        int maxNodesInLevel = nodesByLevel.values().stream()
            .mapToInt(List::size)
            .max()
            .orElse(1);
        canvasHeight = Math.max(900, maxNodesInLevel * ySpacing + 200);
        
        // Posicionar nodos
        for (Map.Entry<Integer, List<Integer>> entry : nodesByLevel.entrySet()) {
            int level = entry.getKey();
            List<Integer> nodesInLevel = entry.getValue();
            
            int x = 100 + level * xSpacing;
            int totalHeight = nodesInLevel.size() * ySpacing;
            int startY = (canvasHeight - totalHeight) / 2;
            
            for (int i = 0; i < nodesInLevel.size(); i++) {
                int nodeId = nodesInLevel.get(i);
                int y = startY + i * ySpacing;
                nodePositions.put(nodeId, new Point(x, y));
            }
        }
    }
    
    private Map<Integer, Integer> calculateNodeLevels() {
        Map<Integer, Integer> levels = new HashMap<>();
        levels.put(0, 0); // I0 está en el nivel 0
        
        boolean changed = true;
        int iterations = 0;
        int maxIterations = 100; // Límite de seguridad
        
        while (changed && iterations++ < maxIterations) {
            changed = false;
            for (Map.Entry<String, String> entry : result.transitions.entrySet()) {
                String[] parts = entry.getKey().split("-");
                int from = Integer.parseInt(parts[0]);
                int to = Integer.parseInt(entry.getValue());
                
                // Ignorar auto-loops
                if (from == to) continue;
                
                int fromLevel = levels.getOrDefault(from, 0);
                int currentToLevel = levels.getOrDefault(to, Integer.MAX_VALUE);
                
                // El nodo destino debe estar al menos un nivel después del origen
                int newToLevel = fromLevel + 1;
                
                if (newToLevel < currentToLevel) {
                    levels.put(to, newToLevel);
                    changed = true;
                }
            }
        }
        
        return levels;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (result == null || nodePositions.isEmpty()) {
            g.setColor(Color.GRAY);
            g.drawString("Construye la tabla LR(1) para ver el grafo", 250, 300);
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Dibujar aristas primero
        drawEdges(g2d);
        
        // Dibujar nodos encima
        drawNodes(g2d);
    }
    
    private void drawEdges(Graphics2D g2d) {
        g2d.setColor(EDGE_COLOR);
        g2d.setStroke(new BasicStroke(2));
        
        for (Map.Entry<String, String> entry : result.transitions.entrySet()) {
            String[] parts = entry.getKey().split("-");
            int from = Integer.parseInt(parts[0]);
            String symbol = parts[1];
            int to = Integer.parseInt(entry.getValue());
            
            Point fromPos = nodePositions.get(from);
            Point toPos = nodePositions.get(to);
            
            if (fromPos == null || toPos == null) continue;
            
            // Calcular centros de los nodos
            int fromCenterX = fromPos.x + NODE_WIDTH / 2;
            int fromCenterY = fromPos.y + NODE_HEIGHT / 2;
            int toCenterX = toPos.x + NODE_WIDTH / 2;
            int toCenterY = toPos.y + NODE_HEIGHT / 2;
            
            // Auto-loop (mismo nodo)
            if (from == to) {
                int loopSize = 40;
                int loopX = fromPos.x + NODE_WIDTH;
                int loopY = fromPos.y + NODE_HEIGHT / 2 - loopSize / 2;
                
                g2d.drawArc(loopX - 10, loopY, loopSize, loopSize, -45, 270);
                
                // Etiqueta
                g2d.setColor(new Color(37, 99, 235));
                g2d.setFont(new Font("JetBrains Mono", Font.BOLD, 12));
                g2d.drawString(symbol, loopX + loopSize - 5, loopY - 5);
                g2d.setColor(EDGE_COLOR);
                continue;
            }
            
            // Calcular ángulo entre nodos
            double angle = Math.atan2(toCenterY - fromCenterY, toCenterX - fromCenterX);
            
            // Calcular puntos de intersección en los bordes de los rectángulos
            int x1, y1, x2, y2;
            
            // Punto de salida en el rectángulo FROM
            if (Math.abs(Math.cos(angle)) > Math.abs(Math.sin(angle))) {
                // Salir por lado horizontal
                if (Math.cos(angle) > 0) { // Derecha
                    x1 = fromPos.x + NODE_WIDTH;
                    y1 = fromCenterY;
                } else { // Izquierda
                    x1 = fromPos.x;
                    y1 = fromCenterY;
                }
            } else {
                // Salir por lado vertical
                if (Math.sin(angle) > 0) { // Abajo
                    x1 = fromCenterX;
                    y1 = fromPos.y + NODE_HEIGHT;
                } else { // Arriba
                    x1 = fromCenterX;
                    y1 = fromPos.y;
                }
            }
            
            // Punto de llegada en el rectángulo TO
            double reverseAngle = angle + Math.PI;
            if (Math.abs(Math.cos(reverseAngle)) > Math.abs(Math.sin(reverseAngle))) {
                // Llegar por lado horizontal
                if (Math.cos(reverseAngle) > 0) { // Derecha
                    x2 = toPos.x + NODE_WIDTH;
                    y2 = toCenterY;
                } else { // Izquierda
                    x2 = toPos.x;
                    y2 = toCenterY;
                }
            } else {
                // Llegar por lado vertical
                if (Math.sin(reverseAngle) > 0) { // Abajo
                    x2 = toCenterX;
                    y2 = toPos.y + NODE_HEIGHT;
                } else { // Arriba
                    x2 = toCenterX;
                    y2 = toPos.y;
                }
            }
            
            // Dibujar línea
            g2d.drawLine(x1, y1, x2, y2);
            
            // Dibujar flecha
            drawArrow(g2d, x1, y1, x2, y2);
            
            // Dibujar etiqueta del símbolo
            g2d.setColor(new Color(37, 99, 235));
            g2d.setFont(new Font("JetBrains Mono", Font.BOLD, 12));
            int labelX = (x1 + x2) / 2 + 5;
            int labelY = (y1 + y2) / 2 - 5;
            
            // Fondo blanco para la etiqueta
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(symbol);
            g2d.setColor(Color.WHITE);
            g2d.fillRect(labelX - 2, labelY - fm.getAscent(), labelWidth + 4, fm.getHeight());
            
            g2d.setColor(new Color(37, 99, 235));
            g2d.drawString(symbol, labelX, labelY);
            g2d.setColor(EDGE_COLOR);
        }
    }
    
    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = 10;
        
        int[] xPoints = {
            x2,
            (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6)),
            (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6))
        };
        
        int[] yPoints = {
            y2,
            (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6)),
            (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6))
        };
        
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    private void drawNodes(Graphics2D g2d) {
        for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
            int state = entry.getKey();
            Point pos = entry.getValue();
            
            // Dibujar rectángulo del nodo
            g2d.setColor(NODE_COLOR);
            g2d.fillRoundRect(pos.x, pos.y, NODE_WIDTH, NODE_HEIGHT, 10, 10);
            
            g2d.setColor(NODE_BORDER);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(pos.x, pos.y, NODE_WIDTH, NODE_HEIGHT, 10, 10);
            
            // Dibujar título del estado
            g2d.setColor(TEXT_COLOR);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
            String label = "I" + state;
            g2d.drawString(label, pos.x + 10, pos.y + 20);
            
            // Dibujar items del estado (resumido)
            List<ItemLR1> items = result.states.get(state);
            g2d.setFont(new Font("JetBrains Mono", Font.PLAIN, 9));
            int y = pos.y + 35;
            int maxItems = 3; // Mostrar solo los primeros 3 items
            
            for (int i = 0; i < Math.min(maxItems, items.size()); i++) {
                ItemLR1 item = items.get(i);
                String before = String.join(" ", item.rhs.subList(0, Math.min(item.dot, item.rhs.size())));
                String after = item.dot < item.rhs.size() ? 
                    String.join(" ", item.rhs.subList(item.dot, item.rhs.size())) : "";
                
                String core = item.lhs + " -> " + before + " . " + after;
                if (core.length() > 25) {
                    core = core.substring(0, 22) + "...";
                }
                
                List<String> laList = new ArrayList<>(item.lookahead);
                Collections.sort(laList);
                String la = String.join("/", laList);
                if (la.length() > 10) {
                    la = la.substring(0, 7) + "...";
                }
                
                g2d.drawString("[" + core + "; " + la + "]", pos.x + 5, y);
                y += 12;
            }
            
            if (items.size() > maxItems) {
                g2d.setFont(new Font("Segoe UI", Font.ITALIC, 9));
                g2d.drawString("... (" + (items.size() - maxItems) + " más)", pos.x + 5, y);
            }
        }
    }
}
