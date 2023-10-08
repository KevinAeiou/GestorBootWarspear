package com.kevin.ceep.model;

import java.io.Serializable;

public class Trabalho implements Serializable {

    private String id;
    private String nome;
    private String profissao;
    private String tipo_licenca;
    private String raridade;
    private Integer estado;
    private Integer nivel;
    private Integer experiencia;
    private boolean recorrencia;

    public Trabalho(){}

    public Trabalho(String id, String nome, String profissao, String tipo_licenca, String raridade, Integer estado, Integer nivel, Integer experiencia, boolean recorrencia) {
        this.id = id;
        this.nome = nome;
        this.profissao = profissao;
        this.tipo_licenca = tipo_licenca;
        this.raridade = raridade;
        this.estado = estado;
        this.nivel = nivel;
        this.experiencia = experiencia;
        this.recorrencia = recorrencia;
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

    public String getTipo_licenca() {
        return tipo_licenca;
    }

    public String getRaridade() {
        return raridade;
    }

    public String getId() {
        return id;
    }

    public Integer getEstado() {
        return estado;
    }

    public boolean isRecorrencia() {
        return recorrencia;
    }

    public Integer getExperiencia() {
        return experiencia;
    }
}