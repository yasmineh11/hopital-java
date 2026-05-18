package controller;

import dao.ConsultationDAO;
import dao.MedecinDAO;
import dao.PatientDAO;
import dao.RendezVousDAO;
import model.Consultation;
import model.Medecin;
import model.Patient;
import model.RendezVous;
import utils.Session;

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
import java.util.List;
import java.util.stream.Collectors;

public class MedecinController {

    @FXML private StackPane centerPane;
    @FXML private Label     lblPageTitle;
    @FXML private Label     lblPageSub;
    @FXML private Label     lblAvatar;
    @FXML private Label     lblUsername;
    @FXML private Button    btnTheme;
    @FXML private Button    btnDash;
    @FXML private Button    btnRdv;
    @FXML private Button    btnConsult;
    @FXML private Button    btnHisto;

    private final ConsultationDAO consultDAO = new ConsultationDAO();
    private final RendezVousDAO   rdvDAO     = new RendezVousDAO();
    private final PatientDAO      patientDAO = new PatientDAO();
    private final MedecinDAO      medecinDAO = new MedecinDAO();

    private boolean darkMode = false;
    private int medecinId = -1;

    // ════════════════════════════════════════════════════════════
    // INITIALISATION
    // ════════════════════════════════════════════════════════════
    @FXML
    public void initialize() {
        if (Session.getCurrentUser() != null) {
            String name = Session.getCurrentUser().getUsername();
            lblUsername.setText(name);
            lblAvatar.setText(name.length() >= 2
                    ? name.substring(0, 2).toUpperCase()
                    : name.toUpperCase());
            medecinId = resoudreMedecinId();
            System.out.println("Utilisateur connecté : " + name);
            System.out.println("ID médecin : " + medecinId);
        }
        showDashboard();
    }

    private int resoudreMedecinId() {
        if (Session.getCurrentUser() == null) return -1;
        String username = Session.getCurrentUser().getUsername();
        Medecin medecin = medecinDAO.findByUsername(username);
        if (medecin != null) {
            System.out.println("Médecin trouvé : " + medecin.getNom());
            return medecin.getId();
        }
        System.out.println("Aucun médecin trouvé pour : " + username);
        return -1;
    }

    private int getMedecinId() { return medecinId; }

    // ════════════════════════════════════════════════════════════
    // TOGGLE THÈME
    // ════════════════════════════════════════════════════════════
    @FXML
    public void toggleTheme() {
        darkMode = !darkMode;
        Scene scene = centerPane.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(
                getClass().getResource(darkMode
                        ? "/resources/hopital-dark.css"
                        : "/resources/hopital-light.css"
                ).toExternalForm());
        btnTheme.setText(darkMode ? "☀️" : "🌙");
    }

    // ════════════════════════════════════════════════════════════
    // NAVIGATION
    // ════════════════════════════════════════════════════════════
    private void setActive(Button active) {
        for (Button b : new Button[]{btnDash, btnRdv, btnConsult, btnHisto}) {
            if (b == null) continue;
            b.getStyleClass().remove("nav-btn-active");
            if (!b.getStyleClass().contains("nav-btn"))
                b.getStyleClass().add("nav-btn");
        }
        if (active != null) active.getStyleClass().add("nav-btn-active");
    }

    private void setTopbar(String titre, String sous) {
        lblPageTitle.setText(titre);
        lblPageSub.setText(sous);
    }

