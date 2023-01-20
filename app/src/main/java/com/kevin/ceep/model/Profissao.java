package com.kevin.ceep.model;
import java.io.Serializable;

public class Profissao implements Serializable {
    private String nome;

    public Profissao(){}

    public Profissao(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
