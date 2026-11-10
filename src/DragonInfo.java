import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DragonInfo {
    private final String nombre;
    private String bando;
    private String color;
    private String nacidoEn;
    private boolean salvaje;
    private final List<String> jinetes;

    public DragonInfo(String nombre) {
        this.nombre = nombre;
        this.jinetes = new ArrayList<>();
        this.bando = "Desconocido / Sin bando";
        this.color = "Desconocido";
        this.nacidoEn = "Desconocido";
        this.salvaje = false;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombreFormateado() {
        if (nombre == null || nombre.isEmpty()) return "";
        String[] partes = nombre.split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : partes) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0)))
                  .append(p.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }

    public String getBando() {
        return bando;
    }

    public void setBando(String bando) {
        this.bando = bando;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNacidoEn() {
        return nacidoEn;
    }

    public void setNacidoEn(String nacidoEn) {
        this.nacidoEn = nacidoEn;
    }

    public boolean isSalvaje() {
        return salvaje;
    }

    public void setSalvaje(boolean salvaje) {
        this.salvaje = salvaje;
    }

    public List<String> getJinetes() {
        return Collections.unmodifiableList(jinetes);
    }

    public void addJinete(String jinete) {
        if (jinete != null && !jinete.trim().isEmpty() && !jinetes.contains(jinete.trim())) {
            jinetes.add(jinete.trim());
        }
    }

    public boolean tieneJinetes() {
        return !jinetes.isEmpty();
    }

    public boolean tieneMultiplesJinetes() {
        return jinetes.size() > 1;
    }

    public String getResumenJinetes() {
        if (jinetes.isEmpty()) {
            return salvaje ? "Sin jinetes (Salvaje)" : "Sin jinete registrado";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < jinetes.size(); i++) {
            sb.append(formatearNombre(jinetes.get(i)));
            if (i < jinetes.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String formatearNombre(String raw) {
        if (raw == null || raw.isEmpty()) return "";
        String[] partes = raw.split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : partes) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0)))
                  .append(p.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }

    @Override
    public String toString() {
        return getNombreFormateado();
    }
}
