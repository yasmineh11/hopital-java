package model;

import java.time.LocalDate;

public class Patient {
    private int id;
    private String nom;
    private String prenom;
    private String cin;
    private LocalDate dateNaissance;
    private String telephone;
    private String email;
    private String adresse;

    // Constructeur complet (avec ID — depuis la BD)
    public Patient(int id, String nom, String prenom, String cin,
                   LocalDate dateNaissance, String telephone,
                   String email, String adresse) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.dateNaissance = dateNaissance;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
    }

    // Constructeur sans ID (pour création)
    public Patient(String nom, String prenom, String cin,
                   LocalDate dateNaissance, String telephone,
                   String email, String adresse) {
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.dateNaissance = dateNaissance;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
    }

    // Getters
    public int getId()               { return id; }
    public String getNom()           { return nom; }
    public String getPrenom()        { return prenom; }
    public String getCin()           { return cin; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public String getTelephone()     { return telephone; }
    public String getEmail()         { return email; }
    public String getAdresse()       { return adresse; }

    // Setters
    public void setNom(String nom)           { this.nom = nom; }
    public void setPrenom(String prenom)     { this.prenom = prenom; }
    public void setCin(String cin)           { this.cin = cin; }
    public void setDateNaissance(LocalDate d){ this.dateNaissance = d; }
    public void setTelephone(String t)       { this.telephone = t; }
    public void setEmail(String email)       { this.email = email; }
    public void setAdresse(String adresse)   { this.adresse = adresse; }

    @Override
    public String toString() {
        return nom + " " + prenom + " (CIN: " + cin + ")";
    }
}
