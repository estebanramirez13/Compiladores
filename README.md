# LR(1) Parser Desktop (Java)

## Descripción
Aplicación Java de escritorio que permite construir la colección canónica, la tabla LR(1) y las transiciones para una gramática ingresada por el usuario. La lógica replica la de la versión web/Javascript.

## Lógica y funcionamiento
El programa implementa el algoritmo LR(1) siguiendo la misma lógica que la versión en JavaScript:

1. **Ingreso de gramática**: El usuario ingresa las reglas en formato `A -> B C` y el símbolo inicial.
2. **Cálculo de conjuntos FIRST**: Se calcula el conjunto FIRST para cada símbolo y para secuencias, igual que en JS.
3. **Construcción de items LR(1)**: Cada item representa una producción con un punto y un conjunto de símbolos de anticipación (lookahead).
4. **Closure LR(1)**: Se expande el conjunto de items aplicando la función de cierre, agregando nuevos items según la gramática y los lookaheads.
5. **GoTo LR(1)**: Se calcula la transición entre conjuntos de items al avanzar el punto sobre un símbolo.
6. **Colección canónica**: Se construyen todos los estados LR(1) mediante closure y goto, formando la colección canónica.
7. **Tabla LR(1)**: Se genera la tabla de análisis LR(1) con acciones (shift, reduce, accept) y transiciones (goto) para cada estado y símbolo.
8. **Visualización**: El usuario puede ver la colección canónica, la tabla LR(1) y las transiciones en la interfaz gráfica.

La estructura y los algoritmos son equivalentes a los del código JS original, asegurando resultados idénticos para la misma gramática.

## ¿Cómo debe hacerse LR(1)?

El algoritmo LR(1) se realiza siguiendo estos pasos fundamentales:

1. **Gramática y símbolo inicial**
   - Se parte de una gramática libre de contexto y se define el símbolo inicial.
   - Se agrega una producción inicial aumentada: S' → S.

2. **Cálculo de conjuntos FIRST**
   - Para cada símbolo y secuencia de símbolos, se calcula el conjunto FIRST, que indica los terminales que pueden aparecer al inicio de una derivación.

3. **Definición de Item LR(1)**
   - Un item LR(1) es una tupla (A → α·β, a), donde:
     - A → αβ es una producción.
     - El punto (·) indica la posición de análisis.
     - a es el símbolo de anticipación (lookahead).

4. **Función de cierre (closureLR1)**
   - Dado un conjunto de items, se agregan nuevos items para cada posible derivación, calculando los lookaheads usando FIRST.
   - Se repite hasta que no se puedan agregar más items.

5. **Función de transición (goToLR1)**
   - Para cada conjunto de items y cada símbolo, se avanza el punto sobre ese símbolo y se aplica closureLR1 al resultado.

6. **Construcción de la colección canónica LR(1)**
   - Se parte del item inicial y se generan todos los estados posibles aplicando goToLR1 y closureLR1.
   - Cada estado es un conjunto de items LR(1).

7. **Construcción de la tabla LR(1)**
   - Para cada estado y símbolo:
     - Si el punto puede avanzar sobre un terminal, se genera una acción de desplazamiento (shift).
     - Si el punto está al final de una producción, se genera una acción de reducción (reduce) para los símbolos de lookahead.
     - Si el item es el inicial aumentado y el lookahead es `$`, se genera la acción de aceptación (accept).
     - Si el punto puede avanzar sobre un no terminal, se genera una transición (goto).

8. **Resolución de conflictos**
   - Si hay conflictos shift/reduce o reduce/reduce, la gramática no es LR(1).

9. **Análisis sintáctico**
   - Usando la tabla LR(1), se puede analizar cualquier cadena de entrada siguiendo las acciones y transiciones.

---

Este procedimiento es el estándar académico y práctico para construir analizadores LR(1), y es el que implementa este proyecto en Java, igual que la versión original en JavaScript.

