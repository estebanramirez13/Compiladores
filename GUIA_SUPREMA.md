# 🏆 GUÍA SUPREMA - DOMINA TU PROYECTO EN 2 HORAS

## 📅 PLAN DE ESTUDIO (120 minutos)

### ⏰ HORA 1: Conceptos y Teoría (60 min)
- 0-15 min: Lee "Lo Esencial" → Entiende QUÉ es
- 15-30 min: Lee "Los 3 Algoritmos" → Entiende CÓMO funciona  
- 30-45 min: Lee "Explicación Línea por Línea" → Entiende el CÓDIGO
- 45-60 min: Practica con el programa (ejecutar.bat)

### ⏰ HORA 2: Práctica y Memorización (60 min)
- 60-75 min: Simulación de preguntas → Responde en voz alta
- 75-90 min: Modifica código (ejercicios prácticos)
- 90-105 min: Repaso rápido (tarjetas mentales)
- 105-120 min: Demostración completa (como con el profesor)

---

## 🎯 PARTE 1: LO ESENCIAL (15 min)

### ¿QUÉ ES UN ANALIZADOR SINTÁCTICO?

**Analogía perfecta:**
Imagina que tienes un libro de recetas (gramática) que dice:
```
Pastel → Base + Relleno + Cubierta
Base → Harina + Huevos + Azúcar
Relleno → Chocolate | Vainilla
```

Un analizador sintáctico es como un inspector que:
1. **Recibe** una lista de ingredientes: "Harina, Huevos, Azúcar, Chocolate"
2. **Verifica** si se puede hacer un pastel con eso
3. **Responde** ✅ "Sí, es un pastel válido" o ❌ "No, falta algo"

**Tu programa hace EXACTAMENTE eso, pero con lenguajes de programación.**

---

### ¿QUÉ ES LR(1)?

Es un **MÉTODO** específico para hacer esa verificación.

**Las siglas significan:**
- **L** = Left-to-right (lee de izquierda a derecha)
- **R** = Rightmost derivation in reverse (construye de abajo hacia arriba)
- **(1)** = Lookahead 1 (mira 1 símbolo adelante)

**En palabras simples:**
- Lee tu código como lees un libro (izquierda → derecha)
- Arma las piezas de abajo hacia arriba (como LEGO)
- Siempre mira 1 paso adelante para saber qué hacer

---

### ¿POR QUÉ ES IMPORTANTE?

**Aplicación real:**
Cuando escribes código en Java, Python o C++:
```java
int x = 5 + 3;
```

El compilador usa un analizador sintáctico (como el tuyo) para:
1. ✓ Verificar que la sintaxis sea correcta
2. ✓ Entender que es una declaración de variable
3. ✓ Construir una estructura interna (árbol de sintaxis)

**Sin analizadores sintácticos NO existirían los lenguajes de programación.**

---

## 🧠 PARTE 2: LOS 3 ALGORITMOS MÁGICOS (30 min)

### ALGORITMO 1: CLOSURE (Expandir) 🔍

**¿Qué hace?**
Expande un conjunto de ítems agregando todas las reglas relacionadas.

**Analogía:**
Tienes una lista de compras: "Comprar pastel"
Closure te dice: "Si quieres pastel, necesitas: harina, huevos, azúcar..."

**Ejemplo visual:**

```
ENTRADA:
[S' -> .S, $]

CLOSURE expande:
[S' -> .S, $]      ← Original
[S -> .C C, $]     ← Agregado (porque S aparece después del punto)
[C -> .c C, c/d]   ← Agregado (porque C aparece después del punto)
[C -> .d, c/d]     ← Agregado (alternativa de C)
```

**Regla de oro:**
Si tienes `[A -> α.Bβ, a]`, busca TODAS las reglas donde B está al lado izquierdo.

**En código (LR1Parser.java, líneas 5-73):**
```java
public Set<ItemLR1> closure(Set<ItemLR1> items) {
    Set<ItemLR1> closure = new HashSet<>(items);
    boolean changed = true;
    
    while (changed) {
        changed = false;
        Set<ItemLR1> toAdd = new HashSet<>();
        
        for (ItemLR1 item : closure) {
            String next = item.nextSymbol();  // B en [A -> α.Bβ, a]
            
            if (next != null && grammar.containsKey(next)) {
                // Agregar todas las reglas B -> γ
                for (String production : grammar.get(next)) {
                    // Calcular lookaheads
                    // Crear nuevos items
                    // Agregarlos al conjunto
                }
            }
        }
        
        if (!toAdd.isEmpty()) {
            closure.addAll(toAdd);
            changed = true;
        }
    }
    
    return closure;
}
```

