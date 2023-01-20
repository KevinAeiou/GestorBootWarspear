package com.kevin.ceep.model;

import java.io.Serializable;

public class Usuario implements Serializable {

    private String id;
    private String nome;

    public Usuario(){}

    public Usuario(String id, String nome) {
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