## Archivos principales
- `LR1DesktopApp.java`: Interfaz gráfica Swing. Permite ingresar la gramática y ver los resultados.
- `LR1Parser.java`: Implementa el algoritmo LR(1): closure, goto, construcción de la tabla y transiciones.
- `GrammarUtils.java`: Calcula los conjuntos FIRST y FIRST de secuencias.
- `ItemLR1.java`: Representa los items LR(1).
- `LR1Result.java`: Formatea y almacena los resultados.

## Uso
1. Instala el JDK en el PC destino y agrega el bin al PATH.
2. Descomprime el archivo `.zip` con los archivos `.java`.
3. Abre una terminal en la carpeta descomprimida.
4. Compila los archivos:
   ```
   javac *.java
   ```
5. Ejecuta la app:
   ```
   java LR1DesktopApp
   ```
6. Ingresa la gramática y el símbolo inicial en la ventana. Haz clic en “Construir LR(1)” para ver los resultados.

## Ejemplo de gramática
```
E -> E + T
E -> T
T -> T * F
T -> F
F -> ( E )
F -> id
```
Símbolo inicial: `E`

## Requisitos
- JDK 17 o superior instalado en el PC destino.

## Fundamentación matemática de LR(1)

### 1. Gramática libre de contexto
Una gramática G = (N, T, P, S) donde:
- N: conjunto de no terminales
- T: conjunto de terminales
- P: conjunto de producciones (A → α, con A ∈ N y α ∈ (N ∪ T)* )
- S: símbolo inicial

### 2. Conjunto FIRST
Para una secuencia de símbolos X₁X₂...Xₙ, el conjunto FIRST(X₁X₂...Xₙ) es:
- Si X₁ es terminal, FIRST(X₁X₂...Xₙ) = {X₁}
- Si X₁ es no terminal, FIRST(X₁X₂...Xₙ) = FIRST(X₁) ∪ (FIRST(X₂...Xₙ) si ε ∈ FIRST(X₁))
- Se calcula recursivamente hasta que no haya cambios.

### 3. Item LR(1)
Un item LR(1) es un par (A → α·β, a) donde:
- A → αβ ∈ P
- El punto (·) indica la posición de análisis
- a ∈ T ∪ {$} es el símbolo de anticipación (lookahead)

### 4. Función de cierre (closureLR1)
Sea I un conjunto de items LR(1):
- Para cada (A → α·Bβ, a) ∈ I, para cada producción B → γ ∈ P y para cada b ∈ FIRST(βa):
  - Se agrega (B → ·γ, b) a I si no está.
- Se repite hasta que no se puedan agregar más items.

### 5. Función de transición (goToLR1)
Sea I un conjunto de items LR(1) y X ∈ N ∪ T:
- goTo(I, X) = closureLR1({ (A → αX·β, a) | (A → α·Xβ, a) ∈ I })