**Palabras clave para explicar:**
- "Expandir el conjunto"
- "Agregar reglas relacionadas"
- "Calcular lookaheads"
- "Repetir hasta que no haya cambios"

---

### ALGORITMO 2: GOTO (Siguiente paso) ➡️

**¿Qué hace?**
Calcula el siguiente estado después de leer un símbolo.

**Analogía:**
Estás en paso 3 de una receta de IKEA.
GOTO te dice: "Si agregas la pieza X, pasas al paso 7"

**Ejemplo visual:**

```
ESTADO ACTUAL:
[C -> c.C, d]
[C -> .c C, d]
[C -> .d, d]

GOTO con símbolo 'c':
[C -> c C., d]     ← Avanzó el punto después de 'c'
[C -> .c C, d]     ← Agregado por closure
[C -> .d, d]       ← Agregado por closure
```

**Regla de oro:**
Para cada ítem `[A -> α.Xβ, a]`, si el símbolo después del punto es X:
1. Mueve el punto: `[A -> αX.β, a]`
2. Aplica closure al resultado

**En código (LR1Parser.java, líneas 75-88):**
```java
public Set<ItemLR1> goTo(Set<ItemLR1> items, String symbol) {
    Set<ItemLR1> result = new HashSet<>();
    
    for (ItemLR1 item : items) {
        if (symbol.equals(item.nextSymbol())) {
            result.add(item.advance());  // Mover el punto
        }
    }
    
    return closure(result);  // Expandir
}
```

**Palabras clave para explicar:**
- "Avanzar el punto"
- "Leer un símbolo"
- "Calcular el siguiente estado"
- "Aplicar closure al resultado"

---

### ALGORITMO 3: CONSTRUIR TABLA (Combinar todo) 📊

**¿Qué hace?**
Crea la tabla de análisis que dice qué hacer en cada situación.

**Analogía:**
Es como un manual de "SI pasa esto, ENTONCES haz aquello"
- SI estás en paso 2 y ves 'c' → Avanza al paso 5
- SI estás en paso 7 y ves '$' → Acepta (terminaste)

**Estructura de la tabla:**

```
Estado | Símbolo | Acción
-------|---------|--------
   0   |    c    | shift 3
   0   |    d    | shift 4
   2   |    $    | accept
   5   |    d    | reduce S->CC
```

**Tipos de acciones:**

1. **SHIFT (Desplazar)**
   - Significado: "Lee el símbolo y avanza al siguiente estado"
   - Ejemplo: `shift 3` = "Lee y ve al estado 3"

2. **REDUCE (Reducir)**
   - Significado: "Construye una estructura usando esta regla"
   - Ejemplo: `reduce S->CC` = "Junta dos C para hacer una S"

3. **ACCEPT (Aceptar)**
   - Significado: "¡Perfecto! La entrada es válida"
   - Ejemplo: `accept` = "Todo está bien"

4. **ERROR (Error)**
   - Significado: "Entrada inválida"
   - Ejemplo: "No se qué hacer aquí"

**En código (LR1Parser.java, líneas 194-328):**
```java
public void buildLR1Table() {
    // 1. Generar todos los estados con closure y goto
    for (int i = 0; i < states.size(); i++) {
        Set<ItemLR1> state = states.get(i);
        
        // Para cada símbolo
        for (String symbol : allSymbols) {
            Set<ItemLR1> nextState = goTo(state, symbol);
            
            if (!nextState.isEmpty()) {
                int nextIndex = findOrAddState(nextState);
                // Agregar acción a la tabla
            }
        }
    }
    
    // 2. Agregar reducciones y aceptación
    for (int i = 0; i < states.size(); i++) {
        for (ItemLR1 item : states.get(i)) {
            if (item.isComplete()) {
                // Agregar reduce
            }
        }
    }
}
```

**Palabras clave para explicar:**
- "Generar todos los estados posibles"
- "Calcular transiciones con goto"
- "Agregar acciones shift y reduce"
- "Detectar el estado de aceptación"

---

## 💻 PARTE 3: EXPLICACIÓN LÍNEA POR LÍNEA (30 min)

### ARCHIVO 1: ItemLR1.java (48 líneas)

**Propósito:** Representa un ítem LR(1): `[A -> α.β, {lookaheads}]`

**Código clave:**

