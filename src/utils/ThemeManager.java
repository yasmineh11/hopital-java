package utils;

import javafx.scene.Scene;

/**
 * ThemeManager — Singleton statique
 * Gère le thème (clair/sombre) pour TOUTE l'application.
 * Un seul endroit pour changer le thème, tous les écrans suivent.
 */
public class ThemeManager {

    public static final String LIGHT = "/resources/hopital-light.css";
    public static final String DARK  = "/resources/hopital-dark.css";

    private static boolean darkMode = false;

    // Appliquer le thème actuel sur une scène
    public static void apply(Scene scene) {
        if (scene == null) return;
        scene.getStylesheets().clear();
        scene.getStylesheets().add(
                ThemeManager.class.getResource(
                        darkMode ? DARK : LIGHT
                ).toExternalForm()
        );
    }

    // Basculer et appliquer sur la scène donnée
    public static void toggle(Scene scene) {
        darkMode = !darkMode;
        apply(scene);
    }

    public static boolean isDark()  { return darkMode; }
    public static String  getIcon() { return darkMode ? "☀️" : "🌙"; }
}