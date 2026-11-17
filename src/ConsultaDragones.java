import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * ============================================================================
 *  CLASE VISTA / CONTROLADOR: ConsultaDragones
 * ============================================================================
 *  Implementa una interfaz gráfica sencilla y didáctica en Java Swing para
 *  interactuar con la base de conocimientos de House of the Dragon en Prolog.
 *
 *  Distribución de la ventana:
 *  - LADO IZQUIERDO: Muestra únicamente la imagen del dragón activo (o el
 *    recuadro [Not found] para dragones sin imagen original en Dragonfire).
 *  - LADO DERECHO:
 *      1. Arriba: Nombre grande del dragón activo y selector desplegable.
 *      2. Centro: Banco de Preguntas didáctico (al hacer clic en un botón,
 *         escribe la consulta Prolog real en la consola y la ejecuta).
 *      3. Abajo:  Consola interactiva Prolog (historial + entrada manual ?-).
 * ============================================================================
 */
public class ConsultaDragones extends JFrame {

    // Conector encargado de toda la comunicación con SWI-Prolog (JPL)
    private final ConectorProlog conector;

    // Dragón seleccionado actualmente en la vista
    private String dragonActual = "vhagar";

    // Bandera para evitar eventos recursivos al actualizar el JComboBox por código
    private boolean actualizandoCombo = false;

    // --- Componentes de la Vista ---
    private PanelImagen panelImagen;
    private JLabel lblNombreDragon;
    private JComboBox<String> comboDragones;
    private JTextArea txtConsola;
    private JTextField txtEntradaConsola;

    public ConsultaDragones() {
        super("Sistema Experto HotD — Prolog + Java (Modo Didáctico)");
        this.conector = new ConectorProlog();

        inicializarBaseConocimientos();
        construirInterfaz();
        cargarDragonesIniciales();
    }

    /**
     * Conecta el archivo dragones.pl con el motor SWI-Prolog al iniciar.
     */
    private void inicializarBaseConocimientos() {
        File archivoPl = new File("dragones.pl");
        boolean ok = conector.conectar(archivoPl.getAbsolutePath());
        if (!ok) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo cargar 'dragones.pl'.\nVerifica que el archivo exista en la raíz del proyecto.",
                    "Error de Conexión Prolog",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Construye los paneles izquierdo (imagen) y derecho (nombre, banco, consola).
     */
    private void construirInterfaz() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 700);
        setMinimumSize(new Dimension(880, 580));
        setLocationRelativeTo(null);

        // Divisor horizontal principal: Izquierda (Imagen) | Derecha (Interacción)
        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPrincipal.setDividerLocation(360);
        splitPrincipal.setDividerSize(6);
        splitPrincipal.setContinuousLayout(true);

        // --------------------------------------------------------------------
        // 1. LADO IZQUIERDO: Únicamente la imagen del dragón
        // --------------------------------------------------------------------
        panelImagen = new PanelImagen();
        splitPrincipal.setLeftComponent(panelImagen);

