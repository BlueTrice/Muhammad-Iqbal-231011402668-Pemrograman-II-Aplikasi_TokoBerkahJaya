package tokoberkahjaya;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class FormCustomer extends JFrame {

    JTextField txtId, txtNama, txtAlamat, txtTelepon;
    JButton    btnSimpan, btnEdit, btnHapus, btnReset;
    JTable     table;
    DefaultTableModel model;
    Connection conn;

    public FormCustomer() {
        conn = Koneksi.getConnection();

        setTitle("Data Customer — Toko Berkah Jaya");
        setSize(820, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_PAGE);
        setContentPane(root);

        root.add(UITheme.createHeader("Data Customer", "Kelola data pelanggan toko"), BorderLayout.NORTH);

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
        gc.weightx = 1; gc.gridx = 0;

        gc.gridy = 0; gc.insets = new Insets(0, 0, 16, 0);
        formCard.add(UITheme.createSectionTitle("Form Input Customer"), gc);

        gc.insets = new Insets(0, 0, 4, 0);
        gc.gridy++; formCard.add(UITheme.createLabel("ID Customer"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 10, 0);
        txtId = UITheme.createTextField(); formCard.add(txtId, gc);

        gc.insets = new Insets(0, 0, 4, 0);
        gc.gridy++; formCard.add(UITheme.createLabel("Nama Customer"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 10, 0);
        txtNama = UITheme.createTextField(); formCard.add(txtNama, gc);

        gc.insets = new Insets(0, 0, 4, 0);
        gc.gridy++; formCard.add(UITheme.createLabel("Alamat"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 10, 0);
        txtAlamat = UITheme.createTextField(); formCard.add(txtAlamat, gc);

        gc.insets = new Insets(0, 0, 4, 0);
        gc.gridy++; formCard.add(UITheme.createLabel("No. Telepon"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 20, 0);
        txtTelepon = UITheme.createTextField(); formCard.add(txtTelepon, gc);

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
        tableCard.add(UITheme.createSectionTitle("Daftar Customer"), BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{"ID", "Nama", "Alamat", "Telepon"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        tableCard.add(UITheme.createScrollPane(table), BorderLayout.CENTER);

        body.add(tableCard, BorderLayout.CENTER);

        // ── Events ──
        btnSimpan.addActionListener(e -> simpanCustomer());
        btnEdit.addActionListener(e -> editCustomer());
        btnHapus.addActionListener(e -> hapusCustomer());
        btnReset.addActionListener(e -> resetForm());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) tampilForm();
        });

        tampilData();
        generateIdCustomer();
        setVisible(true);
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

    private void simpanCustomer() {
        try {
            if (!validasiNama()) return;
            if (!validasiAlamat()) return;
            if (!validasiTelepon()) return;

            // Cek duplikat nama saat simpan baru
            PreparedStatement cek = conn.prepareStatement(
                "SELECT * FROM tb_customer WHERE nama_customer=?");
            cek.setString(1, txtNama.getText().trim());
            if (cek.executeQuery().next()) {
                showError("Customer dengan nama tersebut sudah ada.");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO tb_customer (id_customer,nama_customer,alamat,telepon) VALUES (?,?,?,?)");
            ps.setString(1, txtId.getText().trim());
            ps.setString(2, txtNama.getText().trim());
            ps.setString(3, txtAlamat.getText().trim());
            ps.setString(4, txtTelepon.getText().trim());
            ps.executeUpdate();

            showSuccess("Data customer berhasil disimpan.");
            tampilData(); resetForm();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void editCustomer() {
        try {
            if (!validasiNama()) return;
            if (!validasiAlamat()) return;
            if (!validasiTelepon()) return;

            // FIX: Cek duplikat nama, kecuali untuk ID customer yang sedang diedit
            PreparedStatement cek = conn.prepareStatement(
                "SELECT * FROM tb_customer WHERE nama_customer=? AND id_customer != ?");
            cek.setString(1, txtNama.getText().trim());
            cek.setString(2, txtId.getText().trim());
            if (cek.executeQuery().next()) {
                showError("Nama customer sudah digunakan oleh customer lain.");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                "UPDATE tb_customer SET nama_customer=?,alamat=?,telepon=? WHERE id_customer=?");
            ps.setString(1, txtNama.getText().trim());
            ps.setString(2, txtAlamat.getText().trim());
            ps.setString(3, txtTelepon.getText().trim());
            ps.setString(4, txtId.getText().trim());
            ps.executeUpdate();

            showSuccess("Data customer berhasil diperbarui.");
            tampilData(); resetForm();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void hapusCustomer() {
        try {
            PreparedStatement psCek = conn.prepareStatement(
                "SELECT * FROM tb_penjualan WHERE id_customer=?");
            psCek.setString(1, txtId.getText().trim());
            if (psCek.executeQuery().next()) {
                showError("Customer sudah memiliki transaksi, tidak bisa dihapus.");
                return;
            }
            int opt = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus customer ini?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION) return;

            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM tb_customer WHERE id_customer=?");
            ps.setString(1, txtId.getText().trim());
            ps.executeUpdate();
            showSuccess("Data customer berhasil dihapus.");
            tampilData(); resetForm();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void tampilData() {
        try {
            model.setRowCount(0);
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM tb_customer");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) model.addRow(new Object[]{
                rs.getString("id_customer"), rs.getString("nama_customer"),
                rs.getString("alamat"), rs.getString("telepon")
            });
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void tampilForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtId.setText(model.getValueAt(row, 0).toString());
        txtNama.setText(model.getValueAt(row, 1).toString());
        txtAlamat.setText(model.getValueAt(row, 2).toString());
        txtTelepon.setText(model.getValueAt(row, 3).toString());
    }

    private void resetForm() {
        txtNama.setText("");
        txtAlamat.setText("");
        txtTelepon.setText("");
        table.clearSelection();
        generateIdCustomer();
    }

    private boolean validasiNama() {
        if (txtNama.getText().trim().isEmpty()) {
            showError("Nama customer wajib diisi.");
            txtNama.requestFocus();
            return false;
        }
        if (!txtNama.getText().trim().matches("[a-zA-Z ]+")) {
            showError("Nama customer hanya boleh huruf.");
            txtNama.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validasiTelepon() {
        String telp = txtTelepon.getText().trim();
        if (telp.isEmpty()) {
            showError("Nomor telepon wajib diisi.");
            txtTelepon.requestFocus();
            return false;
        }
        if (!telp.matches("[0-9]+")) {
            showError("Nomor telepon hanya boleh angka.");
            txtTelepon.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validasiAlamat() {
        if (txtAlamat.getText().trim().isEmpty()) {
            showError("Alamat wajib diisi.");
            txtAlamat.requestFocus();
            return false;
        }
        return true;
    }

    private void generateIdCustomer() {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(id_customer) FROM tb_customer");
            String id = "CST001";
            if (rs.next() && rs.getString(1) != null) {
                int nomor = Integer.parseInt(rs.getString(1).substring(3));
                id = String.format("CST%03d", nomor + 1);
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
