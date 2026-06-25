package tokoberkahjaya;

import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class FormBarang extends JFrame {

    JTextField    txtId, txtNama, txtSatuan, txtHarga, txtStok;
    private static final NumberFormat RUPIAH = NumberFormat.getNumberInstance(new Locale("id", "ID"));
    JComboBox<String> cmbKategori;
    JButton       btnSimpan, btnEdit, btnHapus, btnReset;
    JTable        table;
    DefaultTableModel model;
    Connection    conn;

    public FormBarang() {
        conn = Koneksi.getConnection();

        setTitle("Data Barang — Toko Berkah Jaya");
        setSize(820, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_PAGE);
        setContentPane(root);

        String subtitle = Session.isAdmin()
            ? "Kelola data barang & stok"
            : "Kelola data barang & stok  •  Mode Lihat (Kasir tidak dapat mengedit)";
        root.add(UITheme.createHeader("Data Barang", subtitle), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.setBackground(UITheme.BG_PAGE);
        body.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.add(body, BorderLayout.CENTER);

        // ── LEFT: Form Card ──
        JPanel formCard = UITheme.createCard();
        formCard.setLayout(new GridBagLayout());
        formCard.setPreferredSize(new Dimension(270, 0));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.gridx = 0;

        gc.gridy = 0; gc.insets = new Insets(0, 0, 16, 0);
        formCard.add(UITheme.createSectionTitle("Form Input Barang"), gc);

        gc.insets = new Insets(0, 0, 4, 0);
        gc.gridy++; formCard.add(UITheme.createLabel("ID Barang"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 10, 0);
        txtId = UITheme.createTextField(); formCard.add(txtId, gc);

        gc.insets = new Insets(0, 0, 4, 0);
        gc.gridy++; formCard.add(UITheme.createLabel("Nama Barang"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 10, 0);
        txtNama = UITheme.createTextField(); formCard.add(txtNama, gc);

        gc.insets = new Insets(0, 0, 4, 0);
        gc.gridy++; formCard.add(UITheme.createLabel("Kategori"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 10, 0);

        // Panel: combo + tombol tambah kategori
        JPanel kategoriPanel = new JPanel(new BorderLayout(6, 0));
        kategoriPanel.setOpaque(false);
        cmbKategori = UITheme.createComboBox();
        JButton btnTambahKategori = new JButton("＋");
        btnTambahKategori.setToolTipText("Tambah kategori baru");
        btnTambahKategori.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTambahKategori.setForeground(Color.WHITE);
        btnTambahKategori.setBackground(UITheme.SUCCESS);
        btnTambahKategori.setOpaque(true);
        btnTambahKategori.setContentAreaFilled(true);
        btnTambahKategori.setBorderPainted(false);
        btnTambahKategori.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTambahKategori.setPreferredSize(new Dimension(38, 38));
        btnTambahKategori.setFocusPainted(false);
        kategoriPanel.add(cmbKategori, BorderLayout.CENTER);
        kategoriPanel.add(btnTambahKategori, BorderLayout.EAST);
        formCard.add(kategoriPanel, gc);

        // Event tombol tambah kategori
        btnTambahKategori.addActionListener(e -> tambahKategoriBaru());

        tampilKategori();

        gc.insets = new Insets(0, 0, 4, 0);
        gc.gridy++; formCard.add(UITheme.createLabel("Satuan"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 10, 0);
        txtSatuan = UITheme.createTextField(); formCard.add(txtSatuan, gc);

        gc.insets = new Insets(0, 0, 4, 0);
        gc.gridy++; formCard.add(UITheme.createLabel("Harga Jual (contoh: 15.000)"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 10, 0);
        txtHarga = UITheme.createTextField(); formCard.add(txtHarga, gc);

        gc.insets = new Insets(0, 0, 4, 0);
        gc.gridy++; formCard.add(UITheme.createLabel("Stok"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 20, 0);
        txtStok = UITheme.createTextField(); formCard.add(txtStok, gc);

        // Buttons
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
        tableCard.add(UITheme.createSectionTitle("Daftar Barang"), BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{"ID", "Kategori", "Nama Barang", "Satuan", "Harga Jual", "Stok"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        tableCard.add(UITheme.createScrollPane(table), BorderLayout.CENTER);

        body.add(tableCard, BorderLayout.CENTER);

        // ── Events ──
        btnSimpan.addActionListener(e -> simpanBarang());
        btnEdit.addActionListener(e -> editBarang());
        btnHapus.addActionListener(e -> hapusBarang());
        btnReset.addActionListener(e -> resetForm());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) tampilForm();
        });

        tampilData();
        generateIdBarang();
        terapkanHakAkses();
        setVisible(true);
    }

    /**
     * Jika user adalah KASIR: semua input dan tombol edit/tambah/hapus di-disable.
     * KASIR hanya bisa melihat daftar barang (read-only).
     */
    private void terapkanHakAkses() {
        if (!Session.isAdmin()) {
            // Disable semua field input
            txtNama.setEnabled(false);
            txtSatuan.setEnabled(false);
            txtHarga.setEnabled(false);
            txtStok.setEnabled(false);
            cmbKategori.setEnabled(false);

            // Disable tombol aksi (kecuali Reset tidak relevan tapi tetap disable)
            btnSimpan.setEnabled(false);
            btnEdit.setEnabled(false);
            btnHapus.setEnabled(false);
            btnReset.setEnabled(false);

            // Warna tombol abu-abu agar terlihat tidak aktif
            btnSimpan.setToolTipText("Hanya ADMIN yang dapat menambah barang");
            btnEdit.setToolTipText("Hanya ADMIN yang dapat mengedit barang");
            btnHapus.setToolTipText("Hanya ADMIN yang dapat menghapus barang");
        }
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

    private void tampilKategori() {
        try {
            cmbKategori.removeAllItems();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM tb_kategori ORDER BY nama_kategori");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) cmbKategori.addItem(rs.getString("nama_kategori"));
        } catch (Exception e) { showError(e.getMessage()); }
    }

    /** Dialog input & simpan kategori baru ke tb_kategori */
    private void tambahKategoriBaru() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_CARD);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1; gc.gridx = 0;

        gc.gridy = 0; gc.insets = new Insets(0, 0, 6, 0);
        JLabel lbl = new JLabel("Nama Kategori Baru:");
        lbl.setFont(UITheme.FONT_BODY); lbl.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(lbl, gc);

        gc.gridy = 1; gc.insets = new Insets(0, 0, 0, 0);
        JTextField txtKat = UITheme.createTextField();
        txtKat.setPreferredSize(new Dimension(240, 38));
        panel.add(txtKat, gc);

        int result = JOptionPane.showConfirmDialog(this, panel,
            "Tambah Kategori Baru", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String namaKat = txtKat.getText().trim();
        if (namaKat.isEmpty()) { showError("Nama kategori tidak boleh kosong."); return; }
        if (!namaKat.matches("[a-zA-Z0-9 ]+")) {
            showError("Nama kategori hanya boleh huruf, angka, dan spasi."); return;
        }

        try {
            // Cek duplikat (case-insensitive)
            PreparedStatement cek = conn.prepareStatement(
                "SELECT * FROM tb_kategori WHERE LOWER(nama_kategori)=LOWER(?)");
            cek.setString(1, namaKat);
            if (cek.executeQuery().next()) {
                showError("Kategori \"" + namaKat + "\" sudah ada."); return;
            }
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO tb_kategori (nama_kategori) VALUES (?)");
            ps.setString(1, namaKat);
            ps.executeUpdate();
            showSuccess("Kategori \"" + namaKat + "\" berhasil ditambahkan.");
            tampilKategori();
            cmbKategori.setSelectedItem(namaKat); // langsung pilih kategori baru
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private int getIdKategori() {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT id_kategori FROM tb_kategori WHERE nama_kategori=?");
            ps.setString(1, cmbKategori.getSelectedItem().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id_kategori");
        } catch (Exception e) { showError(e.getMessage()); }
        return 0;
    }

    private void simpanBarang() {
        if (!Session.isAdmin()) { showError("Akses ditolak. Hanya ADMIN yang dapat menambah barang."); return; }
        try {
            if (!validasiNamaBarang()) return;
            if (!validasiSatuan()) return;
            if (!validasiAngka(txtHarga, "Harga")) return;
            if (!validasiAngka(txtStok, "Stok")) return;

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO tb_barang (id_barang,id_kategori,nama_barang,satuan,harga_jual,stok) " +
                "VALUES (?,?,?,?,?,?)");
            ps.setString(1, txtId.getText().trim());
            ps.setInt(2, getIdKategori());
            ps.setString(3, txtNama.getText().trim());
            ps.setString(4, txtSatuan.getText().trim());
            ps.setDouble(5, parseHarga(txtHarga.getText()));
            ps.setInt(6, Integer.parseInt(txtStok.getText().trim()));
            ps.executeUpdate();

            showSuccess("Data barang berhasil disimpan.");
            tampilData(); resetForm();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void editBarang() {
        if (!Session.isAdmin()) { showError("Akses ditolak. Hanya ADMIN yang dapat mengedit barang."); return; }
        try {
            if (!validasiNamaBarang()) return;
            if (!validasiSatuan()) return;
            if (!validasiAngka(txtHarga, "Harga")) return;
            if (!validasiAngka(txtStok, "Stok")) return;

            PreparedStatement ps = conn.prepareStatement(
                "UPDATE tb_barang SET id_kategori=?,nama_barang=?,satuan=?,harga_jual=?,stok=? " +
                "WHERE id_barang=?");
            ps.setInt(1, getIdKategori());
            ps.setString(2, txtNama.getText().trim());
            ps.setString(3, txtSatuan.getText().trim());
            ps.setDouble(4, parseHarga(txtHarga.getText()));
            ps.setInt(5, Integer.parseInt(txtStok.getText().trim()));
            ps.setString(6, txtId.getText().trim());
            ps.executeUpdate();

            showSuccess("Data barang berhasil diperbarui.");
            tampilData(); resetForm();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void hapusBarang() {
        if (!Session.isAdmin()) { showError("Akses ditolak. Hanya ADMIN yang dapat menghapus barang."); return; }
        try {
            PreparedStatement psCek = conn.prepareStatement(
                "SELECT * FROM tb_detail_penjualan WHERE id_barang=?");
            psCek.setString(1, txtId.getText().trim());
            if (psCek.executeQuery().next()) {
                showError("Barang sudah dipakai di transaksi, tidak bisa dihapus.");
                return;
            }
            int opt = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus barang ini?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION) return;

            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM tb_barang WHERE id_barang=?");
            ps.setString(1, txtId.getText().trim());
            ps.executeUpdate();
            showSuccess("Data barang berhasil dihapus.");
            tampilData(); resetForm();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void tampilData() {
        try {
            model.setRowCount(0);
            PreparedStatement ps = conn.prepareStatement(
                "SELECT b.id_barang, k.nama_kategori, b.nama_barang, b.satuan, b.harga_jual, b.stok " +
                "FROM tb_barang b JOIN tb_kategori k ON b.id_kategori=k.id_kategori");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) model.addRow(new Object[]{
                rs.getString("id_barang"),
                rs.getString("nama_kategori"),
                rs.getString("nama_barang"),
                rs.getString("satuan"),
                "Rp " + RUPIAH.format(rs.getDouble("harga_jual")),
                rs.getInt("stok")
            });
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void tampilForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        // Kolom: 0=ID, 1=Kategori, 2=NamaBarang, 3=Satuan, 4=HargaJual, 5=Stok
        txtId.setText(model.getValueAt(row, 0).toString());
        cmbKategori.setSelectedItem(model.getValueAt(row, 1).toString());
        txtNama.setText(model.getValueAt(row, 2).toString());
        txtSatuan.setText(model.getValueAt(row, 3).toString());
        // Strip "Rp " prefix dan titik ribuan agar field harga bisa diedit sebagai angka
        String rawHarga = model.getValueAt(row, 4).toString()
            .replace("Rp ", "").replace(".", "").replace(",", ".");
        txtHarga.setText(rawHarga);
        txtStok.setText(model.getValueAt(row, 5).toString());
    }

    private void resetForm() {
        txtNama.setText("");
        txtSatuan.setText("");
        txtHarga.setText("");
        txtStok.setText("");
        generateIdBarang();
        table.clearSelection();
    }

    private boolean validasiAngka(JTextField tf, String nama) {
        // Terima angka bulat, desimal dengan koma/titik, dan format ribuan (1.000 atau 1,000)
        String val = tf.getText().trim().replace(".", "").replace(",", ".");
        if (!val.matches("[0-9]+(\\.[0-9]+)?")) {
            showError(nama + " harus berupa angka (contoh: 15000 atau 15.000).");
            tf.requestFocus();
            return false;
        }
        return true;
    }

    /** Ambil nilai numerik dari field, toleran terhadap format ribuan */
    private double parseHarga(String text) {
        return Double.parseDouble(text.trim().replace(".", "").replace(",", "."));
    }

    private boolean validasiNamaBarang() {
        if (!txtNama.getText().trim().matches("[a-zA-Z0-9 ]+")) {
            showError("Nama barang hanya boleh huruf, angka, dan spasi.");
            txtNama.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validasiSatuan() {
        if (!txtSatuan.getText().trim().matches("[a-zA-Z ]+")) {
            showError("Satuan hanya boleh huruf.");
            txtSatuan.requestFocus();
            return false;
        }
        return true;
    }

    private void generateIdBarang() {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(id_barang) FROM tb_barang");
            String id = "BRG001";
            if (rs.next() && rs.getString(1) != null) {
                int nomor = Integer.parseInt(rs.getString(1).substring(3));
                id = String.format("BRG%03d", nomor + 1);
            }
            txtId.setText(id);
            txtId.setEditable(false);
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }
    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
    }
}