    // ════════════════════════════════════════════════════════════
    // TABLEAU DE BORD
    // ════════════════════════════════════════════════════════════
    @FXML
    public void showDashboard() {
        setActive(btnDash);
        setTopbar("Tableau de bord",
                "Bienvenue, Dr. " + Session.getCurrentUser().getUsername());

        if (getMedecinId() == -1) {
            Label warn = new Label(
                    "⚠  Aucun médecin n'est lié à votre compte (\"" +
                            Session.getCurrentUser().getUsername() + "\").\n" +
                            "Demandez à l'administrateur d'ajouter votre fiche dans Médecins.");
            warn.setWrapText(true);
            warn.setStyle("-fx-text-fill:#C05621;-fx-font-size:13px;" +
                    "-fx-background-color:#FFFBEB;" +
                    "-fx-padding:16;-fx-background-radius:8;");
            centerPane.getChildren().setAll(new VBox(warn));
            return;
        }

        List<RendezVous> tousRdv = rdvDAO.getAllRendezVous().stream()
                .filter(r -> r.getIdMedecin() == getMedecinId())
                .collect(Collectors.toList());

        int  nbConsult      = consultDAO.getByMedecin(getMedecinId()).size();
        long nbRdvAujourd   = tousRdv.stream()
                .filter(r -> LocalDate.now().equals(r.getDateRdv())).count();
        long nbRdvPlanifies = tousRdv.stream()
                .filter(r -> "PLANIFIE".equals(r.getStatut())).count();
        long nbTotal        = tousRdv.size();

        HBox statsRow = new HBox(10);
        statsRow.getChildren().addAll(
                makeStatCard("Mes consultations",
                        String.valueOf(nbConsult),    "stat-card-green",  "stat-value-green"),
                makeStatCard("RDV aujourd'hui",
                        String.valueOf(nbRdvAujourd), "stat-card-blue",   "stat-value-blue"),
                makeStatCard("RDV planifiés",
                        String.valueOf(nbRdvPlanifies),"stat-card-orange", "stat-value-orange"),
                makeStatCard("Total mes RDV",
                        String.valueOf(nbTotal),       "stat-card-purple", "stat-value-purple")
        );

        // Aperçu : 5 prochains RDV
        List<RendezVous> prochains = tousRdv.stream()
                .filter(r -> !r.getDateRdv().isBefore(LocalDate.now()))
                .sorted((a, b) -> {
                    int cmp = a.getDateRdv().compareTo(b.getDateRdv());
                    return cmp != 0 ? cmp : a.getHeureRdv().compareTo(b.getHeureRdv());
                })
                .limit(5)
                .collect(Collectors.toList());

        TableView<RendezVous> tRdv = buildRdvTable(prochains, false);
        tRdv.setPrefHeight(220);
        VBox cardRdv = wrapInCard("Prochains rendez-vous", tRdv);

        VBox page = new VBox(14, statsRow, cardRdv);
        centerPane.getChildren().setAll(page);
    }