```java
public class ItemLR1 {
    private String leftSide;        // A
    private List<String> rightSide; // α β
    private int dotPosition;        // Posición del .
    private Set<String> lookaheads; // {a, b, c}
    
    // ¿Qué símbolo viene después del punto?
    public String nextSymbol() {
        if (dotPosition < rightSide.size()) {
            return rightSide.get(dotPosition);
        }
        return null;
    }
    
    // ¿Está completo? (punto al final)
    public boolean isComplete() {
        return dotPosition >= rightSide.size();
    }
    
    // Avanzar el punto una posición
    public ItemLR1 advance() {
        return new ItemLR1(leftSide, rightSide, 
                          dotPosition + 1, lookaheads);
    }
}
```

**Explicación rápida:**
- Es como una ficha que dice "estoy procesando esta regla hasta aquí ."
- El punto marca hasta dónde he leído
- Los lookaheads son "lo que puede venir después"

---

### ARCHIVO 2: GrammarParser.java (86 líneas)

**Propósito:** Lee el texto de la gramática y lo convierte en estructuras de datos

**Código clave:**

```java
public static Map<String, List<String>> parseGrammar(String text) {
    Map<String, List<String>> grammar = new LinkedHashMap<>();
    
    for (String line : lines) {
        // Separar "S -> C C | d"
        String[] parts = line.split("->");
        String left = parts[0].trim();  // "S"
        String right = parts[1].trim(); // "C C | d"
        
        // Separar alternativas por |
        String[] alternatives = right.split("\\|");
        
        for (String alt : alternatives) {
            // "C C" → ["C", "C"]
            List<String> symbols = Arrays.asList(alt.trim().split("\\s+"));
            grammar.get(left).add(symbols);
        }
    }
    
    return grammar;
}
```

**Explicación rápida:**
- Lee línea por línea
- Separa izquierda → derecha
- Maneja alternativas con |
- Convierte `ε` en lista vacía

---

### ARCHIVO 3: FirstSetsCalculator.java (109 líneas)

**Propósito:** Calcula conjuntos FIRST (qué puede aparecer primero)

**Código clave:**

```java
public Map<String, Set<String>> calculateFirstSets() {
    Map<String, Set<String>> first = new HashMap<>();
    
    // Para símbolos terminales: FIRST(a) = {a}
    for (String terminal : terminals) {
        first.put(terminal, new HashSet<>(Arrays.asList(terminal)));
    }
    
    // Para símbolos no-terminales: calcular iterativamente
    boolean changed = true;
    while (changed) {
        changed = false;
        for (String nonTerminal : grammar.keySet()) {
            for (List<String> production : grammar.get(nonTerminal)) {
                // FIRST(A -> αβ) incluye FIRST(α)
                Set<String> firstOfProd = firstOfSequence(production);
                if (first.get(nonTerminal).addAll(firstOfProd)) {
                    changed = true;
                }
            }
        }
    }
    
    return first;
}
```

**Explicación rápida:**
- FIRST(a) = {a} si a es terminal
- FIRST(A) = todo lo que puede empezar A
- Se calcula iterativamente hasta convergencia

---

### ARCHIVO 4: LR1Parser.java (329 líneas) ⭐⭐⭐

**Propósito:** El cerebro - implementa closure, goto, y construcción de tabla

**YA LO EXPLICAMOS ARRIBA en "Los 3 Algoritmos"**

**Estructura:**
- Líneas 5-73: `closure()`
- Líneas 75-88: `goTo()`
- Líneas 90-192: Construcción de estados
- Líneas 194-328: `buildLR1Table()`

---

### ARCHIVO 5: LRParserTrace.java (151 líneas)

**Propósito:** Verifica si una cadena es válida (reconocimiento)

**Código clave:**

```java
public List<String> runParser(String input) {
    Stack<Integer> stateStack = new Stack<>();
    Stack<String> symbolStack = new Stack<>();
    stateStack.push(0);  // Estado inicial
    
    String[] tokens = input.split("\\s+");
    int i = 0;
    
    while (true) {
        int currentState = stateStack.peek();
        String currentSymbol = (i < tokens.length) ? tokens[i] : "$";
        
        String action = getAction(currentState, currentSymbol);
        
        if (action.startsWith("shift")) {
            // Desplazar
            stateStack.push(nextState);
            symbolStack.push(currentSymbol);
            i++;
        } else if (action.startsWith("reduce")) {
            // Reducir
            // Pop de la pila según longitud de la regla
            // Push del no-terminal
        } else if (action.equals("accept")) {
            return trace;  // ¡Éxito!
        } else {
            return trace;  // Error
        }
    }
}
```

