package tokoberkahjaya;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

/**
 * UITheme — Centralized modern styling for Toko Berkah Jaya
 * Color palette: Deep Navy + White + Accent Indigo
 */
public class UITheme {

    // ── Palette ──────────────────────────────────────────────
    public static final Color BG_PAGE      = new Color(0xEFF3FA);
    public static final Color BG_CARD      = Color.WHITE;
    public static final Color BG_HEADER    = new Color(0x1E2A4A);
    public static final Color BG_SIDEBAR   = new Color(0x253563);

    public static final Color ACCENT       = new Color(0x4F6FE8);
    public static final Color ACCENT_HOVER = new Color(0x3A56C8);
    public static final Color DANGER       = new Color(0xE05252);
    public static final Color SUCCESS      = new Color(0x3CB87A);
    public static final Color WARNING      = new Color(0xF0A030);

    public static final Color TEXT_PRIMARY   = new Color(0x1A2035);
    public static final Color TEXT_SECONDARY = new Color(0x6B7A99);
    public static final Color TEXT_WHITE     = Color.WHITE;

    public static final Color BORDER_COLOR  = new Color(0xDDE3F0);
    public static final Color TABLE_ALT_ROW = new Color(0xF5F8FF);
    public static final Color TABLE_HEADER  = new Color(0x2D3E6F);

    // ── Fonts ────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD,  13);

    // ── Card Panel ───────────────────────────────────────────
    public static JPanel createCard() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(18, 20, 18, 20)
        ));
        return p;
    }

    // ── Header Panel ─────────────────────────────────────────
    public static JPanel createHeader(String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_HEADER);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_WHITE);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(FONT_SMALL);
        lblSub.setForeground(new Color(0xAABBDD));

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);
        text.add(lblTitle);
        text.add(lblSub);

        header.add(text, BorderLayout.WEST);
        return header;
    }

    // ── Styled Label ─────────────────────────────────────────
    public static JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    // ── Styled TextField ─────────────────────────────────────
    public static JTextField createTextField() {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        tf.setFont(FONT_BODY);
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(new Color(0xF8FAFF));
        tf.setOpaque(false);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        tf.setPreferredSize(new Dimension(0, 38));
        return tf;
    }

    // ── Styled ComboBox ──────────────────────────────────────
    public static <T> JComboBox<T> createComboBox() {
        JComboBox<T> cb = new JComboBox<>();
        cb.setFont(FONT_BODY);
        cb.setBackground(new Color(0xF8FAFF));
        cb.setForeground(TEXT_PRIMARY);
        cb.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        cb.setPreferredSize(new Dimension(0, 38));
        return cb;
    }

    // ── Primary Button ───────────────────────────────────────
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()  ? ACCENT_HOVER :
                           getModel().isRollover() ? ACCENT_HOVER : ACCENT;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btn, TEXT_WHITE);
        return btn;
    }

    // ── Danger Button ────────────────────────────────────────
    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? DANGER.darker() : DANGER);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btn, TEXT_WHITE);
        return btn;
    }

    // ── Warning Button ───────────────────────────────────────
    public static JButton createWarningButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? WARNING.darker() : WARNING);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btn, TEXT_WHITE);
        return btn;
    }

    private static void styleButton(JButton btn, Color fg) {
        btn.setFont(FONT_BUTTON);
        btn.setForeground(fg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 38));
        btn.setFocusPainted(false);
    }

    // ── Styled Table ─────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(BG_CARD);
        table.setRowHeight(38);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(0xDDE8FF));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Alternating rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? BG_CARD : TABLE_ALT_ROW);
                    c.setForeground(TEXT_PRIMARY); // FIX: pastikan teks baris terbaca
                }
                ((JLabel) c).setBorder(new EmptyBorder(0, 12, 0, 12));
                return c;
            }
        });

        // Header — FIX: warna teks putih terang, background kontras
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setBackground(TABLE_HEADER);    // navy gelap
        header.setForeground(Color.WHITE);     // putih terang & eksplisit
        header.setOpaque(true);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);

        // Renderer header dengan warna eksplisit agar tidak tertulis hitam
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                lbl.setBackground(TABLE_HEADER);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(FONT_HEADER);
                lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
                lbl.setHorizontalAlignment(JLabel.LEFT);
                lbl.setOpaque(true);
                return lbl;
            }
        };
        table.getTableHeader().setDefaultRenderer(headerRenderer);
        header.setPreferredSize(new Dimension(0, 42));
    }

    // ── ScrollPane ───────────────────────────────────────────
    public static JScrollPane createScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        sp.getViewport().setBackground(BG_CARD);
        return sp;
    }

    // ── Section Title ────────────────────────────────────────
    public static JLabel createSectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }
}
