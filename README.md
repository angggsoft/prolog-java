# Prolog-Java: Sistema Experto de Dragones (House of the Dragon) 🐲

Proyecto que integra **SWI-Prolog** con **Java (Swing)** mediante la librería **JPL** (Java-Prolog Interface) para implementar un sistema experto y base de conocimientos interactiva sobre los dragones del universo de *House of the Dragon* (HotD).

---

## 🚀 Características Principales

1. **Base de Conocimiento Dinámica en Prolog (`dragones.pl`)**:
   - Registro de dragones, colores, lugares de origen, estado salvaje y facciones (*Los Negros*, *Los Verdes*, *Conquista*).
   - **Múltiples Jinetes**: Varios dragones históricos poseen múltiples jinetes registrados a lo largo de las distintas épocas (Vhagar, Balerion, Caraxes, Meleys, Vermithor, etc.).
   - **Reglas de Inferencia**:
     - Detección de múltiples jinetes (`multiples_jinetes/1`).
     - Alianzas y rivalidades (`son_aliados/2`, `son_enemigos/2`).
     - Dragones sin jinete y salvajes (`sin_jinete/1`, `salvaje/1`).
     - Jinetes compartidos entre dragones (`comparten_jinete/3`).
     - Registro dinámico en memoria (`agregar_jinete/2`).

2. **Gestión y Persistencia de Nuevos Jinetes**:
   - Formulario en la interfaz para agregar nuevos jinetes a cualquier dragón.
   - Aplica `assertz/1` en la memoria viva de Prolog y persiste los cambios automáticamente al final del archivo `dragones.pl` para conservarlos tras reiniciar la aplicación.

3. **Interfaz Gráfica en Java Swing (`VentanaDragones`)**:
   - **Panel Izquierdo**: Retrato oficial en alta definición para los dragones pertenecientes al plantel del juego *Game of Thrones: Dragonfire*. Para aquellos dragones que no formaban parte de dicho juego originalmente (como Balerion, Meraxes, Silverwing, Dreamfyre y Caníbal), el visor muestra limpiamente un estado `[Not found]` indicando que no cuentan con modelo original en el juego. Acompañado de la ficha resumen de atributos y jinetes.
   - **Panel Superior Derecho**: Selector rápido de dragón, formulario para añadir jinetes, botones de consultas predefinidas (Los Negros, Los Verdes, Múltiples Jinetes, Salvajes, Comparador de Rivales/Aliados) y visor de resultados.
   - **Panel Inferior Derecho**: Consola interactiva estilo terminal (`?- `) con autoscroll que permite ejecutar cualquier consulta Prolog libremente, mostrando todas las soluciones resueltas (`X = ...`), respuestas booleanas (`true.` / `false.`) y menú de ejemplos rápidos.

---

## 📁 Estructura del Proyecto

```text
prolog-java/
├── dragones.pl                   # Base de conocimiento y reglas en Prolog
├── images/                       # Retratos oficiales de los dragones del juego Dragonfire
├── src/
│   ├── ConsultaDragones.java     # Punto de entrada principal (GUI o CLI)
│   ├── VentanaDragones.java      # Interfaz gráfica de usuario en Swing
│   ├── PanelImagenDragon.java    # Renderizado y escalado de imágenes
│   ├── PrologService.java        # Controlador de conexión JPL, queries y persistencia
│   └── DragonInfo.java           # Modelo de datos del dragón
├── PLF.iml                       # Configuración de módulo para IntelliJ IDEA
├── .gitignore                    # Exclusiones de compilación (.class, out/, .idea/)
└── README.md
```

---

## 🛠️ Requisitos Previos

- **Java JDK 17** o superior.
- **SWI-Prolog** instalado (incluye `jpl.jar` y `jpl.dll`).
  - Ruta típica en Windows: `C:\Program Files\swipl\bin` (debe estar en el `PATH` del sistema) y `C:\Program Files\swipl\lib\jpl.jar`.

---

## 💻 Ejecución

### Desde IntelliJ IDEA:
1. Abre el proyecto en IntelliJ.
2. Asegúrate de tener referenciada la librería `jpl.jar` en las dependencias del módulo (`Project Structure -> Libraries`).
3. Ejecuta la clase `ConsultaDragones` o `VentanaDragones`.

### Desde la Consola (PowerShell / Terminal):
Compilación:
```powershell
javac -encoding UTF-8 -cp "C:\Program Files\swipl\lib\jpl.jar" -d out src\*.java
```

Ejecución (Modo Gráfico):
```powershell
java -cp "out;C:\Program Files\swipl\lib\jpl.jar" ConsultaDragones
```

Ejecución (Modo Terminal CLI):
```powershell
java -cp "out;C:\Program Files\swipl\lib\jpl.jar" ConsultaDragones --cli
```