**Explicación rápida:**
- Usa dos pilas: estados y símbolos
- Lee token por token
- Consulta la tabla para saber qué hacer
- Shift = leer, Reduce = construir, Accept = ¡listo!

---

### ARCHIVO 6: LR1GraphPanel.java (315 líneas)

**Propósito:** Dibuja el grafo visual del autómata

**Código clave:**

```java
protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    
    // 1. Calcular posiciones de los nodos
    calculateNodeLevels();
    
    // 2. Dibujar las flechas (transiciones)
    drawEdges(g2);
    
    // 3. Dibujar los nodos (estados)
    drawNodes(g2);
}

private void drawNodes(Graphics2D g2) {
    for (int i = 0; i < states.size(); i++) {
        // Dibujar rectángulo
        g2.fillRect(x, y, width, height);
        
        // Dibujar número de estado
        g2.drawString("Estado " + i, x, y);
        
        // Dibujar items dentro del nodo
        for (ItemLR1 item : states.get(i)) {
            g2.drawString(item.toString(), x, y);
        }
    }
}
```

**Explicación rápida:**
- Organiza nodos por niveles (jerárquico)
- Dibuja flechas mostrando transiciones
- Muestra items dentro de cada estado

---

### ARCHIVO 7: LR1App.java (280 líneas)

**Propósito:** Interfaz gráfica (GUI) con 4 pestañas

**Código clave:**

```java
public class LR1App extends JFrame {
    private JTextArea grammarInput;
    private JTabbedPane tabbedPane;
    
    public LR1App() {
        // Crear ventana
        setTitle("Analizador LR(1)");
        
        // Pestaña 1: Colección Canónica
        JPanel collectionPanel = new JPanel();
        
        // Pestaña 2: Tabla
        JPanel tablePanel = new JPanel();
        
        // Pestaña 3: Grafo
        LR1GraphPanel graphPanel = new LR1GraphPanel();
        
        // Pestaña 4: Reconocedor
        JPanel recognizerPanel = new JPanel();
        
        // Botón construir
        buildButton.addActionListener(e -> {
            LR1Parser parser = new LR1Parser(grammar);
            parser.buildLR1Table();
            // Actualizar todas las pestañas
        });
    }
}
```

**Explicación rápida:**
- JFrame = ventana principal
- JTabbedPane = pestañas
- JTextArea = cajas de texto
- ActionListener = qué hacer cuando se hace clic

---

## 🎤 PARTE 4: SIMULACIÓN DE PREGUNTAS (30 min)

### NIVEL FÁCIL (Entendimiento básico)

**P1: ¿Qué hace tu programa?**
```
R: "Mi programa es un analizador sintáctico LR(1) que verifica 
   si una secuencia de símbolos cumple con las reglas de una gramática. 
   
   Es como un corrector gramatical pero para lenguajes formales. 
   
   Tiene 4 funcionalidades principales: generar la colección canónica,
   construir la tabla de análisis, visualizar el autómata, y 
   reconocer cadenas."
```

**P2: ¿Por qué LR(1)?**
```
R: "LR(1) es uno de los métodos más potentes para análisis sintáctico.
   
   Puede manejar gramáticas más complejas que otros métodos como SLR.
   
   El '1' significa que mira un símbolo adelante (lookahead) para
   tomar decisiones, lo que le da más precisión."
```

**P3: ¿Cuánto código escribiste?**
```
R: "1,318 líneas distribuidas en 7 archivos Java.
   
   Todo el código es 100% Java estándar, sin librerías externas.
   
   Usé Swing para la interfaz gráfica y estructuras de datos como
   HashMap, HashSet y ArrayList para la eficiencia."
```

---

### NIVEL MEDIO (Detalles técnicos)

**P4: ¿Qué es un Item LR(1)?**
```
R: "Un ítem LR(1) es una producción con un punto que marca el 
   progreso de lectura, más un conjunto de lookaheads.
   
   Por ejemplo: [S -> C.C, $] significa:
   - Estoy procesando la regla S -> CC
   - Ya leí el primer C (antes del punto)
   - Me falta leer el segundo C (después del punto)
   - El lookahead es $ (fin de entrada)
   
   El punto es como un marcador de 'estoy aquí'."
```

