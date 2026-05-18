package dao;

import model.Consultation;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationDAO {

    // ── Ajouter une consultation ─────────────────────────────────
    // CORRECTION : id_rdv = 0 → NULL en base (évite la violation FK)
    public boolean addConsultation(Consultation c) {
        String sql = "INSERT INTO consultations " +
                "(id_patient, id_medecin, id_rdv, " +
                " date_consultation, diagnostic, traitement, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {

            ps.setInt(1, c.getIdPatient());
            ps.setInt(2, c.getIdMedecin());

            // ← CORRECTION CLEF : si pas de RDV lié → NULL, pas 0
            if (c.getIdRdv() > 0) {
                ps.setInt(3, c.getIdRdv());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setDate(4, Date.valueOf(c.getDate()));
            ps.setString(5, c.getDiagnostic());
            ps.setString(6, c.getTraitement());
            ps.setString(7, c.getNotes());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("[ConsultationDAO] Erreur addConsultation : "
                    + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ── Toutes les consultations d'un patient ────────────────────
    public List<Consultation> getByPatient(int idPatient) {
        List<Consultation> list = new ArrayList<>();
        String sql = "SELECT * FROM consultations " +
                "WHERE id_patient = ? " +
                "ORDER BY date_consultation DESC";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idPatient);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── Toutes les consultations d'un médecin ────────────────────
    public List<Consultation> getByMedecin(int idMedecin) {
        List<Consultation> list = new ArrayList<>();
        String sql = "SELECT * FROM consultations " +
                "WHERE id_medecin = ? " +
                "ORDER BY date_consultation DESC";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idMedecin);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── Toutes les consultations ─────────────────────────────────
    public List<Consultation> getAll() {
        List<Consultation> list = new ArrayList<>();
        String sql = "SELECT * FROM consultations " +
                "ORDER BY date_consultation DESC";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── Utilitaire : mapper un ResultSet → Consultation ──────────
    private Consultation mapRow(ResultSet rs) throws SQLException {
        // id_rdv peut être NULL en base → getInt retourne 0
        int idRdv = rs.getInt("id_rdv");
        if (rs.wasNull()) idRdv = 0;

        return new Consultation(
                rs.getInt("id_consultation"),
                rs.getInt("id_patient"),
                rs.getInt("id_medecin"),
                idRdv,
                rs.getDate("date_consultation").toLocalDate(),
                rs.getString("diagnostic"),
                rs.getString("traitement"),
                rs.getString("notes")
        );
    }
}