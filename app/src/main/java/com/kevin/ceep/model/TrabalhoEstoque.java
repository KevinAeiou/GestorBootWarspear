package com.kevin.ceep.model;

import java.io.Serializable;

public class TrabalhoEstoque implements Serializable {
    private String id;
    private String nome;
    private Integer quantidade;

    private TrabalhoEstoque(){}
    public TrabalhoEstoque(String id, String nome, Integer quantidade) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getQuantidade() {
        return quantidade;
    }
}
