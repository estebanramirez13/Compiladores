# Analizador Sintáctico LR(1)

Implementación completa de un analizador sintáctico LR(1) con interfaz gráfica desarrollado en Java puro.

## Autor
Esteban Ramírez  
Universidad del Norte  
Compiladores - 2025

## Descripción

Este proyecto implementa un analizador sintáctico LR(1) completo que permite:
- Analizar gramáticas libres de contexto
- Construir la colección canónica de items LR(1)
- Generar la tabla de parsing LR(1)
- Visualizar el autómata de estados con transiciones
- Reconocer cadenas con traza paso a paso

## Características

### 1. Análisis de Gramáticas
- Entrada de gramáticas en formato estándar: `A -> B C`
- Soporte para múltiples producciones usando `|`
- Manejo de producciones vacías (ε)
- Cálculo automático de conjuntos FIRST

### 2. Algoritmo LR(1)
- Construcción de items con lookahead
- Función de closure con propagación de lookaheads
- Función goto para transiciones entre estados
- Generación de colección canónica completa
- Tabla de parsing con acciones shift, reduce y accept

### 3. Visualización
- **Colección Canónica**: Muestra todos los estados con sus items y lookaheads
- **Tabla LR(1)**: Tabla formateada con colores para acciones y goto
- **Grafo de Transiciones**: Visualización gráfica del autómata con layout jerárquico
- **Reconocedor**: Análisis de cadenas con traza completa del stack

## Estructura del Proyecto

```
├── FirstSetsCalculator.java  # Cálculo de conjuntos FIRST
├── GrammarParser.java         # Parser de gramáticas
├── ItemLR1.java               # Clase para items LR(1) con lookahead
├── LR1Parser.java             # Algoritmo LR(1) principal
├── LR1GraphPanel.java         # Visualización del grafo
├── LRParserTrace.java         # Reconocedor de cadenas
└── LR1App.java                # Interfaz gráfica principal
```

## Compilación y Ejecución

### Compilar
```bash
javac -encoding UTF-8 *.java
```

### Ejecutar
```bash
java LR1App
```

## Uso

1. **Ingresar la gramática** en el área de texto superior usando el formato:
   ```
   S -> C C
   C -> c C | d
   ```

2. **Especificar el símbolo inicial** (por defecto es el primero)

3. **Presionar "Construir LR(1)"** para generar:
   - Colección canónica de estados
   - Tabla de parsing
   - Grafo de transiciones

4. **Navegar entre pestañas** para ver diferentes vistas

5. **Reconocer cadenas** en la pestaña correspondiente ingresando tokens separados por espacios

## Ejemplo de Gramática

```
E -> E + T
E -> T
T -> T * F
T -> F
F -> ( E )
F -> id
```

Símbolo inicial: `E`  
Cadena de ejemplo: `id + id * id`

## Requisitos

- Java JDK 8 o superior
- No requiere librerías externas

## Notas Técnicas

- Implementación pura en Java sin dependencias externas
- Uso de Swing para la interfaz gráfica
- Java2D para renderizado del grafo
- Algoritmo de closure optimizado con Map para evitar duplicados
- Layout jerárquico del grafo para mejor visualización

## Licencia

Proyecto académico - Universidad del Norte © 2025
