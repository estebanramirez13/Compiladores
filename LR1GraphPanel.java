import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class LR1GraphPanel extends JPanel {
    private LR1Parser.LR1Result result;
    private Map<Integer, Point> nodePositions;
    private static final int NODE_WIDTH = 220;
    private static final int NODE_HEIGHT = 90;
    private static final Color NODE_COLOR = new Color(255, 255, 255);
    private static final Color NODE_BORDER = new Color(71, 85, 105);
    private static final Color EDGE_COLOR = new Color(100, 116, 139);
    private static final Color ARROW_COLOR = new Color(71, 85, 105);
    private static final Color TEXT_COLOR = new Color(15, 23, 42);
    private static final Color LABEL_BG = new Color(255, 255, 255);
    private static final Color STATE_0_COLOR = new Color(59, 130, 246);
    private static final Color ACCEPT_COLOR = new Color(34, 197, 94);
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
        int maxIterations = result.states.size() * 2; // Más iteraciones
        
        while (changed && iterations++ < maxIterations) {
            changed = false;
            for (Map.Entry<String, String> entry : result.transitions.entrySet()) {
                try {
                    String[] parts = entry.getKey().split("-");
                    if (parts.length < 2) continue;
                    
                    int from = Integer.parseInt(parts[0].trim());
                    String toStr = entry.getValue().trim();
                    int to = Integer.parseInt(toStr);
                    
                    // Validar que los estados existan
                    if (from < 0 || to < 0 || from >= result.states.size() || to >= result.states.size()) {
                        continue;
                    }
                    
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
                } catch (NumberFormatException e) {
                    // Ignorar transiciones con formato incorrecto
                    continue;
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
        g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        for (Map.Entry<String, String> entry : result.transitions.entrySet()) {
            try {
                String[] parts = entry.getKey().split("-", 2);
                if (parts.length < 2) continue;
                
                int from = Integer.parseInt(parts[0].trim());
                String symbol = parts[1];
                int to = Integer.parseInt(entry.getValue().trim());
                
                // Validar estados
                if (from < 0 || to < 0 || from >= result.states.size() || to >= result.states.size()) {
                    continue;
                }
                
                Point fromPos = nodePositions.get(from);
                Point toPos = nodePositions.get(to);
                
                if (fromPos == null || toPos == null) continue;
                
                int fromCenterX = fromPos.x + NODE_WIDTH / 2;
                int fromCenterY = fromPos.y + NODE_HEIGHT / 2;
                int toCenterX = toPos.x + NODE_WIDTH / 2;
                int toCenterY = toPos.y + NODE_HEIGHT / 2;
                
                // Auto-loop
                if (from == to) {
                    drawSelfLoop(g2d, fromPos, symbol);
                    continue;
                }
                
                // Calcular puntos de conexión en los bordes
                double angle = Math.atan2(toCenterY - fromCenterY, toCenterX - fromCenterX);
                
                Point start = getEdgePoint(fromPos, angle);
                Point end = getEdgePoint(toPos, angle + Math.PI);
                
                // Dibujar curva suave
                g2d.setColor(EDGE_COLOR);
                drawCurvedArrow(g2d, start.x, start.y, end.x, end.y);
                
                // Dibujar etiqueta
                drawEdgeLabel(g2d, start.x, start.y, end.x, end.y, symbol);
            } catch (Exception e) {
                // Ignorar transiciones problemáticas
                continue;
            }
        }
    }
    
    private void drawCurvedArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        // Calcular puntos de control para curva Bezier
        int dx = x2 - x1;
        int dy = y2 - y1;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Curva más pronunciada para distancias mayores
        double curveFactor = Math.min(distance * 0.25, 80);
        
        // Punto de control perpendicular a la línea
        double angle = Math.atan2(dy, dx);
        double perpAngle = angle + Math.PI / 2;
        
        int ctrlX = (x1 + x2) / 2 + (int)(curveFactor * Math.cos(perpAngle));
        int ctrlY = (y1 + y2) / 2 + (int)(curveFactor * Math.sin(perpAngle));
        
        // Dibujar curva Bezier cuadrática
        QuadCurve2D curve = new QuadCurve2D.Double(x1, y1, ctrlX, ctrlY, x2, y2);
        g2d.draw(curve);
        
        // Calcular ángulo en el punto final para la flecha
        double t = 0.95; // punto cerca del final
        double finalX = (1-t)*(1-t)*x1 + 2*(1-t)*t*ctrlX + t*t*x2;
        double finalY = (1-t)*(1-t)*y1 + 2*(1-t)*t*ctrlY + t*t*y2;
        double arrowAngle = Math.atan2(y2 - finalY, x2 - finalX);
        
        // Dibujar flecha
        int arrowSize = 12;
        int[] xPoints = {
            x2,
            (int) (x2 - arrowSize * Math.cos(arrowAngle - Math.PI / 7)),
            (int) (x2 - arrowSize * Math.cos(arrowAngle + Math.PI / 7))
        };
        
        int[] yPoints = {
            y2,
            (int) (y2 - arrowSize * Math.sin(arrowAngle - Math.PI / 7)),
            (int) (y2 - arrowSize * Math.sin(arrowAngle + Math.PI / 7))
        };
        
        g2d.setColor(ARROW_COLOR);
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    private void drawSelfLoop(Graphics2D g2d, Point pos, String symbol) {
        int loopRadius = 35;
        int loopX = pos.x + NODE_WIDTH - 10;
        int loopY = pos.y - loopRadius - 5;
        
        // Dibujar círculo suave para el loop
        g2d.setColor(EDGE_COLOR);
        g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawOval(loopX, loopY, loopRadius * 2, loopRadius * 2);
        
        // Flecha en la parte superior derecha del círculo
        int arrowX = loopX + loopRadius * 2 - 8;
        int arrowY = loopY + 15;
        double arrowAngle = Math.PI / 4; // 45 grados
        
        int arrowSize = 12;
        int[] xPoints = {
            arrowX,
            (int) (arrowX - arrowSize * Math.cos(arrowAngle - Math.PI / 7)),
            (int) (arrowX - arrowSize * Math.cos(arrowAngle + Math.PI / 7))
        };
        
        int[] yPoints = {
            arrowY,
            (int) (arrowY - arrowSize * Math.sin(arrowAngle - Math.PI / 7)),
            (int) (arrowY - arrowSize * Math.sin(arrowAngle + Math.PI / 7))
        };
        
        g2d.setColor(ARROW_COLOR);
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        // Etiqueta en la parte superior del loop
        g2d.setFont(new Font("JetBrains Mono", Font.BOLD, 13));
        FontMetrics fm = g2d.getFontMetrics();
        int labelW = fm.stringWidth(symbol);
        int labelX = loopX + loopRadius - labelW / 2;
        int labelY = loopY - 5;
        
        // Fondo de la etiqueta
        g2d.setColor(LABEL_BG);
        g2d.fillRoundRect(labelX - 5, labelY - fm.getAscent() - 2, labelW + 10, fm.getHeight() + 2, 8, 8);
        
        g2d.setColor(new Color(226, 232, 240));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(labelX - 5, labelY - fm.getAscent() - 2, labelW + 10, fm.getHeight() + 2, 8, 8);
        
        // Texto
        g2d.setColor(new Color(59, 130, 246));
        g2d.drawString(symbol, labelX, labelY);
    }
    
    private Point getEdgePoint(Point nodePos, double angle) {
        int centerX = nodePos.x + NODE_WIDTH / 2;
        int centerY = nodePos.y + NODE_HEIGHT / 2;
        
        double absAngle = Math.abs(angle);
        double cos = Math.abs(Math.cos(angle));
        double sin = Math.abs(Math.sin(angle));
        
        int x, y;
        
        if (cos > sin) {
            // Horizontal edge
            if (Math.cos(angle) > 0) {
                x = nodePos.x + NODE_WIDTH;
                y = centerY;
            } else {
                x = nodePos.x;
                y = centerY;
            }
        } else {
            // Vertical edge
            if (Math.sin(angle) > 0) {
                x = centerX;
                y = nodePos.y + NODE_HEIGHT;
            } else {
                x = centerX;
                y = nodePos.y;
            }
        }
        
        return new Point(x, y);
    }
    
    private void drawEdgeLabel(Graphics2D g2d, int x1, int y1, int x2, int y2, String symbol) {
        // Calcular posición en el punto medio de la curva
        int dx = x2 - x1;
        int dy = y2 - y1;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double curveFactor = Math.min(distance * 0.25, 80);
        
        double angle = Math.atan2(dy, dx);
        double perpAngle = angle + Math.PI / 2;
        
        int midX = (x1 + x2) / 2;
        int midY = (y1 + y2) / 2;
        
        // Posición sobre la curva
        int labelX = midX + (int)(curveFactor * Math.cos(perpAngle)) + 10;
        int labelY = midY + (int)(curveFactor * Math.sin(perpAngle));
        
        g2d.setFont(new Font("JetBrains Mono", Font.BOLD, 13));
        FontMetrics fm = g2d.getFontMetrics();
        int labelWidth = fm.stringWidth(symbol);
        int labelHeight = fm.getHeight();
        
        // Fondo con borde
        g2d.setColor(LABEL_BG);
        g2d.fillRoundRect(labelX - 5, labelY - fm.getAscent() - 2, 
                         labelWidth + 10, labelHeight + 2, 8, 8);
        
        g2d.setColor(new Color(226, 232, 240));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(labelX - 5, labelY - fm.getAscent() - 2, 
                         labelWidth + 10, labelHeight + 2, 8, 8);
        
        // Texto
        g2d.setColor(new Color(59, 130, 246));
        g2d.drawString(symbol, labelX, labelY);
    }
    
    private void drawNodes(Graphics2D g2d) {
        for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
            int state = entry.getKey();
            Point pos = entry.getValue();
            
            // Detectar si es estado de aceptación
            boolean isAccept = isAcceptState(state);
            boolean isInitial = (state == 0);
            
            // Sombra
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.fillRoundRect(pos.x + 4, pos.y + 4, NODE_WIDTH, NODE_HEIGHT, 12, 12);
            
            // Fondo del nodo
            if (isInitial) {
                GradientPaint gradient = new GradientPaint(
                    pos.x, pos.y, new Color(219, 234, 254),
                    pos.x, pos.y + NODE_HEIGHT, Color.WHITE
                );
                g2d.setPaint(gradient);
            } else if (isAccept) {
                GradientPaint gradient = new GradientPaint(
                    pos.x, pos.y, new Color(220, 252, 231),
                    pos.x, pos.y + NODE_HEIGHT, Color.WHITE
                );
                g2d.setPaint(gradient);
            } else {
                g2d.setColor(NODE_COLOR);
            }
            g2d.fillRoundRect(pos.x, pos.y, NODE_WIDTH, NODE_HEIGHT, 12, 12);
            
            // Borde del nodo
            if (isInitial) {
                g2d.setColor(STATE_0_COLOR);
                g2d.setStroke(new BasicStroke(3));
            } else if (isAccept) {
                g2d.setColor(ACCEPT_COLOR);
                g2d.setStroke(new BasicStroke(3));
            } else {
                g2d.setColor(NODE_BORDER);
                g2d.setStroke(new BasicStroke(2));
            }
            g2d.drawRoundRect(pos.x, pos.y, NODE_WIDTH, NODE_HEIGHT, 12, 12);
            
            // Encabezado del estado
            int headerHeight = 24;
            if (isInitial) {
                g2d.setColor(new Color(59, 130, 246, 40));
            } else if (isAccept) {
                g2d.setColor(new Color(34, 197, 94, 40));
            } else {
                g2d.setColor(new Color(241, 245, 249));
            }
            g2d.fillRoundRect(pos.x, pos.y, NODE_WIDTH, headerHeight, 12, 12);
            g2d.fillRect(pos.x, pos.y + 12, NODE_WIDTH, 12);
            
            // Título del estado
            g2d.setColor(TEXT_COLOR);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 15));
            String stateLabel = "Estado I" + state;
            if (isInitial) stateLabel += " (inicial)";
            if (isAccept) stateLabel += " ✓";
            g2d.drawString(stateLabel, pos.x + 10, pos.y + 18);
            
            // Dibujar items
            List<ItemLR1> items = result.states.get(state);
            g2d.setFont(new Font("JetBrains Mono", Font.PLAIN, 10));
            int y = pos.y + 38;
            int maxItems = 2;
            
            for (int i = 0; i < Math.min(maxItems, items.size()); i++) {
                ItemLR1 item = items.get(i);
                String itemStr = formatItemCompact(item);
                
                g2d.setColor(new Color(71, 85, 105));
                g2d.drawString(itemStr, pos.x + 8, y);
                y += 13;
            }
            
            if (items.size() > maxItems) {
                g2d.setFont(new Font("Segoe UI", Font.ITALIC, 10));
                g2d.setColor(new Color(148, 163, 184));
                g2d.drawString("+" + (items.size() - maxItems) + " items más...", pos.x + 8, y);
            }
        }
    }
    
    private boolean isAcceptState(int state) {
        // Un estado es de aceptación si tiene un item S' -> S.
        List<ItemLR1> items = result.states.get(state);
        for (ItemLR1 item : items) {
            if (item.lhs.equals("S'") && item.dot >= item.rhs.size()) {
                return true;
            }
        }
        return false;
    }
    
    private String formatItemCompact(ItemLR1 item) {
        String before = String.join("", item.rhs.subList(0, Math.min(item.dot, item.rhs.size())));
        String after = item.dot < item.rhs.size() ? 
            String.join("", item.rhs.subList(item.dot, item.rhs.size())) : "";
        
        String core = item.lhs + "→" + before + "•" + after;
        if (core.length() > 22) {
            core = core.substring(0, 19) + "...";
        }
        
        List<String> laList = new ArrayList<>(item.lookahead);
        Collections.sort(laList);
        String la = laList.isEmpty() ? "$" : laList.get(0);
        if (laList.size() > 1) la += "...";
        
        return core + " [" + la + "]";
    }
}
