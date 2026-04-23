package utils;

import model.User;

// Classe statique — stocke l'utilisateur connecté
public class Session {
    private static User currentUser = null;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static String getRole() {
        return currentUser != null ? currentUser.getRole() : "";
    }

    public static void logout() {
        currentUser = null;
    }
}
