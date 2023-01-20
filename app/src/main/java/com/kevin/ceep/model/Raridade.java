package com.kevin.ceep.model;

import java.io.Serializable;

public class Raridade implements Serializable {

    private String id;
    private String nome;

    public Raridade(){}

    public Raridade(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public String getId() {
        return id;
    }
}
