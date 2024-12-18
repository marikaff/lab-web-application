package com.dipartimento.demowebapplications.persistence.dao;

import com.dipartimento.demowebapplications.model.Ristorante;

import java.util.List;
import java.util.Set;

public interface RistoranteDao {

    public List<Ristorante> findAll();

    public Ristorante findByPrimaryKey(String nome);

    public void save(Ristorante ristorante);

    public void delete(Ristorante ristorante);

    public List<Ristorante> findAllByPiattoName(String piattoName);

    public Set<String> findAllIDsByPiattoName(String piattoName);

}
