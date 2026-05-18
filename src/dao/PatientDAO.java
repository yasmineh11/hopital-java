package dao;

import model.Patient;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    // ── Ajouter un patient ───────────────────────────────────────
    public boolean addPatient(Patient p) {
        String sql = "INSERT INTO patients " +
                "(nom, prenom, cin, date_naissance, " +
                " telephone, email, adresse) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setString(3, p.getCin());
            ps.setDate(4, p.getDateNaissance() != null
                    ? Date.valueOf(p.getDateNaissance()) : null);
            ps.setString(5, p.getTelephone());
            ps.setString(6, p.getEmail());
            ps.setString(7, p.getAdresse());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── Lister tous les patients ─────────────────────────────────
    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY nom";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── Modifier un patient ──────────────────────────────────────
    public boolean updatePatient(Patient p) {
        String sql = "UPDATE patients SET " +
                "nom=?, prenom=?, cin=?, date_naissance=?, " +
                "telephone=?, email=?, adresse=? " +
                "WHERE id_patient=?";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setString(3, p.getCin());
            ps.setDate(4, p.getDateNaissance() != null
                    ? Date.valueOf(p.getDateNaissance()) : null);
            ps.setString(5, p.getTelephone());
            ps.setString(6, p.getEmail());
            ps.setString(7, p.getAdresse());
            ps.setInt(8, p.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── Supprimer un patient par ID ──────────────────────────────
    public boolean deletePatient(int id) {
        String sql = "DELETE FROM patients WHERE id_patient = ?";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── Rechercher par nom, prénom ou CIN ────────────────────────
    public List<Patient> searchPatients(String keyword) {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients " +
                "WHERE nom LIKE ? OR prenom LIKE ? OR cin LIKE ? " +
                "ORDER BY nom";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── Trouver un patient par ID ────────────────────────────────
    public Patient findById(int id) {
        String sql = "SELECT * FROM patients WHERE id_patient = ?";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── Utilitaire : construire un Patient depuis un ResultSet ───
    private Patient mapRow(ResultSet rs) throws SQLException {
        Date d = rs.getDate("date_naissance");
        return new Patient(
                rs.getInt("id_patient"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("cin"),
                d != null ? d.toLocalDate() : null,
                rs.getString("telephone"),
                rs.getString("email"),
                rs.getString("adresse")
        );
    }
}