**P5: ¿Cómo funciona closure?**
```
R: "Closure expande un conjunto de ítems agregando todas las 
   reglas relacionadas.
   
   Si tengo [A -> α.Bβ, a], significa que necesito derivar B,
   entonces busco todas las reglas donde B está al lado izquierdo
   (B -> γ) y las agrego al conjunto.
   
   Los lookaheads de los nuevos ítems se calculan usando 
   FIRST(βa).
   
   Se repite hasta que no haya más ítems nuevos que agregar.
   
   Está implementado en LR1Parser.java líneas 5-73."
```

**P6: ¿Cómo funciona goto?**
```
R: "Goto calcula el siguiente estado después de leer un símbolo.
   
   Toma todos los ítems donde el símbolo X está inmediatamente
   después del punto, avanza el punto una posición, y aplica
   closure al resultado.
   
   Es como decir: 'si leo X, ¿a dónde voy?'
   
   Está implementado en LR1Parser.java líneas 75-88."
```

---

### NIVEL DIFÍCIL (Implementación)

**P7: ¿Por qué usaste HashSet en lugar de ArrayList para los ítems?**
```
R: "Usé HashSet porque necesito:
   
   1. Evitar duplicados automáticamente
   2. Búsqueda rápida O(1) en lugar de O(n)
   3. Operaciones de conjunto como union y contains
   
   En closure, puedo tener el mismo ítem agregado múltiples veces
   desde diferentes caminos, y HashSet elimina duplicados
   automáticamente sin código extra."
```

**P8: ¿Cómo calculas los lookaheads?**
```
R: "Los lookaheads se calculan usando conjuntos FIRST.
   
   Si tengo [A -> α.Bβ, a] y quiero agregar [B -> .γ, ?],
   el lookahead ? es FIRST(βa).
   
   Esto se implementa en el método closure usando
   FirstSetsCalculator.firstOfSequence().
   
   Básicamente: 'lo que puede venir después de B es lo que
   puede empezar β, o si β es nullable, entonces a'."
```

**P9: ¿Cómo evitas duplicar estados en la colección canónica?**
```
R: "Uso un Map con el núcleo (core) del estado como clave.
   
   Dos estados son equivalentes si tienen el mismo núcleo
   (mismos ítems sin considerar lookaheads).
   
   Si encuentro un estado con núcleo existente, no creo uno nuevo,
   sino que combino los lookaheads.
   
   Esto está en buildLR1Table() donde uso stateMap para
   detectar duplicados."
```

**P10: ¿Qué pasa si hay un conflicto shift-reduce?**
```
R: "Un conflicto shift-reduce ocurre cuando en un estado y símbolo
   hay dos acciones posibles: shift y reduce.
   
   Mi implementación detecta estos conflictos al construir la tabla.
   
   Si ocurre, puedo:
   1. Reportar el error al usuario
   2. Mostrar qué producción causa el conflicto
   3. Sugerir modificar la gramática
   
   Las gramáticas LR(1) bien formadas no deberían tener conflictos."
```

---

## ✏️ PARTE 5: EJERCICIOS PRÁCTICOS (30 min)

### EJERCICIO 1: Ejecutar con gramática simple

**Tarea:** Abre el programa y prueba esta gramática:
```
S -> a S b
S -> ε
```

**Pasos:**
1. Doble clic en `ejecutar.bat`
2. Escribe la gramática en la caja
3. Click "Construir LR(1)"
4. Ve a cada pestaña y observa

**Qué esperar:**
- Colección: Ver los estados generados
- Tabla: Ver shift/reduce/accept
- Grafo: Ver el autómata visual
- Reconocedor: Probar "a a b b"

---

### EJERCICIO 2: Modificar código (agregar print)

**Tarea:** Agrega un mensaje que imprima cuántos estados se generaron

**Pasos:**
1. Abre `LR1Parser.java`
2. Busca la función `buildLR1Table()`
3. Al final, antes del `}`, agrega:
```java
System.out.println("Se generaron " + states.size() + " estados");
```
4. Guarda (Ctrl+S)
5. Compila: `javac -encoding UTF-8 *.java`
6. Ejecuta: `java LR1App`

**Qué esperar:**
En la consola verás: "Se generaron X estados"

---

### EJERCICIO 3: Encontrar funciones clave

**Tarea:** Ubica estas 3 funciones usando Ctrl+F

1. Busca `closure` en LR1Parser.java
   - ¿En qué línea empieza? (Respuesta: 5)
   
2. Busca `goTo` en LR1Parser.java
   - ¿Cuántos parámetros recibe? (Respuesta: 2)
   
3. Busca `nextSymbol` en ItemLR1.java
   - ¿Qué retorna si el punto está al final? (Respuesta: null)

