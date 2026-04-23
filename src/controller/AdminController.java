package controller;

import dao.MedecinDAO;
import dao.UserDAO;
import model.Medecin;
import model.User;
import utils.Session;
import javafx.collections.FXCollections;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class AdminController {

    @FXML private StackPane centerPane;

    private MedecinDAO medecinDAO = new MedecinDAO();
    private UserDAO    userDAO    = new UserDAO();

    // ── TABLEAU DE BORD ──────────────────────────────────────
    @FXML
    public void showDashboard() {
        VBox box = new VBox(15);
        box.setStyle("-fx-padding: 30;");
        box.getChildren().addAll(
                new Label("Bienvenue, " + Session.getCurrentUser().getUsername()),
                new Label("Médecins enregistrés : " + medecinDAO.getAllMedecins().size()),
                new Label("Utilisateurs actifs : " + userDAO.getAllUsers().size())
        );
        centerPane.getChildren().setAll(box);
    }

    // ── GESTION MÉDECINS ─────────────────────────────────────
    @FXML
    public void showMedecins() {
        VBox box = new VBox(10);
        box.setStyle("-fx-padding: 20;");

        // TableView
        TableView<Medecin> table = new TableView<>();
        TableColumn<Medecin, String> colNom  = new TableColumn<>("Nom");
        TableColumn<Medecin, String> colSpec = new TableColumn<>("Spécialité");
        TableColumn<Medecin, String> colTel  = new TableColumn<>("Téléphone");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colSpec.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        table.getColumns().addAll(colNom, colSpec, colTel);
        table.setItems(FXCollections.observableArrayList(medecinDAO.getAllMedecins()));
        table.setPrefHeight(300);

        // Formulaire ajout
        TextField tfNom   = new TextField(); tfNom.setPromptText("Nom");
        TextField tfPre   = new TextField(); tfPre.setPromptText("Prénom");
        TextField tfSpec  = new TextField(); tfSpec.setPromptText("Spécialité");
        TextField tfTel   = new TextField(); tfTel.setPromptText("Téléphone");
        TextField tfEmail = new TextField(); tfEmail.setPromptText("Email");
        Button btnAdd = new Button("➕ Ajouter médecin");
        Button btnDel = new Button("🗑 Supprimer sélection");

        btnAdd.setOnAction(e -> {
            Medecin m = new Medecin(tfNom.getText(), tfPre.getText(),
                    tfSpec.getText(), tfTel.getText(), tfEmail.getText());
            if (medecinDAO.addMedecin(m)) {
                table.setItems(FXCollections.observableArrayList(medecinDAO.getAllMedecins()));
                tfNom.clear(); tfPre.clear(); tfSpec.clear();
                tfTel.clear(); tfEmail.clear();
            }
        });

        btnDel.setOnAction(e -> {
            Medecin sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                medecinDAO.deleteMedecin(sel.getId());
                table.setItems(FXCollections.observableArrayList(medecinDAO.getAllMedecins()));
            }
        });

        HBox form = new HBox(8, tfNom, tfPre, tfSpec, tfTel, tfEmail, btnAdd, btnDel);
        box.getChildren().addAll(new Label("👨‍⚕️ Gestion des Médecins"), table, form);
        centerPane.getChildren().setAll(box);
    }

    // ── GESTION UTILISATEURS ─────────────────────────────────
    @FXML
    public void showUtilisateurs() {
        VBox box = new VBox(10);
        box.setStyle("-fx-padding: 20;");

        TableView<User> table = new TableView<>();
        TableColumn<User, String> colUser = new TableColumn<>("Username");
        TableColumn<User, String> colRole = new TableColumn<>("Rôle");
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        table.getColumns().addAll(colUser, colRole);
        table.setItems(FXCollections.observableArrayList(userDAO.getAllUsers()));
        table.setPrefHeight(250);

        TextField tfUser = new TextField(); tfUser.setPromptText("Username");
        PasswordField tfPass = new PasswordField(); tfPass.setPromptText("Password");
        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll("ADMIN", "MEDECIN", "SECRETAIRE");
        cbRole.setValue("MEDECIN");
        Button btnAdd = new Button("➕ Créer compte");
        Button btnDel = new Button("🗑 Supprimer");

        btnAdd.setOnAction(e -> {
            User u = new User(tfUser.getText(), tfPass.getText(), cbRole.getValue());
            if (userDAO.addUser(u)) {
                table.setItems(FXCollections.observableArrayList(userDAO.getAllUsers()));
                tfUser.clear(); tfPass.clear();
            }
        });

        btnDel.setOnAction(e -> {
            User sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                userDAO.deleteUser(sel.getId());
                table.setItems(FXCollections.observableArrayList(userDAO.getAllUsers()));
            }
        });

        HBox form = new HBox(8, tfUser, tfPass, cbRole, btnAdd, btnDel);
        box.getChildren().addAll(new Label("👥 Gestion des Utilisateurs"), table, form);
        centerPane.getChildren().setAll(box);
    }

    // ── DÉCONNEXION ──────────────────────────────────────────
    @FXML
    public void logout() {
        Session.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Stage stage = (Stage) centerPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Hôpital — Connexion");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void showStats() {
        Label lbl = new Label(
                "📊 Statistiques\n" +
                        "Médecins : " + medecinDAO.getAllMedecins().size() + "\n" +
                        "Utilisateurs : " + userDAO.getAllUsers().size()
        );
        lbl.setStyle("-fx-font-size:18; -fx-padding:30;");
        centerPane.getChildren().setAll(lbl);
    }
}
