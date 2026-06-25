# Muhammad-Iqbal-231011402668-Pemrograman-II-Aplikasi_TokoBerkahJaya
Aplikasi Toko Berkah Jaya - Project Akhir Pemrograman II

# 🛒 Toko Berkah Jaya

Aplikasi **Toko Berkah Jaya** merupakan sistem informasi penjualan (Point of Sale/POS) berbasis desktop yang dikembangkan menggunakan **Java Swing** dan **MySQL**. Aplikasi ini dirancang untuk membantu proses pengelolaan data barang, customer, pengguna, serta transaksi penjualan secara lebih efektif dan terkomputerisasi.

## ✨ Fitur Utama

- 🔐 Login dengan autentikasi pengguna
- 👤 Manajemen User (Administrator & Kasir)
- 📦 Manajemen Data Barang
- 🏷️ Manajemen Kategori Barang
- 👥 Manajemen Data Customer
- 🛒 Transaksi Penjualan
- 💰 Perhitungan otomatis Subtotal, Grand Total, dan Kembalian
- 📜 Riwayat Transaksi
- 🔒 Hak akses berdasarkan level pengguna (Role-Based Access)

---

## 🖥️ Teknologi yang Digunakan

- Java
- Java Swing
- JDBC
- MySQL
- XAMPP
- Apache NetBeans IDE

---

## 📂 Struktur Project

```
tokoberkahjaya/
│
├── Login.java
├── MenuUtama.java
├── FormBarang.java
├── FormCustomer.java
├── FormUser.java
├── FormPenjualan.java
├── Koneksi.java
├── Session.java
├── UITheme.java
└── Database/
    └── tokoberkahjaya.sql
```

---

## 🗄️ Database

Import file database terlebih dahulu.

```
tokoberkahjaya.sql
```

Database menggunakan **MySQL**.

Pastikan konfigurasi koneksi pada file **Koneksi.java** sesuai dengan database lokal.

```java
String url = "jdbc:mysql://localhost:3306/tokoberkahjaya";
String user = "root";
String password = "";
```

---

## 🚀 Cara Menjalankan Project

1. Clone repository

```bash
git clone https://github.com/username/tokoberkahjaya.git
```

2. Import project ke Apache NetBeans IDE.

3. Jalankan XAMPP kemudian aktifkan:

- Apache
- MySQL

4. Import database

```
tokoberkahjaya.sql
```

5. Pastikan MySQL Connector telah ditambahkan ke project.

6. Jalankan file

```
Login.java
```

---

## 👥 Hak Akses

### Administrator

- Login
- Kelola Barang
- Kelola Customer
- Kelola User
- Transaksi Penjualan
- Logout

### Kasir

- Login
- Melihat Data Barang
- Melihat Data User
- Kelola Customer
- Transaksi Penjualan
- Logout

---

## 📸 Tampilan Aplikasi

- Login
- Dashboard
- Data Barang
- Data Customer
- Data User
- Penjualan

*(Tambahkan screenshot aplikasi di sini jika diperlukan.)*

---

## 📋 Pengujian

Aplikasi telah diuji menggunakan metode **Black Box Testing** pada seluruh modul, meliputi:

- Login
- Data Barang
- Data Customer
- Data User
- Penjualan
- Logout

Hasil pengujian menunjukkan seluruh fungsi utama berjalan sesuai dengan kebutuhan sistem.

---

## 📄 Lisensi

Project ini dibuat untuk keperluan pembelajaran dan tugas perkuliahan Program Studi Teknik Informatika Universitas Pamulang.

Silakan digunakan sebagai referensi pembelajaran dengan tetap menghargai karya dan tidak melakukan plagiarisme.

---

## 👨‍💻 Author

**Nama:** *Nama Kamu*  
**Program Studi:** Teknik Informatika  
**Universitas:** Universitas Pamulang
