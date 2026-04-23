package controller;

import dao.UserDAO;
import model.User;
import utils.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label         lblError;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Veuillez remplir tous les champs.");
            return;
        }

        User user = userDAO.findByLogin(username, password);

        if (user == null) {
            lblError.setText("Identifiants incorrects !");
            return;
        }

        // Stocker l'utilisateur en session
        Session.setCurrentUser(user);

        // Rediriger selon le rôle
        String fxmlPath;
        switch (user.getRole()) {
            case "ADMIN":      fxmlPath = "/view/admin/AdminDashboard.fxml"; break;
            case "MEDECIN":    fxmlPath = "/view/medecin/MedecinDash.fxml"; break;
            case "SECRETAIRE": fxmlPath = "/view/secretaire/SecretaireDash.fxml"; break;
            default:           lblError.setText("Rôle inconnu."); return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Hôpital — " + user.getRole());
        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Erreur de chargement de l'interface.");
        }
    }
}