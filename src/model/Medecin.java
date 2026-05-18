package model;

public class Medecin {

    private int id;
    private String nom;
    private String prenom;
    private String specialite;
    private String telephone;
    private String email;
    private String username;

    // Constructeur complet avec ID + username
    public Medecin(int id,
                   String nom,
                   String prenom,
                   String specialite,
                   String telephone,
                   String email,
                   String username) {

        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
        this.telephone = telephone;
        this.email = email;
        this.username = username;
    }

    // Constructeur sans ID mais avec username
    public Medecin(String nom,
                   String prenom,
                   String specialite,
                   String telephone,
                   String email,
                   String username) {

        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
        this.telephone = telephone;
        this.email = email;
        this.username = username;
    }

    // Constructeur avec ID sans username
    public Medecin(int id,
                   String nom,
                   String prenom,
                   String specialite,
                   String telephone,
                   String email) {

        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
        this.telephone = telephone;
        this.email = email;
    }

    // Constructeur sans ID sans username
    public Medecin(String nom,
                   String prenom,
                   String specialite,
                   String telephone,
                   String email) {

        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
        this.telephone = telephone;
        this.email = email;
    }


    // Getters
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getSpecialite() {
        return specialite;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    // Setters
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return nom + " " + prenom;
    }
}