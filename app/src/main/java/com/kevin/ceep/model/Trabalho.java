package com.kevin.ceep.model;

import java.io.Serializable;

public class Trabalho implements Serializable {

    private String id;
    private String nome;
    private String profissao;
    private String raridade;
    private Integer nivel;
    private Integer experiencia;

    public Trabalho(){}

    public Trabalho(String id, String nome, String profissao, String raridade, Integer nivel, Integer experiencia) {
        this.id = id;
        this.nome = nome;
        this.profissao = profissao;
        this.raridade = raridade;
        this.nivel = nivel;
        this.experiencia = experiencia;
    }

    public String getNome() {
        return nome;
    }

    public String getProfissao() {
        return profissao;
    }

    public Integer getNivel() {
        return nivel;
    }

    public String getRaridade() {
        return raridade;
    }

    public String getId() {
        return id;
    }

    public Integer getExperiencia() {
        return experiencia;
    }
}