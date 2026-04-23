package controller;

import dao.MedecinDAO;
import dao.PatientDAO;
import dao.RendezVousDAO;
import model.Medecin;
import model.Patient;
import model.RendezVous;
import utils.Session;
import javafx.collections.FXCollections;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SecretaireController {

    @FXML
    private StackPane centerPane;

    private PatientDAO patientDAO = new PatientDAO();
    private RendezVousDAO rdvDAO = new RendezVousDAO();
    private MedecinDAO medecinDAO = new MedecinDAO();

    // ── TABLEAU DE BORD ──────────────────────────────
    @FXML
    public void showDashboard() {
        VBox box = new VBox(15);
        box.setStyle("-fx-padding: 30;");
        box.getChildren().addAll(
                new Label("Bienvenue, " + Session.getCurrentUser().getUsername()),
                new Label("Patients enregistrés : " +
                        patientDAO.getAllPatients().size()),
                new Label("Rendez-vous planifiés : " +
                        rdvDAO.getAllRendezVous().stream()
                                .filter(r -> "PLANIFIE".equals(r.getStatut()))
                                .count())
        );
        centerPane.getChildren().setAll(box);
    }
    // ── GESTION PATIENTS ─────────────────────────────
    @FXML
    public void showPatients() {
        VBox box = new VBox(10);
        box.setStyle("-fx-padding: 20;");

        // TableView
        TableView<Patient> table = new TableView<>();
        TableColumn<Patient, String> colNom  = new TableColumn<>("Nom");
        TableColumn<Patient, String> colPre  = new TableColumn<>("Prénom");
        TableColumn<Patient, String> colCin  = new TableColumn<>("CIN");
        TableColumn<Patient, String> colTel  = new TableColumn<>("Téléphone");

        colNom.setCellValueFactory(
                new PropertyValueFactory<>("nom"));
        colPre.setCellValueFactory(
                new PropertyValueFactory<>("prenom"));
        colCin.setCellValueFactory(
                new PropertyValueFactory<>("cin"));
        colTel.setCellValueFactory(
                new PropertyValueFactory<>("telephone"));

        table.getColumns().addAll(colNom, colPre, colCin, colTel);
        table.setItems(FXCollections.observableArrayList(
                patientDAO.getAllPatients()));
        table.setPrefHeight(280);

        // Formulaire ajout / modification
        TextField tfNom    = new TextField();
        tfNom.setPromptText("Nom");
        TextField tfPre    = new TextField();
        tfPre.setPromptText("Prénom");
        TextField tfCin    = new TextField();
        tfCin.setPromptText("CIN");
        DatePicker dpNaiss = new DatePicker();
        TextField tfTel    = new TextField();
        tfTel.setPromptText("Téléphone");
        TextField tfEmail  = new TextField();
        tfEmail.setPromptText("Email");
        TextField tfAdr    = new TextField();
        tfAdr.setPromptText("Adresse");

        // Remplir le formulaire quand on sélectionne une ligne
        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, sel) -> {
                    if (sel != null) {
                        tfNom.setText(sel.getNom());
                        tfPre.setText(sel.getPrenom());
                        tfCin.setText(sel.getCin());
                        dpNaiss.setValue(sel.getDateNaissance());
                        tfTel.setText(sel.getTelephone());
                        tfEmail.setText(sel.getEmail());
                        tfAdr.setText(sel.getAdresse());
                    }
                });

        Button btnAdd = new Button("➕ Ajouter");
        Button btnUpd = new Button("✏️ Modifier");

        // Ajouter un patient
        btnAdd.setOnAction(e -> {
            Patient p = new Patient(
                    tfNom.getText(), tfPre.getText(), tfCin.getText(),
                    dpNaiss.getValue(), tfTel.getText(),
                    tfEmail.getText(), tfAdr.getText()
            );
            if (patientDAO.addPatient(p)) {
                table.setItems(FXCollections.observableArrayList(
                        patientDAO.getAllPatients()));
                clearPatientForm(tfNom,tfPre,tfCin,dpNaiss,tfTel,tfEmail,tfAdr);
            }
        });

        // Modifier le patient sélectionné
        btnUpd.setOnAction(e -> {
            Patient sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                sel.setNom(tfNom.getText());
                sel.setPrenom(tfPre.getText());
                sel.setCin(tfCin.getText());
                sel.setDateNaissance(dpNaiss.getValue());
                sel.setTelephone(tfTel.getText());
                sel.setEmail(tfEmail.getText());
                sel.setAdresse(tfAdr.getText());
                if (patientDAO.updatePatient(sel)) {
                    table.setItems(FXCollections.observableArrayList(
                            patientDAO.getAllPatients()));
                    clearPatientForm(
                            tfNom,tfPre,tfCin,dpNaiss,tfTel,tfEmail,tfAdr);
                }
            }
        });

        GridPane form = new GridPane();
        form.setHgap(8); form.setVgap(6);
        form.addRow(0, new Label("Nom:"),    tfNom,
                new Label("Prénom:"), tfPre);
        form.addRow(1, new Label("CIN:"),    tfCin,
                new Label("Né(e) le:"), dpNaiss);
        form.addRow(2, new Label("Tél:"),    tfTel,
                new Label("Email:"),  tfEmail);
        form.addRow(3, new Label("Adresse:"), tfAdr);

        HBox buttons = new HBox(8, btnAdd, btnUpd);
        box.getChildren().addAll(
                new Label("👤 Gestion des Patients"),
                table, form, buttons);
        centerPane.getChildren().setAll(box);
    }

    // Méthode utilitaire — vider le formulaire patient
    private void clearPatientForm(TextField n, TextField p,
                                  TextField c, DatePicker dp,
                                  TextField t, TextField e, TextField a) {
        n.clear(); p.clear(); c.clear();
        dp.setValue(null);
        t.clear(); e.clear(); a.clear();
    }

    // ── GESTION RENDEZ-VOUS ──────────────────────────
    @FXML
    public void showRendezVous() {
        VBox box = new VBox(10);
        box.setStyle("-fx-padding: 20;");

        // TableView rendez-vous
        TableView<RendezVous> table = new TableView<>();
        TableColumn<RendezVous,String> colDate =
                new TableColumn<>("Date");
        TableColumn<RendezVous,String> colHeure =
                new TableColumn<>("Heure");
        TableColumn<RendezVous,String> colMotif =
                new TableColumn<>("Motif");
        TableColumn<RendezVous,String> colStatut =
                new TableColumn<>("Statut");

        colDate.setCellValueFactory(
                new PropertyValueFactory<>("dateRdv"));
        colHeure.setCellValueFactory(
                new PropertyValueFactory<>("heureRdv"));
        colMotif.setCellValueFactory(
                new PropertyValueFactory<>("motif"));
        colStatut.setCellValueFactory(
                new PropertyValueFactory<>("statut"));

        table.getColumns().addAll(
                colDate, colHeure, colMotif, colStatut);
        table.setItems(FXCollections.observableArrayList(
                rdvDAO.getAllRendezVous()));
        table.setPrefHeight(250);

        // ComboBox patients
        ComboBox<Patient> cbPatient = new ComboBox<>();
        cbPatient.setPromptText("Choisir patient");
        cbPatient.getItems().addAll(patientDAO.getAllPatients());

        // ComboBox médecins
        ComboBox<Medecin> cbMedecin = new ComboBox<>();
        cbMedecin.setPromptText("Choisir médecin");
        cbMedecin.getItems().addAll(medecinDAO.getAllMedecins());

        DatePicker dpDate = new DatePicker(LocalDate.now());
        TextField  tfHeure = new TextField();
        tfHeure.setPromptText("HH:MM (ex: 09:30)");
        TextField  tfMotif = new TextField();
        tfMotif.setPromptText("Motif de la visite");

        ComboBox<String> cbStatut = new ComboBox<>();
        cbStatut.getItems().addAll(
                "PLANIFIE", "CONFIRME", "ANNULE");
        cbStatut.setValue("PLANIFIE");

        // Remplir formulaire sur sélection
        table.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, sel) -> {
                    if (sel != null) {
                        dpDate.setValue(sel.getDateRdv());
                        tfHeure.setText(sel.getHeureRdv().toString());
                        tfMotif.setText(sel.getMotif());
                        cbStatut.setValue(sel.getStatut());
                        // Pré-sélectionner le médecin
                        medecinDAO.getAllMedecins().stream()
                                .filter(m -> m.getId() == sel.getIdMedecin())
                                .findFirst()
                                .ifPresent(cbMedecin::setValue);
                    }
                });

        Button btnAdd = new Button("➕ Prendre RDV");
        Button btnUpd = new Button("✏️ Modifier RDV");
        Button btnAnn = new Button("❌ Annuler RDV");

        // Prendre un rendez-vous
        btnAdd.setOnAction(e -> {
            Patient pat = cbPatient.getValue();
            Medecin med = cbMedecin.getValue();
            if (pat == null || med == null ||
                    dpDate.getValue() == null ||
                    tfHeure.getText().isEmpty()) {
                // TODO: afficher message d'erreur
                return;
            }
            RendezVous rdv = new RendezVous(
                    pat.getId(),
                    med.getId(),
                    dpDate.getValue(),
                    LocalTime.parse(tfHeure.getText()),
                    tfMotif.getText(),
                    cbStatut.getValue()
            );
            if (rdvDAO.addRendezVous(rdv)) {
                table.setItems(FXCollections.observableArrayList(
                        rdvDAO.getAllRendezVous()));
            }
        });

        // Modifier un rendez-vous
        btnUpd.setOnAction(e -> {
            RendezVous sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                Medecin med = cbMedecin.getValue();
                if (med != null) sel.setIdMedecin(med.getId());
                sel.setDateRdv(dpDate.getValue());
                sel.setHeureRdv(
                        LocalTime.parse(tfHeure.getText()));
                sel.setMotif(tfMotif.getText());
                sel.setStatut(cbStatut.getValue());
                if (rdvDAO.updateRendezVous(sel)) {
                    table.setItems(FXCollections.observableArrayList(
                            rdvDAO.getAllRendezVous()));
                }
            }
        });

        // Annuler un rendez-vous
        btnAnn.setOnAction(e -> {
            RendezVous sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                rdvDAO.annulerRendezVous(sel.getId());
                table.setItems(FXCollections.observableArrayList(
                        rdvDAO.getAllRendezVous()));
            }
        });

        GridPane form = new GridPane();
        form.setHgap(8); form.setVgap(6);
        form.addRow(0,
                new Label("Patient:"),  cbPatient,
                new Label("Médecin:"),  cbMedecin);
        form.addRow(1,
                new Label("Date:"),     dpDate,
                new Label("Heure:"),    tfHeure);
        form.addRow(2,
                new Label("Motif:"),    tfMotif,
                new Label("Statut:"),   cbStatut);

        HBox buttons = new HBox(8, btnAdd, btnUpd, btnAnn);
        box.getChildren().addAll(
                new Label("📅 Gestion des Rendez-vous"),
                table, form, buttons);
        centerPane.getChildren().setAll(box);
    }


    // ── RECHERCHE PATIENT ────────────────────────────
    @FXML
    public void showRecherche() {
        VBox box = new VBox(10);
        box.setStyle("-fx-padding: 20;");

        TextField tfSearch = new TextField();
        tfSearch.setPromptText("Rechercher par nom, prénom ou CIN...");
        tfSearch.setPrefWidth(350);

        Button btnSearch = new Button("🔍 Rechercher");

        TableView<Patient> table = new TableView<>();
        TableColumn<Patient,String> colNom  = new TableColumn<>("Nom");
        TableColumn<Patient,String> colPre  = new TableColumn<>("Prénom");
        TableColumn<Patient,String> colCin  = new TableColumn<>("CIN");
        TableColumn<Patient,String> colTel  = new TableColumn<>("Téléphone");

        colNom.setCellValueFactory(
                new PropertyValueFactory<>("nom"));
        colPre.setCellValueFactory(
                new PropertyValueFactory<>("prenom"));
        colCin.setCellValueFactory(
                new PropertyValueFactory<>("cin"));
        colTel.setCellValueFactory(
                new PropertyValueFactory<>("telephone"));

        table.getColumns().addAll(colNom, colPre, colCin, colTel);
        table.setPrefHeight(300);

        // Recherche au clic
        btnSearch.setOnAction(e -> {
            String kw = tfSearch.getText().trim();
            if (!kw.isEmpty()) {
                table.setItems(FXCollections.observableArrayList(
                        patientDAO.searchPatients(kw)));
            }
        });

        // Recherche en temps réel (optionnel)
        tfSearch.textProperty().addListener((obs, old, val) -> {
            if (val.length() >= 2) {
                table.setItems(FXCollections.observableArrayList(
                        patientDAO.searchPatients(val)));
            } else if (val.isEmpty()) {
                table.setItems(FXCollections.observableArrayList(
                        patientDAO.getAllPatients()));
            }
        });

        HBox searchBar = new HBox(8, tfSearch, btnSearch);
        box.getChildren().addAll(
                new Label("🔍 Recherche Patient"),
                searchBar, table);
        centerPane.getChildren().setAll(box);
    }


    // ── DÉCONNEXION ──────────────────────────────────
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

} // Fin de SecretaireController



