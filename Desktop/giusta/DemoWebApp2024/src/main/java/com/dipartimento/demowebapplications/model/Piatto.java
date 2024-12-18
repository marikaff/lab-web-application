package com.dipartimento.demowebapplications.model;

import lombok.*;

import java.util.List;



@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Piatto {

    protected String nome;
    protected String ingredienti;
    protected List<Ristorante> ristoranti;

}
