package com.dipartimento.demowebapplications.service;


import com.dipartimento.demowebapplications.exception.PiattoNotValidException;
import com.dipartimento.demowebapplications.model.Piatto;

import java.util.Collection;

public interface IPiattoService {

    // list
    Collection<Piatto> findAll();

    // retrieve byID
    Piatto findById(String nome);

    // create
    Piatto createPiatto(Piatto piatto) throws Exception;

    // update
    Piatto updatePiatto(String nomePiatto, Piatto piatto) throws Exception;

    // delete
    void deletePiatto(String nome);


}
