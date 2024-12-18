package com.dipartimento.demowebapplications.persistence.dao.impljdbc;

import com.dipartimento.demowebapplications.model.Piatto;
import com.dipartimento.demowebapplications.model.Ristorante;
import com.dipartimento.demowebapplications.persistence.DBManager;
import com.dipartimento.demowebapplications.persistence.dao.RistoranteDao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RistoranteDaoJDBC implements RistoranteDao {

    private Connection connection;

    public RistoranteDaoJDBC(Connection connection) { this.connection = connection; }

    private void addToRistorantePiattoTable(String ristoranteName, List<Piatto> piatti) {

        String query = "INSERT INTO ristorante_piatto (ristorante_nome, piatto_nome) VALUES (?, ?)" +
                ", (?, ?)".repeat(Math.max(0, piatti.size() - 1));

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            for (int i = 0; i < piatti.size(); i++) {
                statement.setString(2*i, ristoranteName);
                statement.setString(2*i + 1, piatti.get(i).getNome());
            }

            statement.executeUpdate();

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void deleteFromRistorantePiattoTable(String ristoranteName) {
        deleteFromRistorantePiattoTable(ristoranteName, null);
    }
    private void deleteFromRistorantePiattoTable(String ristoranteName, List<String> idsToDelete) {

        // Costruzione della Query per un range di valori
        String query = "DELETE From ristorante_piatto" +
                "WHERE ristorante_nome = ?";

        if (idsToDelete != null)
        { query += " and piatto_nome in [?" + ", ?".repeat(Math.max(0, idsToDelete.size() - 1)) + "]"; }

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, ristoranteName);

            if (idsToDelete != null) {
                for (int i = 0; i < idsToDelete.size(); i++)
                { statement.setString(i + 1, idsToDelete.get(i)); }
            }

            statement.executeUpdate();

        } catch (Exception e) { e.printStackTrace(); }

    }

    private void updateRistorantePiattoTable(Ristorante ristorante) {

        List<Piatto> piattiReali = ristorante.getPiatti();
        Set<String> idsPiattiRegistrati = DBManager.getInstance().getPiattoDao().findAllIDsbyRistoranteName(ristorante.getNome());

        for (Piatto piatto : piattiReali) {
            if (idsPiattiRegistrati.contains(piatto.getNome())) {
                idsPiattiRegistrati.remove(piatto.getNome());
                piattiReali.remove(piatto);
            }
        }

        // In questo punto: piattiReali - piattiRegistrati --> piattiDaRegistrare
        //                  idsPiattiRegistrati - idsPiattiReali --> idsPiattiDaEliminare

        if (!idsPiattiRegistrati.isEmpty())
        { deleteFromRistorantePiattoTable(ristorante.getNome(), idsPiattiRegistrati.stream().toList()); }

        if (!piattiReali.isEmpty())
        { addToRistorantePiattoTable(ristorante.getNome(), piattiReali); }

    }


    @Override
    public List<Ristorante> findAll() {
        List<Ristorante> ristoranti = new ArrayList<Ristorante>();

        Statement st = null;
        String query = "select * from ristorante";
        try {
            st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                Ristorante rist = new RistoranteProxy();
                rist.setNome(rs.getString("nome"));
                rist.setDescrizione(rs.getString("descrizione"));
                rist.setUbicazione(rs.getString("ubicazione"));
                ristoranti.add(rist);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ristoranti;
    }

    @Override
    public Ristorante findByPrimaryKey(String nome) {

        String query = "SELECT nome, descrizione, ubicazione FROM ristorante WHERE nome = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nome);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                Ristorante rist = new RistoranteProxy();
                rist.setNome(nome);
                rist.setDescrizione(rs.getString("descrizione"));
                rist.setUbicazione(rs.getString("ubicazione"));
                return rist;
            }

        } catch (Exception e) { e.printStackTrace();}

        return null;
    }

    @Override
    public void save(Ristorante ristorante) {
        String query = "INSERT INTO ristorante (nome, descrizione, ubicazione) VALUES (?, ?, ?) " +
                "ON CONFLICT (nome) DO UPDATE SET " +
                "   descrizione = EXCLUDED.descrizione , "+
                "   ubicazione = EXCLUDED.ubicazione ";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ristorante.getNome());
            statement.setString(2, ristorante.getDescrizione());
            statement.setString(3, ristorante.getUbicazione());
            statement.executeUpdate();

            updateRistorantePiattoTable(ristorante);

        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void delete(Ristorante ristorante) {

        String query = "DELETE FROM ristorante WHERE nome = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ristorante.getNome());
            statement.executeUpdate();

            deleteFromRistorantePiattoTable(ristorante.getNome());

        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public List<Ristorante> findAllByPiattoName(String piattoName) {

        String query = "SELECT nome, descrizione, ubicazione FROM ristorante, ristorante_piatto " +
                "WHERE ristorante_piatto.piatto_nome = ? " +
                "and ristorante.nome = ristorante_piatto.ristorante_nome";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, piattoName);
            ResultSet rs = statement.executeQuery(query);

            List<Ristorante> ristoranti = new ArrayList<>();
            while (rs.next()){
                Ristorante rist = new RistoranteProxy();
                rist.setNome(rs.getString("nome"));
                rist.setDescrizione(rs.getString("descrizione"));
                rist.setUbicazione(rs.getString("ubicazione"));
                ristoranti.add(rist);
            }

            return ristoranti;

        } catch (Exception e) { e.printStackTrace(); }

        return List.of();
    }

    @Override
    public Set<String> findAllIDsByPiattoName(String piattoName) {

        Set<String> setOfIDs = new HashSet<>();

        String query = "SELECT ristorante_nome FROM ristorante_piatto " +
                "WHERE ristorante_piatto.piatto_nome = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, piattoName);
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                setOfIDs.add(rs.getString("ristorante_nome"));
            }

        } catch (Exception e) { e.printStackTrace(); }

        return setOfIDs;
    }

}
