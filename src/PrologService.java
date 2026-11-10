import org.jpl7.Atom;
import org.jpl7.Query;
import org.jpl7.Term;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PrologService {
    private static PrologService instance;
    private File archivoProlog;
    private boolean cargado = false;

    private PrologService() {
    }

    public static synchronized PrologService getInstance() {
        if (instance == null) {
            instance = new PrologService();
        }
        return instance;
    }

    public synchronized boolean inicializar(String rutaArchivo) {
        try {
            archivoProlog = new File(rutaArchivo);
            if (!archivoProlog.exists()) {
                System.err.println("No se encontró el archivo: " + archivoProlog.getAbsolutePath());
                return false;
            }

            String rutaNormalizada = archivoProlog.getAbsolutePath().replace("\\", "/");
            Query q = new Query("consult", new Term[]{new Atom(rutaNormalizada)});
            cargado = q.hasSolution();
            return cargado;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isCargado() {
        return cargado;
    }

    /**
     * Devuelve la lista ordenada de todos los nombres de dragones registrados.
     */
    public List<String> obtenerDragones() {
        Set<String> dragones = new TreeSet<>();
        if (!cargado) return new ArrayList<>(dragones);

        try {
            Query q = new Query("dragon(D)");
            while (q.hasMoreSolutions()) {
                Map<String, Term> sol = q.nextSolution();
                dragones.add(sol.get("D").name());
            }
        } catch (Exception e) {
            System.err.println("Error al obtener dragones: " + e.getMessage());
        }
        return new ArrayList<>(dragones);
    }

    /**
     * Obtiene la información completa de un dragón específico.
     */
    public DragonInfo obtenerDetallesDragon(String dragon) {
        String d = normalizarAtomo(dragon);
        DragonInfo info = new DragonInfo(d);
        if (!cargado) return info;

        try {
            // Jinetes
            Query qJinetes = new Query("jinete(" + d + ", J)");
            while (qJinetes.hasMoreSolutions()) {
                Map<String, Term> sol = qJinetes.nextSolution();
                info.addJinete(sol.get("J").name());
            }

            // Bando
            Query qBando = new Query("bando(" + d + ", B)");
            if (qBando.hasSolution()) {
                info.setBando(qBando.oneSolution().get("B").name());
            }

            // Color
            Query qColor = new Query("color(" + d + ", C)");
            if (qColor.hasSolution()) {
                info.setColor(qColor.oneSolution().get("C").name());
            }

            // Lugar de nacimiento
            Query qNacido = new Query("nacido_en(" + d + ", L)");
            if (qNacido.hasSolution()) {
                info.setNacidoEn(qNacido.oneSolution().get("L").name());
            }

            // Salvaje
            Query qSalvaje = new Query("salvaje(" + d + ")");
            info.setSalvaje(qSalvaje.hasSolution());

        } catch (Exception e) {
            System.err.println("Error al obtener detalles de " + dragon + ": " + e.getMessage());
        }

        return info;
    }

    /**
     * Agrega un jinete dinámicamente tanto en memoria (assertz) como persistido en dragones.pl
     */
    public synchronized boolean agregarJinete(String dragon, String jinete) {
        if (!cargado) return false;

        String d = normalizarAtomo(dragon);
        String j = normalizarAtomo(jinete);

        if (d.isEmpty() || j.isEmpty()) return false;

        try {
            // Verificar si el dragón existe; si no, registrarlo
            Query qExisteDragon = new Query("dragon(" + d + ")");
            boolean dragonExiste = qExisteDragon.hasSolution();
            if (!dragonExiste) {
                new Query("assertz(dragon(" + d + "))").hasSolution();
            }

            // Verificar si ya tiene registrado ese jinete
            Query qExisteJinete = new Query("jinete(" + d + ", " + j + ")");
            if (qExisteJinete.hasSolution()) {
                return true; // Ya existe, no duplicar
            }

            // Realizar assert en Prolog
            Query qAssert = new Query("assertz(jinete(" + d + ", " + j + "))");
            boolean ok = qAssert.hasSolution();

            if (ok) {
                // Persistir en el archivo dragones.pl
                persistirEnArchivo(d, j, !dragonExiste);
            }
            return ok;
        } catch (Exception e) {
            System.err.println("Error agregando jinete: " + e.getMessage());
            return false;
        }
    }

    private void persistirEnArchivo(String dragon, String jinete, boolean guardarDragonNuevo) {
        if (archivoProlog == null) return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoProlog, true))) {
            writer.newLine();
            writer.write("% --- Registro añadido dinámicamente desde la interfaz ---");
            writer.newLine();
            if (guardarDragonNuevo) {
                writer.write("dragon(" + dragon + ").");
                writer.newLine();
            }
            writer.write("jinete(" + dragon + ", " + jinete + ").");
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al persistir en archivo: " + e.getMessage());
        }
    }

    /**
     * Ejecuta una consulta libre escrita por el usuario.
     */
    public String ejecutarConsultaManual(String consultaStr) {
        if (!cargado) return "Error: La base de conocimientos no está cargada.";

        String queryLimpia = consultaStr.trim();
        if (queryLimpia.startsWith("?-")) {
            queryLimpia = queryLimpia.substring(2).trim();
        }
        if (queryLimpia.endsWith(".")) {
            queryLimpia = queryLimpia.substring(0, queryLimpia.length() - 1).trim();
        }

        if (queryLimpia.isEmpty()) {
            return "Consulta vacía.";
        }

        StringBuilder salida = new StringBuilder();
        try {
            Query q = new Query(queryLimpia);

            if (!q.hasSolution()) {
                return "false.\n(Sin soluciones para la consulta)";
            }

            int count = 0;
            while (q.hasMoreSolutions()) {
                count++;
                Map<String, Term> solucion = q.nextSolution();
                if (solucion.isEmpty()) {
                    // Consulta cerrada / booleana sin variables (ej. jinete(vhagar, aemond))
                    return "true.\n";
                }

                salida.append("Solución ").append(count).append(":\n");
                for (Map.Entry<String, Term> ent : solucion.entrySet()) {
                    salida.append("   ").append(ent.getKey()).append(" = ").append(ent.getValue().toString()).append("\n");
                }
            }

            if (count == 0) {
                salida.append("false.\n");
            }
        } catch (Exception e) {
            return "Error de sintaxis o ejecución en Prolog:\n" + e.getMessage() + "\n";
        }

        return salida.toString();
    }

    /**
     * Consultas rápidas predefinidas
     */
    public String consultarTodosDragonesYJinetes() {
        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("       DRAGONES REGISTRADOS Y SUS JINETES         \n");
        sb.append("==================================================\n\n");

        List<String> dragones = obtenerDragones();
        for (String d : dragones) {
            DragonInfo info = obtenerDetallesDragon(d);
            sb.append("🐲 ").append(info.getNombreFormateado());
            if (info.isSalvaje()) {
                sb.append(" [Salvaje]");
            }
            sb.append(" (Bando: ").append(DragonInfo.formatearNombre(info.getBando())).append(")\n");
            
            List<String> jinetes = info.getJinetes();
            if (jinetes.isEmpty()) {
                sb.append("   * Sin jinete registrado.\n");
            } else {
                for (String j : jinetes) {
                    sb.append("   * ").append(DragonInfo.formatearNombre(j)).append("\n");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String consultarPorBando(String bando) {
        String b = normalizarAtomo(bando);
        StringBuilder sb = new StringBuilder();
        sb.append("Dragones del bando '").append(DragonInfo.formatearNombre(b)).append("':\n");

        try {
            Query q = new Query("bando(D, " + b + ")");
            int count = 0;
            while (q.hasMoreSolutions()) {
                Map<String, Term> sol = q.nextSolution();
                String d = sol.get("D").name();
                DragonInfo info = obtenerDetallesDragon(d);
                count++;
                sb.append(count).append(". ").append(info.getNombreFormateado())
                  .append(" -> Color: ").append(DragonInfo.formatearNombre(info.getColor()))
                  .append(" | Jinetes: ").append(info.getResumenJinetes()).append("\n");
            }
            if (count == 0) {
                sb.append("No se encontraron dragones para este bando.\n");
            }
        } catch (Exception e) {
            sb.append("Error: ").append(e.getMessage());
        }
        return sb.toString();
    }

    public String consultarMultiplesJinetes() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dragones que han tenido MÚLTIPLES JINETES a lo largo de la historia:\n\n");

        try {
            Query q = new Query("multiples_jinetes(D)");
            int count = 0;
            while (q.hasMoreSolutions()) {
                count++;
                String d = q.nextSolution().get("D").name();
                DragonInfo info = obtenerDetallesDragon(d);
                sb.append(count).append(". 🐲 ").append(info.getNombreFormateado())
                  .append(" (").append(info.getJinetes().size()).append(" jinetes):\n");
                for (String j : info.getJinetes()) {
                    sb.append("     - ").append(DragonInfo.formatearNombre(j)).append("\n");
                }
            }
            if (count == 0) {
                sb.append("Ningún dragón tiene múltiples jinetes registrados.\n");
            }
        } catch (Exception e) {
            sb.append("Error: ").append(e.getMessage());
        }
        return sb.toString();
    }

    public String consultarSalvajesYSinJinete() {
        StringBuilder sb = new StringBuilder();
        sb.append("DRAGONES SALVAJES O SIN JINETE:\n\n");

        try {
            sb.append("• Dragones declarados salvajes:\n");
            Query qSal = new Query("salvaje(D)");
            while (qSal.hasMoreSolutions()) {
                String d = qSal.nextSolution().get("D").name();
                sb.append("  - ").append(DragonInfo.formatearNombre(d)).append("\n");
            }

            sb.append("\n• Dragones que no tienen jinete registrado actualmente:\n");
            Query qSin = new Query("sin_jinete(D)");
            while (qSin.hasMoreSolutions()) {
                String d = qSin.nextSolution().get("D").name();
                sb.append("  - ").append(DragonInfo.formatearNombre(d)).append("\n");
            }
        } catch (Exception e) {
            sb.append("Error: ").append(e.getMessage());
        }
        return sb.toString();
    }

    public String compararDragones(String d1, String d2) {
        String atom1 = normalizarAtomo(d1);
        String atom2 = normalizarAtomo(d2);

        if (atom1.equals(atom2)) {
            return "Has seleccionado el mismo dragón (" + DragonInfo.formatearNombre(atom1) + ").";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Comparación entre ").append(DragonInfo.formatearNombre(atom1))
          .append(" y ").append(DragonInfo.formatearNombre(atom2)).append(":\n\n");

        DragonInfo info1 = obtenerDetallesDragon(atom1);
        DragonInfo info2 = obtenerDetallesDragon(atom2);

        sb.append("• ").append(info1.getNombreFormateado()).append(" -> Bando: ").append(DragonInfo.formatearNombre(info1.getBando())).append("\n");
        sb.append("• ").append(info2.getNombreFormateado()).append(" -> Bando: ").append(DragonInfo.formatearNombre(info2.getBando())).append("\n\n");

        Query qEnemigos = new Query("son_enemigos(" + atom1 + ", " + atom2 + ")");
        if (qEnemigos.hasSolution()) {
            sb.append("⚔️ ¡SON ENEMIGOS / RIVALES! Pertenecen a bandos contrarios en la Danza.\n");
        } else {
            Query qAliados = new Query("son_aliados(" + atom1 + ", " + atom2 + ")");
            if (qAliados.hasSolution()) {
                sb.append("🛡️ ¡SON ALIADOS! Ambos luchan por el mismo bando (").append(DragonInfo.formatearNombre(info1.getBando())).append(").\n");
            } else {
                sb.append("⚖️ No son enemigos directos ni aliados declarados (uno o ambos son salvajes o de otra época).\n");
            }
        }

        // Verificar si compartieron algún jinete
        Query qComp = new Query("comparten_jinete(" + atom1 + ", " + atom2 + ", J)");
        if (qComp.hasSolution()) {
            sb.append("\n👑 Dato curioso: ¡Ambos dragones fueron montados por el mismo jinete en distintas épocas!\n");
            while (qComp.hasMoreSolutions()) {
                String j = qComp.nextSolution().get("J").name();
                sb.append("   - Jinete compartido: ").append(DragonInfo.formatearNombre(j)).append("\n");
            }
        }

        return sb.toString();
    }

    public static String normalizarAtomo(String texto) {
        if (texto == null) return "";
        return texto.trim()
                .toLowerCase()
                .replace(" ", "_")
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ñ", "n");
    }
}
