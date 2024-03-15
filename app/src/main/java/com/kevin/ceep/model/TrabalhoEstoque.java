package com.kevin.ceep.model;

import java.io.Serializable;

public class TrabalhoEstoque extends Trabalho implements Serializable {
    private Integer quantidade;

    private TrabalhoEstoque(){}
    public TrabalhoEstoque(String id, String nome, String profissao, String raridade, Integer nivel, Integer experiencia, Integer quantidade) {
        super(id,nome,profissao,raridade,nivel,experiencia);
        this.quantidade = quantidade;
    }
    public Integer getQuantidade() {
        return quantidade;
    }
}
