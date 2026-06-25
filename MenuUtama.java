package tokoberkahjaya;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MenuUtama extends JFrame {

    // Accent warna per menu card
    private static final Color[] CARD_COLORS = {
        new Color(0x4F6FE8), // indigo  — Barang
        new Color(0x3CB87A), // hijau   — Customer
        new Color(0xF0A030), // oranye  — Penjualan
        new Color(0xE05252), // merah   — User
    };

    public MenuUtama() {
        setTitle("Menu Utama — Toko Berkah Jaya");
        setSize(800, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // ── Root dengan gradient background ──
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0xF0F4FB), 0, getHeight(), new Color(0xE3EAF7));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(root);

        // ── HEADER ──
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x1A2545), getWidth(), 0, new Color(0x2D3E6F));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Dekorasi lingkaran blur di header
                g2.setColor(new Color(0x4F6FE8, true));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                g2.fillOval(getWidth() - 160, -40, 200, 200);
                g2.fillOval(-60, -30, 160, 160);
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(0, 110));
        header.setBorder(new EmptyBorder(0, 32, 0, 32));

        // Kiri: icon + teks
        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        headerLeft.setOpaque(false);

        // Icon toko dalam lingkaran
        JLabel iconLbl = new JLabel("🛒") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFFFFFF, true));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        iconLbl.setPreferredSize(new Dimension(56, 56));
        iconLbl.setHorizontalAlignment(JLabel.CENTER);
        iconLbl.setVerticalAlignment(JLabel.CENTER);

        JPanel textStack = new JPanel(new GridLayout(2, 1, 0, 3));
        textStack.setOpaque(false);
        JLabel lblAppName = new JLabel("Toko Berkah Jaya");
        lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblAppName.setForeground(Color.WHITE);
        JLabel lblTagline = new JLabel("Sistem Manajemen Toko  •  v1.0");
        lblTagline.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTagline.setForeground(new Color(0xAAC0E0));
        textStack.add(lblAppName);
        textStack.add(lblTagline);

        headerLeft.add(iconLbl);
        headerLeft.add(textStack);

        // Kanan: user info + tombol logout
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        headerRight.setOpaque(false);

        // Avatar user

 

        JButton btnLogout = new JButton("Logout") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                    ? new Color(0xE05252)
                    : new Color(0xFFFFFF, true);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    getModel().isRollover() ? 1f : 0.12f));
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setOpaque(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setPreferredSize(new Dimension(110, 36));
        btnLogout.setFocusPainted(false);

        headerRight.add(btnLogout);

        header.add(headerLeft, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        // ── CENTER: Grid 2×2 kartu menu ──
        JPanel center = new JPanel(new GridLayout(2, 2, 16, 16));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(28, 36, 14, 36));

        String[][] menus = {
            {"📦", "Data Barang",   "Kelola stok & harga barang"},
            {"👥", "Data Customer", "Kelola data pelanggan toko"},
            {"🧾", "Penjualan",     "Catat & kelola transaksi"},
            {"👤", "Data User",     "Kelola akun pengguna sistem"},
        };

        for (int i = 0; i < menus.length; i++) {
            final int idx = i;
            // Kartu Data Barang (0) dan Data User (3) = read-only untuk KASIR
            boolean readOnly = !Session.isAdmin() && (idx == 0 || idx == 3);
            JPanel card = createMenuCard(menus[i][0], menus[i][1], menus[i][2], CARD_COLORS[i], readOnly);
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (readOnly) {
                String tip = (idx == 0)
                    ? "Kasir hanya dapat melihat data barang (tidak dapat mengedit)"
                    : "Kasir hanya dapat melihat data user (tidak dapat mengedit)";
                card.setToolTipText(tip);
            }
            card.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e)  { openMenu(idx); }
                @Override public void mousePressed(MouseEvent e)  { card.repaint(); }
                @Override public void mouseReleased(MouseEvent e) { card.repaint(); }
                @Override public void mouseEntered(MouseEvent e)  { card.putClientProperty("hover", true);  card.repaint(); }
                @Override public void mouseExited(MouseEvent e)   { card.putClientProperty("hover", false); card.repaint(); }
            });
            center.add(card);
        }

        root.add(center, BorderLayout.CENTER);

        // ── FOOTER ──
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        footer.setOpaque(false);
        JLabel footerLbl = new JLabel("© 2025 Toko Berkah Jaya  •  Semua hak dilindungi");
        footerLbl.setFont(UITheme.FONT_SMALL);
        footerLbl.setForeground(new Color(0x9AABBF));
        footer.add(footerLbl);
        root.add(footer, BorderLayout.SOUTH);

        // ── Events ──
        btnLogout.addActionListener(e -> logout());
        setVisible(true);
    }

    private JPanel createMenuCard(String emoji, String title, String desc, Color accentColor, boolean readOnly) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean hover = Boolean.TRUE.equals(getClientProperty("hover"));

                // Shadow
                if (hover) {
                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.fillRoundRect(4, 6, getWidth()-8, getHeight()-8, 18, 18);
                }

                // Card background — sedikit redup jika read-only
                Color bg = readOnly ? new Color(0xF7F7FA) : (hover ? new Color(0xF0F5FF) : Color.WHITE);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);

                // Accent bar kiri — abu jika read-only
                g2.setColor(readOnly ? new Color(0xBBBBCC) : accentColor);
                g2.fillRoundRect(0, 0, 5, getHeight()-1, 4, 4);

                // Border tipis
                g2.setColor(readOnly ? new Color(0xCCCCDD) : (hover ? accentColor.brighter() : new Color(0xDDE3F0)));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 24, 20, 20));

        // Kiri: ikon dalam lingkaran berwarna
        JPanel iconCircle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = readOnly
                    ? new Color(180, 180, 195, 40)
                    : new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 25);
                g2.setColor(bg);
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(58, 58));
        iconCircle.setLayout(new GridBagLayout());
        JLabel emojiLbl = new JLabel(emoji, JLabel.CENTER);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        iconCircle.add(emojiLbl);

        // Kanan: teks
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(0, 16, 0, 0));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1; gc.anchor = GridBagConstraints.WEST;

        gc.gridy = 0; gc.insets = new Insets(0, 0, 4, 0);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(readOnly ? UITheme.TEXT_SECONDARY : UITheme.TEXT_PRIMARY);
        textPanel.add(titleLbl, gc);

        gc.gridy++; gc.insets = new Insets(0, 0, 6, 0);
        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(UITheme.FONT_SMALL);
        descLbl.setForeground(UITheme.TEXT_SECONDARY);
        textPanel.add(descLbl, gc);

        gc.gridy++; gc.insets = new Insets(0, 0, 0, 0);
        // Teks link: "Buka →" untuk normal, "Lihat Saja" untuk read-only
        JLabel linkLbl = new JLabel(readOnly ? "Lihat Saja" : "Buka  →");
        linkLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        linkLbl.setForeground(readOnly ? new Color(0xAAAAAA) : accentColor);
        textPanel.add(linkLbl, gc);

        card.add(iconCircle, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private void logout() {
        int opt = JOptionPane.showConfirmDialog(this,
            "Yakin ingin keluar dari sistem?", "Konfirmasi Logout",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            dispose();
            new Login();
        }
    }

    private void openMenu(int idx) {
        switch (idx) {
            case 0 -> new FormBarang();
            case 1 -> new FormCustomer();
            case 2 -> new FormPenjualan();
            case 3 -> new FormUser();
        }
    }
}