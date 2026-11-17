import org.jpl7.Atom;
import org.jpl7.Query;
import org.jpl7.Term;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ============================================================================
 *  Esta clase tiene solo una responsabilidad, servir como puente de comunicación
 *  entre Java y el motor de SWI-Prolog utilizando la librería oficial JPL (org.jpl7.*).
 *
 *  ¿Cómo funciona JPL?
 *  - Atom:  Representa un átomo constante de Prolog (ej. new Atom("dragones.pl")).
 *  - Term:  Clase base para cualquier término Prolog (átomos, variables, listas).
 *  - Query: Representa una consulta ?- Meta. Permite abrir la consulta, comprobar
 *           si tiene soluciones y recorrerlas mediante backtracking.
 * ============================================================================
 */
public class ConectorProlog {

    private File archivoProlog;
    private boolean conectado = false;

    /**
     * Guarda el nombre del último dragón involucrado en una consulta o respuesta,
     * para que la interfaz gráfica pueda actualizar automáticamente su imagen.
     */
    private String ultimoDragonDetectado = "";

    /**
     * Carga el archivo .pl en la memoria del motor Prolog usando consult/1.
     *
     * @param rutaArchivo Ruta al archivo de base de conocimientos (ej. "dragones.pl").
     * @return true si el archivo existe y Prolog lo compiló sin errores.
     */
    public boolean conectar(String rutaArchivo) {
        try {
            archivoProlog = new File(rutaArchivo);
            if (!archivoProlog.exists()) {
                System.err.println("No se encontró el archivo: " + archivoProlog.getAbsolutePath());
                conectado = false;
                return false;
            }

            // Prolog requiere barras inclinadas '/' en rutas, incluso en Windows.
            String rutaNormalizada = archivoProlog.getAbsolutePath().replace("\\", "/");

            // Equivale en Prolog a ejecutar: ?- consult('C:/.../dragones.pl').
            Query consultaCarga = new Query("consult", new Term[]{new Atom(rutaNormalizada)});
            conectado = consultaCarga.hasSolution();
            return conectado;
        } catch (Exception e) {
            System.err.println("Error al conectar con Prolog: " + e.getMessage());
            conectado = false;
            return false;
        }
    }

    /**
     * Indica si la base de conocimientos fue cargada correctamente.
     */
    public boolean isConectado() {
        return conectado;
    }

    /**
     * Devuelve el último dragón detectado en la consulta más reciente o su resultado.
     */
    public String getUltimoDragonDetectado() {
        return ultimoDragonDetectado;
    }

    /**
     * Consulta todos los hechos dragon(D) declarados en la base de conocimientos.
     *
     * @return Lista alfabética con los átomos de todos los dragones registrados.
     */
    public List<String> obtenerDragones() {
        Set<String> dragones = new TreeSet<>();
        if (!conectado) {
            return new ArrayList<>(dragones);
        }

        Query q = null;
        try {
            q = new Query("dragon(D)");
            while (q.hasMoreSolutions()) {
                Map<String, Term> solucion = q.nextSolution();
                dragones.add(solucion.get("D").name());
            }
        } catch (Exception e) {
            System.err.println("Error al consultar lista de dragones: " + e.getMessage());
        } finally {
            if (q != null) {
                q.close();
            }
        }
        return new ArrayList<>(dragones);
    }

    /**
     * Ejecuta cualquier consulta Prolog recibida como texto (desde la consola
     * manual o escrita por un botón del banco de preguntas) y retorna la
     * respuesta formateada tal como respondería un intérprete Prolog.
     *
     * Además:
     * - Si se ejecuta agregar_jinete/2 o assertz(jinete/2), persiste el nuevo
     *   hecho en el archivo dragones.pl para conservarlo en disco.
     * - Detecta si la consulta o sus resultados mencionan algún dragón para
     *   sincronizar la imagen en la interfaz gráfica.
     *
     * @param consultaUsuario Texto de la consulta (con o sin '?-' y punto final '.').
     * @return Cadena con las soluciones encontradas, "true.", "false." o el error.
     */
    public String ejecutarConsulta(String consultaUsuario) {
        if (!conectado) {
            return "Error: La base de conocimientos no está conectada.";
        }

        // 1. Limpiar el texto para que JPL lo acepte (sin '?-' al inicio ni '.' al final)
        String consultaLimpia = limpiarConsulta(consultaUsuario);
        if (consultaLimpia.isEmpty()) {
            return "Consulta vacía. Escribe una meta Prolog válida.";
        }

        // 2. Verificar si la consulta intenta agregar un jinete para persistirlo en disco si es nuevo
        String[] nuevoJinete = extraerRegistroJinete(consultaLimpia);
        boolean dragonExistiaAntes = false;
        boolean jineteExistiaAntes = false;

        if (nuevoJinete != null) {
            dragonExistiaAntes = existeHecho("dragon(" + nuevoJinete[0] + ")");
            jineteExistiaAntes = existeHecho("jinete(" + nuevoJinete[0] + ", " + nuevoJinete[1] + ")");
        }

        // 3. Ejecutar la consulta en SWI-Prolog (una única ejecución usando hasMoreSolutions)
        StringBuilder salida = new StringBuilder();
        List<String> valoresEnSoluciones = new ArrayList<>();
        Query q = null;

        try {
            q = new Query(consultaLimpia);
            int contador = 0;

            while (q.hasMoreSolutions()) {
                contador++;
                Map<String, Term> solucion = q.nextSolution();

                // Si el mapa está vacío, la consulta no contenía variables (consulta booleana)
                if (solucion.isEmpty()) {
                    salida.append("true.\n");
                    break;
                }

                salida.append("Solución ").append(contador).append(":\n");
                for (Map.Entry<String, Term> entrada : solucion.entrySet()) {
                    String nombreVar = entrada.getKey();
                    String valorTerm = entrada.getValue().toString();
                    salida.append("  ").append(nombreVar).append(" = ").append(valorTerm).append("\n");
                    valoresEnSoluciones.add(valorTerm);
                }
            }

            if (contador == 0) {
                salida.append("false.\n");
            } else if (nuevoJinete != null && !jineteExistiaAntes) {
                // La consulta de inserción tuvo éxito y el hecho es nuevo: guardar en dragones.pl
                persistirJineteEnArchivo(nuevoJinete[0], nuevoJinete[1], !dragonExistiaAntes);
                salida.append("(Hecho guardado permanentemente en dragones.pl)\n");
            }

            // 4. Detectar el dragón involucrado para sincronizar la interfaz gráfica
            detectarDragonEnConsultaOSoluciones(consultaLimpia, valoresEnSoluciones);

        } catch (Exception e) {
            return "Error de sintaxis o ejecución en Prolog:\n  " + e.getMessage() + "\n";
        } finally {
            if (q != null) {
                q.close();
            }
        }

        return salida.toString();
    }

