package com.kevin.ceep.model;

import java.io.Serializable;

public class TrabalhoEstoque implements Serializable {
    private String id;
    private String nome;
    private String profissao;
    private String raridade;
    private Integer nivel;
    private Integer quantidade;
    private String trabalhoId;
    private TrabalhoEstoque(){}
    public TrabalhoEstoque(String id, String nome, String profissao, String raridade, Integer nivel, Integer quantidade, String trabalhoId) {
        this.id = id;
        this.nome = nome;
        this.profissao = profissao;
        this.raridade = raridade;
        this.nivel = nivel;
        this.quantidade = quantidade;
        this.trabalhoId = trabalhoId;
    }
    public Integer getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(int novaQuantidade) {
        this.quantidade = novaQuantidade;
    }

    public Integer getNivel() {
        return nivel;
    }

    public String getId() {
        return id;
    }

    public void setId(String novoId) {
        this.id = novoId;
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

    public String getRaridade() {
        return raridade;
    }

    public String getTrabalhoId() {
        return trabalhoId;
    }

    public void setTrabalhoId(String trabalhoId) {
        this.trabalhoId = trabalhoId;
    }
}
