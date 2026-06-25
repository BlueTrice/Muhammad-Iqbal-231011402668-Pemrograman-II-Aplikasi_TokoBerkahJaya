package tokoberkahjaya;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class FormUser extends JFrame {

    JTextField     txtId, txtNama, txtUsername;
    JPasswordField txtPassword;
    JComboBox<String> cmbRole;
    JButton        btnSimpan, btnEdit, btnHapus, btnReset;
    JTable         table;
    DefaultTableModel model;
    Connection     conn;

    public FormUser() {
        conn = Koneksi.getConnection();

        setTitle("Data User — Toko Berkah Jaya");
        setSize(820, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_PAGE);
        setContentPane(root);

        String subtitle = Session.isAdmin()
            ? "Kelola akun pengguna sistem"
            : "Kelola akun pengguna sistem  •  Mode Lihat (Kasir tidak dapat mengedit)";
        root.add(UITheme.createHeader("Data User", subtitle), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.setBackground(UITheme.BG_PAGE);
        body.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.add(body, BorderLayout.CENTER);

        // ── LEFT: Form Card ──
        JPanel formCard = UITheme.createCard();
        formCard.setLayout(new GridBagLayout());
        formCard.setPreferredSize(new Dimension(280, 0));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1; gc.gridx = 0;

        gc.gridy = 0; gc.insets = new Insets(0, 0, 16, 0);
        formCard.add(UITheme.createSectionTitle("Form Input User"), gc);

        gc.insets = new Insets(0, 0, 4, 0);
        gc.gridy++; formCard.add(UITheme.createLabel("ID User"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 10, 0);
        txtId = UITheme.createTextField();
        txtId.setEditable(false);
        formCard.add(txtId, gc);

        gc.insets = new Insets(0, 0, 4, 0);
        gc.gridy++; formCard.add(UITheme.createLabel("Nama Lengkap"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 10, 0);
        txtNama = UITheme.createTextField(); formCard.add(txtNama, gc);

        gc.insets = new Insets(0, 0, 4, 0);
        gc.gridy++; formCard.add(UITheme.createLabel("Username"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 10, 0);
        txtUsername = UITheme.createTextField(); formCard.add(txtUsername, gc);

        gc.insets = new Insets(0, 0, 4, 0);
        gc.gridy++; formCard.add(UITheme.createLabel("Password"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 4, 0);
        txtPassword = new JPasswordField();
        txtPassword.setFont(UITheme.FONT_BODY);
        txtPassword.setBackground(new Color(0xF8FAFF));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(UITheme.BORDER_COLOR, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        txtPassword.setPreferredSize(new Dimension(0, 38));
        formCard.add(txtPassword, gc);

        // Hint password kosong saat edit
        gc.gridy++; gc.insets = new Insets(0, 2, 10, 0);
        JLabel hintPass = new JLabel("Kosongkan jika tidak ingin mengubah password");
        hintPass.setFont(UITheme.FONT_SMALL);
        hintPass.setForeground(new Color(0xE08030));
        formCard.add(hintPass, gc);

        gc.insets = new Insets(0, 0, 4, 0);
        gc.gridy++; formCard.add(UITheme.createLabel("Role"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 20, 0);
        cmbRole = UITheme.createComboBox();
        cmbRole.addItem("ADMIN");
        cmbRole.addItem("KASIR");
        formCard.add(cmbRole, gc);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);
        btnSimpan = UITheme.createPrimaryButton("Simpan");
        btnEdit   = UITheme.createWarningButton("Edit");
        btnHapus  = UITheme.createDangerButton("Hapus");
        btnReset  = new JButton("Reset");
        styleOutlineButton(btnReset);
        btnPanel.add(btnSimpan);
        btnPanel.add(btnEdit);
        btnPanel.add(btnHapus);
        btnPanel.add(btnReset);

        gc.gridy++; gc.insets = new Insets(0, 0, 0, 0);
        formCard.add(btnPanel, gc);

        gc.gridy++; gc.weighty = 1;
        formCard.add(Box.createVerticalGlue(), gc);

        body.add(formCard, BorderLayout.WEST);

        // ── RIGHT: Table Card ──
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.add(UITheme.createSectionTitle("Daftar User"), BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{"ID", "Username", "Nama Lengkap", "Level"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        tableCard.add(UITheme.createScrollPane(table), BorderLayout.CENTER);

        JLabel secNote = new JLabel("Password tidak ditampilkan di tabel demi keamanan.");
        secNote.setFont(UITheme.FONT_SMALL);
        secNote.setForeground(new Color(0xE08030));
        secNote.setBorder(new EmptyBorder(8, 0, 0, 0));
        tableCard.add(secNote, BorderLayout.SOUTH);

        body.add(tableCard, BorderLayout.CENTER);

        // ── Events ──
        btnSimpan.addActionListener(e -> simpanUser());
        btnEdit.addActionListener(e -> editUser());
        btnHapus.addActionListener(e -> hapusUser());
        btnReset.addActionListener(e -> resetForm());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) tampilForm();
        });

        tampilData();
        generateIdUser();
        terapkanHakAkses();
        setVisible(true);
    }

    /**
     * Jika user adalah KASIR: semua input dan tombol edit/hapus di-disable.
     * KASIR hanya bisa melihat daftar user (read-only).
     */
    private void terapkanHakAkses() {
        if (!Session.isAdmin()) {
            txtNama.setEnabled(false);
            txtUsername.setEnabled(false);
            txtPassword.setEnabled(false);
            cmbRole.setEnabled(false);

            btnSimpan.setEnabled(false);
            btnEdit.setEnabled(false);
            btnHapus.setEnabled(false);
            btnReset.setEnabled(false);

            btnSimpan.setToolTipText("Hanya ADMIN yang dapat menambah user");
            btnEdit.setToolTipText("Hanya ADMIN yang dapat mengedit user");
            btnHapus.setToolTipText("Hanya ADMIN yang dapat menghapus user");
        }
    }

    private void simpanUser() {
        if (!Session.isAdmin()) { showError("Akses ditolak. Hanya ADMIN yang dapat menambah user."); return; }
        try {
            if (!validasiNamaLengkap()) return;
            if (!validasiUsername()) return;
            if (!validasiPasswordBaru()) return;  // Password wajib saat simpan baru

            PreparedStatement cek = conn.prepareStatement(
                "SELECT * FROM tb_user WHERE username=?");
            cek.setString(1, txtUsername.getText().trim());
            if (cek.executeQuery().next()) {
                showError("Username sudah digunakan.");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO tb_user (username,password,nama_lengkap,level) VALUES (?,?,?,?)");
            ps.setString(1, txtUsername.getText().trim());
            ps.setString(2, new String(txtPassword.getPassword()).trim());
            ps.setString(3, txtNama.getText().trim());
            ps.setString(4, cmbRole.getSelectedItem().toString());
            ps.executeUpdate();

            showSuccess("User berhasil ditambahkan.");
            tampilData(); resetForm();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void editUser() {
        if (!Session.isAdmin()) { showError("Akses ditolak. Hanya ADMIN yang dapat mengedit user."); return; }
        try {
            if (!validasiNamaLengkap()) return;
            if (!validasiUsername()) return;

            String newPass = new String(txtPassword.getPassword()).trim();

            // FIX: Jika password diisi saat edit, validasi panjang minimum
            if (!newPass.isEmpty() && newPass.length() < 6) {
                showError("Password minimal 6 karakter.");
                return;
            }

            if (newPass.isEmpty()) {
                // Tidak mengubah password
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE tb_user SET username=?, nama_lengkap=?, level=? WHERE id_user=?");
                ps.setString(1, txtUsername.getText().trim());
                ps.setString(2, txtNama.getText().trim());
                ps.setString(3, cmbRole.getSelectedItem().toString());
                ps.setInt(4, Integer.parseInt(txtId.getText()));
                ps.executeUpdate();
            } else {
                // Update termasuk password baru
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE tb_user SET username=?, password=?, nama_lengkap=?, level=? WHERE id_user=?");
                ps.setString(1, txtUsername.getText().trim());
                ps.setString(2, newPass);
                ps.setString(3, txtNama.getText().trim());
                ps.setString(4, cmbRole.getSelectedItem().toString());
                ps.setInt(5, Integer.parseInt(txtId.getText()));
                ps.executeUpdate();
            }

            showSuccess("Data user berhasil diperbarui.");
            tampilData(); resetForm();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void hapusUser() {
        if (!Session.isAdmin()) { showError("Akses ditolak. Hanya ADMIN yang dapat menghapus user."); return; }
        try {
            int opt = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus user ini?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION) return;

            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM tb_user WHERE id_user=?");
            ps.setString(1, txtId.getText().trim());
            ps.executeUpdate();

            showSuccess("User berhasil dihapus.");
            tampilData(); resetForm();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void tampilData() {
        try {
            model.setRowCount(0);
            PreparedStatement ps = conn.prepareStatement(
                "SELECT id_user, username, nama_lengkap, level FROM tb_user ORDER BY id_user");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) model.addRow(new Object[]{
                rs.getInt("id_user"),
                rs.getString("username"),
                rs.getString("nama_lengkap"),
                rs.getString("level")
            });
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void tampilForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtId.setText(model.getValueAt(row, 0).toString());
        txtUsername.setText(model.getValueAt(row, 1).toString());
        txtNama.setText(model.getValueAt(row, 2).toString());
        cmbRole.setSelectedItem(model.getValueAt(row, 3).toString());
        txtPassword.setText(""); // Jangan tampilkan password
    }

    private void resetForm() {
        txtId.setText(""); txtNama.setText("");
        txtUsername.setText(""); txtPassword.setText("");
        cmbRole.setSelectedIndex(0);
        generateIdUser();
        table.clearSelection();
    }

    private void styleOutlineButton(JButton btn) {
        btn.setFont(UITheme.FONT_BUTTON);
        btn.setForeground(UITheme.TEXT_SECONDARY);
        btn.setBackground(UITheme.BG_CARD);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(UITheme.BORDER_COLOR, 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 38));
        btn.setFocusPainted(false);
    }

    private boolean validasiUsername() {
        String user = txtUsername.getText().trim();
        if (user.isEmpty()) {
            showError("Username wajib diisi.");
            return false;
        }
        if (!user.matches("[a-zA-Z0-9_]+")) {
            showError("Username hanya boleh huruf, angka, dan underscore.");
            return false;
        }
        return true;
    }

    // Validasi password wajib isi (untuk simpan baru)
    private boolean validasiPasswordBaru() {
        String pass = new String(txtPassword.getPassword()).trim();
        if (pass.isEmpty()) {
            showError("Password wajib diisi untuk user baru.");
            return false;
        }
        if (pass.length() < 6) {
            showError("Password minimal 6 karakter.");
            return false;
        }
        return true;
    }

    private boolean validasiNamaLengkap() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            showError("Nama lengkap wajib diisi.");
            return false;
        }
        if (!nama.matches("[a-zA-Z ]+")) {
            showError("Nama lengkap hanya boleh huruf.");
            return false;
        }
        return true;
    }

    private void generateIdUser() {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT IFNULL(MAX(id_user),0)+1 FROM tb_user");
            if (rs.next()) txtId.setText(String.valueOf(rs.getInt(1)));
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }
    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
    }
}