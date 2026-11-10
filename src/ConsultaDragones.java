import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ConsultaDragones {

    public static void main(String[] args) {
        // Si el usuario pasa --cli, ejecutamos la consola original por terminal
        if (args.length > 0 && args[0].equalsIgnoreCase("--cli")) {
            ejecutarModoConsola();
            return;
        }

        // Por defecto, iniciamos la interfaz gráfica Swing solicitada
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            try {
                VentanaDragones ventana = new VentanaDragones();
                ventana.setVisible(true);
            } catch (Exception e) {
                System.err.println("Error iniciando la interfaz gráfica Swing: " + e.getMessage());
                System.out.println("Iniciando en modo consola alternativo...");
                ejecutarModoConsola();
            }
        });
    }

    private static void ejecutarModoConsola() {
        System.out.println("=================================================");
        System.out.println("        HOUSE OF THE DRAGON (MODO CONSOLA)       ");
        System.out.println("=================================================");

        PrologService service = PrologService.getInstance();
        boolean ok = service.inicializar("dragones.pl");
        if (!ok) {
            System.err.println("Error: No se pudo cargar dragones.pl.");
            return;
        }

        java.util.Scanner scanner = new java.util.Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("-------------------------------------------------");
            System.out.println("Selecciona una opción:");
            System.out.println("1. Consultar jinetes de un dragón");
            System.out.println("2. Consultar los dragones de un bando (negros / verdes)");
            System.out.println("3. Añadir jinete a un dragón");
            System.out.println("4. Dragones con múltiples jinetes");
            System.out.println("5. Verificar si dos dragones son rivales/enemigos");
            System.out.println("6. Realizar una consulta libre en Prolog");
            System.out.println("7. Salir");
            System.out.print("Opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    System.out.print("Nombre del dragón: ");
                    String d = scanner.nextLine().trim();
                    DragonInfo info = service.obtenerDetallesDragon(d);
                    System.out.println("-> Jinetes de " + info.getNombreFormateado() + ": " + info.getResumenJinetes());
                    break;
                case "2":
                    System.out.print("Bando (negros / verdes): ");
                    String b = scanner.nextLine().trim();
                    System.out.println(service.consultarPorBando(b));
                    break;
                case "3":
                    System.out.print("Nombre del dragón: ");
                    String drag = scanner.nextLine().trim();
                    System.out.print("Nombre del jinete a agregar: ");
                    String jin = scanner.nextLine().trim();
                    boolean agregado = service.agregarJinete(drag, jin);
                    if (agregado) {
                        System.out.println("-> Jinete agregado exitosamente.");
                    } else {
                        System.out.println("-> No se pudo agregar el jinete.");
                    }
                    break;
                case "4":
                    System.out.println(service.consultarMultiplesJinetes());
                    break;
                case "5":
                    System.out.print("Primer dragón: ");
                    String d1 = scanner.nextLine().trim();
                    System.out.print("Segundo dragón: ");
                    String d2 = scanner.nextLine().trim();
                    System.out.println(service.compararDragones(d1, d2));
                    break;
                case "6":
                    System.out.print("Escribe tu consulta Prolog: ");
                    String q = scanner.nextLine().trim();
                    System.out.println(service.ejecutarConsultaManual(q));
                    break;
                case "7":
                    continuar = false;
                    System.out.println("¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
            System.out.println();
        }
        scanner.close();
    }
}
