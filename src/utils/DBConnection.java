package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/hopital_db"
                    + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";
    private static final String PASSWORD = ""; // votre mot de passe MySQL

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            // Reconnexion automatique si connexion perdue ou fermée
            if (connection == null
                    || connection.isClosed()
                    || !connection.isValid(2)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DBConnection] Connexion MySQL établie.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] Driver introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("[DBConnection] Erreur : " + e.getMessage());
        }
        return connection;
    }

    public static void close() {
        if (connection != null) {
            try { connection.close(); connection = null; }
            catch (SQLException e) { e.printStackTrace(); }
        }
    }
}