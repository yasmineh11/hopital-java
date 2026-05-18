package model;
import java.time.LocalDate;
public class Consultation {
    private int id;
    private int idPatient;
    private int idMedecin;
    private int idRdv; // 0 si sans RDV
    private LocalDate date;
    private String diagnostic;
    private String traitement;
    private String notes;
    // Constructeur complet (depuis la BD)
    public Consultation(int id, int idPatient, int idMedecin,
                        int idRdv, LocalDate date,
                        String diagnostic, String traitement, String notes) {
        this.id = id; this.idPatient = idPatient;
        this.idMedecin = idMedecin; this.idRdv = idRdv;
        this.date = date; this.diagnostic = diagnostic;
        this.traitement = traitement; this.notes = notes;
    }
    // Constructeur sans ID (pour creation)
    public Consultation(int idPatient, int idMedecin,
                        int idRdv, LocalDate date,
                        String diagnostic, String traitement, String notes) {
        this.idPatient = idPatient; this.idMedecin = idMedecin;
        this.idRdv = idRdv; this.date = date;
        this.diagnostic = diagnostic; this.traitement = traitement;
        this.notes = notes;
    }
    // Getters
    public int getId() { return id; }
    public int getIdPatient() { return idPatient; }
    public int getIdMedecin() { return idMedecin; }
    public int getIdRdv() { return idRdv; }
    public LocalDate getDate() { return date; }
    public String getDiagnostic() { return diagnostic; }
    public String getTraitement() { return traitement; }
    public String getNotes() { return notes; }
    // Setters
    public void setDiagnostic(String d) { this.diagnostic = d; }
    public void setTraitement(String t) { this.traitement = t; }
    public void setNotes(String n) { this.notes = n; }
    @Override
    public String toString() {
        return date + " — " + diagnostic;
    }
}
