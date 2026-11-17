# Prolog-Java: Sistema Experto Didáctico de Dragones (HotD) 🐲

Proyecto educativo que conecta **SWI-Prolog** con **Java (Swing)** mediante la librería **JPL** (Java-Prolog Interface) para explorar una base de conocimientos sobre los dragones de *House of the Dragon* (HotD) con una arquitectura simple y comentada paso a paso.

---

## 🚀 Características Principales

1. **Base de Conocimiento Didáctica y Dinámica (`dragones.pl`)**:
   - Documentación detallada de átomos, variables, aridad, predicados dinámicos (`:- dynamic`), hechos y reglas de inferencia.
   - **Múltiples Jinetes**: Relación 1:N para dragones con varios jinetes históricos (Vhagar, Balerion, Caraxes, Meleys, Vermithor, Silverwing, Seasmoke, Dreamfyre).
   - **Reglas de Inferencia**: `tiene_jinete/1`, `sin_jinete/1`, `multiples_jinetes/1`, `jinetes_de/2`, `dragones_de_jinete/2`, `comparten_jinete/3`, `son_aliados/2`, `son_enemigos/2`, `bando_del_jinete/2` y `agregar_jinete/2`.

2. **Arquitectura Java en 2 Clases**:
   - **`ConectorProlog.java` (Conector JPL)**: Encargada exclusivamente de cargar `dragones.pl`, ejecutar consultas contra el motor SWI-Prolog, formatear soluciones, detectar el dragón consultado y persistir nuevos jinetes en disco.
   - **`ConsultaDragones.java` (Vista / Controlador Swing)**: Ventana minimalista y comentada que integra la interfaz gráfica con el conector.

3. **Interfaz Gráfica Didáctica (`ConsultaDragones`)**:
   - **Lado Izquierdo**: Muestra **únicamente la imagen** del dragón seleccionado (retratos oficiales de *Game of Thrones: Dragonfire*, o `[Not found]` para Balerion, Meraxes, Caníbal, Silverwing y Dreamfyre).
   - **Lado Derecho**:
     - **Cabecera**: Nombre del dragón activo y selector desplegable para cambiarlo.
     - **Banco de Preguntas**: Botones con preguntas frecuentes (jinetes, color y bando, origen, estado salvaje, aliados, enemigos, múltiples jinetes, sin jinete y añadir nuevo jinete). Al hacer clic en un botón, la consulta Prolog real se escribe en la consola y se ejecuta desde ahí.
     - **Consola Interactiva (`?- `)**: Muestra las consultas generadas y sus respuestas, y permite escribir manualmente cualquier consulta Prolog para ver cómo responde y sincroniza la imagen en la vista.

---

## 📁 Estructura del Proyecto

```text
prolog-java/
├── dragones.pl                   # Base de conocimientos documentada en Prolog
├── images/                       # Retratos oficiales de los dragones de Dragonfire
├── src/
│   ├── ConectorProlog.java       # Clase conectora con SWI-Prolog (JPL)
│   └── ConsultaDragones.java     # Clase Vista/Controlador en Java Swing
├── PLF.iml                       # Configuración de módulo para IntelliJ IDEA
├── .gitignore                    # Exclusiones de compilación (.class, out/, .idea/)
└── README.md
```

---

## 🛠️ Requisitos Previos

- **Java JDK 17** o superior.
- **SWI-Prolog** instalado (incluye `jpl.jar` y `jpl.dll`).
  - Ruta típica en Windows: `C:\Program Files\swipl\bin` (en el `PATH` del sistema) y `C:\Program Files\swipl\lib\jpl.jar`.

---

## 💻 Ejecución

### Desde IntelliJ IDEA:
1. Abre el proyecto en IntelliJ.
2. Asegúrate de tener referenciada la librería `jpl.jar` en las dependencias del módulo (`Project Structure -> Libraries`).
3. Ejecuta la clase `ConsultaDragones`.

### Desde la Consola (PowerShell / Terminal):
Compilación:
```powershell
javac -encoding UTF-8 -cp "C:\Program Files\swipl\lib\jpl.jar" -d out src\ConectorProlog.java src\ConsultaDragones.java
```

Ejecución:
```powershell
java -cp "out;C:\Program Files\swipl\lib\jpl.jar" ConsultaDragones
```
