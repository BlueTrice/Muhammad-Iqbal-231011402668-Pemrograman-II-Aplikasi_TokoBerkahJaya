package tokoberkahjaya;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Login extends JFrame {

    JTextField     txtUser;
    JPasswordField txtPass;
    JButton        btnLogin;
    Connection     conn;

    // Warna modern
    private static final Color C_BG_LEFT   = new Color(0x1A2545);
    private static final Color C_BG_RIGHT  = new Color(0xF4F7FE);
    private static final Color C_ACCENT    = new Color(0x4F6FE8);
    private static final Color C_ACCENT2   = new Color(0x7C4DFF);
    private static final Color C_TEXT_DARK = new Color(0x1A2035);
    private static final Color C_TEXT_GRAY = new Color(0x6B7A99);
    private static final Color C_BORDER    = new Color(0xDDE3F0);
    private static final Color C_INPUT_BG  = new Color(0xF8FAFF);

    public Login() {
        conn = Koneksi.getConnection();

        setTitle("Login — Toko Berkah Jaya");
        setSize(820, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(false);

        // ── Root: split kiri (branding) | kanan (form) ──
        JPanel root = new JPanel(new GridLayout(1, 2));
        setContentPane(root);

        // ══════════════════════════════════════
        // PANEL KIRI — Branding / Ilustrasi
        // ══════════════════════════════════════
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x1A2545), getWidth(), getHeight(), new Color(0x2D3E6F));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Dekorasi lingkaran / blob
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
                g2.setColor(C_ACCENT);
                g2.fillOval(-60, -60, 260, 260);
                g2.fillOval(getWidth() - 100, getHeight() - 120, 220, 220);

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2.setColor(C_ACCENT2);
                g2.fillOval(getWidth() / 2 - 60, getHeight() / 2 - 20, 200, 200);

                // Grid dots dekoratif
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
                g2.setColor(Color.WHITE);
                for (int x = 20; x < getWidth(); x += 28) {
                    for (int y = 20; y < getHeight(); y += 28) {
                        g2.fillOval(x, y, 3, 3);
                    }
                }
                g2.dispose();
            }
        };

        GridBagConstraints glc = new GridBagConstraints();
        glc.gridx = 0; glc.gridy = 0; glc.anchor = GridBagConstraints.CENTER;
        glc.insets = new Insets(0, 0, 0, 0);

        // Icon belanja besar dalam lingkaran
        JPanel iconCircle = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, C_ACCENT, getWidth(), getHeight(), C_ACCENT2);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth()-1, getHeight()-1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(96, 96));
        JLabel iconEmoji = new JLabel("🛒");
        iconEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        iconCircle.add(iconEmoji);
        leftPanel.add(iconCircle, glc);

        // Nama app
        glc.gridy = 1; glc.insets = new Insets(20, 0, 6, 0);
        JLabel lblApp = new JLabel("Toko Berkah Jaya", JLabel.CENTER);
        lblApp.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblApp.setForeground(Color.WHITE);
        leftPanel.add(lblApp, glc);

        // Tagline
        glc.gridy = 2; glc.insets = new Insets(0, 24, 0, 24);
        JLabel lblTag = new JLabel("<html><center>Sistem Manajemen Toko<br>Modern & Efisien</center></html>", JLabel.CENTER);
        lblTag.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTag.setForeground(new Color(0xAAC0E0));
        leftPanel.add(lblTag, glc);

        // Fitur-fitur kecil
        glc.gridy = 3; glc.insets = new Insets(28, 20, 0, 20);
        JPanel featPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        featPanel.setOpaque(false);
        String[] feats = {"Kelola Stok & Barang", "Catat Transaksi Penjualan", "Manajemen Customer"};
        for (String f : feats) {
            JLabel fl = new JLabel(f);
            fl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            fl.setForeground(new Color(0xCCDDEE));
            featPanel.add(fl);
        }
        leftPanel.add(featPanel, glc);

        // Copyright
        glc.gridy = 4; glc.insets = new Insets(32, 0, 0, 0);
        JLabel copy = new JLabel("© 2026 Toko Berkah Jaya", JLabel.CENTER);
        copy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copy.setForeground(new Color(0x6688AA));
        leftPanel.add(copy, glc);

        root.add(leftPanel);

        // ══════════════════════════════════════
        // PANEL KANAN — Form Login
        // ══════════════════════════════════════
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(C_BG_RIGHT);

        GridBagConstraints grc = new GridBagConstraints();
        grc.gridx = 0; grc.fill = GridBagConstraints.HORIZONTAL; grc.weightx = 1;
        grc.insets = new Insets(0, 48, 0, 48);

        // "Selamat Datang"
        grc.gridy = 0; grc.insets = new Insets(0, 48, 4, 48);
        JLabel lblWelcome = new JLabel("Selamat Datang");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblWelcome.setForeground(C_TEXT_DARK);
        rightPanel.add(lblWelcome, grc);

        grc.gridy = 1; grc.insets = new Insets(0, 48, 36, 48);
        JLabel lblSub = new JLabel("Masuk ke sistem untuk melanjutkan");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(C_TEXT_GRAY);
        rightPanel.add(lblSub, grc);

        // Username label
        grc.gridy = 2; grc.insets = new Insets(0, 48, 6, 48);
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(C_TEXT_DARK);
        rightPanel.add(lblUser, grc);

        // Username field dengan ikon
        grc.gridy = 3; grc.insets = new Insets(0, 48, 18, 48);
        txtUser = createStyledField("Masukkan username...", false);
        rightPanel.add(txtUser, grc);

        // Password label
        grc.gridy = 4; grc.insets = new Insets(0, 48, 6, 48);
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(C_TEXT_DARK);
        rightPanel.add(lblPass, grc);

        // Password field
        grc.gridy = 5; grc.insets = new Insets(0, 48, 32, 48);
        txtPass = (JPasswordField) createStyledField("••••••••", true);
        rightPanel.add(txtPass, grc);

        // Tombol Login
        grc.gridy = 6; grc.insets = new Insets(0, 48, 16, 48);
        btnLogin = new JButton("Masuk  →") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color a = getModel().isPressed()  ? C_ACCENT.darker() :
                          getModel().isRollover() ? C_ACCENT.darker() : C_ACCENT;
                GradientPaint gp = new GradientPaint(0, 0, a, getWidth(), 0, C_ACCENT2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setOpaque(false); btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(0, 46));
        btnLogin.setFocusPainted(false);
        rightPanel.add(btnLogin, grc);

        // Divider
        grc.gridy = 7; grc.insets = new Insets(0, 48, 0, 48);
        JPanel divider = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(C_BORDER);
                g.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
            }
        };
        divider.setOpaque(false);
        divider.setPreferredSize(new Dimension(0, 16));
        rightPanel.add(divider, grc);

        // Info versi
        grc.gridy = 8; grc.insets = new Insets(12, 48, 0, 48);
        JLabel lblVer = new JLabel("Toko Berkah Jaya  v1.0  •  2025", JLabel.CENTER);
        lblVer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVer.setForeground(C_TEXT_GRAY);
        rightPanel.add(lblVer, grc);

        root.add(rightPanel);

        // ── Events ──
        btnLogin.addActionListener(e -> login());
        txtPass.addActionListener(e -> login());
        txtUser.addActionListener(e -> txtPass.requestFocus());

        setVisible(true);
    }

    /** Buat styled text / password field dengan placeholder dan focus effect */
    private JTextField createStyledField(String placeholder, boolean isPassword) {
        JTextField tf = isPassword ? new JPasswordField() : new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(new Color(0xBBCCDD));
                    g2.setFont(getFont().deriveFont(Font.PLAIN));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left, getHeight() - ins.bottom - 4);
                }
            }
        };

        if (isPassword) {
            JPasswordField pf = (JPasswordField) tf;
            // Placeholder effect untuk password field
            tf = new JPasswordField() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (getPassword().length == 0 && !isFocusOwner()) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setColor(new Color(0xBBCCDD));
                        g2.setFont(getFont().deriveFont(Font.PLAIN));
                        Insets ins = getInsets();
                        g2.drawString(placeholder, ins.left, getHeight() - ins.bottom - 4);
                    }
                }
            };
        }

        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setForeground(C_TEXT_DARK);
        tf.setBackground(C_INPUT_BG);
        tf.setOpaque(true);

        // Border normal
        javax.swing.border.Border normalBorder = BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(C_BORDER, 1, true),
            new EmptyBorder(10, 14, 10, 14));
        // Border fokus
        javax.swing.border.Border focusBorder = BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(C_ACCENT, 2, true),
            new EmptyBorder(9, 13, 9, 13));

        tf.setBorder(normalBorder);
        tf.setPreferredSize(new Dimension(0, 48));

        JTextField finalTf = tf;
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                finalTf.setBorder(focusBorder);
                finalTf.setBackground(Color.WHITE);
            }
            @Override public void focusLost(FocusEvent e) {
                finalTf.setBorder(normalBorder);
                finalTf.setBackground(C_INPUT_BG);
            }
        });

        return tf;
    }

    private void login() {
        try {
            String username = txtUser.getText().trim();
            String password = new String(txtPass.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Username dan password wajib diisi."); return;
            }

            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM tb_user WHERE username=? AND password=?");
            ps.setString(1, username); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Session.login(
                    rs.getInt("id_user"),
                    rs.getString("nama_lengkap"),
                    rs.getString("username"),
                    rs.getString("level")
                );
                JOptionPane.showMessageDialog(this,
                    "Selamat datang, " + Session.getNamaLengkap() + "!\nLevel: " + Session.getLevel(),
                    "Login Berhasil", JOptionPane.INFORMATION_MESSAGE);
                new MenuUtama();
                dispose();
            } else {
                // Shake animasi error
                shakeWindow();
                showError("Username atau password salah.");
            }
        } catch (Exception e) { showError(e.getMessage()); }
    }

    /** Animasi shake jendela saat login gagal */
    private void shakeWindow() {
        final Point origin = getLocation();
        Timer t = new Timer(30, null);
        final int[] step = {0};
        final int[] offsets = {-8, 8, -6, 6, -4, 4, -2, 2, 0};
        t.addActionListener(e -> {
            if (step[0] < offsets.length) {
                setLocation(origin.x + offsets[step[0]], origin.y);
                step[0]++;
            } else {
                setLocation(origin);
                t.stop();
            }
        });
        t.start();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(Login::new);
    }
}
