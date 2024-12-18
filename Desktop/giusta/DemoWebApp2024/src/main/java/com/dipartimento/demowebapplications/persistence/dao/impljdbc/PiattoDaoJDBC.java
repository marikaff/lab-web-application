package com.dipartimento.demowebapplications.persistence.dao.impljdbc;

import com.dipartimento.demowebapplications.model.Piatto;
import com.dipartimento.demowebapplications.model.Ristorante;
import com.dipartimento.demowebapplications.persistence.DBManager;
import com.dipartimento.demowebapplications.persistence.dao.PiattoDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PiattoDaoJDBC implements PiattoDao {

    private Connection connection;

    public PiattoDaoJDBC(Connection connection) { this.connection = connection; }

    private void addToRistorantePiattoTable(String piattoName, List<Ristorante> ristoranti) {

        String query = "INSERT INTO ristorante_piatto (ristorante_nome, piatto_nome) VALUES (?, ?)" +
                ", (?, ?)".repeat(Math.max(0, ristoranti.size() - 1));

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            for (int i = 0; i < ristoranti.size(); i++) {
                statement.setString(2*i, ristoranti.get(i).getNome());
                statement.setString(2*i + 1, piattoName);
            }

            statement.executeUpdate();

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void deleteFromRistorantePiattoTable(String piattoName) {
        deleteFromRistorantePiattoTable(piattoName, null);
    }
    private void deleteFromRistorantePiattoTable(String piattoName, List<String> idsToDelete) {

        // Costruzione della Query per un range di valori
        String query = "DELETE From ristorante_piatto" +
                "WHERE piatto_nome = ?";

        if (idsToDelete != null)
        { query += "and ristorante_nome in [?" + ", ?".repeat(Math.max(0, idsToDelete.size() - 1)) + "]"; }

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, piattoName);

            if (idsToDelete != null) {
                for (int i = 0; i < idsToDelete.size(); i++)
                { statement.setString(i + 1, idsToDelete.get(i)); }
            }

            statement.executeUpdate();

        } catch (Exception e) { e.printStackTrace(); }

    }

    private void updateRistorantePiattoTable(Piatto piatto) {

        List<Ristorante> ristorantiReali = piatto.getRistoranti();
        Set<String> idsRistorantiRegistrati = DBManager.getInstance().getRistoranteDao().findAllIDsByPiattoName(piatto.getNome());

        for (Ristorante ristorante : ristorantiReali) {
            if (idsRistorantiRegistrati.contains(ristorante.getNome())) {
                idsRistorantiRegistrati.remove(ristorante.getNome());
                ristorantiReali.remove(ristorante);
            }
        }

        // In questo punto: ristorantiReali - ristorantiRegistrati --> ristorantiDaRegistrare
        //                  idsRistorantiRegistrati - idsRistorantiReali --> idsRistorantiDaEliminare

        if (!idsRistorantiRegistrati.isEmpty())
        { deleteFromRistorantePiattoTable(piatto.getNome(), idsRistorantiRegistrati.stream().toList()); }

        if (!ristorantiReali.isEmpty())
        { addToRistorantePiattoTable(piatto.getNome(), ristorantiReali); }

    }


    @Override
    public List<Piatto> findAll() {
        List<Piatto> piatti = new ArrayList<Piatto>();

        Statement st = null;
        String query = "select * from piatto";
        try {
            st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                Piatto p = new PiattoProxy();
                p.setNome(rs.getString("nome"));
                p.setIngredienti(rs.getString("ingredienti"));
                piatti.add(p);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }

        return piatti;
    }

    @Override
    public Piatto findByPrimaryKey(String nome) {

        String query = "SELECT nome, ingredienti FROM piatto WHERE nome = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nome);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                PiattoProxy p = new PiattoProxy();
                p.setNome(nome);
                p.setIngredienti(rs.getString("ingredienti"));
                return p;
            }

        } catch (Exception e) { e.printStackTrace();}

        return null;
    }

    @Override
    public void save(Piatto piatto) {

        String query = "INSERT INTO piatto (nome, ingredienti) VALUES (?, ?) " +
            "ON CONFLICT (nome) DO UPDATE SET " +
            "   ingredienti = EXCLUDED.ingredienti";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, piatto.getNome());
            statement.setString(2, piatto.getIngredienti());
            statement.executeUpdate();

            updateRistorantePiattoTable(piatto);

        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void delete(Piatto piatto) {

        String query = "DELETE FROM piatto WHERE nome = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, piatto.getNome());
            statement.executeUpdate();

            deleteFromRistorantePiattoTable(piatto.getNome());

        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public List<Piatto> findAllByRistoranteName(String ristoranteName) {

        String query = "SELECT nome, ingredienti FROM piatto, ristorante_piatto " +
                "WHERE ristorante_piatto.ristorante_nome = ? " +
                    "and piatto.nome = ristorante_piatto.piatto_nome";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ristoranteName);
            ResultSet rs = statement.executeQuery(query);

            List<Piatto> piatti = new ArrayList<>();
            while (rs.next()){
                Piatto p = new PiattoProxy();
                p.setNome(rs.getString("nome"));
                p.setIngredienti(rs.getString("ingredienti"));
                piatti.add(p);
            }

            return piatti;

        } catch (Exception e) { e.printStackTrace(); }

        return List.of();
    }

    @Override
    public Set<String> findAllIDsbyRistoranteName(String ristoranteName) {

        Set<String> setOfIDs = new HashSet<>();

        String query = "SELECT piatto_nome FROM ristorante_piatto " +
                "WHERE ristorante_piatto.ristorante_nome = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ristoranteName);
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                setOfIDs.add(rs.getString("piatto_nome"));
            }

        } catch (Exception e) { e.printStackTrace(); }

        return setOfIDs;
    }

}