---

### EJERCICIO 4: Probar reconocimiento

**Tarea:** Prueba estas cadenas y predice el resultado

Gramática:
```
E -> E + T
E -> T
T -> id
```

Cadenas:
1. `id` → ✅ Válida
2. `id + id` → ✅ Válida
3. `id + id + id` → ✅ Válida
4. `+ id` → ❌ Inválida
5. `id +` → ❌ Inválida

---

### EJERCICIO 5: Interpretar la tabla

**Tarea:** Entiende esta fila de la tabla:

```
Estado 3, símbolo 'd': shift 4
```

**Preguntas:**
1. ¿Qué significa? "Si estoy en estado 3 y leo 'd', voy al estado 4"
2. ¿Qué tipo de acción es? "Shift (desplazar)"
3. ¿Qué hace el reconocedor? "Lee el símbolo y cambia de estado"

---

## 🧩 PARTE 6: TARJETAS MENTALES (15 min)

### TARJETA 1: Los 7 Archivos

```
┌─────────────────────────────────────┐
│ ItemLR1.java                        │
│ → Representa un ítem [A->α.β, a]    │
│ → nextSymbol(), isComplete()        │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ GrammarParser.java                  │
│ → Lee texto → Map<String, List>     │
│ → Maneja | y ε                      │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ FirstSetsCalculator.java            │
│ → Calcula FIRST(X)                  │
│ → Usado en lookaheads               │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ LR1Parser.java ⭐                    │
│ → closure(), goTo(), buildLR1Table()│
│ → EL CEREBRO - 329 líneas           │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ LRParserTrace.java                  │
│ → Reconoce cadenas                  │
│ → Shift/Reduce/Accept               │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ LR1GraphPanel.java                  │
│ → Dibuja el autómata                │
│ → Layout jerárquico                 │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ LR1App.java                         │
│ → GUI con 4 pestañas                │
│ → Swing/AWT                         │
└─────────────────────────────────────┘
```

---

### TARJETA 2: Los 3 Algoritmos

```
╔═══════════════════════════════════════╗
║ CLOSURE (Expandir)                    ║
╠═══════════════════════════════════════╣
║ Input:  Conjunto de ítems             ║
║ Output: Conjunto expandido            ║
║ Regla:  [A->α.Bβ,a] → [B->.γ, FIRST]║
║ Loop:   Hasta que no haya cambios     ║
╚═══════════════════════════════════════╝

╔═══════════════════════════════════════╗
║ GOTO (Siguiente paso)                 ║
╠═══════════════════════════════════════╣
║ Input:  Estado + Símbolo              ║
║ Output: Siguiente estado              ║
║ Paso 1: Avanzar punto si X coincide   ║
║ Paso 2: Aplicar closure               ║
╚═══════════════════════════════════════╝

╔═══════════════════════════════════════╗
║ BUILD TABLE (Construir tabla)         ║
╠═══════════════════════════════════════╣
║ Paso 1: Generar estados con goto      ║
║ Paso 2: Agregar acciones shift        ║
║ Paso 3: Agregar acciones reduce       ║
║ Paso 4: Marcar estado accept          ║
╚═══════════════════════════════════════╝
```

---

### TARJETA 3: Acciones de la Tabla

```
┌────────┬──────────────────────────────┐
│ SHIFT  │ Lee símbolo, cambia estado   │
│        │ "shift 5" = ve al estado 5   │
├────────┼──────────────────────────────┤
│ REDUCE │ Construye usando una regla   │
│        │ "reduce S->CC" = junta 2 C   │
├────────┼──────────────────────────────┤
│ ACCEPT │ ¡Éxito! Cadena válida        │
│        │ Solo en estado final con $   │
├────────┼──────────────────────────────┤
│ ERROR  │ Entrada inválida             │
│        │ No hay acción definida       │
└────────┴──────────────────────────────┘
```

---

## 🎭 PARTE 7: DEMOSTRACIÓN COMPLETA (15 min)

### GUION PARA EL PROFESOR (Memoriza esto)

**MINUTO 1-2: Introducción**
```
"Buenos días, profesor. Voy a presentar mi analizador sintáctico LR(1).

El proyecto consiste en un programa que verifica si una secuencia
de símbolos cumple con las reglas de una gramática formal.

Está implementado 100% en Java, con 1,318 líneas de código
distribuidas en 7 archivos, usando solo librerías estándar."
```