### 6. Colección canónica LR(1)
- Se parte de closureLR1({ (S' → ·S, $) })
- Para cada conjunto de items I y cada X ∈ N ∪ T, se calcula goTo(I, X)
- Se agregan nuevos conjuntos hasta que no haya más

### 7. Construcción de la tabla LR(1)
Para cada estado i (conjunto de items Iᵢ):
- Si (A → α·aβ, b) ∈ Iᵢ y goTo(Iᵢ, a) = Iⱼ, a ∈ T: acción[i, a] = shift j
- Si (A → α·, a) ∈ Iᵢ y A ≠ S': acción[i, a] = reduce A → α
- Si (S' → S·, $) ∈ Iᵢ: acción[i, $] = accept
- Si (A → α·Bβ, a) ∈ Iᵢ y goTo(Iᵢ, B) = Iⱼ, B ∈ N: goto[i, B] = j

### 8. Análisis LR(1)
El análisis sintáctico se realiza usando la tabla:
- Se mantiene una pila de estados y símbolos
- Se lee la entrada y se aplican las acciones de la tabla
- Se aceptan, desplazan o reducen símbolos según corresponda

### 9. Conflictos
- Si alguna celda de la tabla tiene más de una acción, la gramática no es LR(1)

---

Esta fundamentación matemática asegura que el algoritmo LR(1) implementado en este proyecto es correcto y equivalente al estándar académico.

## Matemática y teoría detrás de la app LR(1) (referencia JS)

La app original en JavaScript, y esta versión en Java, se basan en los siguientes conceptos matemáticos y algoritmos formales:

### 1. Gramática aumentada
Se agrega una producción inicial S' → S para asegurar que el análisis LR(1) tenga un único punto de aceptación.

### 2. Cálculo de conjuntos FIRST
Para cada símbolo X:
- Si X es terminal, FIRST(X) = {X}
- Si X es no terminal, FIRST(X) = {a | X → aα ∈ P, a ∈ T} ∪ {b | X → Y₁Y₂...Yₙ ∈ P, b ∈ FIRST(Y₁), ...}
- Si X → ε ∈ P, entonces ε ∈ FIRST(X)
- Para una secuencia X₁X₂...Xₙ:
  - FIRST(X₁X₂...Xₙ) = FIRST(X₁) ∪ (FIRST(X₂...Xₙ) si ε ∈ FIRST(X₁)), recursivamente

### 3. Item LR(1)
Un item LR(1) es (A → α·β, a), donde:
- El punto indica el avance en la producción
- a es el lookahead, el terminal que puede seguir a la derivación

### 4. Función closureLR1
Para un conjunto de items I:
- Para cada (A → α·Bβ, a) ∈ I, para cada producción B → γ ∈ P y para cada b ∈ FIRST(βa):
  - Se agrega (B → ·γ, b) a I si no está
- Se repite hasta que no haya cambios

### 5. Función goToLR1
Para I y X ∈ N ∪ T:
- goTo(I, X) = closureLR1({ (A → αX·β, a) | (A → α·Xβ, a) ∈ I })

### 6. Colección canónica LR(1)
- Se parte de closureLR1({ (S' → ·S, $) })
- Para cada conjunto de items I y cada X ∈ N ∪ T, se calcula goTo(I, X)
- Se agregan nuevos conjuntos hasta que no haya más

### 7. Construcción de la tabla LR(1)
Para cada estado i (conjunto de items Iᵢ):
- Si (A → α·aβ, b) ∈ Iᵢ y goTo(Iᵢ, a) = Iⱼ, a ∈ T: acción[i, a] = shift j
- Si (A → α·, a) ∈ Iᵢ y A ≠ S': acción[i, a] = reduce A → α
- Si (S' → S·, $) ∈ Iᵢ: acción[i, $] = accept
- Si (A → α·Bβ, a) ∈ Iᵢ y goTo(Iᵢ, B) = Iⱼ, B ∈ N: goto[i, B] = j

### 8. Ejemplo formal de closureLR1
Supón que tienes el item (A → α·Bβ, a):
- Para cada producción B → γ, calcula FIRST(βa)
- Para cada b ∈ FIRST(βa), agrega (B → ·γ, b)

### 9. Ejemplo formal de goToLR1
Para el conjunto I y símbolo X:
- goTo(I, X) = closureLR1({ (A → αX·β, a) | (A → α·Xβ, a) ∈ I })

### 10. Análisis sintáctico LR(1)
- Se mantiene una pila de estados y símbolos
- Se lee la entrada y se aplican las acciones de la tabla
- Se aceptan, desplazan o reducen símbolos según corresponda

### 11. Conflictos
- Si alguna celda de la tabla tiene más de una acción, la gramática no es LR(1)

---

Esta matemática y teoría son la base de la app JS original y de esta versión Java, asegurando equivalencia total en resultados y funcionamiento.

## Algoritmos clave implementados (pseudocódigo y explicación)

### closureLR1 (Función de cierre LR(1))
```pseudo
function closureLR1(items, grammar, firstSets):
    closureMap = mapa vacío
    para cada item en items:
        core = núcleo del item
        si core en closureMap:
            fusionar lookaheads
        sino:
            agregar item al mapa
    repetir hasta que no haya cambios:
        para cada item en closureMap:
            B = símbolo después del punto
            si B es no terminal:
                beta = símbolos después de B
                fBeta = FIRST(beta + lookahead)
                para cada producción B → γ:
                    crear item (B → ·γ, fBeta)
                    si no existe, agregar; si existe, fusionar lookaheads
    devolver items en closureMap
```

### goToLR1 (Función de transición LR(1))
```pseudo
function goToLR1(items, symbol, grammar, firstSets):
    nextItems = []
    para cada item en items:
        si el símbolo después del punto es symbol:
            mover el punto y mantener lookaheads
            agregar a nextItems
    si nextItems no está vacío:
        devolver closureLR1(nextItems, grammar, firstSets)
    sino:
        devolver []
```

### Construcción de la colección canónica y tabla LR(1)
```pseudo
function buildLR1Table(grammar, startSymbol):
    crear gramática aumentada
    calcular FIRST sets
    crear item inicial (S' → ·S, $)
    I0 = closureLR1([item inicial], grammar, firstSets)
    states = [I0]
    transitions = {}
    repetir hasta que no haya cambios:
        para cada estado:
            para cada símbolo:
                targetState = goToLR1(estado, símbolo, ...)
                si targetState es nuevo:
                    agregar a states
                guardar transición
    construir tabla LR(1):
        para cada estado y símbolo:
            si hay shift, reduce, accept, goto, agregar acción
    devolver colección canónica, tabla y transiciones
```

### Implementación en Java
Estos algoritmos están implementados en las clases:
- `LR1Parser.java`: closureLR1, goToLR1, buildLR1Table
- `GrammarUtils.java`: cálculo de FIRST
- `ItemLR1.java`: representación de items

El código Java sigue exactamente este pseudocódigo, asegurando equivalencia con la lógica y resultados de la versión JS original.

## Funcionamiento de la app (solo LR(1))

Esta aplicación Java de escritorio está diseñada exclusivamente para construir y mostrar el análisis LR(1) de una gramática libre de contexto. El flujo de funcionamiento es el siguiente:

1. **Ingreso de datos**
   - El usuario ingresa la gramática en formato `A -> B C` y el símbolo inicial en la interfaz gráfica.

2. **Procesamiento LR(1)**
   - Al pulsar el botón, la app toma la gramática y el símbolo inicial y ejecuta el algoritmo LR(1):
     - Calcula los conjuntos FIRST para todos los símbolos.
     - Construye el item inicial aumentado (S' → ·S, $).
     - Aplica la función de cierre (closureLR1) para generar el primer estado.
     - Itera sobre todos los estados posibles, aplicando la función de transición (goToLR1) y closureLR1 para construir la colección canónica de conjuntos de items LR(1).
     - Para cada estado y símbolo, genera las acciones de la tabla LR(1): desplazamiento (shift), reducción (reduce), aceptación (accept) y transición (goto).

3. **Visualización de resultados**
   - La app muestra en pantalla:
     - La colección canónica LR(1): todos los estados y sus items.
     - La tabla LR(1): acciones y transiciones para cada estado y símbolo.
     - Las transiciones entre estados.

4. **Equivalencia con la teoría y la referencia JS**
   - El algoritmo y los resultados son equivalentes a los de la versión JS original y a la teoría formal de LR(1).
   - Si la gramática no es LR(1), la tabla mostrará los conflictos.

5. **Limitaciones y alcance**
   - La app no realiza el análisis sintáctico de cadenas, solo construye la estructura LR(1).
   - No implementa otros algoritmos (SLR, LALR, etc.), solo LR(1).

---

En resumen, la app toma una gramática, ejecuta el algoritmo LR(1) completo y muestra todos los resultados relevantes para el análisis LR(1), siguiendo la lógica y matemática estándar.

## ¿Cómo es el algoritmo LR(1)?

El algoritmo LR(1) se compone de los siguientes pasos principales:

1. **Gramática aumentada**
   - Se agrega una producción inicial S' → S, donde S es el símbolo inicial original.

2. **Cálculo de conjuntos FIRST**
   - Para cada símbolo y secuencia, se calcula el conjunto FIRST, que indica los terminales que pueden aparecer al inicio de una derivación.

3. **Item LR(1)**
   - Se define un item como (A → α·β, a), donde el punto indica la posición de análisis y a es el lookahead.

4. **Closure LR(1)**
   - Para un conjunto de items, se agregan nuevos items para cada posible derivación:
     - Si el punto está antes de un no terminal B, se agregan todos los items (B → ·γ, b) para cada producción B → γ y cada b ∈ FIRST(βa), donde β son los símbolos después de B y a es el lookahead.
     - Se repite hasta que no haya cambios.

5. **GoTo LR(1)**
   - Para cada conjunto de items y cada símbolo X, se avanza el punto sobre X y se aplica closureLR1 al resultado.

6. **Colección canónica LR(1)**
   - Se parte del item inicial aumentado y se generan todos los estados posibles aplicando goToLR1 y closureLR1.

7. **Construcción de la tabla LR(1)**
   - Para cada estado y símbolo:
     - Si el punto puede avanzar sobre un terminal, se genera una acción de desplazamiento (shift).
     - Si el punto está al final de una producción, se genera una acción de reducción (reduce) para los símbolos de lookahead.
     - Si el item es el inicial aumentado y el lookahead es `$`, se genera la acción de aceptación (accept).
     - Si el punto puede avanzar sobre un no terminal, se genera una transición (goto).

8. **Detección de conflictos**
   - Si alguna celda de la tabla tiene más de una acción, la gramática no es LR(1).

---

Este algoritmo permite construir analizadores sintácticos LR(1) eficientes y es el núcleo de la app, siguiendo la lógica académica y la referencia JS original.

## Explicación técnica para ingenieros de sistemas

Este proyecto implementa el algoritmo LR(1) siguiendo los principios de diseño de compiladores y teoría de lenguajes formales. A continuación se explica cómo se desarrolló y estructuró la solución:

### 1. Diseño modular
- Cada concepto clave del algoritmo LR(1) se encapsula en una clase:
  - `ItemLR1`: Representa los items LR(1) con núcleo, punto y lookahead.
  - `GrammarUtils`: Calcula los conjuntos FIRST y FIRST de secuencias.
  - `LR1Parser`: Implementa closureLR1, goToLR1 y la construcción de la colección canónica y la tabla LR(1).
  - `LR1Result`: Formatea y almacena los resultados para visualización.
  - `LR1DesktopApp`: Proporciona la interfaz gráfica y orquesta el flujo de datos.

### 2. Lógica del algoritmo LR(1)
- Se parte de la gramática aumentada y se calcula FIRST para todos los símbolos.
- Se construye el item inicial y se aplica closureLR1 para generar el primer estado.
- Se exploran todos los estados posibles mediante goToLR1 y closureLR1, almacenando transiciones y evitando duplicados.
- Se genera la tabla LR(1) con acciones (shift, reduce, accept) y transiciones (goto) para cada estado y símbolo.
- Se detectan conflictos para validar si la gramática es LR(1).

### 3. Decisiones técnicas
- Se usan estructuras de datos eficientes (HashMap, HashSet, ArrayList) para manejar estados, transiciones y conjuntos.
- El algoritmo sigue la notación y lógica académica, asegurando equivalencia con la referencia JS y la teoría formal.
- La interfaz Swing permite probar el algoritmo de forma interactiva y visualizar todos los resultados relevantes.

### 4. Equivalencia y portabilidad
- El código Java replica exactamente la lógica de la versión JS, permitiendo comparar resultados y validar el funcionamiento.
- El diseño modular facilita la extensión, el mantenimiento y la integración en otros sistemas.

### 5. Flujo de ejecución
1. El usuario ingresa la gramática y símbolo inicial.
2. La app ejecuta el algoritmo LR(1) completo:
   - Calcula FIRST
   - Construye la colección canónica
   - Genera la tabla LR(1)
3. Se muestran los resultados en pantalla.

---

Esta explicación permite a cualquier ingeniero de sistemas comprender el diseño, la lógica y las decisiones técnicas detrás de la implementación del algoritmo LR(1) en esta app Java.
