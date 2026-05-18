package controller;

import dao.UserDAO;
import model.User;
import utils.Session;
import utils.ThemeManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label         lblError;

    private final UserDAO userDAO = new UserDAO();

    // Applique le thème actuel dès que l'écran login se charge
    @FXML
    public void initialize() {
        // Appliquer le thème après le rendu (runLater pour avoir la scène)
        javafx.application.Platform.runLater(() -> {
            if (txtUsername.getScene() != null)
                ThemeManager.apply(txtUsername.getScene());
        });
    }

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

        Session.setCurrentUser(user);

        String fxmlPath = switch (user.getRole()) {
            case "ADMIN"      -> "/view/admin/AdminDashboard.fxml";
            case "MEDECIN"    -> "/view/medecin/MedecinDash.fxml";
            case "SECRETAIRE" -> "/view/secretaire/SecretaireDash.fxml";
            default -> null;
        };

        if (fxmlPath == null) {
            lblError.setText("Rôle inconnu.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath));
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            Scene scene = new Scene(loader.load());

            // ← Appliquer le thème en cours sur la nouvelle scène
            ThemeManager.apply(scene);

            stage.setScene(scene);
            stage.setTitle("Hôpital — " + user.getRole());
        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Erreur de chargement de l'interface.");
        }
    }
}