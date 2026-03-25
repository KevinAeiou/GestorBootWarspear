package com.kevin.ceep.model;

public class ProfissaoBase {
    private String id;
    private String nome;

    public ProfissaoBase(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public ProfissaoBase() {}

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}