        // --------------------------------------------------------------------
        // 2. LADO DERECHO: Nombre + Banco de Preguntas + Consola Prolog
        // --------------------------------------------------------------------
        JPanel panelDerecho = new JPanel(new BorderLayout(8, 8));
        panelDerecho.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 2.1 Arriba: Nombre grande del dragón y selector rápido
        JPanel panelCabecera = new JPanel(new BorderLayout(8, 4));
        panelCabecera.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 205), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        lblNombreDragon = new JLabel("Vhagar");
        lblNombreDragon.setFont(new Font("SansSerif", Font.BOLD, 24));
        panelCabecera.add(lblNombreDragon, BorderLayout.WEST);

        JPanel panelSelector = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelSelector.add(new JLabel("Cambiar dragón:"));
        comboDragones = new JComboBox<>();
        comboDragones.setPreferredSize(new Dimension(170, 26));
        comboDragones.addActionListener(e -> onCambioEnCombo());
        panelSelector.add(comboDragones);
        panelCabecera.add(panelSelector, BorderLayout.EAST);

        panelDerecho.add(panelCabecera, BorderLayout.NORTH);

        // 2.2 Centro y Abajo: Banco de Preguntas (arriba) + Consola (abajo)
        JPanel panelCuerpoDerecho = new JPanel(new BorderLayout(8, 8));
        panelCuerpoDerecho.add(crearPanelBancoPreguntas(), BorderLayout.NORTH);
        panelCuerpoDerecho.add(crearPanelConsola(), BorderLayout.CENTER);

        panelDerecho.add(panelCuerpoDerecho, BorderLayout.CENTER);

        splitPrincipal.setRightComponent(panelDerecho);
        getContentPane().add(splitPrincipal, BorderLayout.CENTER);
    }

    /**
     * Crea el Banco de Preguntas. Cada botón construye una consulta Prolog real
     * usando el dragón seleccionado, la escribe en la consola y la ejecuta allí.
     */
    private JPanel crearPanelBancoPreguntas() {
        JPanel panelBanco = new JPanel(new GridLayout(3, 3, 6, 6));
        panelBanco.setBorder(BorderFactory.createTitledBorder(
                "Banco de Preguntas (Escribe la consulta en la consola y la ejecuta)"
        ));

        JButton btnJinetes = new JButton("¿Quiénes son sus jinetes?");
        btnJinetes.addActionListener(e ->
                escribirYEjecutarEnConsola("jinete(" + dragonActual + ", Jinete).")
        );

        JButton btnColorBando = new JButton("¿Cuál es su color y bando?");
        btnColorBando.addActionListener(e ->
                escribirYEjecutarEnConsola("color(" + dragonActual + ", Color), bando(" + dragonActual + ", Bando).")
        );

        JButton btnOrigen = new JButton("¿Dónde nació?");
        btnOrigen.addActionListener(e ->
                escribirYEjecutarEnConsola("nacido_en(" + dragonActual + ", Lugar).")
        );

        JButton btnSalvaje = new JButton("¿Es un dragón salvaje?");
        btnSalvaje.addActionListener(e ->
                escribirYEjecutarEnConsola("salvaje(" + dragonActual + ").")
        );

        JButton btnAliados = new JButton("¿Quiénes son sus aliados?");
        btnAliados.addActionListener(e ->
                escribirYEjecutarEnConsola("son_aliados(" + dragonActual + ", Aliado).")
        );

        JButton btnEnemigos = new JButton("¿Quiénes son sus enemigos?");
        btnEnemigos.addActionListener(e ->
                escribirYEjecutarEnConsola("son_enemigos(" + dragonActual + ", Enemigo).")
        );

        JButton btnMultiples = new JButton("Dragones con múltiples jinetes");
        btnMultiples.addActionListener(e ->
                escribirYEjecutarEnConsola("multiples_jinetes(Dragon).")
        );

        JButton btnSinJinete = new JButton("Dragones sin jinete");
        btnSinJinete.addActionListener(e ->
                escribirYEjecutarEnConsola("sin_jinete(Dragon).")
        );

        JButton btnAgregarJinete = new JButton("➕ Añadir nuevo jinete");
        btnAgregarJinete.addActionListener(e -> solicitarYAgregarJinete());

        panelBanco.add(btnJinetes);
        panelBanco.add(btnColorBando);
        panelBanco.add(btnOrigen);
        panelBanco.add(btnSalvaje);
        panelBanco.add(btnAliados);
        panelBanco.add(btnEnemigos);
        panelBanco.add(btnMultiples);
        panelBanco.add(btnSinJinete);
        panelBanco.add(btnAgregarJinete);

        return panelBanco;
    }

    /**
     * Crea la consola interactiva de Prolog con historial de salida y campo de entrada.
     */
    private JPanel crearPanelConsola() {
        JPanel panelConsola = new JPanel(new BorderLayout(6, 6));
        panelConsola.setBorder(BorderFactory.createTitledBorder(
                "Consola Interactiva Prolog (Observa las consultas o escribe manualmente)"
        ));

        txtConsola = new JTextArea();
        txtConsola.setEditable(false);
        txtConsola.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtConsola.setBackground(new Color(24, 24, 28));
        txtConsola.setForeground(new Color(220, 225, 230));
        txtConsola.setCaretColor(Color.WHITE);
        txtConsola.setMargin(new Insets(8, 10, 8, 10));

        JScrollPane scrollConsola = new JScrollPane(txtConsola);
        panelConsola.add(scrollConsola, BorderLayout.CENTER);

        // Barra inferior: Prompt '?-' + Campo de texto + Botones Ejecutar y Limpiar
        JPanel panelEntrada = new JPanel(new BorderLayout(6, 0));

        JLabel lblPrompt = new JLabel(" ?- ");
        lblPrompt.setFont(new Font("Consolas", Font.BOLD, 14));
        panelEntrada.add(lblPrompt, BorderLayout.WEST);

        txtEntradaConsola = new JTextField();
        txtEntradaConsola.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtEntradaConsola.setToolTipText("Escribe una consulta Prolog (ej. jinete(caraxes, X).) y pulsa Enter");
        txtEntradaConsola.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onEjecutarManualmente();
                }
            }
        });
        panelEntrada.add(txtEntradaConsola, BorderLayout.CENTER);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        JButton btnEjecutar = new JButton("Ejecutar");
        btnEjecutar.addActionListener(e -> onEjecutarManualmente());

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> txtConsola.setText(""));

        panelAcciones.add(btnEjecutar);
        panelAcciones.add(btnLimpiar);
        panelEntrada.add(panelAcciones, BorderLayout.EAST);

        panelConsola.add(panelEntrada, BorderLayout.SOUTH);

        imprimirEnConsola(
                "======================================================================\n" +
                "  Consola Didáctica SWI-Prolog (Base de Conocimiento: dragones.pl)    \n" +
                "======================================================================\n" +
                "• Haz clic en cualquier botón del Banco de Preguntas para ver cómo se\n" +
                "  escribe su consulta Prolog y obtener la respuesta aquí abajo.\n" +
                "• También puedes escribir tus propias consultas y presionar Enter.\n\n"
        );

        return panelConsola;
    }

    /**
     * Carga la lista inicial de dragones desde Prolog y muestra el primero.
     */
    private void cargarDragonesIniciales() {
        refrescarComboDragones();
        if (comboDragones.getItemCount() > 0) {
            if (existeEnCombo("vhagar")) {
                seleccionarDragonEnUI("vhagar");
            } else {
                seleccionarDragonEnUI((String) comboDragones.getItemAt(0));
            }
        }
    }

    /**
     * Sincroniza los elementos del JComboBox con los hechos dragon/1 en Prolog.
     */
    private void refrescarComboDragones() {
        actualizandoCombo = true;
        try {
            List<String> dragones = conector.obtenerDragones();
            comboDragones.removeAllItems();
            for (String d : dragones) {
                comboDragones.addItem(d);
            }
            if (dragonActual != null && existeEnCombo(dragonActual)) {
                comboDragones.setSelectedItem(dragonActual);
            }
        } finally {
            actualizandoCombo = false;
        }
    }

    private boolean existeEnCombo(String dragon) {
        for (int i = 0; i < comboDragones.getItemCount(); i++) {
            if (comboDragones.getItemAt(i).equals(dragon)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Manejador cuando el usuario cambia el dragón desde el selector desplegable.
     */
    private void onCambioEnCombo() {
        if (actualizandoCombo) return;
        String seleccionado = (String) comboDragones.getSelectedItem();
        if (seleccionado != null && !seleccionado.isEmpty()) {
            seleccionarDragonEnUI(seleccionado);
            escribirYEjecutarEnConsola("color(" + seleccionado + ", Color), bando(" + seleccionado + ", Bando).");
        }
    }

    /**
     * Abre un diálogo simple para capturar un nuevo jinete y lo registra vía consola.
     */
    private void solicitarYAgregarJinete() {
        String nombreJinete = JOptionPane.showInputDialog(
                this,
                "Ingresa el nombre del nuevo jinete para " + ConectorProlog.formatearNombre(dragonActual) + ":",
                "Añadir Jinete en Prolog",
                JOptionPane.QUESTION_MESSAGE
        );

        if (nombreJinete == null || nombreJinete.trim().isEmpty()) {
            return;
        }

        String atomoJinete = ConectorProlog.normalizarAtomo(nombreJinete);
        if (atomoJinete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre ingresado no es válido para un átomo Prolog.");
            return;
        }

        // Escribimos y ejecutamos la regla agregar_jinete/2 directamente en la consola
        escribirYEjecutarEnConsola("agregar_jinete(" + dragonActual + ", " + atomoJinete + ").");
    }

    /**
     * Flujo didáctico para botones del Banco de Preguntas:
     * 1) Escribe la consulta en la barra de entrada de la consola para que el
     *    usuario vea la sintaxis exacta de Prolog.
     * 2) La envía y ejecuta en la consola, reflejando los cambios en la UI.
     */
    private void escribirYEjecutarEnConsola(String consultaProlog) {
        txtEntradaConsola.setText(consultaProlog);
        ejecutarDesdeConsola(consultaProlog);
    }

    /**
     * Manejador cuando el usuario escribe manualmente en la consola y pulsa Enter o "Ejecutar".
     */
    private void onEjecutarManualmente() {
        String consulta = txtEntradaConsola.getText().trim();
        if (consulta.isEmpty()) return;

        ejecutarDesdeConsola(consulta);
        txtEntradaConsola.setText("");
        txtEntradaConsola.requestFocus();
    }

    /**
     * Controlador central: envía la consulta al conector Prolog, imprime pregunta
     * y respuesta en la consola, y sincroniza la imagen y el título de la ventana.
     */
    private void ejecutarDesdeConsola(String consulta) {
        String consultaMostrada = consulta.endsWith(".") ? consulta : consulta + ".";
        imprimirEnConsola("?- " + consultaMostrada + "\n");

        String respuesta = conector.ejecutarConsulta(consulta);
        imprimirEnConsola(respuesta + "\n");

        // Refrescar lista de dragones por si se añadió un dragón nuevo dinámicamente
        refrescarComboDragones();

        // Si la consulta o su respuesta mencionó algún dragón, actualizar imagen y nombre
        String dragonDetectado = conector.getUltimoDragonDetectado();
        if (dragonDetectado != null && !dragonDetectado.isEmpty()) {
            seleccionarDragonEnUI(dragonDetectado);
        }
    }

    /**
     * Actualiza el título del lado derecho, la selección del combo y la imagen izquierda.
     */
    private void seleccionarDragonEnUI(String dragon) {
        this.dragonActual = dragon;
        lblNombreDragon.setText(ConectorProlog.formatearNombre(dragon));

        actualizandoCombo = true;
        try {
            if (existeEnCombo(dragon)) {
                comboDragones.setSelectedItem(dragon);
            }
        } finally {
            actualizandoCombo = false;
        }

        panelImagen.setDragon(dragon);
    }

    private void imprimirEnConsola(String texto) {
        txtConsola.append(texto);
        txtConsola.setCaretPosition(txtConsola.getDocument().getLength());
    }

    // ========================================================================
    //  COMPONENTE VISUAL IZQUIERDO: PanelImagen
    //  Muestra únicamente la imagen del dragón o el recuadro [Not found].
    // ========================================================================
    private static class PanelImagen extends JPanel {
        // Dragones que no formaban parte originalmente del juego Dragonfire
        private static final Set<String> SIN_IMAGEN_ORIGINAL = new HashSet<>(Arrays.asList(
                "balerion", "meraxes", "canibal", "silverwing", "dreamfyre"
        ));

        private String dragon = "";
        private final Map<String, Image> cacheImagenes = new HashMap<>();

        public PanelImagen() {
            setPreferredSize(new Dimension(360, 660));
            setMinimumSize(new Dimension(280, 400));
            setBackground(new Color(20, 20, 24));
            setBorder(new LineBorder(new Color(60, 60, 65), 1));
        }

        public void setDragon(String dragon) {
            this.dragon = (dragon != null) ? dragon.trim().toLowerCase() : "";
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();

            Image img = cargarImagen(dragon);
            if (img != null) {
                int imgW = img.getWidth(null);
                int imgH = img.getHeight(null);
                if (imgW > 0 && imgH > 0) {
                    double escala = Math.min((double) (w - 24) / imgW, (double) (h - 24) / imgH);
                    int drawW = (int) (imgW * escala);
                    int drawH = (int) (imgH * escala);
                    int drawX = (w - drawW) / 2;
                    int drawY = (h - drawH) / 2;

                    g2.setColor(new Color(40, 40, 46));
                    g2.fillRoundRect(drawX - 4, drawY - 4, drawW + 8, drawH + 8, 12, 12);
                    g2.drawImage(img, drawX, drawY, drawW, drawH, null);
                }
            } else {
                dibujarNotFound(g2, w, h);
            }

            g2.dispose();
        }

        private Image cargarImagen(String nombreDragon) {
            if (nombreDragon == null || nombreDragon.isEmpty()) return null;
            if (SIN_IMAGEN_ORIGINAL.contains(nombreDragon)) return null;
            if (cacheImagenes.containsKey(nombreDragon)) return cacheImagenes.get(nombreDragon);

            String[] extensiones = {".png", ".jpg", ".jpeg"};
            for (String ext : extensiones) {
                File f = new File("images", nombreDragon + ext);
                if (f.exists()) {
                    ImageIcon icon = new ImageIcon(f.getAbsolutePath());
                    if (icon.getIconWidth() > 0) {
                        cacheImagenes.put(nombreDragon, icon.getImage());
                        return icon.getImage();
                    }
                }
            }
            return null;
        }

        private void dibujarNotFound(Graphics2D g2, int w, int h) {
            float[] dash = {6.0f, 6.0f};
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
            g2.setColor(new Color(85, 85, 95));
            g2.drawRoundRect(16, 16, w - 32, h - 32, 14, 14);

            int cx = w / 2;
            int cy = h / 2 - 15;

            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(new Color(48, 48, 55));
            g2.fillRoundRect(cx - 45, cy - 45, 90, 90, 12, 12);
            g2.setColor(new Color(75, 75, 85));
            g2.drawRoundRect(cx - 45, cy - 45, 90, 90, 12, 12);

            g2.setColor(new Color(160, 160, 175));
            g2.setFont(new Font("Consolas", Font.BOLD, 42));
            FontMetrics fmQ = g2.getFontMetrics();
            g2.drawString("?", cx - fmQ.stringWidth("?") / 2, cy + 14);

            g2.setFont(new Font("Consolas", Font.BOLD, 20));
            g2.setColor(new Color(240, 90, 90));
            String msg = "[Not found]";
            FontMetrics fmNF = g2.getFontMetrics();
            g2.drawString(msg, cx - fmNF.stringWidth(msg) / 2, cy + 78);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(new Color(150, 150, 160));
            String sub = "Sin imagen original en Dragonfire";
            FontMetrics fmSub = g2.getFontMetrics();
            g2.drawString(sub, cx - fmSub.stringWidth(sub) / 2, cy + 102);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            ConsultaDragones ventana = new ConsultaDragones();
            ventana.setVisible(true);
        });
    }
}
