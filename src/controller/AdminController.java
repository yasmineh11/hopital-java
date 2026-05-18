package controller;

import dao.MedecinDAO;
import dao.UserDAO;
import model.Medecin;
import model.User;
import utils.Session;
import utils.Validator;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class AdminController {

    // ── Références FXML ──────────────────────────────────────────
    @FXML private StackPane centerPane;
    @FXML private Label     lblPageTitle;
    @FXML private Label     lblPageSub;
    @FXML private Label     lblAvatar;
    @FXML private Label     lblUsername;
    @FXML private TextField tfSearch;
    @FXML private Button    btnPrimary;
    @FXML private Button    btnTheme;

    // Boutons nav
    @FXML private Button btnDash;
    @FXML private Button btnMedecins;
    @FXML private Button btnUsers;
    @FXML private Button btnStats;

    // ── DAO ──────────────────────────────────────────────────────
    private final MedecinDAO medecinDAO = new MedecinDAO();
    private final UserDAO    userDAO    = new UserDAO();

    // ── Thème ────────────────────────────────────────────────────
    private boolean darkMode = false;

    // ════════════════════════════════════════════════════════════
    // INITIALISATION
    // ════════════════════════════════════════════════════════════
    @FXML
    public void initialize() {
        if (Session.getCurrentUser() != null) {
            String name = Session.getCurrentUser().getUsername();
            lblUsername.setText(name);
            lblAvatar.setText(
                    name.length() >= 2
                            ? name.substring(0, 2).toUpperCase()
                            : name.toUpperCase()
            );
        }
        showDashboard();
    }

    // ════════════════════════════════════════════════════════════
    // TOGGLE THÈME
    // ════════════════════════════════════════════════════════════
    @FXML
    public void toggleTheme() {
        darkMode = !darkMode;
        Scene scene = centerPane.getScene();
        scene.getStylesheets().clear();
        String css = darkMode
                ? "/resources/hopital-dark.css"
                : "/resources/hopital-light.css";
        scene.getStylesheets().add(
                getClass().getResource(css).toExternalForm());
        btnTheme.setText(darkMode ? "☀️" : "🌙");
    }

    // ════════════════════════════════════════════════════════════
    // NAVIGATION
    // ════════════════════════════════════════════════════════════
    private void setActive(Button active) {
        for (Button b : new Button[]{btnDash, btnMedecins, btnUsers, btnStats}) {
            if (b == null) continue;
            b.getStyleClass().remove("nav-btn-active");
            if (!b.getStyleClass().contains("nav-btn"))
                b.getStyleClass().add("nav-btn");
        }
        if (active != null) active.getStyleClass().add("nav-btn-active");
    }

    private void setTopbar(String titre, String sous,
                           String btnLabel, boolean showSearch) {
        lblPageTitle.setText(titre);
        lblPageSub.setText(sous);
        if (btnLabel != null && !btnLabel.isBlank()) {
            btnPrimary.setText(btnLabel);
            btnPrimary.setVisible(true);
        } else {
            btnPrimary.setVisible(false);
        }
        if (tfSearch != null) tfSearch.setVisible(showSearch);
    }

    @FXML public void handlePrimaryAction() {}

    // ════════════════════════════════════════════════════════════
    // TABLEAU DE BORD
    // ════════════════════════════════════════════════════════════
    @FXML
    public void showDashboard() {
        setActive(btnDash);
        setTopbar("Tableau de bord", "Vue générale du système", null, false);

        int nbMed  = medecinDAO.getAllMedecins().size();
        int nbUser = userDAO.getAllUsers().size();

        HBox statsRow = new HBox(10);
        statsRow.getChildren().addAll(
                makeStatCard("Médecins",      String.valueOf(nbMed),
                        "stat-card-green",  "stat-value-green",  "+2 ce mois"),
                makeStatCard("Utilisateurs",  String.valueOf(nbUser),
                        "stat-card-blue",   "stat-value-blue",   "3 rôles"),
                makeStatCard("RDV aujourd'hui", "24",
                        "stat-card-orange", "stat-value-orange", "8 confirmés"),
                makeStatCard("Comptes actifs", String.valueOf(nbUser),
                        "stat-card-purple", "stat-value-purple", "")
        );

        TableView<Medecin> tMed = buildMedecinTable(
                medecinDAO.getAllMedecins().stream().limit(4).toList(),
                null, null, null, null, null, false);
        tMed.setPrefHeight(180);
        VBox cardMed = wrapInCard("Médecins enregistrés",
                nbMed + " médecins", tMed);

        TableView<User> tUser = buildUserTable(
                userDAO.getAllUsers(), false);
        tUser.setPrefHeight(180);
        VBox cardUser = wrapInCard("Comptes utilisateurs",
                nbUser + " comptes", tUser);

        HBox split = new HBox(14, cardMed, cardUser);
        HBox.setHgrow(cardMed,  Priority.ALWAYS);
        HBox.setHgrow(cardUser, Priority.ALWAYS);

        VBox page = new VBox(14, statsRow, split);
        centerPane.getChildren().setAll(page);
    }

    // ════════════════════════════════════════════════════════════
    // GESTION MÉDECINS
    // ════════════════════════════════════════════════════════════
    @FXML
    public void showMedecins() {
        setActive(btnMedecins);
        setTopbar("Médecins",
                medecinDAO.getAllMedecins().size() + " médecins enregistrés",
                "+ Ajouter médecin", true);

        TextField tfNom   = styledField("Nom *");
        TextField tfPre   = styledField("Prénom *");
        TextField tfSpec  = styledField("Spécialité");
        TextField tfTel   = styledField("Téléphone");
        TextField tfEmail = styledField("Email");
        Label     lblMsg  = new Label();

        TableView<Medecin> table = buildMedecinTable(
                medecinDAO.getAllMedecins(),
                tfNom, tfPre, tfSpec, tfTel, tfEmail, true);
        table.setPrefHeight(280);

        if (tfSearch != null) {
            tfSearch.textProperty().addListener((obs, old, val) -> {
                List<Medecin> all = medecinDAO.getAllMedecins();
                if (val == null || val.isBlank()) {
                    table.setItems(FXCollections.observableArrayList(all));
                } else {
                    String kw = val.toLowerCase();
                    table.setItems(FXCollections.observableArrayList(
                            all.stream().filter(m ->
                                    m.getNom().toLowerCase().contains(kw) ||
                                            m.getPrenom().toLowerCase().contains(kw) ||
                                            (m.getSpecialite() != null &&
                                                    m.getSpecialite().toLowerCase().contains(kw))
                            ).toList()));
                }
            });
        }

        Button btnAdd = new Button("➕  Ajouter");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> {
            if (tfNom.getText().isBlank() || tfPre.getText().isBlank()) {
                showMsg(lblMsg, "Le nom et le prénom sont obligatoires.", false);
                return;
            }
            Medecin m = new Medecin(
                    tfNom.getText().trim(), tfPre.getText().trim(),
                    tfSpec.getText().trim(), tfTel.getText().trim(),
                    tfEmail.getText().trim());
            if (medecinDAO.addMedecin(m)) {
                table.setItems(FXCollections.observableArrayList(
                        medecinDAO.getAllMedecins()));
                clearFields(tfNom, tfPre, tfSpec, tfTel, tfEmail);
                showMsg(lblMsg, "✓ Médecin ajouté avec succès !", true);
                setTopbar("Médecins",
                        medecinDAO.getAllMedecins().size() + " médecins enregistrés",
                        "+ Ajouter médecin", true);
            }
        });

        Button btnCancel = new Button("Annuler");
        btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> {
            clearFields(tfNom, tfPre, tfSpec, tfTel, tfEmail);
            lblMsg.setText("");
        });

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(16, 16, 10, 16));
        grid.addRow(0, fg("Nom *",       tfNom),  fg("Prénom *",    tfPre));
        grid.addRow(1, fg("Spécialité",  tfSpec), fg("Téléphone",   tfTel));
        grid.addRow(2, fg("Email",       tfEmail));
        GridPane.setHgrow(grid.getChildren().get(0), Priority.ALWAYS);
        GridPane.setHgrow(grid.getChildren().get(2), Priority.ALWAYS);
        GridPane.setHgrow(grid.getChildren().get(4), Priority.ALWAYS);

        HBox footer = buildFooter(btnAdd, btnCancel);
        lblMsg.setPadding(new Insets(0, 16, 4, 16));

        VBox formCard = buildFormCard(
                "Ajouter / Modifier un médecin", grid, lblMsg, footer);

        btnPrimary.setOnAction(e -> tfNom.requestFocus());

        VBox page = new VBox(14,
                wrapInCard("Liste des médecins",
                        medecinDAO.getAllMedecins().size() + " médecins", table),
                formCard);
        centerPane.getChildren().setAll(page);
    }

    // ════════════════════════════════════════════════════════════
    // GESTION UTILISATEURS — avec liaison médecin
    // ════════════════════════════════════════════════════════════
    @FXML
    public void showUtilisateurs() {
        setActive(btnUsers);
        setTopbar("Utilisateurs",
                userDAO.getAllUsers().size() + " comptes",
                "+ Créer compte", true);

        // ── Champs formulaire ─────────────────────────────────
        TextField     tfUser    = styledField("Nom d'utilisateur *");
        PasswordField tfPass    = new PasswordField();
        tfPass.setPromptText("Mot de passe *  (min. 6 caractères)");
        tfPass.getStyleClass().add("form-input");
        tfPass.setMaxWidth(Double.MAX_VALUE);

        PasswordField tfPassConf = new PasswordField();
        tfPassConf.setPromptText("Confirmer le mot de passe *");
        tfPassConf.getStyleClass().add("form-input");
        tfPassConf.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll("ADMIN", "MEDECIN", "SECRETAIRE");
        cbRole.setValue("MEDECIN");
        cbRole.getStyleClass().add("combo-box");
        cbRole.setMaxWidth(Double.MAX_VALUE);

        // ── ComboBox médecin (visible seulement si rôle = MEDECIN) ──
        ComboBox<Medecin> cbMedecin = new ComboBox<>();
        cbMedecin.getStyleClass().add("combo-box");
        cbMedecin.setMaxWidth(Double.MAX_VALUE);
        cbMedecin.setPromptText("Lier à un médecin existant *");

        // Rendu enrichi : "Dr. Nom Prénom — Spécialité"
        cbMedecin.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Medecin m, boolean empty) {
                super.updateItem(m, empty);
                if (empty || m == null) { setText(null); return; }
                String spec = m.getSpecialite() != null ? m.getSpecialite() : "—";
                setText("Dr. " + m.getNom() + " " + m.getPrenom() + "  —  " + spec);
            }
        });
        cbMedecin.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Medecin m, boolean empty) {
                super.updateItem(m, empty);
                if (empty || m == null) { setText(null); return; }
                setText("Dr. " + m.getNom() + " " + m.getPrenom());
            }
        });

        // Conteneur du groupe médecin (label + combo)
        VBox grpMedecin = fg("Médecin à lier *", cbMedecin);
        grpMedecin.setVisible(true);   // visible par défaut car MEDECIN est sélectionné
        grpMedecin.setManaged(true);

        // Peupler la liste selon le rôle sélectionné
        Runnable refreshMedecinCombo = () -> {
            cbMedecin.getItems().setAll(
                    medecinDAO.getMedecinsWithoutAccount());
            cbMedecin.setValue(null);
        };
        refreshMedecinCombo.run(); // peuplement initial

        // Afficher/masquer le sélecteur médecin selon le rôle choisi
        cbRole.valueProperty().addListener((obs, old, val) -> {
            boolean isMedecin = "MEDECIN".equals(val);
            grpMedecin.setVisible(isMedecin);
            grpMedecin.setManaged(isMedecin);
            if (isMedecin) refreshMedecinCombo.run();
        });

        Label lblMsg = new Label();

        // ── TableView utilisateurs ────────────────────────────
        TableView<User> table = buildUserTable(userDAO.getAllUsers(), true);
        table.setPrefHeight(260);

        if (tfSearch != null) {
            tfSearch.textProperty().addListener((obs, old, val) -> {
                List<User> all = userDAO.getAllUsers();
                if (val == null || val.isBlank()) {
                    table.setItems(FXCollections.observableArrayList(all));
                } else {
                    String kw = val.toLowerCase();
                    table.setItems(FXCollections.observableArrayList(
                            all.stream().filter(u ->
                                    u.getUsername().toLowerCase().contains(kw) ||
                                            u.getRole().toLowerCase().contains(kw)
                            ).toList()));
                }
            });
        }

        // ── Bouton Créer ──────────────────────────────────────
        Button btnAdd = new Button("✔  Créer le compte");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setPrefWidth(160);
        btnAdd.setOnAction(e -> {

            // Validation username
            if (!Validator.isValidUsername(tfUser.getText())) {
                showMsg(lblMsg,
                        "Username invalide — 3 à 50 caractères, lettres/chiffres/_ uniquement.",
                        false);
                tfUser.requestFocus();
                return;
            }
            // Validation mot de passe
            if (!Validator.isValidPassword(tfPass.getText())) {
                showMsg(lblMsg,
                        "Le mot de passe doit contenir au moins 6 caractères.",
                        false);
                tfPass.requestFocus();
                return;
            }
            // Confirmation mot de passe
            if (!tfPass.getText().equals(tfPassConf.getText())) {
                showMsg(lblMsg,
                        "Les deux mots de passe ne correspondent pas.",
                        false);
                tfPassConf.requestFocus();
                return;
            }
            // Validation rôle
            if (!Validator.isValidRole(cbRole.getValue())) {
                showMsg(lblMsg, "Veuillez choisir un rôle.", false);
                return;
            }
            // Si MEDECIN, un médecin doit être sélectionné
            if ("MEDECIN".equals(cbRole.getValue()) && cbMedecin.getValue() == null) {
                showMsg(lblMsg,
                        "Veuillez sélectionner le médecin à lier à ce compte.",
                        false);
                cbMedecin.requestFocus();
                return;
            }

            User u = new User(
                    tfUser.getText().trim(),
                    tfPass.getText(),
                    cbRole.getValue());

            if (userDAO.addUser(u)) {
                // Si MEDECIN → mettre à jour le champ username dans la table medecins
                if ("MEDECIN".equals(cbRole.getValue()) && cbMedecin.getValue() != null) {
                    medecinDAO.linkUsername(
                            cbMedecin.getValue().getId(),
                            tfUser.getText().trim());
                }

                table.setItems(FXCollections.observableArrayList(
                        userDAO.getAllUsers()));
                tfUser.clear();
                tfPass.clear();
                tfPassConf.clear();
                cbRole.setValue("MEDECIN");
                cbMedecin.setValue(null);
                refreshMedecinCombo.run(); // rafraîchir (médecin lié disparaît de la liste)
                showMsg(lblMsg,
                        "✓ Compte « " + u.getUsername() + " » créé et lié avec succès !",
                        true);
                setTopbar("Utilisateurs",
                        userDAO.getAllUsers().size() + " comptes",
                        "+ Créer compte", true);
            } else {
                showMsg(lblMsg,
                        "Erreur — ce nom d'utilisateur existe peut-être déjà.",
                        false);
            }
        });

        Button btnCancel = new Button("Réinitialiser");
        btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> {
            tfUser.clear();
            tfPass.clear();
            tfPassConf.clear();
            cbRole.setValue("MEDECIN");
            cbMedecin.setValue(null);
            refreshMedecinCombo.run();
            lblMsg.setText("");
        });

        // ── Grille formulaire ─────────────────────────────────
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        grid.setPadding(new Insets(18, 20, 14, 20));

        // Ligne 0 : Username (pleine largeur)
        VBox grpUser = fg("Nom d'utilisateur *", tfUser);
        GridPane.setColumnSpan(grpUser, 2);
        GridPane.setHgrow(grpUser, Priority.ALWAYS);
        grid.add(grpUser, 0, 0);

        // Ligne 1 : Mot de passe | Confirmation
        VBox grpPass     = fg("Mot de passe *", tfPass);
        VBox grpPassConf = fg("Confirmer le mot de passe *", tfPassConf);
        GridPane.setHgrow(grpPass,     Priority.ALWAYS);
        GridPane.setHgrow(grpPassConf, Priority.ALWAYS);
        grid.addRow(1, grpPass, grpPassConf);

        // Ligne 2 : Rôle | Légende
        VBox grpRole = fg("Rôle *", cbRole);
        GridPane.setHgrow(grpRole, Priority.ALWAYS);
        VBox legendeRoles = buildRoleLegend();
        GridPane.setHgrow(legendeRoles, Priority.ALWAYS);
        grid.addRow(2, grpRole, legendeRoles);

        // Ligne 3 : Médecin à lier (pleine largeur, visible si MEDECIN)
        GridPane.setColumnSpan(grpMedecin, 2);
        GridPane.setHgrow(grpMedecin, Priority.ALWAYS);
        grid.add(grpMedecin, 0, 3);

        // Colonnes égales
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50); col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50); col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        HBox footer = buildFooter(btnAdd, btnCancel);
        lblMsg.setPadding(new Insets(0, 20, 4, 20));

        VBox formCard = buildFormCard(
                "Créer un compte utilisateur", grid, lblMsg, footer);

        btnPrimary.setOnAction(e -> tfUser.requestFocus());

        VBox page = new VBox(14,
                wrapInCard("Comptes utilisateurs",
                        userDAO.getAllUsers().size() + " comptes", table),
                formCard);
        centerPane.getChildren().setAll(page);
    }

    // ════════════════════════════════════════════════════════════
    // STATISTIQUES
    // ════════════════════════════════════════════════════════════
    @FXML
    public void showStats() {
        setActive(btnStats);
        setTopbar("Statistiques", "Données du système", null, false);

        long nbAdmin = userDAO.getAllUsers().stream()
                .filter(u -> "ADMIN".equals(u.getRole())).count();
        long nbMed   = userDAO.getAllUsers().stream()
                .filter(u -> "MEDECIN".equals(u.getRole())).count();
        long nbSec   = userDAO.getAllUsers().stream()
                .filter(u -> "SECRETAIRE".equals(u.getRole())).count();

        HBox row1 = new HBox(10);
        row1.getChildren().addAll(
                makeStatCard("Total médecins",
                        String.valueOf(medecinDAO.getAllMedecins().size()),
                        "stat-card-green", "stat-value-green", ""),
                makeStatCard("Total utilisateurs",
                        String.valueOf(userDAO.getAllUsers().size()),
                        "stat-card-blue", "stat-value-blue", ""),
                makeStatCard("Admins",
                        String.valueOf(nbAdmin),
                        "stat-card-red", "stat-value-red", ""),
                makeStatCard("Médecins (comptes)",
                        String.valueOf(nbMed),
                        "stat-card-purple", "stat-value-purple", ""),
                makeStatCard("Secrétaires",
                        String.valueOf(nbSec),
                        "stat-card-orange", "stat-value-orange", "")
        );

        VBox page = new VBox(16, row1);
        centerPane.getChildren().setAll(page);
    }

    // ════════════════════════════════════════════════════════════
    // DÉCONNEXION
    // ════════════════════════════════════════════════════════════
    @FXML
    public void logout() {
        Session.logout();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/login.fxml"));
            Stage stage = (Stage) centerPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Hôpital — Connexion");
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ════════════════════════════════════════════════════════════
    // TABLEVIEW MÉDECINS
    // ════════════════════════════════════════════════════════════
    private TableView<Medecin> buildMedecinTable(
            List<Medecin> data,
            TextField tfNom, TextField tfPre,
            TextField tfSpec, TextField tfTel, TextField tfEmail,
            boolean withActions) {

        TableView<Medecin> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(FXCollections.observableArrayList(data));

        TableColumn<Medecin, String> colNom  = colStr("Nom",    "nom");
        TableColumn<Medecin, String> colPre  = colStr("Prénom", "prenom");

        TableColumn<Medecin, String> colSpec = new TableColumn<>("Spécialité");
        colSpec.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        colSpec.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label b = new Label(item);
                int hash = Math.abs(item.hashCode()) % 3;
                b.getStyleClass().add(switch (hash) {
                    case 0 -> "badge-spec1";
                    case 1 -> "badge-spec2";
                    default -> "badge-spec3";
                });
                setGraphic(b); setText(null);
            }
        });

        // Colonne Compte lié
        TableColumn<Medecin, String> colCompte = new TableColumn<>("Compte");
        colCompte.setCellValueFactory(new PropertyValueFactory<>("username"));
        colCompte.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); setText(null); return; }
                if (item != null && !item.isBlank()) {
                    Label b = new Label("@" + item);
                    b.getStyleClass().add("badge-info");
                    setGraphic(b); setText(null);
                } else {
                    Label b = new Label("Aucun compte");
                    b.setStyle("-fx-text-fill:#A0AEC0;-fx-font-size:11px;");
                    setGraphic(b); setText(null);
                }
            }
        });
        colCompte.setMaxWidth(140); colCompte.setMinWidth(110);

        TableColumn<Medecin, String> colTel   = colStr("Téléphone", "telephone");
        TableColumn<Medecin, String> colEmail = colStr("Email",     "email");

        table.getColumns().addAll(colNom, colPre, colSpec, colTel, colEmail, colCompte);

        if (withActions) {
            table.getSelectionModel().selectedItemProperty()
                    .addListener((obs, old, sel) -> {
                        if (sel == null) return;
                        if (tfNom != null)   tfNom.setText(sel.getNom());
                        if (tfPre != null)   tfPre.setText(sel.getPrenom());
                        if (tfSpec != null)  tfSpec.setText(sel.getSpecialite());
                        if (tfTel != null)   tfTel.setText(sel.getTelephone());
                        if (tfEmail != null) tfEmail.setText(sel.getEmail());
                    });

            TableColumn<Medecin, Void> colAct = new TableColumn<>("");
            colAct.setMaxWidth(70); colAct.setMinWidth(70);
            colAct.setCellFactory(c -> new TableCell<>() {
                final Button del = iconBtn("🗑", "btn-icon-delete");
                {
                    del.setOnAction(e -> {
                        Medecin m = getTableView().getItems().get(getIndex());
                        medecinDAO.deleteMedecin(m.getId());
                        getTableView().setItems(FXCollections.observableArrayList(
                                medecinDAO.getAllMedecins()));
                    });
                }
                @Override protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : del);
                }
            });
            table.getColumns().add(colAct);
        }
        return table;
    }

    // ════════════════════════════════════════════════════════════
    // TABLEVIEW UTILISATEURS
    // ════════════════════════════════════════════════════════════
    private TableView<User> buildUserTable(List<User> data, boolean withActions) {
        TableView<User> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(FXCollections.observableArrayList(data));

        TableColumn<User, String> colUser = new TableColumn<>("Username");
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUser.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); setText(null); return; }
                Label avatar = new Label(
                        item.length() >= 2
                                ? item.substring(0, 2).toUpperCase()
                                : item.toUpperCase());
                avatar.setStyle(
                        "-fx-background-color:#1D9E75;" +
                                "-fx-text-fill:white;" +
                                "-fx-font-size:10px;" +
                                "-fx-font-weight:bold;" +
                                "-fx-min-width:28;-fx-min-height:28;" +
                                "-fx-max-width:28;-fx-max-height:28;" +
                                "-fx-background-radius:14;" +
                                "-fx-alignment:CENTER;");
                Label name = new Label(item);
                name.setStyle("-fx-font-weight:bold;-fx-font-size:12.5px;");
                HBox row = new HBox(8, avatar, name);
                row.setAlignment(Pos.CENTER_LEFT);
                setGraphic(row); setText(null);
            }
        });

        TableColumn<User, String> colRole = new TableColumn<>("Rôle");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setMaxWidth(160); colRole.setMinWidth(120);
        colRole.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label b = new Label(item);
                b.getStyleClass().add(switch (item) {
                    case "ADMIN"   -> "badge-danger";
                    case "MEDECIN" -> "badge-info";
                    default        -> "badge-warning";
                });
                setGraphic(b); setText(null);
            }
        });

        table.getColumns().addAll(colUser, colRole);

        if (withActions) {
            TableColumn<User, Void> colAct = new TableColumn<>("");
            colAct.setMaxWidth(60); colAct.setMinWidth(60);
            colAct.setCellFactory(c -> new TableCell<>() {
                final Button del = iconBtn("🗑", "btn-icon-delete");
                {
                    del.setOnAction(e -> {
                        User u = getTableView().getItems().get(getIndex());
                        if (u.getUsername().equals(
                                Session.getCurrentUser().getUsername())) return;
                        userDAO.deleteUser(u.getId());
                        getTableView().setItems(
                                FXCollections.observableArrayList(
                                        userDAO.getAllUsers()));
                    });
                }
                @Override protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        User u = getTableView().getItems().get(getIndex());
                        del.setDisable(u.getUsername().equals(
                                Session.getCurrentUser().getUsername()));
                    }
                    setGraphic(empty ? null : del);
                }
            });
            table.getColumns().add(colAct);
        }
        return table;
    }

    // ════════════════════════════════════════════════════════════
    // LÉGENDE DES RÔLES
    // ════════════════════════════════════════════════════════════
    private VBox buildRoleLegend() {
        Label titre = new Label("Droits par rôle");
        titre.getStyleClass().add("field-label");

        VBox box = new VBox(6);
        box.setPadding(new Insets(10, 12, 10, 12));
        box.setStyle("-fx-background-color:-fx-background-color;" +
                "-fx-border-color:#E2E8F0;" +
                "-fx-border-width:1;" +
                "-fx-border-radius:7;" +
                "-fx-background-radius:7;");

        box.getChildren().addAll(
                roleLegendRow("badge-danger",  "ADMIN",
                        "Gestion complète — médecins, users, stats"),
                roleLegendRow("badge-info",    "MEDECIN",
                        "RDV du jour, consultations, historique"),
                roleLegendRow("badge-warning", "SECRETAIRE",
                        "Patients, rendez-vous, recherche")
        );

        VBox grp = new VBox(4, titre, box);
        VBox.setVgrow(box, Priority.ALWAYS);
        return grp;
    }

    private HBox roleLegendRow(String badgeClass, String role, String desc) {
        Label b = new Label(role);
        b.getStyleClass().add(badgeClass);
        b.setMinWidth(90);
        Label d = new Label(desc);
        d.setStyle("-fx-font-size:11px;-fx-text-fill:#718096;");
        d.setWrapText(true);
        HBox row = new HBox(8, b, d);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // ════════════════════════════════════════════════════════════
    // UTILITAIRES PRIVÉS
    // ════════════════════════════════════════════════════════════
    private VBox makeStatCard(String label, String val,
                              String cardClass, String valClass, String diff) {
        Label lbl  = new Label(label); lbl.getStyleClass().add("stat-label");
        Label v    = new Label(val);   v.getStyleClass().addAll("stat-value", valClass);
        VBox card  = new VBox(6, lbl, v);
        card.getStyleClass().addAll("stat-card", cardClass);
        card.setPrefWidth(150);
        if (!diff.isBlank()) {
            Label dl = new Label(diff);
            dl.getStyleClass().add("stat-diff");
            card.getChildren().add(dl);
        }
        return card;
    }

    private VBox wrapInCard(String titre, String sous, TableView<?> table) {
        Label h = new Label(titre);
        h.getStyleClass().add("section-card-title");
        h.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(h, Priority.ALWAYS);
        Label s = new Label(sous);
        s.getStyleClass().add("section-card-sub");
        HBox head = new HBox(8, h, s);
        head.setAlignment(Pos.CENTER_LEFT);
        head.getStyleClass().add("section-card-header");
        head.setPadding(new Insets(11, 16, 11, 16));
        VBox card = new VBox(0, head, table);
        card.getStyleClass().add("section-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private VBox buildFormCard(String titre, GridPane grid,
                               Label lblMsg, HBox footer) {
        Label h = new Label(titre);
        h.getStyleClass().add("section-card-title");
        h.setMaxWidth(Double.MAX_VALUE);
        h.setPadding(new Insets(11, 16, 11, 16));
        h.setStyle("-fx-border-color:transparent transparent #E2E8F0 transparent;" +
                "-fx-border-width:0 0 1 0;");
        VBox card = new VBox(0, h, grid);
        if (lblMsg != null) card.getChildren().add(lblMsg);
        card.getChildren().add(footer);
        card.getStyleClass().add("section-card");
        return card;
    }

    private <T> TableColumn<T, String> colStr(String titre, String prop) {
        TableColumn<T, String> c = new TableColumn<>(titre);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        return c;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("form-input");
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private VBox fg(String label, Control field) {
        Label lbl = new Label(label);
        lbl.getStyleClass().add("field-label");
        field.setMaxWidth(Double.MAX_VALUE);
        VBox g = new VBox(4, lbl, field);
        GridPane.setHgrow(g, Priority.ALWAYS);
        return g;
    }

    private Button iconBtn(String text, String styleClass) {
        Button b = new Button(text);
        b.getStyleClass().add(styleClass);
        return b;
    }

    private HBox buildFooter(Button... buttons) {
        HBox box = new HBox(8);
        box.getChildren().addAll(buttons);
        box.setPadding(new Insets(10, 16, 14, 16));
        box.setStyle("-fx-border-color:#E2E8F0 transparent transparent transparent;" +
                "-fx-border-width:1 0 0 0;");
        return box;
    }

    private void clearFields(TextField... fields) {
        for (TextField f : fields) if (f != null) f.clear();
    }

    private void showMsg(Label lbl, String msg, boolean success) {
        lbl.getStyleClass().setAll(success ? "lbl-success" : "lbl-error");
        lbl.setText(msg);
    }
}