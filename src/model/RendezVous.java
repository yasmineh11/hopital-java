package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class RendezVous {
    private int id;
    private int idPatient;
    private int idMedecin;
    private LocalDate dateRdv;
    private LocalTime heureRdv;
    private String motif;
    private String statut; // PLANIFIE, CONFIRME, ANNULE

    // Constructeur complet (depuis la BD)
    public RendezVous(int id, int idPatient, int idMedecin,
                      LocalDate dateRdv, LocalTime heureRdv,
                      String motif, String statut) {
        this.id = id;
        this.idPatient = idPatient;
        this.idMedecin = idMedecin;
        this.dateRdv = dateRdv;
        this.heureRdv = heureRdv;
        this.motif = motif;
        this.statut = statut;
    }

    // Constructeur sans ID (pour création)
    public RendezVous(int idPatient, int idMedecin,
                      LocalDate dateRdv, LocalTime heureRdv,
                      String motif, String statut) {
        this.idPatient = idPatient;
        this.idMedecin = idMedecin;
        this.dateRdv = dateRdv;
        this.heureRdv = heureRdv;
        this.motif = motif;
        this.statut = statut;
    }

    // Getters
    public int getId()           { return id; }
    public int getIdPatient()    { return idPatient; }
    public int getIdMedecin()    { return idMedecin; }
    public LocalDate getDateRdv()  { return dateRdv; }
    public LocalTime getHeureRdv() { return heureRdv; }
    public String getMotif()     { return motif; }
    public String getStatut()    { return statut; }

    // Setters
    public void setDateRdv(LocalDate d)  { this.dateRdv = d; }
    public void setHeureRdv(LocalTime h) { this.heureRdv = h; }
    public void setMotif(String motif)   { this.motif = motif; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setIdMedecin(int id)     { this.idMedecin = id; }

    @Override
    public String toString() {
        return dateRdv + " " + heureRdv + " — " + motif;
    }
}