**MINUTO 3-4: Demostración - Entrada**
```
[Abrir programa]

"Primero, ingreso la gramática. Voy a usar este ejemplo:

S -> C C
C -> c C | d

Esto significa:
- S se compone de dos C
- C puede ser 'c' seguido de más C, o simplemente 'd'"

[Escribir gramática y click en Construir]
```

**MINUTO 5-7: Demostración - Colección Canónica**
```
[Click en pestaña Colección]

"Esta pestaña muestra la colección canónica de estados LR(1).

El algoritmo generó 10 estados, cada uno con un conjunto de ítems.

Por ejemplo, el Estado 0 es el inicial y contiene:
[S' -> .S, $]  ← Item inicial aumentado
[S -> .C C, $] ← Expandido por closure
[C -> .c C, c/d] ← Reglas de C

El punto marca dónde estamos en la lectura, y al final
están los lookaheads."
```

**MINUTO 8-10: Demostración - Tabla**
```
[Click en pestaña Tabla]

"Esta es la tabla de análisis sintáctico.

Tiene dos secciones:
- ACTION: Qué hacer con terminales (shift, reduce, accept)
- GOTO: A dónde ir con no-terminales

Por ejemplo:
Estado 0, símbolo 'c': shift 3 → Lee 'c' y va al estado 3
Estado 2, símbolo '$': accept → Entrada válida, terminar

Esta tabla es el corazón del reconocedor."
```

**MINUTO 11-12: Demostración - Grafo**
```
[Click en pestaña Grafo]

"Esta es la visualización del autómata LR(1).

Los rectángulos son estados, las flechas son transiciones.

Cada estado muestra sus ítems internos.

El layout es jerárquico: el estado inicial arriba,
y los demás organizados por niveles según sus transiciones."
```

**MINUTO 13-15: Demostración - Reconocedor**
```
[Click en pestaña Reconocedor]

"Finalmente, esta pestaña permite reconocer cadenas.

Voy a probar la cadena: c c d d

[Escribir y click en Analizar]

El programa muestra la traza paso a paso:
- Cada fila muestra: pila, entrada restante, y acción
- Va haciendo shift (leer) y reduce (construir)
- Al final, si llega a 'accept', la cadena es válida

En este caso: ✓ Cadena aceptada"
```

---

## 🔥 PARTE 8: RESPUESTAS A PREGUNTAS TRAMPA (15 min)

### TRAMPA 1: "¿Por qué no usaste SLR?"

**Mala respuesta:** "No sé qué es SLR"

**Buena respuesta:**
```
"SLR es Simple LR, una versión más sencilla pero menos potente.

LR(1) es más preciso porque usa lookaheads específicos de cada ítem,
mientras que SLR usa lookaheads globales (FOLLOW).

Esto permite que LR(1) maneje gramáticas más complejas sin conflictos.

Por ejemplo, LR(1) puede distinguir contextos donde SLR fallaría."
```

---

### TRAMPA 2: "¿Qué pasa si la gramática tiene recursión izquierda?"

**Mala respuesta:** "Eso no funciona"

**Buena respuesta:**
```
"LR(1) maneja recursión izquierda sin problema, a diferencia
de los analizadores descendentes.

Por ejemplo, la gramática:
E -> E + T
E -> T

Tiene recursión izquierda en la primera regla, y mi programa
la procesa correctamente.

La recursión izquierda es natural en LR porque construye
de abajo hacia arriba (bottom-up)."
```

---

### TRAMPA 3: "Explica la complejidad temporal de tu algoritmo"

**Mala respuesta:** "No calculé eso"

**Buena respuesta:**
```
"La construcción de la tabla LR(1) tiene complejidad O(n³)
en el peor caso, donde n es el tamaño de la gramática.

Esto se debe a:
- Closure: O(n²) por iteración
- Goto: O(n) por símbolo
- Estados: Puede haber O(n) estados

El reconocimiento de cadenas es O(m) donde m es la longitud
de la entrada, porque cada símbolo se procesa una vez."
```

---

### TRAMPA 4: "¿Cómo manejas ambigüedad en la gramática?"

**Mala respuesta:** "No sé"

**Buena respuesta:**
```
"Una gramática ambigua puede generar conflictos shift-reduce
o reduce-reduce en la tabla.

Mi programa detecta estos conflictos al construir la tabla.

Si la gramática es ambigua, el usuario debe:
1. Reescribir la gramática sin ambigüedad
2. O agregar reglas de precedencia/asociatividad

LR(1) no resuelve ambigüedad automáticamente, pero la detecta."
```

---

