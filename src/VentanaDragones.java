import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

public class VentanaDragones extends JFrame {
    private final PrologService prologService;
    private PanelImagenDragon panelImagen;

    // Controles de selección y consulta rápida
    private JComboBox<String> comboDragones;
    private JTextArea txtResultadoRapido;

    // Controles para añadir jinete
    private JComboBox<String> comboDragonAgregar;
    private JTextField txtNuevoJinete;
    private JButton btnAgregarJinete;

    // Consola interactiva
    private JTextArea txtConsola;
    private JTextField txtEntradaConsola;
    private JButton btnEjecutarConsulta;
    private JButton btnLimpiarConsola;
    private JButton btnEjemplos;

    public VentanaDragones() {
        super("Sistema Experto: Dragones de House of the Dragon (Prolog + Java)");
        this.prologService = PrologService.getInstance();

        inicializarProlog();
        construirUI();
        cargarDragones();
    }

    private void inicializarProlog() {
        File archivo = new File("dragones.pl");
        boolean exito = prologService.inicializar(archivo.getAbsolutePath());
        if (!exito) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar 'dragones.pl'.\nVerifica que el archivo esté en la raíz del proyecto.",
                    "Error al cargar Prolog",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void construirUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 780);
        setMinimumSize(new Dimension(980, 650));
        setLocationRelativeTo(null);

        // Contenedor principal con división horizontal (Panel izquierdo de imagen / Panel derecho de consultas)
        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPrincipal.setDividerLocation(350);
        splitPrincipal.setContinuousLayout(true);
        splitPrincipal.setDividerSize(6);

        // ==========================================
        // 1. PANEL IZQUIERDO: Imagen y Atributos
        // ==========================================
        panelImagen = new PanelImagenDragon();
        splitPrincipal.setLeftComponent(panelImagen);

        // ==========================================
        // 2. PANEL DERECHO: Consultas y Consola
        // ==========================================
        JPanel panelDerecho = new JPanel(new BorderLayout(8, 8));
        panelDerecho.setBorder(new EmptyBorder(8, 8, 8, 8));

        // División vertical entre Consultas Rápidas (arriba) y Consola Interactiva (abajo)
        JSplitPane splitDerecho = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitDerecho.setDividerLocation(380);
        splitDerecho.setContinuousLayout(true);
        splitDerecho.setDividerSize(6);

        // --- SECCIÓN SUPERIOR: Consultas Rápidas & Añadir Jinete ---
        JPanel panelConsultasSuperiores = new JPanel(new BorderLayout(6, 6));

        // Subpanel A: Barra superior de selección y gestión de jinetes
        JPanel panelGestion = new JPanel(new GridLayout(2, 1, 6, 6));

        // Fila 1: Selección rápida del dragón
        JPanel filaSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaSeleccion.setBorder(BorderFactory.createTitledBorder("Selección Rápida de Dragón"));
        filaSeleccion.add(new JLabel("Dragón:"));
        comboDragones = new JComboBox<>();
        comboDragones.setPreferredSize(new Dimension(180, 26));
        comboDragones.addActionListener(e -> onDragonSeleccionado());
        filaSeleccion.add(comboDragones);

        JButton btnActualizar = new JButton("Ver Detalles");
        btnActualizar.addActionListener(e -> onDragonSeleccionado());
        filaSeleccion.add(btnActualizar);

        panelGestion.add(filaSeleccion);

        // Fila 2: Formulario para añadir jinetes a un dragón
        JPanel filaAgregarJinete = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filaAgregarJinete.setBorder(BorderFactory.createTitledBorder("Añadir Más Jinetes a un Dragón (Persistencia en BD)"));
        filaAgregarJinete.add(new JLabel("Dragón:"));
        comboDragonAgregar = new JComboBox<>();
        comboDragonAgregar.setPreferredSize(new Dimension(140, 26));
        filaAgregarJinete.add(comboDragonAgregar);

        filaAgregarJinete.add(new JLabel("Nuevo Jinete:"));
        txtNuevoJinete = new JTextField(15);
        txtNuevoJinete.setToolTipText("Ejemplo: daemon_targaryen o Daemon Targaryen");
        txtNuevoJinete.addActionListener(e -> onAgregarJinete());
        filaAgregarJinete.add(txtNuevoJinete);

        btnAgregarJinete = new JButton("➕ Añadir Jinete");
        btnAgregarJinete.setBackground(new Color(230, 240, 250));
        btnAgregarJinete.addActionListener(e -> onAgregarJinete());
        filaAgregarJinete.add(btnAgregarJinete);

        panelGestion.add(filaAgregarJinete);
        panelConsultasSuperiores.add(panelGestion, BorderLayout.NORTH);

