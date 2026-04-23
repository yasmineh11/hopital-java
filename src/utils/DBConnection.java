package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Paramètres de connexion — à adapter
    private static final String URL      = "jdbc:mysql://localhost:3306/hopital_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "";  // Votre mot de passe MySQL

    private static Connection connection = null;

    // Méthode Singleton — une seule connexion partagée
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion MySQL réussie !");
            } catch (ClassNotFoundException e) {
                System.err.println("Driver JDBC introuvable : " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Erreur connexion BD : " + e.getMessage());
            }
        }
        return connection;
    }
}
