import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PanelImagenDragon extends JPanel {
    private String dragonActual = "";
    private DragonInfo infoActual = null;
    private final Map<String, Image> cacheImagenes = new HashMap<>();

    private final JLabel lblTitulo;
    private final JLabel lblBandoBadge;
    private final JPanel lienzoImagen;
    private final JTextArea txtDetalles;

    public PanelImagenDragon() {
        setLayout(new BorderLayout(8, 8));
        setPreferredSize(new Dimension(340, 650));
        setMinimumSize(new Dimension(300, 500));
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(60, 60, 60), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        setBackground(new Color(24, 24, 27));

        // Cabecera con Nombre y Bando
        JPanel panelCabecera = new JPanel(new BorderLayout(4, 4));
        panelCabecera.setOpaque(false);

        lblTitulo = new JLabel("Selecciona un dragón", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(245, 245, 245));

        lblBandoBadge = new JLabel("• BASE DE CONOCIMIENTO •", SwingConstants.CENTER);
        lblBandoBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblBandoBadge.setForeground(new Color(180, 180, 180));

        panelCabecera.add(lblTitulo, BorderLayout.NORTH);
        panelCabecera.add(lblBandoBadge, BorderLayout.SOUTH);
        add(panelCabecera, BorderLayout.NORTH);

        // Lienzo central de dibujo de imagen
        lienzoImagen = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int w = getWidth();
                int h = getHeight();

                Image img = obtenerImagen(dragonActual);
                if (img != null) {
                    // Dibujar imagen centrada manteniendo proporción
                    int imgW = img.getWidth(null);
                    int imgH = img.getHeight(null);

                    if (imgW > 0 && imgH > 0) {
                        double scale = Math.min((double) (w - 16) / imgW, (double) (h - 16) / imgH);
                        int drawW = (int) (imgW * scale);
                        int drawH = (int) (imgH * scale);
                        int drawX = (w - drawW) / 2;
                        int drawY = (h - drawH) / 2;

                        // Marco sutil
                        g2.setColor(new Color(40, 40, 45));
                        g2.fillRoundRect(drawX - 4, drawY - 4, drawW + 8, drawH + 8, 12, 12);
                        g2.setColor(new Color(80, 80, 90));
                        g2.drawRoundRect(drawX - 4, drawY - 4, drawW + 8, drawH + 8, 12, 12);

                        g2.drawImage(img, drawX, drawY, drawW, drawH, null);
                    }
                } else {
                    // Dibujar avatar genérico o mensaje cuando no hay imagen
                    dibujarPlaceholder(g2, w, h);
                }
                g2.dispose();
            }
        };
        lienzoImagen.setPreferredSize(new Dimension(310, 310));
        lienzoImagen.setBackground(new Color(18, 18, 20));
        lienzoImagen.setBorder(new LineBorder(new Color(50, 50, 55), 1));
        add(lienzoImagen, BorderLayout.CENTER);

        // Panel inferior con resumen de atributos
        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));
        panelInferior.setOpaque(false);

        JLabel lblDetalleTitulo = new JLabel("Detalles del Dragón:");
        lblDetalleTitulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblDetalleTitulo.setForeground(new Color(210, 210, 210));
        panelInferior.add(lblDetalleTitulo, BorderLayout.NORTH);

        txtDetalles = new JTextArea(8, 20);
        txtDetalles.setEditable(false);
        txtDetalles.setLineWrap(true);
        txtDetalles.setWrapStyleWord(true);
        txtDetalles.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtDetalles.setBackground(new Color(30, 30, 35));
        txtDetalles.setForeground(new Color(230, 230, 230));
        txtDetalles.setBorder(new EmptyBorder(6, 6, 6, 6));

        JScrollPane scrollDetalles = new JScrollPane(txtDetalles);
        scrollDetalles.setBorder(new LineBorder(new Color(55, 55, 60), 1));
        panelInferior.add(scrollDetalles, BorderLayout.CENTER);

        add(panelInferior, BorderLayout.SOUTH);
    }

    public void actualizarDragon(String dragon, DragonInfo info) {
        this.dragonActual = (dragon != null) ? dragon.trim().toLowerCase() : "";
        this.infoActual = info;

        if (info != null) {
            lblTitulo.setText(info.getNombreFormateado());
            String bando = info.getBando().toLowerCase();

            if (bando.contains("negros")) {
                lblBandoBadge.setText("• BANDO: LOS NEGROS (RHAENYRA) •");
                lblBandoBadge.setForeground(new Color(235, 75, 75));
            } else if (bando.contains("verdes")) {
                lblBandoBadge.setText("• BANDO: LOS VERDES (AEGON II) •");
                lblBandoBadge.setForeground(new Color(75, 200, 110));
            } else if (info.isSalvaje()) {
                lblBandoBadge.setText("• DRAGÓN SALVAJE •");
                lblBandoBadge.setForeground(new Color(220, 170, 70));
            } else {
                lblBandoBadge.setText("• " + DragonInfo.formatearNombre(info.getBando()).toUpperCase() + " •");
                lblBandoBadge.setForeground(new Color(190, 190, 220));
            }

            StringBuilder sb = new StringBuilder();
            sb.append("• Color: ").append(DragonInfo.formatearNombre(info.getColor())).append("\n");
            sb.append("• Origen: ").append(DragonInfo.formatearNombre(info.getNacidoEn())).append("\n");
            sb.append("• Salvaje: ").append(info.isSalvaje() ? "Sí" : "No").append("\n");
            sb.append("• Total jinetes: ").append(info.getJinetes().size()).append("\n\n");
            sb.append("Jinetes registrados:\n");

            if (info.getJinetes().isEmpty()) {
                sb.append("   (Sin jinetes registrados)\n");
            } else {
                int i = 1;
                for (String j : info.getJinetes()) {
                    sb.append("  ").append(i++).append(". ").append(DragonInfo.formatearNombre(j)).append("\n");
                }
            }

            txtDetalles.setText(sb.toString());
            txtDetalles.setCaretPosition(0);
        } else {
            lblTitulo.setText("Dragón no seleccionado");
            lblBandoBadge.setText("• SELECCIÓN •");
            lblBandoBadge.setForeground(Color.GRAY);
            txtDetalles.setText("");
        }

        lienzoImagen.repaint();
    }

    private Image obtenerImagen(String dragon) {
        if (dragon == null || dragon.isEmpty()) return null;
        if (cacheImagenes.containsKey(dragon)) {
            return cacheImagenes.get(dragon);
        }

        // Buscar imagen en carpeta images/
        String[] extensiones = {".png", ".jpg", ".jpeg", ".webp"};
        File archivo = null;
        for (String ext : extensiones) {
            File f = new File("images", dragon + ext);
            if (f.exists()) {
                archivo = f;
                break;
            }
        }

        if (archivo != null) {
            ImageIcon icon = new ImageIcon(archivo.getAbsolutePath());
            if (icon.getIconWidth() > 0) {
                cacheImagenes.put(dragon, icon.getImage());
                return icon.getImage();
            }
        }

        return null;
    }

    private void dibujarPlaceholder(Graphics2D g2, int w, int h) {
        // Fondo degradado
        GradientPaint gp = new GradientPaint(0, 0, new Color(30, 25, 30), w, h, new Color(15, 12, 18));
        g2.setPaint(gp);
        g2.fillRoundRect(10, 10, w - 20, h - 20, 16, 16);

        // Borde decorativo
        g2.setColor(new Color(100, 80, 40));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(14, 14, w - 28, h - 28, 14, 14);

        // Silueta / Icono
        g2.setColor(new Color(200, 160, 60));
        int cx = w / 2;
        int cy = h / 2 - 15;

        // Círculo central
        g2.drawOval(cx - 50, cy - 50, 100, 100);

        // Cabeza y alas estilizadas
        Polygon alas = new Polygon();
        alas.addPoint(cx, cy - 35);
        alas.addPoint(cx + 40, cy - 10);
        alas.addPoint(cx + 25, cy + 20);
        alas.addPoint(cx, cy + 5);
        alas.addPoint(cx - 25, cy + 20);
        alas.addPoint(cx - 40, cy - 10);
        g2.fillPolygon(alas);

        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        String txt = (dragonActual.isEmpty()) ? "Sin Selección" : DragonInfo.formatearNombre(dragonActual);
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(txt);
        g2.drawString(txt, cx - tw / 2, cy + 65);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(Color.LIGHT_GRAY);
        String sub = "House of the Dragon";
        int sw = g2.getFontMetrics().stringWidth(sub);
        g2.drawString(sub, cx - sw / 2, cy + 85);
    }
}
