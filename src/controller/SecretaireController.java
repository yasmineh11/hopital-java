package controller;

import dao.MedecinDAO;
import dao.PatientDAO;
import dao.RendezVousDAO;
import dao.UserDAO;
import model.Medecin;
import model.Patient;
import model.RendezVous;
import model.User;
import utils.Session;
import utils.ThemeManager;

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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class SecretaireController {

    @FXML private StackPane centerPane;
    @FXML private Label     lblPageTitle;
    @FXML private Label     lblPageSub;
    @FXML private Label     lblAvatar;
    @FXML private Label     lblUsername;
    @FXML private TextField tfSearch;
    @FXML private Button    btnPrimary;
    @FXML private Button    btnTheme;
    @FXML private Button    btnDash;
    @FXML private Button    btnPatients;
    @FXML private Button    btnRdv;
    @FXML private Button    btnRecherche;

    private final PatientDAO    patientDAO = new PatientDAO();
    private final RendezVousDAO rdvDAO     = new RendezVousDAO();
    private final MedecinDAO    medecinDAO = new MedecinDAO();
    private final UserDAO       userDAO    = new UserDAO();

    // ════════════════════════════════════════════════════════════
    // INIT
    // ════════════════════════════════════════════════════════════
    @FXML
    public void initialize() {
        if (Session.getCurrentUser() != null) {
            String name = Session.getCurrentUser().getUsername();
            lblUsername.setText(name);
            lblAvatar.setText(name.length() >= 2
                    ? name.substring(0, 2).toUpperCase()
                    : name.toUpperCase());
        }
        if (btnTheme != null) btnTheme.setText(ThemeManager.getIcon());
        showDashboard();
    }

    // ════════════════════════════════════════════════════════════
    // THÈME GLOBAL
    // ════════════════════════════════════════════════════════════
    @FXML
    public void toggleTheme() {
        ThemeManager.toggle(centerPane.getScene());
        btnTheme.setText(ThemeManager.getIcon());
    }

    // ════════════════════════════════════════════════════════════
    // NAVIGATION
    // ════════════════════════════════════════════════════════════
    private void setActive(Button active) {
        for (Button b : new Button[]{btnDash, btnPatients, btnRdv, btnRecherche}) {
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
        btnPrimary.setVisible(btnLabel != null && !btnLabel.isBlank());
        if (btnLabel != null) btnPrimary.setText(btnLabel);
        if (tfSearch != null) tfSearch.setVisible(showSearch);
    }

    @FXML public void handlePrimaryAction() {}

    // ════════════════════════════════════════════════════════════
    // TABLEAU DE BORD
    // ════════════════════════════════════════════════════════════
    @FXML
    public void showDashboard() {
        setActive(btnDash);
        setTopbar("Tableau de bord",
                "Gestion patients et rendez-vous", null, false);

        int  nbPat       = patientDAO.getAllPatients().size();
        long nbAujourd   = rdvDAO.getAllRendezVous().stream()
                .filter(r -> LocalDate.now().equals(r.getDateRdv())).count();
        long nbPlanifies = rdvDAO.getAllRendezVous().stream()
                .filter(r -> "PLANIFIE".equals(r.getStatut())).count();
        long nbAnnules   = rdvDAO.getAllRendezVous().stream()
                .filter(r -> "ANNULE".equals(r.getStatut())).count();

        HBox stats = new HBox(10);
        stats.getChildren().addAll(
                makeStatCard("Patients",        String.valueOf(nbPat),
                        "stat-card-green",  "stat-value-green"),
                makeStatCard("RDV aujourd'hui", String.valueOf(nbAujourd),
                        "stat-card-blue",   "stat-value-blue"),
                makeStatCard("En attente",      String.valueOf(nbPlanifies),
                        "stat-card-orange", "stat-value-orange"),
                makeStatCard("Annulés",         String.valueOf(nbAnnules),
                        "stat-card-red",    "stat-value-red")
        );

        List<RendezVous> rdvDuJour = rdvDAO.getAllRendezVous().stream()
                .filter(r -> LocalDate.now().equals(r.getDateRdv()))
                .limit(5).toList();
        TableView<RendezVous> tRdv = buildRdvTable(rdvDuJour, false);
        tRdv.setPrefHeight(200);

        List<Patient> recents = patientDAO.getAllPatients().stream()
                .limit(4).toList();
        TableView<Patient> tPat = buildPatientTable(recents, false);
        tPat.setPrefHeight(200);

        HBox split = new HBox(14,
                wrapInCard("RDV du jour",
                        rdvDuJour.size() + " rendez-vous", tRdv),
                wrapInCard("Patients récents",
                        nbPat + " patients", tPat));
        HBox.setHgrow(split.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(split.getChildren().get(1), Priority.ALWAYS);

        centerPane.getChildren().setAll(new VBox(14, stats, split));
    }

    // ════════════════════════════════════════════════════════════
    // PATIENTS
    // ════════════════════════════════════════════════════════════
    @FXML
    public void showPatients() {
        setActive(btnPatients);
        setTopbar("Patients",
                patientDAO.getAllPatients().size() + " patients",
                "+ Nouveau patient", true);

        TextField    tfNom   = sf("Nom *");
        TextField    tfPre   = sf("Prénom *");
        TextField    tfCin   = sf("CIN");
        DatePicker   dpNaiss = sdp();
        TextField    tfTel   = sf("Téléphone");
        TextField    tfEmail = sf("Email");
        TextField    tfAdr   = sf("Adresse");
        Label        lblMsg  = new Label();

        TableView<Patient> table = buildPatientTable(
                patientDAO.getAllPatients(), true);
        table.setPrefHeight(260);

        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, sel) -> {
                    if (sel == null) return;
                    tfNom.setText(sel.getNom());
                    tfPre.setText(sel.getPrenom());
                    tfCin.setText(sel.getCin());
                    dpNaiss.setValue(sel.getDateNaissance());
                    tfTel.setText(sel.getTelephone());
                    tfEmail.setText(sel.getEmail());
                    tfAdr.setText(sel.getAdresse());
                });

        if (tfSearch != null)
            tfSearch.textProperty().addListener((obs, old, val) ->
                    table.setItems(FXCollections.observableArrayList(
                            val == null || val.isBlank()
                                    ? patientDAO.getAllPatients()
                                    : patientDAO.searchPatients(val))));

        Button btnAdd = new Button("➕  Ajouter");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> {
            if (tfNom.getText().isBlank() || tfPre.getText().isBlank()) {
                showMsg(lblMsg, "Nom et prénom obligatoires.", false); return;
            }
            if (patientDAO.addPatient(new Patient(
                    tfNom.getText().trim(), tfPre.getText().trim(),
                    tfCin.getText().trim(), dpNaiss.getValue(),
                    tfTel.getText().trim(), tfEmail.getText().trim(),
                    tfAdr.getText().trim()))) {
                table.setItems(FXCollections.observableArrayList(
                        patientDAO.getAllPatients()));
                clearPForm(tfNom, tfPre, tfCin, dpNaiss, tfTel, tfEmail, tfAdr);
                showMsg(lblMsg, "✓ Patient ajouté !", true);
            }
        });

        Button btnUpd = new Button("✏  Modifier");
        btnUpd.getStyleClass().add("btn-secondary");
        btnUpd.setOnAction(e -> {
            Patient sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showMsg(lblMsg, "Sélectionnez un patient.", false); return;
            }
            sel.setNom(tfNom.getText().trim());
            sel.setPrenom(tfPre.getText().trim());
            sel.setCin(tfCin.getText().trim());
            sel.setDateNaissance(dpNaiss.getValue());
            sel.setTelephone(tfTel.getText().trim());
            sel.setEmail(tfEmail.getText().trim());
            sel.setAdresse(tfAdr.getText().trim());
            if (patientDAO.updatePatient(sel)) {
                table.setItems(FXCollections.observableArrayList(
                        patientDAO.getAllPatients()));
                clearPForm(tfNom, tfPre, tfCin, dpNaiss, tfTel, tfEmail, tfAdr);
                showMsg(lblMsg, "✓ Patient modifié !", true);
            }
        });

        Button btnCancel = new Button("Annuler");
        btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> {
            clearPForm(tfNom, tfPre, tfCin, dpNaiss, tfTel, tfEmail, tfAdr);
            lblMsg.setText("");
        });

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(14, 16, 10, 16));
        grid.addRow(0, fg("Nom *",       tfNom),  fg("Prénom *",      tfPre));
        grid.addRow(1, fg("CIN",         tfCin),  fg("Date naissance", dpNaiss));
        grid.addRow(2, fg("Téléphone",   tfTel),  fg("Email",          tfEmail));
        grid.addRow(3, fg("Adresse",     tfAdr));
        setCol(grid, 2);
        lblMsg.setPadding(new Insets(0, 16, 0, 16));

        btnPrimary.setOnAction(e -> tfNom.requestFocus());
        centerPane.getChildren().setAll(new VBox(14,
                wrapInCard("Liste des patients",
                        patientDAO.getAllPatients().size() + " patients", table),
                buildFormCard("Ajouter / Modifier un patient",
                        grid, lblMsg, buildFooter(btnAdd, btnUpd, btnCancel))));
    }

    // ════════════════════════════════════════════════════════════
    // RENDEZ-VOUS — avec ComboBox médecins enrichie
    // ════════════════════════════════════════════════════════════
    @FXML
    public void showRendezVous() {
        setActive(btnRdv);
        setTopbar("Rendez-vous", "Planification et suivi",
                "+ Nouveau RDV", false);

        ComboBox<Patient>          cbPat    = buildPatientCombo();
        ComboBox<MedecinAvecCompte> cbMed   = buildMedecinCombo();
        DatePicker                 dpDate   = sdp();
        dpDate.setValue(LocalDate.now());
        TextField                  tfHeure  = sf("HH:MM  (ex: 09:30)");
        TextField                  tfMotif  = sf("Motif de la visite");
        ComboBox<String>           cbStatut = new ComboBox<>();
        cbStatut.getItems().addAll("PLANIFIE","CONFIRME","ANNULE");
        cbStatut.setValue("PLANIFIE");
        cbStatut.getStyleClass().add("combo-box");
        cbStatut.setMaxWidth(Double.MAX_VALUE);
        Label lblMsg = new Label();

        TableView<RendezVous> table = buildRdvTable(
                rdvDAO.getAllRendezVous(), true);
        table.setPrefHeight(240);

        // Pré-remplissage au clic
        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, sel) -> {
                    if (sel == null) return;
                    dpDate.setValue(sel.getDateRdv());
                    tfHeure.setText(sel.getHeureRdv().toString().substring(0, 5));
                    tfMotif.setText(sel.getMotif());
                    cbStatut.setValue(sel.getStatut());
                    cbMed.getItems().stream()
                            .filter(m -> m.medecin.getId() == sel.getIdMedecin())
                            .findFirst().ifPresent(cbMed::setValue);
                    patientDAO.getAllPatients().stream()
                            .filter(p -> p.getId() == sel.getIdPatient())
                            .findFirst().ifPresent(cbPat::setValue);
                });

        Button btnAdd = new Button("➕  Créer RDV");
        btnAdd.getStyleClass().add("btn-primary");
        btnAdd.setOnAction(e -> {
            if (cbPat.getValue() == null) {
                showMsg(lblMsg, "Sélectionnez un patient.", false); return; }
            if (cbMed.getValue() == null) {
                showMsg(lblMsg, "Sélectionnez un médecin.", false); return; }
            if (dpDate.getValue() == null || tfHeure.getText().isBlank()) {
                showMsg(lblMsg, "Date et heure obligatoires.", false); return; }
            try {
                if (rdvDAO.addRendezVous(new RendezVous(
                        cbPat.getValue().getId(),
                        cbMed.getValue().medecin.getId(),
                        dpDate.getValue(),
                        LocalTime.parse(pad(tfHeure.getText().trim())),
                        tfMotif.getText().trim(),
                        cbStatut.getValue()))) {
                    table.setItems(FXCollections.observableArrayList(
                            rdvDAO.getAllRendezVous()));
                    cbPat.setValue(null); cbMed.setValue(null);
                    tfHeure.clear(); tfMotif.clear();
                    cbStatut.setValue("PLANIFIE");
                    showMsg(lblMsg, "✓ Rendez-vous créé !", true);
                }
            } catch (Exception ex) {
                showMsg(lblMsg, "Format heure invalide. Utilisez HH:MM (ex: 09:30)", false);
            }
        });

        Button btnUpd = new Button("✏  Modifier");
        btnUpd.getStyleClass().add("btn-secondary");
        btnUpd.setOnAction(e -> {
            RendezVous sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showMsg(lblMsg, "Sélectionnez un RDV.", false); return; }
            try {
                if (cbMed.getValue() != null)
                    sel.setIdMedecin(cbMed.getValue().medecin.getId());
                sel.setDateRdv(dpDate.getValue());
                sel.setHeureRdv(LocalTime.parse(pad(tfHeure.getText().trim())));
                sel.setMotif(tfMotif.getText().trim());
                sel.setStatut(cbStatut.getValue());
                if (rdvDAO.updateRendezVous(sel)) {
                    table.setItems(FXCollections.observableArrayList(
                            rdvDAO.getAllRendezVous()));
                    showMsg(lblMsg, "✓ RDV modifié !", true);
                }
            } catch (Exception ex) {
                showMsg(lblMsg, "Format heure invalide.", false);
            }
        });

        Button btnAnn = new Button("✕  Annuler RDV");
        btnAnn.getStyleClass().add("btn-danger");
        btnAnn.setOnAction(e -> {
            RendezVous sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showMsg(lblMsg, "Sélectionnez un RDV.", false); return; }
            rdvDAO.annulerRendezVous(sel.getId());
            table.setItems(FXCollections.observableArrayList(
                    rdvDAO.getAllRendezVous()));
            showMsg(lblMsg, "✓ RDV annulé.", true);
        });

        Button btnReset = new Button("Réinitialiser");
        btnReset.getStyleClass().add("btn-secondary");
        btnReset.setOnAction(e -> {
            cbPat.setValue(null); cbMed.setValue(null);
            dpDate.setValue(LocalDate.now());
            tfHeure.clear(); tfMotif.clear();
            cbStatut.setValue("PLANIFIE"); lblMsg.setText("");
        });

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(14, 16, 10, 16));
        grid.addRow(0, fg("Patient *",       cbPat),    fg("Médecin *",        cbMed));
        grid.addRow(1, fg("Date *",          dpDate),   fg("Heure * (HH:MM)",  tfHeure));
        grid.addRow(2, fg("Motif",           tfMotif),  fg("Statut",           cbStatut));
        setCol(grid, 2);
        lblMsg.setPadding(new Insets(0, 16, 0, 16));

        btnPrimary.setOnAction(ev -> cbPat.requestFocus());
        centerPane.getChildren().setAll(new VBox(14,
                wrapInCard("Liste des rendez-vous",
                        rdvDAO.getAllRendezVous().size() + " RDV", table),
                buildFormCard("Créer / Modifier un rendez-vous",
                        grid, lblMsg, buildFooter(btnAdd, btnUpd, btnAnn, btnReset))));
    }

    // ════════════════════════════════════════════════════════════
    // COMBOBOX MÉDECINS ENRICHIE (nom + compte @username lié)
    // ════════════════════════════════════════════════════════════
    private static class MedecinAvecCompte {
        final Medecin medecin;
        final String  compte; // username du compte MEDECIN, ou null

        MedecinAvecCompte(Medecin m, String c) {
            this.medecin = m; this.compte = c;
        }

        @Override public String toString() {
            String sp = medecin.getSpecialite() != null
                    ? medecin.getSpecialite() : "";
            String cp = compte != null
                    ? "  [@" + compte + "]" : "  [sans compte]";
            return "Dr. " + medecin.getNom() + " "
                    + medecin.getPrenom() + " — " + sp + cp;
        }
    }

    private ComboBox<MedecinAvecCompte> buildMedecinCombo() {
        ComboBox<MedecinAvecCompte> cb = new ComboBox<>();
        cb.getStyleClass().add("combo-box");
        cb.setPromptText("Médecin *");
        cb.setMaxWidth(Double.MAX_VALUE);

        // Comptes utilisateurs de rôle MEDECIN
        List<User> comptes = userDAO.getAllUsers().stream()
                .filter(u -> "MEDECIN".equals(u.getRole()))
                .collect(Collectors.toList());

        for (Medecin m : medecinDAO.getAllMedecins()) {
            String nomM  = m.getNom().toLowerCase();
            String prenM = m.getPrenom().toLowerCase();
            String found = comptes.stream()
                    .filter(u -> {
                        String un = u.getUsername().toLowerCase();
                        return un.equals(nomM) || un.equals(prenM)
                                || un.contains(nomM) || nomM.contains(un)
                                || un.equals(nomM + prenM)
                                || un.equals(prenM + nomM);
                    })
                    .map(User::getUsername)
                    .findFirst().orElse(null);
            cb.getItems().add(new MedecinAvecCompte(m, found));
        }

        // Rendu liste déroulante : 2 lignes par médecin
        cb.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(MedecinAvecCompte it, boolean empty) {
                super.updateItem(it, empty);
                if (empty || it == null) { setGraphic(null); return; }
                Label nom = new Label("Dr. " + it.medecin.getNom()
                        + " " + it.medecin.getPrenom());
                nom.setStyle("-fx-font-weight:bold;-fx-font-size:12.5px;");
                String spec = it.medecin.getSpecialite() != null
                        ? it.medecin.getSpecialite() : "—";
                String cp = it.compte != null
                        ? "Compte : @" + it.compte : "⚠ Aucun compte lié";
                Label sub = new Label(spec + "   |   " + cp);
                sub.setStyle("-fx-font-size:10.5px;-fx-text-fill:"
                        + (it.compte != null ? "#718096" : "#C05621") + ";");
                setGraphic(new VBox(2, nom, sub)); setText(null);
            }
        });

        // Rendu bouton sélectionné
        cb.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(MedecinAvecCompte it, boolean empty) {
                super.updateItem(it, empty);
                if (empty || it == null) { setText(null); return; }
                String cp = it.compte != null
                        ? " [@" + it.compte + "]" : " [sans compte]";
                setText("Dr. " + it.medecin.getNom()
                        + " " + it.medecin.getPrenom() + cp);
            }
        });
        return cb;
    }

    private ComboBox<Patient> buildPatientCombo() {
        ComboBox<Patient> cb = new ComboBox<>();
        cb.getItems().addAll(patientDAO.getAllPatients());
        cb.setPromptText("Patient *");
        cb.getStyleClass().add("combo-box");
        cb.setMaxWidth(Double.MAX_VALUE);
        return cb;
    }

    // ════════════════════════════════════════════════════════════
    // RECHERCHE
    // ════════════════════════════════════════════════════════════
    @FXML
    public void showRecherche() {
        setActive(btnRecherche);
        setTopbar("Recherche patient",
                "Filtrer par nom, prénom ou CIN", null, true);

        TextField tfQ = sf("Rechercher par nom, prénom ou CIN...");
        tfQ.setPrefWidth(350);
        Button btnS = new Button("🔍  Rechercher");
        btnS.getStyleClass().add("btn-primary");

        TableView<Patient> table = buildPatientTable(
                patientDAO.getAllPatients(), false);
        table.setPrefHeight(340);

        Runnable search = () -> {
            String kw = tfQ.getText().trim();
            table.setItems(FXCollections.observableArrayList(
                    kw.isEmpty()
                            ? patientDAO.getAllPatients()
                            : patientDAO.searchPatients(kw)));
        };
        btnS.setOnAction(e -> search.run());
        tfQ.textProperty().addListener((obs, old, val) -> {
            if (val == null || val.isBlank())
                table.setItems(FXCollections.observableArrayList(
                        patientDAO.getAllPatients()));
            else if (val.length() >= 2) search.run();
        });
        if (tfSearch != null)
            tfSearch.textProperty().addListener(
                    (obs, old, val) -> tfQ.setText(val));

        HBox bar = new HBox(8, tfQ, btnS);
        bar.setPadding(new Insets(14, 16, 10, 16));
        VBox card = new VBox(0, cardTitle("Recherche patient"), bar, table);
        card.getStyleClass().add("section-card");
        centerPane.getChildren().setAll(new VBox(14, card));
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
            Scene scene = new Scene(loader.load());
            ThemeManager.apply(scene);
            stage.setScene(scene);
            stage.setTitle("Hôpital — Connexion");
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ════════════════════════════════════════════════════════════
    // TABLEVIEWS
    // ════════════════════════════════════════════════════════════
    private TableView<Patient> buildPatientTable(List<Patient> data,
                                                 boolean withDel) {
        TableView<Patient> t = new TableView<>();
        t.getStyleClass().add("table-view");
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        t.setItems(FXCollections.observableArrayList(data));
        t.getColumns().addAll(colStr("Nom","nom"), colStr("Prénom","prenom"),
                colStr("CIN","cin"), colStr("Téléphone","telephone"));
        if (withDel) {
            TableColumn<Patient, Void> c = new TableColumn<>("");
            c.setMaxWidth(60); c.setMinWidth(60);
            c.setCellFactory(cv -> new TableCell<>() {
                final Button d = iconBtn("🗑","btn-icon-delete");
                { d.setOnAction(e -> {
                    Patient p = getTableView().getItems().get(getIndex());
                    patientDAO.deletePatient(p.getId());
                    getTableView().setItems(FXCollections.observableArrayList(
                            patientDAO.getAllPatients()));
                }); }
                @Override protected void updateItem(Void i, boolean empty) {
                    super.updateItem(i, empty); setGraphic(empty ? null : d); }
            });
            t.getColumns().add(c);
        }
        return t;
    }

    private TableView<RendezVous> buildRdvTable(List<RendezVous> data,
                                                boolean withDel) {
        TableView<RendezVous> t = new TableView<>();
        t.getStyleClass().add("table-view");
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        t.setItems(FXCollections.observableArrayList(data));

        // Patient (calculé)
        TableColumn<RendezVous, String> cPat = new TableColumn<>("Patient");
        cPat.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String i, boolean empty) {
                super.updateItem(i, empty);
                if (empty) { setText(null); return; }
                RendezVous r = getTableView().getItems().get(getIndex());
                Patient p = patientDAO.findById(r.getIdPatient());
                setText(p != null ? p.getNom() + " " + p.getPrenom() : "—");
            }
        });

        // Médecin (calculé)
        TableColumn<RendezVous, String> cMed = new TableColumn<>("Médecin");
        cMed.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String i, boolean empty) {
                super.updateItem(i, empty);
                if (empty) { setText(null); return; }
                RendezVous r = getTableView().getItems().get(getIndex());
                Medecin m = medecinDAO.findById(r.getIdMedecin());
                setText(m != null
                        ? "Dr. " + m.getNom() + " " + m.getPrenom() : "—");
            }
        });

        TableColumn<RendezVous, String> cDate  = colStr("Date",  "dateRdv");
        cDate.setMaxWidth(100);
        TableColumn<RendezVous, String> cHeure = colStr("Heure", "heureRdv");
        cHeure.setMaxWidth(70);

        // Badge statut
        TableColumn<RendezVous, String> cStat = new TableColumn<>("Statut");
        cStat.setCellValueFactory(new PropertyValueFactory<>("statut"));
        cStat.setMaxWidth(110); cStat.setMinWidth(100);
        cStat.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label b = new Label(item);
                b.getStyleClass().add(switch (item) {
                    case "CONFIRME" -> "badge-success";
                    case "ANNULE"   -> "badge-danger";
                    default         -> "badge-warning";
                });
                setGraphic(b); setText(null);
            }
        });

        t.getColumns().addAll(cDate, cHeure, cPat, cMed, cStat);

        if (withDel) {
            TableColumn<RendezVous, Void> cAct = new TableColumn<>("");
            cAct.setMaxWidth(60); cAct.setMinWidth(55);
            cAct.setCellFactory(c -> new TableCell<>() {
                final Button d = iconBtn("🗑","btn-icon-delete");
                { d.setOnAction(e -> {
                    RendezVous r = getTableView().getItems().get(getIndex());
                    rdvDAO.annulerRendezVous(r.getId());
                    getTableView().setItems(FXCollections.observableArrayList(
                            rdvDAO.getAllRendezVous()));
                }); }
                @Override protected void updateItem(Void i, boolean empty) {
                    super.updateItem(i, empty); setGraphic(empty ? null : d); }
            });
            t.getColumns().add(cAct);
        }
        return t;
    }

    // ════════════════════════════════════════════════════════════
    // UTILITAIRES
    // ════════════════════════════════════════════════════════════
    private VBox makeStatCard(String label, String val,
                              String cardClass, String valClass) {
        Label lbl = new Label(label); lbl.getStyleClass().add("stat-label");
        Label v   = new Label(val);   v.getStyleClass().addAll("stat-value", valClass);
        VBox c = new VBox(6, lbl, v);
        c.getStyleClass().addAll("stat-card", cardClass);
        c.setPrefWidth(160);
        return c;
    }

    private VBox wrapInCard(String titre, String sous, TableView<?> table) {
        Label h = new Label(titre); h.getStyleClass().add("section-card-title");
        h.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(h, Priority.ALWAYS);
        Label s = new Label(sous); s.getStyleClass().add("section-card-sub");
        HBox head = new HBox(8, h, s);
        head.setAlignment(Pos.CENTER_LEFT);
        head.getStyleClass().add("section-card-header");
        head.setPadding(new Insets(11, 16, 11, 16));
        VBox card = new VBox(0, head, table);
        card.getStyleClass().add("section-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private Label cardTitle(String text) {
        Label l = new Label(text); l.getStyleClass().add("section-card-title");
        l.setMaxWidth(Double.MAX_VALUE);
        l.setPadding(new Insets(11, 16, 11, 16));
        l.setStyle("-fx-border-color:transparent transparent #E2E8F0 transparent;" +
                "-fx-border-width:0 0 1 0;");
        return l;
    }

    private VBox buildFormCard(String titre, GridPane grid,
                               Label msg, HBox footer) {
        VBox card = new VBox(0, cardTitle(titre), grid);
        if (msg != null) card.getChildren().add(msg);
        card.getChildren().add(footer);
        card.getStyleClass().add("section-card");
        return card;
    }

    private <T> TableColumn<T, String> colStr(String t, String p) {
        TableColumn<T, String> c = new TableColumn<>(t);
        c.setCellValueFactory(new PropertyValueFactory<>(p));
        return c;
    }

    private TextField sf(String prompt) {
        TextField tf = new TextField(); tf.setPromptText(prompt);
        tf.getStyleClass().add("form-input"); tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private DatePicker sdp() {
        DatePicker dp = new DatePicker();
        dp.getStyleClass().add("form-input"); dp.setMaxWidth(Double.MAX_VALUE);
        return dp;
    }

    private VBox fg(String label, Control field) {
        Label lbl = new Label(label); lbl.getStyleClass().add("field-label");
        field.setMaxWidth(Double.MAX_VALUE);
        VBox g = new VBox(4, lbl, field);
        GridPane.setHgrow(g, Priority.ALWAYS);
        return g;
    }

    private Button iconBtn(String text, String cls) {
        Button b = new Button(text); b.getStyleClass().add(cls); return b;
    }

    private HBox buildFooter(Button... buttons) {
        HBox box = new HBox(8); box.getChildren().addAll(buttons);
        box.setPadding(new Insets(10, 16, 14, 16));
        box.setStyle("-fx-border-color:#E2E8F0 transparent transparent transparent;" +
                "-fx-border-width:1 0 0 0;");
        return box;
    }

    private void setCol(GridPane g, int cols) {
        g.getColumnConstraints().clear();
        for (int i = 0; i < cols; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / cols);
            cc.setHgrow(Priority.ALWAYS);
            g.getColumnConstraints().add(cc);
        }
    }

    private void clearPForm(TextField n, TextField p, TextField c,
                            DatePicker dp, TextField t, TextField e, TextField a) {
        n.clear(); p.clear(); c.clear(); dp.setValue(null);
        t.clear(); e.clear(); a.clear();
    }

    private String pad(String h) {
        return h.length() == 5 ? h + ":00" : h;
    }

    private void showMsg(Label lbl, String msg, boolean ok) {
        lbl.getStyleClass().setAll(ok ? "lbl-success" : "lbl-error");
        lbl.setText(msg);
    }
}