package tokoberkahjaya;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class FormPenjualan extends JFrame {

    // ── Input fields ────────────────────────────────────────────────
    JComboBox<String>  cmbCustomer, cmbBarang;
    JTextField         txtHarga, txtJumlah, txtSubtotal, txtFaktur;
    JTextField         txtUangBayar, txtKembalian;

    // ── Keranjang: pakai JPanel scrollable, bukan JTable ────────────
    JPanel             pnlKeranjangList;   // tempat kartu-kartu item
    JLabel             lblGrandTotal, lblItemBadge;

    // ── Riwayat ─────────────────────────────────────────────────────
    JTable             tblRiwayat;
    DefaultTableModel  modelRiwayat;

    // ── Tombol ──────────────────────────────────────────────────────
    JButton btnTambahItem, btnSimpan, btnBatal, btnDetail, btnHapusTransaksi;

    // ── Data ────────────────────────────────────────────────────────
    Connection     conn;
    int            stokBarang = 0;
    // keranjang[i] = { idBarang(String), namaBarang(String),
    //                  harga(double), jumlah(int), subtotal(double) }
    List<Object[]> keranjang = new ArrayList<>();

    // ── Warna ───────────────────────────────────────────────────────
    private static final Color C_INDIGO_LIGHT = new Color(0xEEF2FF);
    private static final Color C_GREEN        = new Color(0x16A34A);
    private static final Color C_GREEN_BG     = new Color(0xF0FDF4);
    private static final Color C_RED          = new Color(0xDC2626);
    private static final Color C_RED_BG       = new Color(0xFEF2F2);
    private static final Color C_AMBER        = new Color(0xD97706);

    // ════════════════════════════════════════════════════════════════
    public FormPenjualan() {
        conn = Koneksi.getConnection();
        setTitle("Penjualan — Toko Berkah Jaya");
        setSize(1200, 720);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_PAGE);
        setContentPane(root);
        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(14, 0));
        body.setBackground(UITheme.BG_PAGE);
        body.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.add(body, BorderLayout.CENTER);

        // Kiri: form input + keranjang
        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(470, 0));
        GridBagConstraints glc = new GridBagConstraints();
        glc.gridx = 0; glc.fill = GridBagConstraints.BOTH; glc.weightx = 1;

        glc.gridy = 0; glc.weighty = 0; glc.insets = new Insets(0, 0, 12, 0);
        left.add(buildInputCard(), glc);

        glc.gridy = 1; glc.weighty = 1; glc.insets = new Insets(0, 0, 0, 0);
        left.add(buildKeranjangCard(), glc);

        body.add(left, BorderLayout.WEST);
        body.add(buildRiwayatCard(), BorderLayout.CENTER);

        // ── Wire events ──
        cmbBarang.addActionListener(e -> tampilHarga());
        txtJumlah.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { hitungSubtotal(); }
        });
        txtUangBayar.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { hitungKembalian(); }
        });
        btnTambahItem.addActionListener(e -> tambahKeKeranjang());
        btnBatal.addActionListener(e -> batalSemua());
        btnSimpan.addActionListener(e -> simpanPesanan());
        btnDetail.addActionListener(e -> lihatDetail());
        btnHapusTransaksi.addActionListener(e -> hapusTransaksi());

        generateFaktur();
        tampilHarga();
        tampilRiwayat();
        setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════
    // HEADER
    // ════════════════════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(0x1A2545), getWidth(), 0, new Color(0x2E3F6F));
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 0.08f));
                g2.setColor(UITheme.ACCENT);
                g2.fillOval(getWidth() - 140, -30, 180, 180);
                g2.fillOval(-40, -20, 130, 130);
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(0, 68));
        header.setBorder(new EmptyBorder(0, 24, 0, 24));

        // Kiri
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        JLabel ico = new JLabel("🧾");
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        JPanel ts = new JPanel(new GridLayout(2, 1, 0, 1)); ts.setOpaque(false);
        JLabel t = new JLabel("Form Penjualan");
        t.setFont(new Font("Segoe UI", Font.BOLD, 18)); t.setForeground(Color.WHITE);
        JLabel s = new JLabel("Catat transaksi & kelola keranjang belanja");
        s.setFont(UITheme.FONT_SMALL); s.setForeground(new Color(0xAAC0E0));
        ts.add(t); ts.add(s);
        left.add(ico); left.add(ts);

        // Kanan: badge jumlah item
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 16));
        right.setOpaque(false);
        lblItemBadge = new JLabel("0 item") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.ACCENT);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblItemBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblItemBadge.setForeground(Color.WHITE);
        lblItemBadge.setOpaque(false);
        lblItemBadge.setBorder(new EmptyBorder(4, 12, 4, 12));
        right.add(new JLabel("🛒 ") {{
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        }});
        right.add(lblItemBadge);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ════════════════════════════════════════════════════════════════
    // CARD INPUT ITEM
    // ════════════════════════════════════════════════════════════════
    private JPanel buildInputCard() {
        JPanel card = createCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1; gc.gridx = 0;

        gc.gridy = 0; gc.insets = new Insets(0, 0, 14, 0);
        card.add(sectionLabel("✏️  Input Item Pesanan"), gc);

        // No Faktur
        gc.gridy++; gc.insets = new Insets(0, 0, 4, 0);
        card.add(fieldLabel("No. Faktur"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 10, 0);
        txtFaktur = UITheme.createTextField();
        txtFaktur.setEditable(false); txtFaktur.setBackground(C_INDIGO_LIGHT);
        txtFaktur.setFont(new Font("Segoe UI", Font.BOLD, 13));
        card.add(txtFaktur, gc);

        // Customer
        gc.gridy++; gc.insets = new Insets(0, 0, 4, 0);
        card.add(fieldLabel("Customer"), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 10, 0);
        cmbCustomer = UITheme.createComboBox(); tampilCustomer();
        card.add(cmbCustomer, gc);

        // Barang | Harga
        gc.gridy++; gc.insets = new Insets(0, 0, 4, 0);
        JPanel lbl1 = row2(fieldLabel("Barang"), fieldLabel("Harga Satuan"));
        card.add(lbl1, gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 10, 0);
        cmbBarang = UITheme.createComboBox(); tampilBarang();
        txtHarga = UITheme.createTextField();
        txtHarga.setEditable(false); txtHarga.setBackground(C_INDIGO_LIGHT);
        card.add(row2(cmbBarang, txtHarga), gc);

        // Jumlah | Subtotal
        gc.gridy++; gc.insets = new Insets(0, 0, 4, 0);
        card.add(row2(fieldLabel("Jumlah"), fieldLabel("Subtotal")), gc);
        gc.gridy++; gc.insets = new Insets(0, 0, 16, 0);
        txtJumlah = UITheme.createTextField();
        txtSubtotal = UITheme.createTextField();
        txtSubtotal.setEditable(false); txtSubtotal.setBackground(C_INDIGO_LIGHT);
        card.add(row2(txtJumlah, txtSubtotal), gc);

        // Tombol tambah
        gc.gridy++; gc.insets = new Insets(0, 0, 0, 0);
        btnTambahItem = pillBtn("Tambah ke Keranjang",
                                UITheme.ACCENT, new Color(0x3A56C8));
        btnTambahItem.setPreferredSize(new Dimension(0, 42));
        card.add(btnTambahItem, gc);

        return card;
    }

    // ════════════════════════════════════════════════════════════════
    // CARD KERANJANG  — menggunakan JPanel list, BUKAN JTable
    // Dengan begitu tombol +/−/× adalah komponen Swing sungguhan
    // yang langsung bisa diklik tanpa perlu renderer/editor tabel.
    // ════════════════════════════════════════════════════════════════
    private JPanel buildKeranjangCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 10));

        // Header kartu
        JPanel kHead = new JPanel(new BorderLayout(8, 0));
        kHead.setOpaque(false);
        kHead.add(sectionLabel("Keranjang Belanja"), BorderLayout.WEST);
        card.add(kHead, BorderLayout.NORTH);

        // Panel daftar item — diisi ulang tiap refreshKeranjang()
        pnlKeranjangList = new JPanel();
        pnlKeranjangList.setLayout(new BoxLayout(pnlKeranjangList, BoxLayout.Y_AXIS));
        pnlKeranjangList.setBackground(UITheme.BG_PAGE);

        JScrollPane sp = new JScrollPane(pnlKeranjangList);
        sp.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1, true));
        sp.getViewport().setBackground(UITheme.BG_PAGE);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        card.add(sp, BorderLayout.CENTER);

        // Bottom bar: total + bayar + kembalian + tombol
        card.add(buildBottomBar(), BorderLayout.SOUTH);

        return card;
    }

    /**
     * Buat satu baris kartu item di keranjang.
     * Semua tombol (+/−/×) adalah JButton biasa — tidak ada trik renderer.
     */
    private JPanel buildItemRow(int index) {
        Object[] item = keranjang.get(index);
        String nama     = item[1].toString();
        double harga    = (double) item[2];
        int    jumlah   = (int)    item[3];
        double subtotal = (double) item[4];

        // Baris bergantian warna
        Color rowBg = index % 2 == 0 ? Color.WHITE : new Color(0xF8FAFF);

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(rowBg);
        row.setOpaque(true);
        row.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
            new EmptyBorder(8, 12, 8, 10)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        row.setMinimumSize(new Dimension(0, 62));
        row.setPreferredSize(new Dimension(0, 62));

        // Kiri: nama + harga satuan
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        infoPanel.setOpaque(false);
        JLabel lblNama = new JLabel(nama);
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNama.setForeground(UITheme.TEXT_PRIMARY);
        JLabel lblHarga = new JLabel(String.format("Rp %,.0f / pcs", harga));
        lblHarga.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHarga.setForeground(UITheme.TEXT_SECONDARY);
        infoPanel.add(lblNama);
        infoPanel.add(lblHarga);

        // Kanan: kontrol qty + subtotal + hapus
        JPanel ctrlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        ctrlPanel.setOpaque(false);

        // Subtotal
        JLabel lblSub = new JLabel(String.format("Rp %,.0f", subtotal));
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSub.setForeground(UITheme.ACCENT);
        lblSub.setPreferredSize(new Dimension(100, 28));
        lblSub.setHorizontalAlignment(SwingConstants.RIGHT);

        // Tombol −
        JButton btnMinus = roundBtn("−", C_AMBER);
        btnMinus.addActionListener(e -> kurangiItem(index));

        // Label qty
        JLabel lblQty = new JLabel(String.valueOf(jumlah), SwingConstants.CENTER);
        lblQty.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblQty.setForeground(UITheme.TEXT_PRIMARY);
        lblQty.setPreferredSize(new Dimension(30, 28));

        // Tombol +
        JButton btnPlus = roundBtn("+", C_GREEN);
        btnPlus.addActionListener(e -> tambahQtyItem(index));

        // Tombol ×
        JButton btnDel = roundBtn("×", C_RED);
        btnDel.addActionListener(e -> hapusItem(index));

        ctrlPanel.add(lblSub);
        ctrlPanel.add(Box.createHorizontalStrut(8));
        ctrlPanel.add(btnMinus);
        ctrlPanel.add(lblQty);
        ctrlPanel.add(btnPlus);
        ctrlPanel.add(Box.createHorizontalStrut(4));
        ctrlPanel.add(btnDel);

        row.add(infoPanel, BorderLayout.CENTER);
        row.add(ctrlPanel, BorderLayout.EAST);
        return row;
    }

    /** Refresh seluruh panel list keranjang */
    private void refreshKeranjang() {
        pnlKeranjangList.removeAll();

        if (keranjang.isEmpty()) {
            JPanel empty = new JPanel(new GridBagLayout());
            empty.setBackground(UITheme.BG_PAGE);
            empty.setPreferredSize(new Dimension(0, 120));
            JLabel lbl = new JLabel("Keranjang masih kosong");
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(UITheme.TEXT_SECONDARY);
            empty.add(lbl);
            pnlKeranjangList.add(empty);
        } else {
            for (int i = 0; i < keranjang.size(); i++) {
                pnlKeranjangList.add(buildItemRow(i));
            }
        }

        pnlKeranjangList.revalidate();
        pnlKeranjangList.repaint();

        // Hitung grand total & badge
        double grand = 0;
        int totalItem = 0;
        for (Object[] item : keranjang) {
            grand += (double) item[4];
            totalItem += (int) item[3];
        }
        lblGrandTotal.setText(String.format("Grand Total:  Rp %,.0f", grand));
        lblItemBadge.setText(totalItem + " item");
        hitungKembalian();
    }

    // ════════════════════════════════════════════════════════════════
    // BOTTOM BAR (grand total, uang bayar, kembalian, simpan)
    // ════════════════════════════════════════════════════════════════
    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new GridBagLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(10, 0, 0, 0));

        GridBagConstraints b = new GridBagConstraints();
        b.fill = GridBagConstraints.HORIZONTAL; b.weightx = 1;

        // Grand Total
        b.gridx = 0; b.gridy = 0; b.gridwidth = 2; b.insets = new Insets(0, 0, 10, 0);
        JPanel totalBox = new JPanel(new BorderLayout());
        totalBox.setBackground(C_INDIGO_LIGHT);
        totalBox.setBorder(new CompoundBorder(
            new LineBorder(UITheme.BORDER_COLOR, 1, true),
            new EmptyBorder(10, 14, 10, 14)));
        lblGrandTotal = new JLabel("Grand Total:  Rp 0");
        lblGrandTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGrandTotal.setForeground(UITheme.ACCENT);
        totalBox.add(lblGrandTotal);
        bar.add(totalBox, b);

        // Label uang | kembalian
        b.gridwidth = 1;
        b.gridx = 0; b.gridy = 1; b.insets = new Insets(0, 0, 4, 8);
        bar.add(boldLabel("Uang Pelanggan (Rp)"), b);
        b.gridx = 1; b.insets = new Insets(0, 0, 4, 0);
        bar.add(boldLabel("Kembalian (Rp)"), b);

        // Field uang | kembalian
        b.gridx = 0; b.gridy = 2; b.insets = new Insets(0, 0, 12, 8);
        txtUangBayar = UITheme.createTextField();
        bar.add(txtUangBayar, b);
        b.gridx = 1; b.insets = new Insets(0, 0, 12, 0);
        txtKembalian = UITheme.createTextField();
        txtKembalian.setEditable(false);
        txtKembalian.setBackground(C_GREEN_BG);
        txtKembalian.setForeground(C_GREEN);
        txtKembalian.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bar.add(txtKembalian, b);

        // Tombol aksi
        b.gridx = 0; b.gridy = 3; b.gridwidth = 2; b.insets = new Insets(0, 0, 0, 0);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        btnBatal = outlineBtn("Batal Semua");
        btnSimpan = pillBtn("💾  Simpan Pesanan", UITheme.ACCENT, new Color(0x3A56C8));
        btnSimpan.setPreferredSize(new Dimension(170, 40));
        btns.add(btnBatal); btns.add(btnSimpan);
        bar.add(btns, b);

        return bar;
    }

    // ════════════════════════════════════════════════════════════════
    // CARD RIWAYAT
    // ════════════════════════════════════════════════════════════════
    private JPanel buildRiwayatCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 10));

        JPanel rHead = new JPanel(new BorderLayout());
        rHead.setOpaque(false);
        rHead.add(sectionLabel("Riwayat Penjualan"), BorderLayout.WEST);
        JPanel rBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rBtns.setOpaque(false);
        btnDetail = pillBtn("Detail", UITheme.ACCENT, new Color(0x3A56C8));
        btnDetail.setPreferredSize(new Dimension(90, 32));
        btnHapusTransaksi = pillBtn("Hapus", UITheme.DANGER, UITheme.DANGER.darker());
        btnHapusTransaksi.setPreferredSize(new Dimension(90, 32));
        rBtns.add(btnDetail); rBtns.add(btnHapusTransaksi);
        rHead.add(rBtns, BorderLayout.EAST);
        card.add(rHead, BorderLayout.NORTH);

        modelRiwayat = new DefaultTableModel(
            new String[]{"ID","No Faktur","Customer","Total Item","Total Bayar"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tblRiwayat = new JTable(modelRiwayat);
        tblRiwayat.getColumnModel().getColumn(0).setMinWidth(0);
        tblRiwayat.getColumnModel().getColumn(0).setMaxWidth(0);
        tblRiwayat.getColumnModel().getColumn(0).setPreferredWidth(0);
        UITheme.styleTable(tblRiwayat);
        card.add(UITheme.createScrollPane(tblRiwayat), BorderLayout.CENTER);

        return card;
    }

    // ════════════════════════════════════════════════════════════════
    // LOGIKA KERANJANG
    // ════════════════════════════════════════════════════════════════

    private void tambahKeKeranjang() {
        try {
            String jumlahStr = txtJumlah.getText().trim();
            if (jumlahStr.isEmpty()) { showError("Jumlah wajib diisi."); return; }
            int jumlah = Integer.parseInt(jumlahStr);
            if (jumlah <= 0) { showError("Jumlah harus lebih dari 0."); return; }

            String namaBarang = (cmbBarang.getSelectedItem() == null)
                ? "" : cmbBarang.getSelectedItem().toString();
            if (namaBarang.isEmpty()) { showError("Pilih barang terlebih dahulu."); return; }

            // Berapa sudah ada di keranjang untuk barang ini
            int sudah = 0;
            for (Object[] it : keranjang)
                if (it[1].toString().equals(namaBarang)) sudah += (int) it[3];

            if (jumlah + sudah > stokBarang) {
                showError("Stok tidak cukup.\nTersedia: " + stokBarang
                    + ", di keranjang: " + sudah + ", diminta: " + jumlah);
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                "SELECT id_barang FROM tb_barang WHERE nama_barang=?");
            ps.setString(1, namaBarang);
            ResultSet rs = ps.executeQuery();
            String idBarang = rs.next() ? rs.getString("id_barang") : "";

            double harga    = Double.parseDouble(txtHarga.getText().trim());
            double subtotal = harga * jumlah;

            boolean found = false;
            for (Object[] it : keranjang) {
                if (it[0].toString().equals(idBarang)) {
                    it[3] = (int) it[3] + jumlah;
                    it[4] = (double) it[2] * (int) it[3];
                    found = true; break;
                }
            }
            if (!found) keranjang.add(
                new Object[]{idBarang, namaBarang, harga, jumlah, subtotal});

            refreshKeranjang();
            txtJumlah.setText(""); txtSubtotal.setText("");
        } catch (NumberFormatException ex) {
            showError("Jumlah harus berupa angka.");
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    /** Tombol + pada baris keranjang → tambah qty 1 */
    private void tambahQtyItem(int index) {
        if (index < 0 || index >= keranjang.size()) return;
        Object[] item = keranjang.get(index);
        String nama  = item[1].toString();
        int    sudah = (int) item[3];

        // Ambil stok terkini dari DB
        int stok = stokDariDB(nama);
        if (sudah + 1 > stok) {
            showError("Stok tidak cukup. Tersedia: " + stok); return;
        }
        item[3] = sudah + 1;
        item[4] = (double) item[2] * (int) item[3];
        refreshKeranjang();
    }

    /** Tombol − pada baris keranjang → kurangi qty 1 */
    private void kurangiItem(int index) {
        if (index < 0 || index >= keranjang.size()) return;
        Object[] item = keranjang.get(index);
        int qty = (int) item[3];
        if (qty <= 1) {
            int opt = JOptionPane.showConfirmDialog(this,
                "Qty sudah 1. Hapus \"" + item[1] + "\" dari keranjang?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                keranjang.remove(index);
                refreshKeranjang();
            }
            return;
        }
        item[3] = qty - 1;
        item[4] = (double) item[2] * (int) item[3];
        refreshKeranjang();
    }

    /** Tombol × pada baris keranjang → hapus item */
    private void hapusItem(int index) {
        if (index < 0 || index >= keranjang.size()) return;
        int opt = JOptionPane.showConfirmDialog(this,
            "Hapus \"" + keranjang.get(index)[1] + "\" dari keranjang?",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            keranjang.remove(index);
            refreshKeranjang();
        }
    }

    /** Kosongkan seluruh keranjang */
    private void batalSemua() {
        if (keranjang.isEmpty()) return;
        int opt = JOptionPane.showConfirmDialog(this,
            "Kosongkan semua item keranjang?", "Konfirmasi",
            JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            keranjang.clear();
            txtUangBayar.setText(""); txtKembalian.setText("");
            txtKembalian.setBackground(C_GREEN_BG);
            txtKembalian.setForeground(C_GREEN);
            refreshKeranjang();
        }
    }

    // ════════════════════════════════════════════════════════════════
    // SIMPAN PESANAN
    // ════════════════════════════════════════════════════════════════
    private void simpanPesanan() {
        if (keranjang.isEmpty()) { showError("Keranjang masih kosong."); return; }

        String rawUang = txtUangBayar.getText().trim()
                           .replace(".", "").replace(",", "");
        if (rawUang.isEmpty()) {
            showError("Masukkan uang yang dibayarkan pelanggan.");
            txtUangBayar.requestFocus(); return;
        }
        double uangBayar;
        try { uangBayar = Double.parseDouble(rawUang); }
        catch (NumberFormatException ex) {
            showError("Uang pelanggan harus berupa angka.");
            txtUangBayar.requestFocus(); return;
        }
        double grandTotal = grandTotalSekarang();
        if (uangBayar < grandTotal) {
            showError(String.format(
                "Uang pelanggan kurang!\nGrand Total : Rp %,.0f\n"
                + "Uang Masuk  : Rp %,.0f\nKurang      : Rp %,.0f",
                grandTotal, uangBayar, grandTotal - uangBayar));
            txtUangBayar.requestFocus(); return;
        }

        try {
            conn.setAutoCommit(false);
            String idCustomer = getIdCustomer();

            PreparedStatement psH = conn.prepareStatement(
                "INSERT INTO tb_penjualan "
                + "(no_faktur,tgl_transaksi,id_customer,total_bayar,id_user) "
                + "VALUES (?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            psH.setString(1, txtFaktur.getText());
            psH.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            psH.setString(3, idCustomer);
            psH.setDouble(4, grandTotal);
            psH.setInt(5, Session.getIdUser());
            psH.executeUpdate();

            ResultSet rs = psH.getGeneratedKeys();
            int idJual = rs.next() ? rs.getInt(1) : 0;

            for (Object[] item : keranjang) {
                PreparedStatement pd = conn.prepareStatement(
                    "INSERT INTO tb_detail_penjualan "
                    + "(id_jual,id_barang,harga_satuan,jumlah_beli,subtotal) "
                    + "VALUES (?,?,?,?,?)");
                pd.setInt(1, idJual);
                pd.setString(2, item[0].toString());
                pd.setDouble(3, (double) item[2]);
                pd.setInt(4, (int) item[3]);
                pd.setDouble(5, (double) item[4]);
                pd.executeUpdate();

                PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE tb_barang SET stok = stok - ? WHERE id_barang=?");
                ps2.setInt(1, (int) item[3]);
                ps2.setString(2, item[0].toString());
                ps2.executeUpdate();
            }

            conn.commit();
            double kembalian = uangBayar - grandTotal;
            showSuccess(String.format(
                "Transaksi berhasil!\n\n"
                + "Grand Total : Rp %,.0f\n"
                + "Uang Masuk  : Rp %,.0f\n"
                + "Kembalian   : Rp %,.0f",
                grandTotal, uangBayar, kembalian));

            keranjang.clear();
            txtUangBayar.setText(""); txtKembalian.setText("");
            txtKembalian.setBackground(C_GREEN_BG); txtKembalian.setForeground(C_GREEN);
            cmbBarang.removeAllItems(); tampilBarang(); tampilHarga();
            refreshKeranjang(); generateFaktur(); tampilRiwayat();
        } catch (Exception ex) {
            try { conn.rollback(); } catch (Exception e2) {}
            showError(ex.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (Exception e2) {}
        }
    }

    // ════════════════════════════════════════════════════════════════
    // HAPUS TRANSAKSI
    // ════════════════════════════════════════════════════════════════
    private void hapusTransaksi() {
        int row = tblRiwayat.getSelectedRow();
        if (row < 0) { showError("Pilih transaksi yang ingin dihapus."); return; }

        int    idJual   = Integer.parseInt(modelRiwayat.getValueAt(row, 0).toString());
        String noFaktur = modelRiwayat.getValueAt(row, 1).toString();

        int opt = JOptionPane.showConfirmDialog(this,
            "Hapus transaksi " + noFaktur
            + "?\nStok barang akan dikembalikan otomatis.",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (opt != JOptionPane.YES_OPTION) return;

        try {
            conn.setAutoCommit(false);
            PreparedStatement psDet = conn.prepareStatement(
                "SELECT id_barang, jumlah_beli FROM tb_detail_penjualan WHERE id_jual=?");
            psDet.setInt(1, idJual);
            ResultSet rs = psDet.executeQuery();
            while (rs.next()) {
                PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE tb_barang SET stok = stok + ? WHERE id_barang=?");
                ps2.setInt(1, rs.getInt("jumlah_beli"));
                ps2.setString(2, rs.getString("id_barang"));
                ps2.executeUpdate();
            }
            conn.prepareStatement(
                "DELETE FROM tb_detail_penjualan WHERE id_jual=" + idJual)
                .executeUpdate();
            conn.prepareStatement(
                "DELETE FROM tb_penjualan WHERE id_jual=" + idJual)
                .executeUpdate();
            conn.commit();
            showSuccess("Transaksi " + noFaktur + " berhasil dihapus. Stok dikembalikan.");
            cmbBarang.removeAllItems(); tampilBarang(); tampilHarga();
            tampilRiwayat(); generateFaktur();
        } catch (Exception ex) {
            try { conn.rollback(); } catch (Exception e2) {}
            showError(ex.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (Exception e2) {}
        }
    }

    // ════════════════════════════════════════════════════════════════
    // DETAIL FAKTUR
    // ════════════════════════════════════════════════════════════════
    private void lihatDetail() {
        int row = tblRiwayat.getSelectedRow();
        if (row < 0) { showError("Pilih transaksi terlebih dahulu."); return; }
        tampilDetailFaktur(
            Integer.parseInt(modelRiwayat.getValueAt(row, 0).toString()),
            modelRiwayat.getValueAt(row, 1).toString(),
            modelRiwayat.getValueAt(row, 2).toString());
    }

    private void tampilDetailFaktur(int idJual, String noFaktur, String namaCustomer) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT b.nama_barang, d.harga_satuan, d.jumlah_beli, d.subtotal "
                + "FROM tb_detail_penjualan d "
                + "JOIN tb_barang b ON d.id_barang=b.id_barang WHERE d.id_jual=?");
            ps.setInt(1, idJual);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel dm = new DefaultTableModel(
                new String[]{"Nama Barang","Harga Satuan","Qty","Subtotal"}, 0
            ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
            double total = 0;
            while (rs.next()) {
                double sub = rs.getDouble("subtotal");
                dm.addRow(new Object[]{
                    rs.getString("nama_barang"),
                    String.format("Rp %,.0f", rs.getDouble("harga_satuan")),
                    rs.getInt("jumlah_beli"),
                    String.format("Rp %,.0f", sub)
                });
                total += sub;
            }

            JTable t = new JTable(dm); UITheme.styleTable(t);
            t.setPreferredScrollableViewportSize(
                new Dimension(500, Math.max(dm.getRowCount(), 3) * t.getRowHeight()));

            JPanel panel = new JPanel(new BorderLayout(0, 12));
            panel.setBackground(UITheme.BG_CARD);
            panel.setBorder(new EmptyBorder(16, 16, 16, 16));

            JPanel info = new JPanel(new GridLayout(2, 2, 12, 4));
            info.setOpaque(false);
            info.add(UITheme.createLabel("No Faktur"));
            info.add(UITheme.createLabel("Customer"));
            JLabel lf = new JLabel(noFaktur);
            lf.setFont(UITheme.FONT_BODY); lf.setForeground(UITheme.TEXT_PRIMARY);
            JLabel lc = new JLabel(namaCustomer);
            lc.setFont(UITheme.FONT_BODY); lc.setForeground(UITheme.TEXT_PRIMARY);
            info.add(lf); info.add(lc);
            panel.add(info, BorderLayout.NORTH);
            panel.add(UITheme.createScrollPane(t), BorderLayout.CENTER);

            JLabel lblTotal = new JLabel(
                String.format("Total Pembayaran:  Rp %,.0f", total));
            lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblTotal.setForeground(UITheme.ACCENT);
            lblTotal.setBorder(new EmptyBorder(10, 0, 0, 0));
            panel.add(lblTotal, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panel,
                "Detail Faktur — " + noFaktur, JOptionPane.PLAIN_MESSAGE);
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    // ════════════════════════════════════════════════════════════════
    // DATA HELPERS
    // ════════════════════════════════════════════════════════════════
    private void generateFaktur() {
        try {
            ResultSet rs = conn.createStatement()
                .executeQuery("SELECT MAX(no_faktur) FROM tb_penjualan");
            String f = "FKT001";
            if (rs.next() && rs.getString(1) != null) {
                int n = Integer.parseInt(rs.getString(1).substring(3));
                f = String.format("FKT%03d", n + 1);
            }
            txtFaktur.setText(f);
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void tampilCustomer() {
        try {
            ResultSet rs = conn.prepareStatement(
                "SELECT * FROM tb_customer").executeQuery();
            while (rs.next()) cmbCustomer.addItem(rs.getString("nama_customer"));
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void tampilBarang() {
        try {
            ResultSet rs = conn.prepareStatement(
                "SELECT * FROM tb_barang WHERE stok > 0").executeQuery();
            while (rs.next()) cmbBarang.addItem(rs.getString("nama_barang"));
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void tampilHarga() {
        try {
            if (cmbBarang.getSelectedItem() == null) return;
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM tb_barang WHERE nama_barang=?");
            ps.setString(1, cmbBarang.getSelectedItem().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtHarga.setText(String.valueOf(rs.getDouble("harga_jual")));
                stokBarang = rs.getInt("stok");
            }
            hitungSubtotal();
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private void hitungSubtotal() {
        try {
            double h = Double.parseDouble(txtHarga.getText().trim());
            int    j = Integer.parseInt(txtJumlah.getText().trim());
            txtSubtotal.setText(String.format("%.0f", h * j));
        } catch (Exception ignored) { txtSubtotal.setText(""); }
    }

    private void hitungKembalian() {
        try {
            String raw = txtUangBayar.getText().trim()
                           .replace(".", "").replace(",", "");
            if (raw.isEmpty()) { txtKembalian.setText(""); return; }
            double uang = Double.parseDouble(raw);
            double kem  = uang - grandTotalSekarang();
            txtKembalian.setText(String.format("%,.0f", kem));
            if (kem < 0) {
                txtKembalian.setBackground(C_RED_BG);
                txtKembalian.setForeground(C_RED);
            } else {
                txtKembalian.setBackground(C_GREEN_BG);
                txtKembalian.setForeground(C_GREEN);
            }
        } catch (Exception ignored) { txtKembalian.setText(""); }
    }

    private double grandTotalSekarang() {
        double total = 0;
        for (Object[] item : keranjang) total += (double) item[4];
        return total;
    }

    private int stokDariDB(String namaBarang) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT stok FROM tb_barang WHERE nama_barang=?");
            ps.setString(1, namaBarang);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("stok") : 0;
        } catch (Exception ex) { return 0; }
    }

    private void tampilRiwayat() {
        try {
            modelRiwayat.setRowCount(0);
            PreparedStatement ps = conn.prepareStatement(
                "SELECT p.id_jual, p.no_faktur, c.nama_customer, "
                + "SUM(d.jumlah_beli) total_item, p.total_bayar "
                + "FROM tb_penjualan p "
                + "JOIN tb_customer c ON p.id_customer=c.id_customer "
                + "JOIN tb_detail_penjualan d ON p.id_jual=d.id_jual "
                + "GROUP BY p.id_jual ORDER BY p.id_jual DESC");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) modelRiwayat.addRow(new Object[]{
                rs.getInt("id_jual"), rs.getString("no_faktur"),
                rs.getString("nama_customer"), rs.getInt("total_item"),
                String.format("Rp %,.0f", rs.getDouble("total_bayar"))
            });
        } catch (Exception ex) { showError(ex.getMessage()); }
    }

    private String getIdCustomer() throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT id_customer FROM tb_customer WHERE nama_customer=?");
        ps.setString(1, cmbCustomer.getSelectedItem().toString());
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getString("id_customer") : "";
    }

    // ════════════════════════════════════════════════════════════════
    // UI HELPERS
    // ════════════════════════════════════════════════════════════════

    /** Card putih dengan shadow tipis */
    private static JPanel createCard() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(UITheme.BORDER_COLOR, 1, true),
            new EmptyBorder(18, 20, 18, 20)
        ));
        return p;
    }

    /** Tombol bulat untuk ±× di baris keranjang */
    private static JButton roundBtn(String label, Color color) {
        JButton b = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed()  ? color.darker()  :
                            getModel().isRollover() ? color.darker()  : color);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(28, 28));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Tombol pill gradien */
    private static JButton pillBtn(String text, Color c1, Color c2) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()  ? c2.darker() :
                           getModel().isRollover() ? c2 : c1;
                GradientPaint gp = new GradientPaint(0,0,bg,getWidth(),0,c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(UITheme.FONT_BUTTON); b.setForeground(Color.WHITE);
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(130, 38));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Tombol outline tipis (Batal) */
    private static JButton outlineBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(UITheme.FONT_BUTTON);
        b.setForeground(UITheme.TEXT_SECONDARY);
        b.setBackground(Color.WHITE);
        b.setBorder(new CompoundBorder(
            new LineBorder(UITheme.BORDER_COLOR, 1, true),
            new EmptyBorder(6, 14, 6, 14)));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(130, 40));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Panel dua kolom untuk pasangan label/field */
    private static JPanel row2(Component a, Component b) {
        JPanel p = new JPanel(new GridLayout(1, 2, 10, 0));
        p.setOpaque(false); p.add(a); p.add(b);
        return p;
    }

    private static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(UITheme.TEXT_PRIMARY);
        return l;
    }

    private static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(new Color(0x7C8DB0));
        return l;
    }

    private static JLabel boldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(UITheme.TEXT_PRIMARY);
        return l;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Kesalahan",
                                      JOptionPane.ERROR_MESSAGE);
    }
    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Berhasil",
                                      JOptionPane.INFORMATION_MESSAGE);
    }
}