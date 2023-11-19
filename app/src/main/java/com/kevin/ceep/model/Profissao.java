package com.kevin.ceep.model;
import java.io.Serializable;

public class Profissao implements Serializable {
    private String nome;
    private Integer experiencia;
    private boolean prioridade;

    public Profissao(){}

    public Profissao(String nome, Integer experiencia, boolean prioridade) {
        this.nome = nome;
        this.experiencia = experiencia;
        this.prioridade = prioridade;
    }

    public String getNome() {
        return nome;
    }

    public Integer getExperiencia() {
        return experiencia;
    }

    public boolean isPrioridade() {
        return prioridade;
    }
}
