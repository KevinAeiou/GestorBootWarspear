package com.kevin.ceep.model;

import java.io.Serializable;

public class Trabalho implements Serializable {

    private String id;
    private String nome;
    private String profissao;
    private String raridade;
    private String trabalhoNecessario;
    private Integer nivel;
    private Integer experiencia;

    public Trabalho(){}

    public Trabalho(String id, String nome, String profissao, String raridade, String trabalhoNecessario, Integer nivel, Integer experiencia) {
        this.id = id;
        this.nome = nome;
        this.profissao = profissao;
        this.raridade = raridade;
        this.trabalhoNecessario = trabalhoNecessario;
        this.nivel = nivel;
        this.experiencia = experiencia;
    }

    public String getNome() {
        return nome;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
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

    public String getTrabalhoNecessario() {
        return trabalhoNecessario;
    }

    public void setTrabalhoNecessario(String trabalhoNecessario) {
        this.trabalhoNecessario = trabalhoNecessario;
    }
}