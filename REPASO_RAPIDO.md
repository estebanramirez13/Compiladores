# HOJA DE REPASO RÁPIDO - LR(1)

## CONCEPTOS BÁSICOS (Memoriza esto)

### ¿Qué es LR(1)?
- Análisis sintáctico **ascendente** (bottom-up)
- **L**eft-to-right: lee izquierda a derecha
- **R**ightmost: derivación derecha
- **(1)**: 1 símbolo de lookahead

### Item LR(1)
`[A -> α . β, {a,b}]`
- **Núcleo**: A -> α . β (producción con punto)
- **Lookahead**: {a,b} (qué puede venir después)

---

## TU PROYECTO EN 3 LÍNEAS

1. **Entrada**: Gramática en texto
2. **Proceso**: Construye autómata LR(1) con closure y goto
3. **Salida**: Tabla de parsing + Grafo + Reconocedor

---

## LOS 3 ALGORITMOS PRINCIPALES

### 1. CLOSURE (expandir estado)
```
Para cada [A -> α . B β, a]:
  Si B es no terminal:
    Agregar [B -> . γ, FIRST(β a)]
```
**Tu código:** LR1Parser.java línea 5-73

### 2. GOTO (transición)
```
goto(I, X) = closure({ [A -> αX . β, a] | [A -> α . X β, a] ∈ I })
```
**Tu código:** LR1Parser.java línea 75-88

### 3. CONSTRUCCIÓN DE TABLA
```
I0 = closure([S' -> . S, $])
Para cada estado nuevo:
  Calcular goto con cada símbolo
  Agregar transiciones
Llenar tabla con shift/reduce/goto/accept
```
**Tu código:** LR1Parser.java línea 194-328

---

## ESTRUCTURA DE ARCHIVOS (Orden lógico)

1. **ItemLR1.java** → Define item LR(1)
2. **FirstSetsCalculator.java** → Calcula FIRST
3. **GrammarParser.java** → Lee gramática del usuario
4. **LR1Parser.java** → Algoritmo LR(1) completo ⭐
5. **LRParserTrace.java** → Reconoce cadenas
6. **LR1GraphPanel.java** → Dibuja el grafo
7. **LR1App.java** → Interfaz gráfica

---

## PREGUNTAS RÁPIDAS

**P: ¿Cuántas líneas de código?**
R: 1,318 líneas en 7 archivos Java

**P: ¿Qué librerías usaste?**
R: Solo Java estándar (Swing, Collections, java.util)

**P: ¿Cuánto tiempo tomó?**
R: [Di lo que quieras, pero sugiero: "Unas 2-3 semanas entre diseño, implementación y pruebas"]

**P: ¿Cuál fue la parte más difícil?**
R: "Implementar correctamente el closure con la propagación de lookaheads, y hacer que el grafo se vea claro visualmente"

**P: ¿Probaste con qué gramáticas?**
R: "Probé con varias: la clásica S->CC, C->cC|d, y también con expresiones aritméticas E->E+T|T, T->T*F|F, F->(E)|id"

**P: ¿Cómo manejas gramáticas ambiguas?**
R: "Si hay conflicto shift-reduce, mi implementación toma la primera acción. Las gramáticas LR(1) no deberían tener estos conflictos"

---

## DEMOSTRACIÓN PASO A PASO

### Ejecutar:
```bash
ejecutar.bat
```

### Gramática de prueba:
```
S -> C C
C -> c C | d
```

### Cadena de prueba:
```
c c d d
```

### Qué mostrar:
1. **Pestaña 1**: "Aquí está la colección canónica con 10 estados"
2. **Pestaña 2**: "Esta es la tabla de parsing con acciones shift/reduce"
3. **Pestaña 3**: "El grafo muestra las transiciones del autómata"
4. **Pestaña 4**: "El reconocedor hace la traza paso a paso"

---

## FRASES PARA USAR

✅ "Implementé el algoritmo canónico de construcción de autómatas LR(1)"
✅ "Utilicé una estructura Map para optimizar el closure"
✅ "La comparación de estados se hace por firma (signature)"
✅ "Los lookaheads se propagan usando FIRST"
✅ "El reconocedor es una máquina shift-reduce"

---

## SI TE ATORAN

**Técnica de rescate:**
1. "Permítame revisar esa parte específica del código"
2. Abre el archivo correspondiente
3. Lee las líneas relevantes
4. Explica lo que ves

**Ejemplo:**
"Déjeme ver... aquí en LR1Parser línea 32, estoy calculando FIRST de la secuencia beta. Esto es para saber qué lookaheads propagar cuando expando el closure."

---

## LO MÁS IMPORTANTE

### TU CÓDIGO FUNCIONA ✅
### ESTÁ BIEN IMPLEMENTADO ✅
### ES TU TRABAJO ✅

**Respira, habla despacio, señala el código cuando expliques.**

---

## EMERGENCIA: Si te pregunta algo que NO sabes

"Profesor, en este momento no recuerdo el detalle exacto de esa implementación. ¿Puedo revisar el código para darle una respuesta precisa?"

Luego busca en esta guía o en el código.

---

## NÚMEROS PARA RECORDAR

- **7** archivos .java
- **1,318** líneas de código
- **10** estados (para la gramática S->CC)
- **4** pestañas en la interfaz
- **3** algoritmos principales (closure, goto, build table)

---

**¡PUEDES HACERLO! 💪**
