package utils;

public class Validator {

    // ── CHAMPS TEXTE GÉNÉRAUX ─────────────────────────────

    /**
     * Vérifie qu'un champ texte n'est pas vide.
     * Utilisé pour : nom, prénom, username, spécialité…
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Vérifie que le texte respecte une longueur min/max.
     * Ex : isValidLength(nom, 2, 100)
     */
    public static boolean isValidLength(String value, int min, int max) {
        if (value == null) return false;
        int len = value.trim().length();
        return len >= min && len <= max;
    }

    // ── USERNAME ──────────────────────────────────────────

    /**
     * Username : 3 à 50 caractères, lettres/chiffres/underscore uniquement.
     * Correspond à la contrainte VARCHAR(50) UNIQUE de la table utilisateurs.
     */
    public static boolean isValidUsername(String username) {
        if (!isNotEmpty(username)) return false;
        return username.trim().matches("^[a-zA-Z0-9_]{3,50}$");
    }

    // ── MOT DE PASSE ──────────────────────────────────────

    /**
     * Mot de passe : au moins 6 caractères.
     * (Le projet stocke en clair pour l'instant — minimum de sécurité.)
     */
    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && password.length() >= 6;
    }

    // ── EMAIL ─────────────────────────────────────────────

    /**
     * Email : format standard nom@domaine.ext
     * Correspond au champ email VARCHAR(150) de la table medecins.
     * L'email peut être vide/null (champ optionnel pour un médecin).
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return true; // optionnel
        return email.trim().matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    }

    // ── TÉLÉPHONE ─────────────────────────────────────────

    /**
     * Téléphone : 8 à 20 chiffres, espaces et +/- tolérés.
     * Correspond au champ telephone VARCHAR(20) de la table medecins.
     * Optionnel : retourne true si vide.
     */
    public static boolean isValidTelephone(String tel) {
        if (tel == null || tel.trim().isEmpty()) return true; // optionnel
        return tel.trim().matches("^[+\\d][\\d\\s\\-]{7,19}$");
    }

    // ── RÔLE UTILISATEUR ──────────────────────────────────

    /**
     * Vérifie que le rôle est l'un des 3 rôles autorisés
     * par l'ENUM de la table utilisateurs.
     */
    public static boolean isValidRole(String role) {
        return role != null &&
                (role.equals("ADMIN") ||
                        role.equals("MEDECIN") ||
                        role.equals("SECRETAIRE"));
    }

    // ── VALIDATION COMPLÈTE — MÉDECIN ────────────────────

    /**
     * Valide tous les champs obligatoires d'un médecin avant INSERT/UPDATE.
     * Retourne un message d'erreur lisible, ou null si tout est OK.
     */
    public static String validateMedecin(String nom, String prenom,
                                         String specialite, String tel,
                                         String email) {
        if (!isNotEmpty(nom))
            return "Le nom est obligatoire.";
        if (!isValidLength(nom, 2, 100))
            return "Le nom doit contenir entre 2 et 100 caractères.";

        if (!isNotEmpty(prenom))
            return "Le prénom est obligatoire.";
        if (!isValidLength(prenom, 2, 100))
            return "Le prénom doit contenir entre 2 et 100 caractères.";

        if (!isNotEmpty(specialite))
            return "La spécialité est obligatoire.";

        if (!isValidTelephone(tel))
            return "Numéro de téléphone invalide (ex: +216 22 333 444).";

        if (!isValidEmail(email))
            return "Adresse email invalide.";

        return null; // tout est valide
    }

    // ── VALIDATION COMPLÈTE — UTILISATEUR ────────────────

    /**
     * Valide les champs d'un nouveau compte utilisateur.
     * Retourne un message d'erreur, ou null si tout est OK.
     */
    public static String validateUser(String username, String password,
                                      String role) {
        if (!isValidUsername(username))
            return "Username invalide (3-50 caractères, lettres/chiffres/_).";

        if (!isValidPassword(password))
            return "Le mot de passe doit contenir au moins 6 caractères.";

        if (!isValidRole(role))
            return "Rôle invalide. Choisissez ADMIN, MEDECIN ou SECRETAIRE.";

        return null; // tout est valide
    }
}