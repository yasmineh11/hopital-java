package dao;

import model.RendezVous;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RendezVousDAO {

    // Ajouter un rendez-vous
    public boolean addRendezVous(RendezVous r) {
        String sql = "INSERT INTO rendez_vous "+
                "(id_patient,id_medecin,date_rdv,heure_rdv,motif,statut) "+
                "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, r.getIdPatient());
            ps.setInt(2, r.getIdMedecin());
            ps.setDate(3, Date.valueOf(r.getDateRdv()));
            ps.setTime(4, Time.valueOf(r.getHeureRdv()));
            ps.setString(5, r.getMotif());
            ps.setString(6, r.getStatut());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Lister tous les rendez-vous
    public List<RendezVous> getAllRendezVous() {
        List<RendezVous> list = new ArrayList<>();
        String sql = "SELECT * FROM rendez_vous ORDER BY date_rdv, heure_rdv";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new RendezVous(
                        rs.getInt("id_rdv"),
                        rs.getInt("id_patient"),
                        rs.getInt("id_medecin"),
                        rs.getDate("date_rdv").toLocalDate(),
                        rs.getTime("heure_rdv").toLocalTime(),
                        rs.getString("motif"),
                        rs.getString("statut")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Modifier un rendez-vous
    public boolean updateRendezVous(RendezVous r) {
        String sql = "UPDATE rendez_vous SET id_medecin=?,date_rdv=?,"+
                "heure_rdv=?,motif=?,statut=? WHERE id_rdv=?";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, r.getIdMedecin());
            ps.setDate(2, Date.valueOf(r.getDateRdv()));
            ps.setTime(3, Time.valueOf(r.getHeureRdv()));
            ps.setString(4, r.getMotif());
            ps.setString(5, r.getStatut());
            ps.setInt(6, r.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Annuler un rendez-vous (changer statut)
    public boolean annulerRendezVous(int idRdv) {
        String sql = "UPDATE rendez_vous SET statut='ANNULE' WHERE id_rdv=?";
        try (PreparedStatement ps =
                     DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idRdv);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
