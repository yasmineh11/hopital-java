package dao;

import model.Medecin;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedecinDAO {

    // Ajouter un médecin
    public boolean addMedecin(Medecin m) {
        String sql = "INSERT INTO medecins (nom,prenom,specialite,telephone,email)"
                + " VALUES (?,?,?,?,?)";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getPrenom());
            ps.setString(3, m.getSpecialite());
            ps.setString(4, m.getTelephone());
            ps.setString(5, m.getEmail());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Lister tous les médecins
    public List<Medecin> getAllMedecins() {
        List<Medecin> list = new ArrayList<>();
        String sql = "SELECT * FROM medecins";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Medecin(
                        rs.getInt("id_medecin"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("specialite"),
                        rs.getString("telephone"),
                        rs.getString("email"),
                        rs.getString("username")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Modifier un médecin
    public boolean updateMedecin(Medecin m) {
        String sql = "UPDATE medecins SET nom=?,prenom=?,specialite=?,"
                + "telephone=?,email=? WHERE id_medecin=?";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getPrenom());
            ps.setString(3, m.getSpecialite());
            ps.setString(4, m.getTelephone());
            ps.setString(5, m.getEmail());
            ps.setInt(6, m.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Supprimer un médecin
    public boolean deleteMedecin(int id) {
        String sql = "DELETE FROM medecins WHERE id_medecin=?";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Lier un username à un médecin (appelé lors de la création d'un compte MEDECIN)
    public boolean linkUsername(int idMedecin, String username) {
        String sql = "UPDATE medecins SET username=? WHERE id_medecin=?";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, idMedecin);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Lister les médecins sans compte utilisateur (username IS NULL)
    public List<Medecin> getMedecinsWithoutAccount() {
        List<Medecin> list = new ArrayList<>();
        String sql = "SELECT * FROM medecins WHERE username IS NULL ORDER BY nom";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Medecin(
                        rs.getInt("id_medecin"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("specialite"),
                        rs.getString("telephone"),
                        rs.getString("email"),
                        rs.getString("username")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Medecin findByUsername(String username) {
        String sql = "SELECT * FROM medecins WHERE username = ?";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Medecin(
                        rs.getInt("id_medecin"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("specialite"),
                        rs.getString("telephone"),
                        rs.getString("email"),
                        rs.getString("username")
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // CORRECTION : utilise id_medecin (pas "id")
    public Medecin findById(int id) {
        String sql = "SELECT * FROM medecins WHERE id_medecin = ?";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Medecin(
                        rs.getInt("id_medecin"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("specialite"),
                        rs.getString("telephone"),
                        rs.getString("email"),
                        rs.getString("username")
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}