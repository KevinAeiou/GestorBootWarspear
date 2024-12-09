package com.kevin.ceep.model;

import java.io.Serializable;

public class TrabalhoEstoque extends Trabalho implements Serializable {
    private Integer quantidade;
    private String trabalhoId;
    private TrabalhoEstoque(){}
    public TrabalhoEstoque(Integer quantidade, String trabalhoId) {
        super();
        this.quantidade = quantidade;
        this.trabalhoId = trabalhoId;
    }
    public Integer getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(int novaQuantidade) {
        this.quantidade = novaQuantidade;
    }
    public String getTrabalhoId() {
        return trabalhoId;
    }
    public void setTrabalhoId(String trabalhoId) {
        this.trabalhoId = trabalhoId;
    }
}
