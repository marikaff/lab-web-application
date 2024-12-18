package com.dipartimento.demowebapplications.controller;


import com.dipartimento.demowebapplications.exception.PiattoNotValidException;
import com.dipartimento.demowebapplications.model.Piatto;
import com.dipartimento.demowebapplications.service.IPiattoService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Collection;

@RestController
@RequestMapping("/api/piatto/v1")
class PiattoController {


    private final IPiattoService piattoService;


    public PiattoController(IPiattoService piattoService) {
        this.piattoService = piattoService;
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    ResponseEntity<Collection<Piatto>> getAllPiatti(){

        Collection<Piatto> all = this.piattoService.findAll();

        return  ResponseEntity.ok(all);

    }

    @RequestMapping(value = "/{nomePiatto}", method = RequestMethod.GET)
            // opzione 1
//    ResponseEntity<Piatto> getPiattoById(@PathVariable("nomePiatto") String nome){
    // opzione 2
    ResponseEntity<Piatto> getPiattoById(@PathVariable String nomePiatto){
        return  ResponseEntity.ok(
                this.piattoService.findById(nomePiatto)
        );
    }


    @RequestMapping(value = "/", method = RequestMethod.POST)
    ResponseEntity<Piatto> postCreateNewPiatto(@RequestBody  Piatto piatto) throws Exception {

        try{
            return  ResponseEntity.ok(
                    this.piattoService.createPiatto(piatto)
            );
        }catch (PiattoNotValidException e){
            return new ResponseEntity(e.getMessage() , HttpStatusCode.valueOf(400));
        // return ResponseEntity.status(HttpStatusCode.valueOf(400) , ).build();
        }

    }

    @RequestMapping(value = "/{nomePiatto}", method = RequestMethod.POST)
    ResponseEntity<Piatto> postUpdatePiatto(
            @PathVariable String nomePiatto,
            @RequestBody Piatto piatto
    ) throws Exception {
        return  ResponseEntity.ok(
                this.piattoService.updatePiatto(nomePiatto, piatto)
        );
    }


    @RequestMapping(value = "/{nomePiatto}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deletePiatto(String nome){
        this.piattoService.deletePiatto(nome);
        return ResponseEntity.ok().build();

    }






}
