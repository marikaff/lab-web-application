package com.dipartimento.demowebapplications.service;


import com.dipartimento.demowebapplications.exception.PiattoNotValidException;
import com.dipartimento.demowebapplications.model.Piatto;
import com.dipartimento.demowebapplications.persistence.dao.PiattoDao;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
class PiattoService implements IPiattoService {

    //opzione autowiring 1
//    @Autowired
//    private  PiattoDao dao;

    // opzione autowiring 2
    private final PiattoDao piattoDao;

    PiattoService(PiattoDao piattoDao) {
        this.piattoDao = piattoDao;
    }


    @Override
    public Collection<Piatto> findAll() {
        return piattoDao.findAll();
    }

    @Override
    public Piatto findById(String nome) {
        return piattoDao.findByPrimaryKey(nome);
    }

    @Override
    public Piatto createPiatto(Piatto piatto) throws Exception {

        System.out.println("Doing create piatto");
        // verifythata Piatto is consistent
        checkPiattoIsValid(piatto);

        //verify that not exists a Piatto with the same name...
        Piatto byPrimaryKey = piattoDao.findByPrimaryKey(piatto.getNome());
        if(byPrimaryKey!=null){
            throw new Exception("Already exists a Piatto whit the same name:"+piatto.getNome());
        }


        piattoDao.save(piatto);

        return piattoDao.findByPrimaryKey(piatto.getNome());
    }

    private void checkPiattoIsValid(Piatto piatto) throws PiattoNotValidException {
        if(piatto==null){
            throw new PiattoNotValidException("Piatto must be not null");
        }


        if(piatto.getNome()==null || piatto.getNome().isEmpty()){
            throw new PiattoNotValidException("Piatto.nome must be not null and not empty");
        }

        //TODO other checks..

    }

    @Override
    public Piatto updatePiatto(String nomePiatto, Piatto piatto) throws Exception {

        System.out.println("Doing update");
        checkPiattoIsValid(piatto);

        //verify that not exists a Piatto with the same name...
        Piatto byPrimaryKey = piattoDao.findByPrimaryKey(nomePiatto);
        if(byPrimaryKey==null){
            throw new Exception("Not exists a Piatto whit the name:"+piatto.getNome());
        }

        piatto.setNome(nomePiatto);


        piattoDao.save(piatto);

        return piattoDao.findByPrimaryKey(piatto.getNome());
    }

    @Override
    public void deletePiatto(String nome) {
        //TODO modify DAO
//        this.piattoDao.delete(nome);
    }
}
