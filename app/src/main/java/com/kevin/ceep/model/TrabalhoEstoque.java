package com.kevin.ceep.model;

import java.io.Serializable;

public class TrabalhoEstoque extends Trabalho implements Serializable {
    private Integer quantidade;
    private TrabalhoEstoque(){}
    public TrabalhoEstoque(String id,String nomeProducao, String nome, String profissao, String raridade, String trabalhoNecessario, Integer nivel, Integer experiencia, Integer quantidade) {
        super(id,nome, nomeProducao, profissao,raridade, trabalhoNecessario, nivel,experiencia);
        this.quantidade = quantidade;
    }
    public Integer getQuantidade() {
        return quantidade;
    }
}
