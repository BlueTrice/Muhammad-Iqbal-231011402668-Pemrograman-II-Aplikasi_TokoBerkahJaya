package tokoberkahjaya;

/**
 * Session — menyimpan data user yang sedang login.
 * Di-set satu kali saat login berhasil, dibaca oleh semua Form.
 */
public class Session {

    private static int    idUser;
    private static String namaLengkap;
    private static String username;
    private static String level;   // "ADMIN" atau "KASIR"

    public static void login(int id, String nama, String user, String lvl) {
        idUser      = id;
        namaLengkap = nama;
        username    = user;
        level       = lvl;
    }

    public static void logout() {
        idUser      = 0;
        namaLengkap = "";
        username    = "";
        level       = "";
    }

    public static int    getIdUser()      { return idUser;      }
    public static String getNamaLengkap() { return namaLengkap; }
    public static String getUsername()    { return username;    }
    public static String getLevel()       { return level;       }

    /** Kembalikan true jika user yang login adalah ADMIN */
    public static boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(level);
    }
}
