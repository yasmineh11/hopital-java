package model;

public class Medecin {
    private int id;
    private String nom;
    private String prenom;
    private String specialite;
    private String telephone;
    private String email;

    public Medecin(int id, String nom, String prenom,
                   String specialite, String telephone, String email) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.specialite = specialite;
        this.telephone = telephone;
        this.email = email;
    }

    public Medecin(String nom, String prenom, String specialite,
                   String telephone, String email) {
        this.nom = nom; this.prenom = prenom;
        this.specialite = specialite;
        this.telephone = telephone; this.email = email;
    }

    public int getId()            { return id; }
    public String getNom()        { return nom; }
    public String getPrenom()     { return prenom; }
    public String getSpecialite() { return specialite; }
    public String getTelephone()  { return telephone; }
    public String getEmail()      { return email; }

    public void setNom(String nom)               { this.nom = nom; }
    public void setPrenom(String prenom)         { this.prenom = prenom; }
    public void setSpecialite(String s)          { this.specialite = s; }
    public void setTelephone(String t)           { this.telephone = t; }
    public void setEmail(String email)           { this.email = email; }

    @Override
    public String toString() { return nom + " " + prenom + " - " + specialite; }
}