    /**
     * Quita el prefijo '?-' y el punto final '.' si el usuario los escribió.
     */
    private String limpiarConsulta(String texto) {
        if (texto == null) return "";
        String limpia = texto.trim();
        if (limpia.startsWith("?-")) {
            limpia = limpia.substring(2).trim();
        }
        if (limpia.endsWith(".")) {
            limpia = limpia.substring(0, limpia.length() - 1).trim();
        }
        return limpia;
    }

    /**
     * Comprueba si un hecho puntual existe en la base de conocimientos.
     */
    private boolean existeHecho(String meta) {
        Query q = null;
        try {
            q = new Query(meta);
            return q.hasSolution();
        } catch (Exception e) {
            return false;
        } finally {
            if (q != null) {
                q.close();
            }
        }
    }

    /**
     * Detecta patrones como:
     *   agregar_jinete(dragon, jinete)
     *   assertz(jinete(dragon, jinete))
     *   asserta(jinete(dragon, jinete))
     */
    private String[] extraerRegistroJinete(String consulta) {
        Pattern pAgregar = Pattern.compile(
                "^(?:agregar_jinete|assertz\\s*\\(\\s*jinete|asserta\\s*\\(\\s*jinete)\\s*\\(\\s*([a-z][a-z0-9_]*)\\s*,\\s*([a-z][a-z0-9_]*)\\s*\\)\\)?$"
        );
        Matcher m = pAgregar.matcher(consulta);
        if (m.matches()) {
            return new String[]{m.group(1), m.group(2)};
        }
        return null;
    }

    /**
     * Agrega al final de dragones.pl el nuevo hecho para que persista entre ejecuciones.
     */
    private void persistirJineteEnArchivo(String dragon, String jinete, boolean incluirDragonNuevo) {
        if (archivoProlog == null) return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoProlog, true))) {
            writer.newLine();
            writer.write("% --- Registro añadido dinámicamente desde la consola ---");
            writer.newLine();
            if (incluirDragonNuevo) {
                writer.write("dragon(" + dragon + ").");
                writer.newLine();
            }
            writer.write("jinete(" + dragon + ", " + jinete + ").");
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al guardar en dragones.pl: " + e.getMessage());
        }
    }

    /**
     * Busca si la consulta o las respuestas mencionan alguno de los dragones conocidos.
     */
    private void detectarDragonEnConsultaOSoluciones(String consultaLimpia, List<String> valores) {
        List<String> dragones = obtenerDragones();

        // Primero revisamos si el usuario nombró un dragón explícitamente en su consulta
        for (String d : dragones) {
            if (Pattern.compile("\\b" + Pattern.quote(d) + "\\b").matcher(consultaLimpia).find()) {
                ultimoDragonDetectado = d;
                return;
            }
        }

        // Si no se nombró en la consulta (ej. jinete(Dragon, aemond_targaryen)),
        // revisamos si alguna respuesta devolvió el nombre de un dragón.
        for (String val : valores) {
            for (String d : dragones) {
                if (val.equals(d)) {
                    ultimoDragonDetectado = d;
                    return;
                }
            }
        }
    }

    /**
     * Convierte texto libre (ej. "Daemon Targaryen") a un átomo válido en Prolog ("daemon_targaryen").
     */
    public static String normalizarAtomo(String texto) {
        if (texto == null) return "";
        return texto.trim()
                .toLowerCase()
                .replaceAll("\\s+", "_")
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ñ", "n")
                .replaceAll("[^a-z0-9_]", "");
    }

    /**
     * Convierte un átomo Prolog ("daemon_targaryen") a texto legible ("Daemon Targaryen").
     */
    public static String formatearNombre(String atomo) {
        if (atomo == null || atomo.isEmpty()) return "";
        String[] partes = atomo.split("_");
        StringBuilder sb = new StringBuilder();
        for (String parte : partes) {
            if (!parte.isEmpty()) {
                sb.append(Character.toUpperCase(parte.charAt(0)))
                  .append(parte.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
