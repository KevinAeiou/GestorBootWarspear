package com.kevin.ceep.model;

import java.io.Serializable;

public class Raridade implements Serializable {
    private String nome;

    public Raridade(){}
    public Raridade(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