    // ════════════════════════════════════════════════════════════
    // RENDEZ-VOUS — TOUS + FILTRES
    // ════════════════════════════════════════════════════════════
    @FXML
    public void showRendezVous() {
        setActive(btnRdv);

        if (getMedecinId() == -1) {
            setTopbar("Rendez-vous", "—");
            showErreurMedecin();
            return;
        }

        // Tous les RDV de ce médecin
        List<RendezVous> tousRdv = rdvDAO.getAllRendezVous().stream()
                .filter(r -> r.getIdMedecin() == getMedecinId())
                .sorted((a, b) -> {
                    int cmp = b.getDateRdv().compareTo(a.getDateRdv()); // plus récent en premier
                    return cmp != 0 ? cmp : a.getHeureRdv().compareTo(b.getHeureRdv());
                })
                .collect(Collectors.toList());

        long nbAujourd = tousRdv.stream()
                .filter(r -> LocalDate.now().equals(r.getDateRdv())).count();

        setTopbar("Mes rendez-vous",
                tousRdv.size() + " RDV au total  —  " + nbAujourd + " aujourd'hui");

        // ── Barre de filtres ──────────────────────────────────
        // Filtre par statut
        ComboBox<String> cbStatut = new ComboBox<>();
        cbStatut.getItems().addAll("Tous", "PLANIFIE", "CONFIRME", "ANNULE");
        cbStatut.setValue("Tous");
        cbStatut.getStyleClass().add("combo-box");
        cbStatut.setPrefWidth(140);

        // Filtre par date
        ComboBox<String> cbPeriode = new ComboBox<>();
        cbPeriode.getItems().addAll("Toutes les dates", "Aujourd'hui",
                "Cette semaine", "Ce mois");
        cbPeriode.setValue("Toutes les dates");
        cbPeriode.getStyleClass().add("combo-box");
        cbPeriode.setPrefWidth(160);

        // Recherche patient
        TextField tfSearch = new TextField();
        tfSearch.setPromptText("🔍  Rechercher un patient...");
        tfSearch.getStyleClass().add("form-input");
        tfSearch.setPrefWidth(220);

        TableView<RendezVous> table = buildRdvTable(tousRdv, true);
        table.setPrefHeight(420);

        // Compteur résultats
        Label lblCount = new Label(tousRdv.size() + " rendez-vous");
        lblCount.setStyle("-fx-font-size:11px;-fx-text-fill:#718096;");

        // ── Logique de filtrage ────────────────────────────────
        Runnable applyFilters = () -> {
            String statut  = cbStatut.getValue();
            String periode = cbPeriode.getValue();
            String kw      = tfSearch.getText().trim().toLowerCase();

            List<RendezVous> filtered = tousRdv.stream()
                    .filter(r -> {
                        // Filtre statut
                        if (!"Tous".equals(statut) && !statut.equals(r.getStatut()))
                            return false;
                        // Filtre période
                        LocalDate d = r.getDateRdv();
                        LocalDate today = LocalDate.now();
                        switch (periode) {
                            case "Aujourd'hui"   -> { if (!d.equals(today)) return false; }
                            case "Cette semaine" -> {
                                LocalDate lundi = today.minusDays(today.getDayOfWeek().getValue() - 1);
                                LocalDate dim   = lundi.plusDays(6);
                                if (d.isBefore(lundi) || d.isAfter(dim)) return false;
                            }
                            case "Ce mois" -> {
                                if (d.getMonth() != today.getMonth()
                                        || d.getYear() != today.getYear()) return false;
                            }
                        }
                        // Filtre patient (nom/prénom)
                        if (!kw.isEmpty()) {
                            Patient p = patientDAO.findById(r.getIdPatient());
                            if (p == null) return false;
                            String full = (p.getNom() + " " + p.getPrenom()).toLowerCase();
                            if (!full.contains(kw)) return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());

            table.setItems(FXCollections.observableArrayList(filtered));
            lblCount.setText(filtered.size() + " rendez-vous");
        };

        cbStatut.valueProperty().addListener((o, a, b) -> applyFilters.run());
        cbPeriode.valueProperty().addListener((o, a, b) -> applyFilters.run());
        tfSearch.textProperty().addListener((o, a, b) -> applyFilters.run());

        // ── Layout ────────────────────────────────────────────
        HBox filterBar = new HBox(10, tfSearch, cbPeriode, cbStatut, lblCount);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(12, 16, 10, 16));
        filterBar.setStyle(
                "-fx-border-color:transparent transparent #E2E8F0 transparent;" +
                        "-fx-border-width:0 0 1 0;");

        Label titre = new Label("Mes rendez-vous");
        titre.getStyleClass().add("section-card-title");
        titre.setMaxWidth(Double.MAX_VALUE);
        titre.setPadding(new Insets(11, 16, 11, 16));
        titre.setStyle("-fx-border-color:transparent transparent #E2E8F0 transparent;" +
                "-fx-border-width:0 0 1 0;");

        VBox card = new VBox(0, titre, filterBar, table);
        card.getStyleClass().add("section-card");

        centerPane.getChildren().setAll(new VBox(14, card));
    }

    // ════════════════════════════════════════════════════════════
    // NOUVELLE CONSULTATION (depuis le menu)
    // ════════════════════════════════════════════════════════════
    @FXML
    public void showConsultation() {
        setActive(btnConsult);
        setTopbar("Nouvelle consultation", "Documenter un acte médical");
        if (getMedecinId() == -1) { showErreurMedecin(); return; }
        afficherFormulaireConsultation(-1, 0);
    }

    private void ouvrirFormulaireConsultation(RendezVous rdv) {
        setActive(btnConsult);
        setTopbar("Consultation", "RDV du " + rdv.getDateRdv());
        afficherFormulaireConsultation(rdv.getIdPatient(), rdv.getId());
    }

    // ════════════════════════════════════════════════════════════
    // FORMULAIRE DE CONSULTATION
    // ════════════════════════════════════════════════════════════
    private void afficherFormulaireConsultation(int preSelPatientId, int idRdv) {
        ComboBox<Patient> cbPatient = new ComboBox<>();
        cbPatient.getItems().addAll(patientDAO.getAllPatients());
        cbPatient.setPromptText("Sélectionner un patient *");
        cbPatient.getStyleClass().add("combo-box");
        cbPatient.setMaxWidth(Double.MAX_VALUE);

        if (preSelPatientId != -1) {
            patientDAO.getAllPatients().stream()
                    .filter(p -> p.getId() == preSelPatientId)
                    .findFirst().ifPresent(cbPatient::setValue);
        }

        DatePicker dpDate = new DatePicker(LocalDate.now());
        dpDate.getStyleClass().add("form-input");
        dpDate.setMaxWidth(Double.MAX_VALUE);

        TextArea taDiag  = styledArea("Entrez le diagnostic clinique...", 4);
        TextArea taTrait = styledArea("Traitement prescrit...", 4);
        TextArea taNotes = styledArea("Observations et notes médicales...", 3);
        Label lblMsg = new Label();

        Button btnSave   = new Button("Enregistrer la consultation");
        btnSave.getStyleClass().add("btn-primary");
        Button btnCancel = new Button("Annuler");
        btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> {
            cbPatient.setValue(null);
            taDiag.clear(); taTrait.clear(); taNotes.clear();
            lblMsg.setText("");
        });

        final int rdvId = idRdv;
        btnSave.setOnAction(e -> {
            if (cbPatient.getValue() == null) {
                showMsg(lblMsg, "Veuillez sélectionner un patient.", false); return; }
            if (taDiag.getText().trim().isEmpty()) {
                showMsg(lblMsg, "Le diagnostic est obligatoire.", false); return; }
            if (dpDate.getValue() == null) {
                showMsg(lblMsg, "La date est obligatoire.", false); return; }
            if (getMedecinId() == -1) {
                showMsg(lblMsg, "Erreur : aucun médecin lié à votre compte.", false); return; }

            Consultation c = new Consultation(
                    cbPatient.getValue().getId(), getMedecinId(), rdvId,
                    dpDate.getValue(), taDiag.getText().trim(),
                    taTrait.getText().trim(), taNotes.getText().trim());

            if (consultDAO.addConsultation(c)) {
                showMsg(lblMsg, "✓ Consultation enregistrée avec succès !", true);
                cbPatient.setValue(null);
                taDiag.clear(); taTrait.clear(); taNotes.clear();
                dpDate.setValue(LocalDate.now());
            } else {
                showMsg(lblMsg, "Erreur SQL — vérifiez que l'id_medecin "
                        + getMedecinId() + " existe dans la table medecins.", false);
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(12);
        grid.setPadding(new Insets(16, 18, 12, 18));

        VBox grpPat  = fg("Patient *", cbPatient);
        VBox grpDate = fg("Date de consultation", dpDate);
        GridPane.setHgrow(grpPat,  Priority.ALWAYS);
        GridPane.setHgrow(grpDate, Priority.ALWAYS);
        grid.addRow(0, grpPat, grpDate);

        VBox grpDiag = fg("Diagnostic *", taDiag);
        GridPane.setColumnSpan(grpDiag, 2);
        GridPane.setHgrow(grpDiag, Priority.ALWAYS);
        grid.add(grpDiag, 0, 1);

        VBox grpTrait = fg("Traitement prescrit",  taTrait);
        VBox grpNotes = fg("Notes / Observations", taNotes);
        GridPane.setHgrow(grpTrait, Priority.ALWAYS);
        GridPane.setHgrow(grpNotes, Priority.ALWAYS);
        grid.addRow(2, grpTrait, grpNotes);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50); c1.setHgrow(Priority.ALWAYS);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(50); c2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c1, c2);

        HBox footer = new HBox(8, btnSave, btnCancel);
        footer.setPadding(new Insets(10, 18, 14, 18));
        footer.setStyle("-fx-border-color:#E2E8F0 transparent transparent transparent;" +
                "-fx-border-width:1 0 0 0;");
        lblMsg.setPadding(new Insets(0, 18, 4, 18));

        Label titre = new Label("Fiche de consultation");
        titre.getStyleClass().add("section-card-title");
        titre.setMaxWidth(Double.MAX_VALUE);
        titre.setPadding(new Insets(11, 18, 11, 18));
        titre.setStyle("-fx-border-color:transparent transparent #E2E8F0 transparent;" +
                "-fx-border-width:0 0 1 0;");

        VBox card = new VBox(0, titre, grid, lblMsg, footer);
        card.getStyleClass().add("section-card");
        centerPane.getChildren().setAll(new VBox(14, card));
    }

    // ════════════════════════════════════════════════════════════
    // HISTORIQUE PATIENT
    // ════════════════════════════════════════════════════════════
    @FXML
    public void showHistorique() {
        setActive(btnHisto);
        setTopbar("Historique patient", "Consultez les fiches précédentes");

        ComboBox<Patient> cbPat = new ComboBox<>();
        cbPat.getItems().addAll(patientDAO.getAllPatients());
        cbPat.setPromptText("Choisir un patient...");
        cbPat.getStyleClass().add("combo-box");
        cbPat.setPrefWidth(300);

        Label lblCount = new Label("Sélectionnez un patient");
        lblCount.setStyle("-fx-text-fill:#718096;-fx-font-size:11.5px;");

        TableView<Consultation> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(320);
        table.setPlaceholder(new Label("Sélectionnez un patient pour voir son historique."));
        table.getColumns().addAll(
                colStr("Date",       "date"),
                colStr("Diagnostic", "diagnostic"),
                colStr("Traitement", "traitement"),
                colStr("Notes",      "notes"));

        cbPat.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, pat) -> {
                    if (pat == null) return;
                    List<Consultation> hist = consultDAO.getByPatient(pat.getId());
                    table.setItems(FXCollections.observableArrayList(hist));
                    lblCount.setText(hist.size() + " consultation(s) pour "
                            + pat.getNom() + " " + pat.getPrenom());
                });

