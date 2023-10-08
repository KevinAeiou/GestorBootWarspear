package com.kevin.ceep.model;
import java.io.Serializable;

public class Profissao implements Serializable {
    private String nome;
    private Integer experiencia;

    public Profissao(){}

    public Profissao(String nome, Integer experiencia) {
        this.nome = nome;
        this.experiencia = experiencia;
    }

    public String getNome() {
        return nome;
    }

    public Integer getExperiencia() {
        return experiencia;
    }
}