        // Subpanel B: Botones de Consultas Rápidas
        JPanel panelBotonesRapidos = new JPanel(new GridLayout(2, 3, 6, 6));
        panelBotonesRapidos.setBorder(BorderFactory.createTitledBorder("Consultas Rápidas"));

        JButton btnTodos = new JButton("📜 Todos Dragones y Jinetes");
        btnTodos.addActionListener(e -> ejecutarConsultaRapida("TODOS"));
        panelBotonesRapidos.add(btnTodos);

        JButton btnNegros = new JButton("🖤 Bando Los Negros");
        btnNegros.addActionListener(e -> ejecutarConsultaRapida("NEGROS"));
        panelBotonesRapidos.add(btnNegros);

        JButton btnVerdes = new JButton("💚 Bando Los Verdes");
        btnVerdes.addActionListener(e -> ejecutarConsultaRapida("VERDES"));
        panelBotonesRapidos.add(btnVerdes);

        JButton btnMultiples = new JButton("👥 Múltiples Jinetes");
        btnMultiples.addActionListener(e -> ejecutarConsultaRapida("MULTIPLES"));
        panelBotonesRapidos.add(btnMultiples);

        JButton btnSalvajes = new JButton("🌲 Salvajes / Sin Jinete");
        btnSalvajes.addActionListener(e -> ejecutarConsultaRapida("SALVAJES"));
        panelBotonesRapidos.add(btnSalvajes);

        JButton btnComparar = new JButton("⚔️ Comparar Rivales / Aliados");
        btnComparar.addActionListener(e -> abrirDialogoComparacion());
        panelBotonesRapidos.add(btnComparar);

        panelConsultasSuperiores.add(panelBotonesRapidos, BorderLayout.CENTER);

        // Subpanel C: Área de Resultados de la Consulta Rápida
        JPanel panelVisorRapido = new JPanel(new BorderLayout());
        panelVisorRapido.setBorder(BorderFactory.createTitledBorder("Resultado de Consulta Rápida"));
        txtResultadoRapido = new JTextArea(6, 40);
        txtResultadoRapido.setEditable(false);
        txtResultadoRapido.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtResultadoRapido.setMargin(new Insets(4, 4, 4, 4));
        JScrollPane scrollVisor = new JScrollPane(txtResultadoRapido);
        panelVisorRapido.add(scrollVisor, BorderLayout.CENTER);

        panelConsultasSuperiores.add(panelVisorRapido, BorderLayout.SOUTH);
        splitDerecho.setTopComponent(panelConsultasSuperiores);

        // --- SECCIÓN INFERIOR: Consola Interactiva Prolog ---
        JPanel panelConsolaCompleto = new JPanel(new BorderLayout(4, 4));
        panelConsolaCompleto.setBorder(BorderFactory.createTitledBorder("Consola Interactiva Prolog (Consulta Libre & Dinámica)"));

        // Pantalla de la consola
        txtConsola = new JTextArea();
        txtConsola.setEditable(false);
        txtConsola.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtConsola.setBackground(new Color(24, 24, 28));
        txtConsola.setForeground(new Color(220, 225, 230));
        txtConsola.setCaretColor(Color.WHITE);
        txtConsola.setMargin(new Insets(6, 8, 6, 8));

        JScrollPane scrollConsola = new JScrollPane(txtConsola);
        panelConsolaCompleto.add(scrollConsola, BorderLayout.CENTER);

        // Barra inferior de la consola (Input + Botones)
        JPanel panelInputConsola = new JPanel(new BorderLayout(6, 0));
        panelInputConsola.setBorder(new EmptyBorder(4, 2, 2, 2));

        JLabel lblPrompt = new JLabel(" ?- ");
        lblPrompt.setFont(new Font("Consolas", Font.BOLD, 14));
        panelInputConsola.add(lblPrompt, BorderLayout.WEST);