### TRAMPA 5: "¿Por qué Java y no Python?"

**Mala respuesta:** "Porque sí"

**Buena respuesta:**
```
"Java ofrece varias ventajas para este proyecto:

1. Tipado estático: Detecta errores en compilación
2. Swing: Biblioteca robusta para GUIs de escritorio
3. Rendimiento: Más rápido que Python para algoritmos intensivos
4. Estructuras de datos: HashMap, HashSet optimizados

Python sería más conciso, pero Java da más control
y mejor rendimiento para un analizador sintáctico."
```

---

## 📚 PARTE 9: GLOSARIO TÉCNICO (Memoriza 10)

| Término | Definición Simple | Ejemplo |
|---------|-------------------|---------|
| **Item** | Producción con punto que marca progreso | `[S -> C.C, $]` |
| **Lookahead** | Símbolo que puede venir después | `$, a, b` |
| **Closure** | Expandir ítems con reglas relacionadas | Ver algoritmo 1 |
| **Goto** | Calcular siguiente estado | Ver algoritmo 2 |
| **Shift** | Leer símbolo y avanzar | `shift 5` |
| **Reduce** | Aplicar una regla de producción | `reduce S->CC` |
| **Accept** | Entrada válida, terminar | Solo con S' y $ |
| **Core** | Ítems sin lookaheads | Para comparar estados |
| **Canonical Collection** | Todos los estados LR(1) | Colección canónica |
| **Parse Table** | Tabla con acciones y gotos | ACTION + GOTO |

---

## 🎯 CHECKLIST FINAL (Antes del profesor)

### ✅ Conocimiento Teórico
- [ ] Puedo explicar qué es LR(1) en 30 segundos
- [ ] Entiendo closure, goto, y build table
- [ ] Sé qué es shift, reduce, accept
- [ ] Conozco la diferencia con SLR y LALR

### ✅ Conocimiento del Código
- [ ] Puedo abrir y navegar LR1Parser.java
- [ ] Sé dónde está closure (línea 5-73)
- [ ] Sé dónde está goTo (línea 75-88)
- [ ] Entiendo la estructura de ItemLR1

### ✅ Demostración Práctica
- [ ] Ejecuté el programa 3 veces
- [ ] Probé con 3 gramáticas diferentes
- [ ] Reconocí cadenas válidas e inválidas
- [ ] Vi todas las pestañas funcionando

### ✅ Modificación de Código
- [ ] Agregué un System.out.println
- [ ] Compilé después del cambio
- [ ] Ejecuté y vi el resultado

### ✅ Preparación Mental
- [ ] Leí EXPLICACION_SIMPLE.md
- [ ] Practiqué respuestas en voz alta
- [ ] Tengo confianza en mostrar el código
- [ ] Sé que puedo decir "déjeme revisar"

---

## ⏱️ PLAN DE REPASO (Última hora)

### 60 MINUTOS ANTES:

**0-10 min:** Lee REPASO_RAPIDO.md completo

**10-20 min:** Abre cada archivo .java y mira las primeras 20 líneas

**20-30 min:** Ejecuta el programa y prueba 2 gramáticas

**30-40 min:** Practica responder en voz alta:
- "¿Qué hace tu programa?"
- "¿Cómo funciona closure?"
- "¿Por qué LR(1)?"

**40-50 min:** Revisa este checklist

**50-60 min:** Respira profundo, relájate

---

## 💪 MANTRA FINAL

```
✅ Mi programa FUNCIONA perfectamente
✅ Tengo 1,318 líneas de código FUNCIONANDO
✅ Puedo DEMOSTRAR todas las funcionalidades
✅ Sé dónde está CADA algoritmo clave
✅ Puedo CONSULTAR el código si olvido algo
✅ NO necesito saberlo TODO de memoria
✅ ENTIENDO lo esencial y eso es suficiente

¡ESTOY PREPARADO! 🚀
```

---

## 🆘 NÚMEROS DE EMERGENCIA (Memoriza)

**Si te preguntan y no recuerdas:**

1. **Número de archivos:** 7
2. **Líneas de código:** 1,318
3. **Línea de closure:** 5-73 en LR1Parser.java
4. **Línea de goTo:** 75-88 en LR1Parser.java
5. **Pestañas:** 4 (Colección, Tabla, Grafo, Reconocedor)

**Frase mágica para todo:**
> "Déjeme mostrárselo en el código funcionando"

---

**ESTUDIASTE TODO → AHORA CONFÍA EN TI → ¡VAS A TENER ÉXITO! 🏆**
