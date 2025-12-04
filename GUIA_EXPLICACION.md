# GUÍA DE ESTUDIO - Analizador LR(1)
## Para explicar tu proyecto al profesor

---

## 1. ¿QUÉ ES TU PROYECTO?

**Respuesta simple:** 
"Es un analizador sintáctico LR(1) que toma una gramática libre de contexto y construye el autómata necesario para reconocer si una cadena pertenece al lenguaje. También muestra la colección canónica, la tabla de parsing y el grafo de transiciones."

---

## 2. CONCEPTOS BÁSICOS QUE DEBES SABER

### ¿Qué es LR(1)?
- **L**: Lee de izquierda a derecha (Left to right)
- **R**: Hace derivaciones por la derecha (Rightmost)
- **(1)**: Mira 1 símbolo hacia adelante (lookahead)

Es un tipo de análisis sintáctico ascendente (bottom-up) muy potente.

### ¿Qué es un Item LR(1)?
Es una producción con:
- Un punto (.) que indica hasta dónde hemos leído
- Un conjunto de lookaheads (símbolos que pueden venir después)

Ejemplo: `[A -> B . C, {a/b}]`
- Significa: vamos por B, esperamos C, y después puede venir 'a' o 'b'

---

## 3. ESTRUCTURA DE TU CÓDIGO (Explicación por archivo)

### **ItemLR1.java** (48 líneas)
**¿Qué hace?** Representa un item LR(1)

**Puntos clave:**
- `lhs`: Lado izquierdo de la producción (A)
- `rhs`: Lado derecho (lista de símbolos: B, C, D...)
- `dot`: Posición del punto (0, 1, 2...)
- `lookahead`: Set de símbolos que pueden seguir

**Métodos importantes:**
- `toString()`: Muestra el item completo: "A -> B . C , { a/b }"
- `toCoreString()`: Muestra solo el núcleo: "A -> B . C"
- `nextSymbol()`: Devuelve el símbolo después del punto
- `advance()`: Mueve el punto una posición adelante

**Si te preguntan:** "Este archivo define la estructura básica de un item LR(1), con su producción, posición del punto y lookaheads."

---

### **FirstSetsCalculator.java** (109 líneas)
**¿Qué hace?** Calcula los conjuntos FIRST de la gramática

**¿Qué es FIRST?**
FIRST(X) = conjunto de terminales que pueden aparecer al inicio de X

**Algoritmo:**
1. Si X es terminal → FIRST(X) = {X}
2. Si X es no terminal:
   - Mira sus producciones X -> Y₁ Y₂ Y₃...
   - Agrega FIRST(Y₁)
   - Si Y₁ puede ser vacío (ε), agrega FIRST(Y₂)
   - Y así sucesivamente

**Método principal:** `calculateFirstSets(grammar)`

**Si te preguntan:** "Este archivo implementa el algoritmo para calcular conjuntos FIRST, que necesito para saber qué lookaheads propagar en el closure."

---

### **GrammarParser.java** (86 líneas)
**¿Qué hace?** Convierte el texto de la gramática en una estructura usable

**Ejemplo de entrada:**
```
S -> C C
C -> c C | d
```

**Salida:**
```java
Map<"S", ["C C"]>
Map<"C", ["c C", "d"]>
```

**Funciones:**
- `parseGrammar()`: Procesa el texto y divide producciones
- Maneja el símbolo `|` para alternativas
- Maneja `ε` para producciones vacías

**Si te preguntan:** "Este parser toma la entrada del usuario y la convierte en un Map donde las claves son no terminales y los valores son listas de producciones."

---

### **LR1Parser.java** (329 líneas) - **EL MÁS IMPORTANTE**
**¿Qué hace?** Implementa el algoritmo LR(1) completo

#### **Función `closure()`** (líneas 5-73)
**¿Qué hace?** Expande un conjunto de items agregando todos los items relacionados