        HBox header = new HBox(12, new Label("Patient :"), cbPat, lblCount);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 16, 8, 16));
        header.setStyle("-fx-border-color:transparent transparent #E2E8F0 transparent;" +
                "-fx-border-width:0 0 1 0;");

        Label titre = new Label("Historique des consultations");
        titre.getStyleClass().add("section-card-title");
        titre.setMaxWidth(Double.MAX_VALUE);
        titre.setPadding(new Insets(11, 16, 11, 16));
        titre.setStyle("-fx-border-color:transparent transparent #E2E8F0 transparent;" +
                "-fx-border-width:0 0 1 0;");

        VBox card = new VBox(0, titre, header, table);
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
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Hôpital — Connexion");
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ════════════════════════════════════════════════════════════
    // TABLEVIEW RENDEZ-VOUS
    // ════════════════════════════════════════════════════════════
    private TableView<RendezVous> buildRdvTable(List<RendezVous> data,
                                                boolean withConsulterBtn) {
        TableView<RendezVous> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(FXCollections.observableArrayList(data));
        table.setPlaceholder(new Label("Aucun rendez-vous trouvé."));

        // Date
        TableColumn<RendezVous, String> colDate = colStr("Date", "dateRdv");
        colDate.setMaxWidth(110); colDate.setMinWidth(100);

        // Heure
        TableColumn<RendezVous, String> colHeure = colStr("Heure", "heureRdv");
        colHeure.setMaxWidth(75); colHeure.setMinWidth(75);

        // Patient (résolu depuis l'id)
        TableColumn<RendezVous, String> colPat = new TableColumn<>("Patient");
        colPat.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setText(null); return; }
                RendezVous rdv = getTableView().getItems().get(getIndex());
                Patient p = patientDAO.findById(rdv.getIdPatient());
                if (p != null) {
                    Label avatar = new Label(
                            p.getNom().length() >= 2
                                    ? p.getNom().substring(0, 2).toUpperCase()
                                    : p.getNom().toUpperCase());
                    avatar.setStyle(
                            "-fx-background-color:#2563EB;-fx-text-fill:white;" +
                                    "-fx-font-size:10px;-fx-font-weight:bold;" +
                                    "-fx-min-width:26;-fx-min-height:26;" +
                                    "-fx-max-width:26;-fx-max-height:26;" +
                                    "-fx-background-radius:13;-fx-alignment:CENTER;");
                    Label name = new Label(p.getNom() + " " + p.getPrenom());
                    name.setStyle("-fx-font-size:12.5px;-fx-font-weight:bold;");
                    HBox box = new HBox(8, avatar, name);
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box); setText(null);
                } else {
                    setText("—"); setGraphic(null);
                }
            }
        });

        // Motif
        TableColumn<RendezVous, String> colMotif = colStr("Motif", "motif");

        // Statut avec badge
        TableColumn<RendezVous, String> colStat = new TableColumn<>("Statut");
        colStat.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStat.setMaxWidth(115); colStat.setMinWidth(110);
        colStat.setCellFactory(c -> new TableCell<>() {
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

        table.getColumns().addAll(colDate, colHeure, colPat, colMotif, colStat);

        if (withConsulterBtn) {
            TableColumn<RendezVous, Void> colAct = new TableColumn<>("");
            colAct.setMaxWidth(110); colAct.setMinWidth(110);
            colAct.setCellFactory(c -> new TableCell<>() {
                final Button btn = new Button("Consulter");
                {
                    btn.getStyleClass().add("btn-icon-consult");
                    btn.setOnAction(e -> ouvrirFormulaireConsultation(
                            getTableView().getItems().get(getIndex())));
                }
                @Override protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        RendezVous rdv = getTableView().getItems().get(getIndex());
                        btn.setDisable("ANNULE".equals(rdv.getStatut()));
                    }
                    setGraphic(empty ? null : btn);
                }
            });
            table.getColumns().add(colAct);
        }
        return table;
    }

    // ════════════════════════════════════════════════════════════
    // UTILITAIRES
    // ════════════════════════════════════════════════════════════
    private VBox makeStatCard(String label, String val,
                              String cardClass, String valClass) {
        Label lbl = new Label(label); lbl.getStyleClass().add("stat-label");
        Label v   = new Label(val);   v.getStyleClass().addAll("stat-value", valClass);
        VBox card = new VBox(6, lbl, v);
        card.getStyleClass().addAll("stat-card", cardClass);
        card.setPrefWidth(180);
        return card;
    }

    private VBox wrapInCard(String titre, TableView<?> table) {
        Label h = new Label(titre);
        h.getStyleClass().add("section-card-title");
        h.setMaxWidth(Double.MAX_VALUE);
        h.setPadding(new Insets(11, 16, 11, 16));
        h.setStyle("-fx-border-color:transparent transparent #E2E8F0 transparent;" +
                "-fx-border-width:0 0 1 0;");
        VBox card = new VBox(0, h, table);
        card.getStyleClass().add("section-card");
        return card;
    }

    private <T> TableColumn<T, String> colStr(String titre, String prop) {
        TableColumn<T, String> c = new TableColumn<>(titre);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        return c;
    }

    private TextArea styledArea(String prompt, int rows) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setPrefRowCount(rows);
        ta.setWrapText(true);
        ta.getStyleClass().add("form-input");
        ta.setMaxWidth(Double.MAX_VALUE);
        return ta;
    }

    private VBox fg(String label, Control field) {
        Label lbl = new Label(label);
        lbl.getStyleClass().add("field-label");
        field.setMaxWidth(Double.MAX_VALUE);
        VBox g = new VBox(4, lbl, field);
        GridPane.setHgrow(g, Priority.ALWAYS);
        return g;
    }

    private void showMsg(Label lbl, String msg, boolean success) {
        lbl.getStyleClass().setAll(success ? "lbl-success" : "lbl-error");
        lbl.setText(msg);
    }

    private void showErreurMedecin() {
        Label warn = new Label(
                "⚠  Aucun médecin n'est lié à votre compte.\n" +
                        "Demandez à l'administrateur d'ajouter votre fiche dans la section Médecins.");
        warn.setWrapText(true);
        warn.setStyle("-fx-text-fill:#C05621;-fx-font-size:13px;" +
                "-fx-background-color:#FFFBEB;" +
                "-fx-padding:16;-fx-background-radius:8;");
        centerPane.getChildren().setAll(new VBox(warn));
    }
}