        txtEntradaConsola = new JTextField();
        txtEntradaConsola.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtEntradaConsola.setToolTipText("Escribe una consulta Prolog y presiona Enter (ej. jinete(vhagar, X))");
        txtEntradaConsola.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onEjecutarConsultaConsola();
                }
            }
        });
        panelInputConsola.add(txtEntradaConsola, BorderLayout.CENTER);

        JPanel panelBotonesConsola = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        btnEjecutarConsulta = new JButton("Consultar");
        btnEjecutarConsulta.addActionListener(e -> onEjecutarConsultaConsola());
        panelBotonesConsola.add(btnEjecutarConsulta);

        btnEjemplos = new JButton("Ejemplos ▾");
        btnEjemplos.addActionListener(e -> mostrarMenuEjemplos(btnEjemplos));
        panelBotonesConsola.add(btnEjemplos);

        btnLimpiarConsola = new JButton("Limpiar");
        btnLimpiarConsola.addActionListener(e -> txtConsola.setText(""));
        panelBotonesConsola.add(btnLimpiarConsola);

        panelInputConsola.add(panelBotonesConsola, BorderLayout.EAST);
        panelConsolaCompleto.add(panelInputConsola, BorderLayout.SOUTH);

        splitDerecho.setBottomComponent(panelConsolaCompleto);
        panelDerecho.add(splitDerecho, BorderLayout.CENTER);

        splitPrincipal.setRightComponent(panelDerecho);
        getContentPane().add(splitPrincipal, BorderLayout.CENTER);

        // Mensaje inicial en la consola interactiva
        imprimirEnConsola("========================================================================\n" +
                "  Bienvenido a la Consola Interactiva de Dragones (SWI-Prolog + Java)   \n" +
                "========================================================================\n" +
                "• Escribe cualquier consulta Prolog válida en la línea inferior y pulsa Enter.\n" +
                "• Ejemplos: jinete(caraxes, J). | bando(D, negros). | multiples_jinetes(D).\n" +
                "• También puedes agregar hechos con: assertz(jinete(syrax, mi_jinete)).\n\n");
    }

    private void cargarDragones() {
        List<String> dragones = prologService.obtenerDragones();

        comboDragones.removeAllItems();
        comboDragonAgregar.removeAllItems();

        for (String d : dragones) {
            comboDragones.addItem(d);
            comboDragonAgregar.addItem(d);
        }

        if (!dragones.isEmpty()) {
            // Seleccionar vhagar o el primer dragón por defecto
            if (dragones.contains("vhagar")) {
                comboDragones.setSelectedItem("vhagar");
                comboDragonAgregar.setSelectedItem("vhagar");
            } else {
                comboDragones.setSelectedIndex(0);
                comboDragonAgregar.setSelectedIndex(0);
            }
            onDragonSeleccionado();
        }
    }

    private void onDragonSeleccionado() {
        String dragonSeleccionado = (String) comboDragones.getSelectedItem();
        if (dragonSeleccionado != null) {
            comboDragonAgregar.setSelectedItem(dragonSeleccionado);
            DragonInfo info = prologService.obtenerDetallesDragon(dragonSeleccionado);
            panelImagen.actualizarDragon(dragonSeleccionado, info);

            // Mostrar resumen en el visor rápido
            StringBuilder sb = new StringBuilder();
            sb.append(">>> DRAGÓN SELECCIONADO: ").append(info.getNombreFormateado()).append(" <<<\n");
            sb.append("• Bando: ").append(DragonInfo.formatearNombre(info.getBando())).append("\n");
            sb.append("• Color: ").append(DragonInfo.formatearNombre(info.getColor())).append("\n");
            sb.append("• Lugar de origen: ").append(DragonInfo.formatearNombre(info.getNacidoEn())).append("\n");
            sb.append("• Estado: ").append(info.isSalvaje() ? "Salvaje" : "Domesticado").append("\n");
            sb.append("• Jinetes (").append(info.getJinetes().size()).append("):\n");
            for (String j : info.getJinetes()) {
                sb.append("    * ").append(DragonInfo.formatearNombre(j)).append("\n");
            }
            txtResultadoRapido.setText(sb.toString());
            txtResultadoRapido.setCaretPosition(0);
        }
    }

    private void onAgregarJinete() {
        String dragon = (String) comboDragonAgregar.getSelectedItem();
        String jinete = txtNuevoJinete.getText().trim();

        if (dragon == null || dragon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un dragón.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (jinete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el nombre del jinete.", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtNuevoJinete.requestFocus();
            return;
        }

        boolean ok = prologService.agregarJinete(dragon, jinete);
        if (ok) {
            String dragonNorm = PrologService.normalizarAtomo(dragon);
            String jineteNorm = PrologService.normalizarAtomo(jinete);

            imprimirEnConsola("?- assertz(jinete(" + dragonNorm + ", " + jineteNorm + ")).\n" +
                    "-> true. (Jinete '" + DragonInfo.formatearNombre(jineteNorm) +
                    "' añadido exitosamente a " + DragonInfo.formatearNombre(dragonNorm) + ")\n\n");

            txtNuevoJinete.setText("");

            // Recargar detalles y dragones
            DragonInfo info = prologService.obtenerDetallesDragon(dragonNorm);
            panelImagen.actualizarDragon(dragonNorm, info);
            txtResultadoRapido.setText("¡Jinete agregado!\nDragón: " + info.getNombreFormateado() +
                    "\nJinetes actuales: " + info.getResumenJinetes());

            // Asegurar que el combo tenga los dragones al día
            comboDragones.setSelectedItem(dragonNorm);
            comboDragonAgregar.setSelectedItem(dragonNorm);

            JOptionPane.showMessageDialog(this,
                    "¡Jinete '" + DragonInfo.formatearNombre(jineteNorm) + "' registrado exitosamente!\n" +
                    "Guardado en la base de conocimientos y en dragones.pl",
                    "Jinete Agregado",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo agregar el jinete a la base de conocimiento.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarConsultaRapida(String tipo) {
        String resultado = "";
        switch (tipo) {
            case "TODOS":
                resultado = prologService.consultarTodosDragonesYJinetes();
                imprimirEnConsola("?- consultar_todos_dragones_y_jinetes.\n" + resultado);
                break;
            case "NEGROS":
                resultado = prologService.consultarPorBando("negros");
                imprimirEnConsola("?- bando(Dragon, negros).\n" + resultado);
                break;
            case "VERDES":
                resultado = prologService.consultarPorBando("verdes");
                imprimirEnConsola("?- bando(Dragon, verdes).\n" + resultado);
                break;
            case "MULTIPLES":
                resultado = prologService.consultarMultiplesJinetes();
                imprimirEnConsola("?- multiples_jinetes(Dragon).\n" + resultado);
                break;
            case "SALVAJES":
                resultado = prologService.consultarSalvajesYSinJinete();
                imprimirEnConsola("?- salvaje(Dragon) ; sin_jinete(Dragon).\n" + resultado);
                break;
        }

        txtResultadoRapido.setText(resultado);
        txtResultadoRapido.setCaretPosition(0);
    }

    private void abrirDialogoComparacion() {
        List<String> dragones = prologService.obtenerDragones();
        if (dragones.size() < 2) {
            JOptionPane.showMessageDialog(this, "Se necesitan al menos 2 dragones registrados.");
            return;
        }

        JComboBox<String> cb1 = new JComboBox<>(dragones.toArray(new String[0]));
        JComboBox<String> cb2 = new JComboBox<>(dragones.toArray(new String[0]));

        String selActual = (String) comboDragones.getSelectedItem();
        if (selActual != null) cb1.setSelectedItem(selActual);
        if (cb2.getItemCount() > 1) cb2.setSelectedIndex(1);

        JPanel panelDialog = new JPanel(new GridLayout(2, 2, 8, 8));
        panelDialog.add(new JLabel("Primer Dragón:"));
        panelDialog.add(cb1);
        panelDialog.add(new JLabel("Segundo Dragón:"));
        panelDialog.add(cb2);

        int opt = JOptionPane.showConfirmDialog(this, panelDialog, "Comparar Dragones (Rivales / Aliados)",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (opt == JOptionPane.OK_OPTION) {
            String d1 = (String) cb1.getSelectedItem();
            String d2 = (String) cb2.getSelectedItem();
            String res = prologService.compararDragones(d1, d2);
            txtResultadoRapido.setText(res);
            txtResultadoRapido.setCaretPosition(0);

            imprimirEnConsola("?- comparar(" + d1 + ", " + d2 + ").\n" + res + "\n");
        }
    }

    private void onEjecutarConsultaConsola() {
        String consulta = txtEntradaConsola.getText().trim();
        if (consulta.isEmpty()) return;

        imprimirEnConsola("?- " + consulta + (consulta.endsWith(".") ? "" : ".") + "\n");
        String resultado = prologService.ejecutarConsultaManual(consulta);
        imprimirEnConsola(resultado + "\n");

        txtEntradaConsola.setText("");

        // Si la consulta fue un assert o retract, refrescar datos
        if (consulta.startsWith("assert") || consulta.startsWith("retract")) {
            cargarDragones();
        }
    }

    private void mostrarMenuEjemplos(Component comp) {
        JPopupMenu menu = new JPopupMenu();

        String[] ejemplos = {
                "jinete(caraxes, Jinete)",
                "jinete(vhagar, Jinete)",
                "multiples_jinetes(Dragon)",
                "bando(Dragon, negros)",
                "bando(Dragon, verdes)",
                "son_enemigos(caraxes, vhagar)",
                "son_aliados(caraxes, syrax)",
                "sin_jinete(Dragon)",
                "color(Dragon, rojo_sangre)",
                "nacido_en(Dragon, Lugar)",
                "comparten_jinete(D1, D2, Jinete)"
        };

        for (String ej : ejemplos) {
            JMenuItem item = new JMenuItem(ej);
            item.addActionListener(e -> {
                txtEntradaConsola.setText(ej);
                txtEntradaConsola.requestFocus();
            });
            menu.add(item);
        }

        menu.show(comp, 0, comp.getHeight());
    }

    private void imprimirEnConsola(String texto) {
        txtConsola.append(texto);
        txtConsola.setCaretPosition(txtConsola.getDocument().getLength());
    }

    public static void main(String[] args) {
        // Establecer Look and Feel agradable del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            VentanaDragones ventana = new VentanaDragones();
            ventana.setVisible(true);
        });
    }
}