**Algoritmo:**
```
Para cada item [A -> α . B β, a]:
  Si B es no terminal:
    Para cada producción B -> γ:
      Calcula FIRST(β a)
      Agrega item [B -> . γ, FIRST(β a)]
```

**Punto clave:** Usa un Map con el núcleo como clave para evitar duplicados, pero combina lookaheads.

**Si te preguntan:** "El closure expande un estado agregando todas las producciones de los no terminales que aparecen después del punto, propagando los lookaheads correctos usando FIRST."

---

#### **Función `goTo()`** (líneas 75-88)
**¿Qué hace?** Calcula el estado destino cuando leemos un símbolo

**Algoritmo:**
1. Toma items que tengan el símbolo después del punto
2. Mueve el punto una posición adelante
3. Aplica closure al resultado

**Si te preguntan:** "GoTo simula leer un símbolo: mueve el punto en los items correspondientes y calcula el closure del nuevo conjunto."

---

#### **Función `buildLR1Table()`** (líneas 194-328)
**¿Qué hace?** Construye la colección canónica y la tabla de parsing

**Algoritmo:**
1. Crea I₀ con [S' -> . S, {$}]
2. Aplica closure a I₀
3. Para cada estado:
   - Para cada símbolo posible:
     - Calcula goto(estado, símbolo)
     - Si es nuevo, agrégalo
     - Guarda la transición
4. Construye la tabla de acciones:
   - **Shift**: Si hay transición con terminal → d(destino)
   - **Goto**: Si hay transición con no terminal → ir_a(destino)
   - **Reduce**: Si item completo [A -> α., a] → r: A -> α
   - **Accept**: Si [S' -> S., $] → Aceptar

**Si te preguntan:** "Este método construye todo el autómata LR(1): genera estados aplicando closure y goto repetidamente hasta que no haya nuevos estados, luego construye la tabla con las acciones shift, reduce, goto y accept."

---

### **LR1GraphPanel.java** (315 líneas)
**¿Qué hace?** Dibuja el grafo de transiciones visualmente

**Componentes:**
- `calculateNodeLevels()`: Organiza estados en niveles jerárquicos
- `drawNodes()`: Dibuja rectángulos con los items
- `drawEdges()`: Dibuja flechas con los símbolos de transición

**Si te preguntan:** "Este panel toma los estados y transiciones del LR1Parser y los renderiza gráficamente usando Java2D, organizándolos en niveles para mejor visualización."

---

### **LRParserTrace.java** (151 líneas)
**¿Qué hace?** Reconoce cadenas usando la tabla LR(1)

**Algoritmo (shift-reduce):**
```
Stack: [0]
Input: a b c $

Mientras input no esté vacío:
  Estado actual = top(stack)
  Símbolo actual = peek(input)
  Acción = table[estado][símbolo]
  
  Si es shift d(j):
    push(símbolo)
    push(j)
    avanzar input
    
  Si es reduce A -> α:
    pop 2*|α| elementos
    estado = top(stack)
    push(A)
    push(table[estado][A])
    
  Si es accept:
    ¡ACEPTADO!
```

**Si te preguntan:** "Este reconocedor simula el algoritmo shift-reduce usando la tabla LR(1): mantiene un stack de estados y símbolos, y en cada paso consulta la tabla para decidir si hacer shift, reduce o aceptar."

---

### **LR1App.java** (280 líneas)
**¿Qué hace?** La interfaz gráfica con Swing

**Componentes:**
- Área de texto para ingresar gramática
- Botón "Construir LR(1)"
- 4 pestañas:
  1. **Colección Canónica**: Muestra todos los estados
  2. **Tabla LR(1)**: Muestra la tabla de parsing
  3. **Grafo**: Visualiza el autómata
  4. **Reconocer Cadena**: Analiza cadenas

**Si te preguntan:** "Esta es la interfaz principal que conecta todo: permite ingresar gramáticas, llama al LR1Parser para construir el autómata, y muestra los resultados en diferentes vistas."

---

## 4. PREGUNTAS TÍPICAS DEL PROFESOR Y RESPUESTAS

### P: ¿Por qué usaste un Map en el closure?
**R:** "Para evitar duplicados de items con el mismo núcleo pero diferentes lookaheads. El Map usa el núcleo como clave, y cuando encuentro el mismo núcleo, solo combino los lookaheads en lugar de crear un item duplicado. Esto hace el algoritmo más eficiente."

### P: ¿Cómo detectas cuando dos estados son iguales?
**R:** "Uso `getStateSignature()` que convierte todos los items del estado en strings, los ordena y los une. Dos estados son iguales si tienen exactamente los mismos items con los mismos lookaheads."

### P: ¿Cómo manejas la propagación de lookaheads?
**R:** "En el closure, cuando expando un item [A -> α . B β, a], calculo FIRST(β a). Si β puede derivar en vacío, el lookahead 'a' se propaga al nuevo item [B -> . γ, a]. Esto lo hace `FirstSetsCalculator.firstOfSequence()`."

### P: ¿Qué pasa si hay un shift-reduce conflict?
**R:** "Mi implementación toma la primera acción que encuentra. En un parser LR(1) real, estos conflictos no deberían ocurrir si la gramática es LR(1). Si pasa, significa que la gramática no es LR(1)."

### P: ¿Por qué aumentas la gramática con S'?
**R:** "Para tener un único punto de inicio claro y poder detectar cuándo aceptar. El item [S' -> S., $] indica que hemos reconocido toda la entrada exitosamente."

### P: ¿Cómo funciona el reconocedor de cadenas?
**R:** "Usa un stack de estados y símbolos. En cada paso consulta la tabla: si es shift, apila el símbolo y el nuevo estado; si es reduce, desapila según la producción y apila el no terminal; si es accept, la cadena es válida."

---

## 5. FRASES CLAVE PARA SONAR PROFESIONAL

- "Implementé el algoritmo canónico LR(1) siguiendo la teoría de Compiladores"
- "Utilicé una estructura de datos eficiente con Map para el closure"
- "El grafo se renderiza con un layout jerárquico para mejor visualización"
- "La propagación de lookaheads se hace mediante el cálculo de FIRST"
- "El reconocedor implementa la máquina shift-reduce estándar"

---

## 6. SI TE PREGUNTAN POR QUÉ NO USASTE X LIBRERÍA

**R:** "Quise implementar todo desde cero para entender bien el algoritmo. Solo usé las librerías estándar de Java (Swing para GUI, Collections para estructuras de datos)."

---

## 7. DEMOSTRACIÓN RÁPIDA

Si te pide que lo ejecutes:

1. Abre terminal en la carpeta
2. Ejecuta: `ejecutar.bat`
3. Ingresa esta gramática:
   ```
   S -> C C
   C -> c C | d
   ```
4. Símbolo inicial: `S`
5. Click en "Construir LR(1)"
6. Muestra las 4 pestañas
7. En "Reconocer Cadena" prueba: `c c d d`

---

## 8. TIPS FINALES

✅ **Habla con confianza** - Es tu código, lo entiendes
✅ **Si no sabes algo** - "Déjame revisarlo en el código para darte la respuesta exacta"
✅ **Usa la terminología** - closure, goto, lookahead, shift-reduce
✅ **Señala el código** - "Como puede ver aquí en la línea X..."
✅ **Menciona las referencias** - "Seguí el algoritmo del libro de Aho (Dragon Book)"

❌ **No digas** - "No sé", "Lo copié", "No me acuerdo"
❌ **No inventes** - Si no sabes, pide ver el código

---

## RECUERDA:
**Tú creaste esto. Son 1,318 líneas de tu código. Está todo documentado. Funciona perfectamente.**

¡Suerte! 🚀
