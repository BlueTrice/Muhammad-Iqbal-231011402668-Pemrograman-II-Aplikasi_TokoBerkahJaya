package tokoberkahjaya;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class Koneksi {
    private static Connection koneksi;

    public static Connection getConnection() {

        try {

            String url =
                "jdbc:mysql://localhost:3306/tokoberkahjaya";

            String user = "root";
            String password = "";

            DriverManager.registerDriver(
                new com.mysql.cj.jdbc.Driver()
            );

            koneksi = DriverManager.getConnection(
                url,
                user,
                password
            );

            System.out.println(
                "Koneksi berhasil"
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                null,
                "Koneksi gagal : " + e.getMessage()
            );

        }

        return koneksi;
    }
}