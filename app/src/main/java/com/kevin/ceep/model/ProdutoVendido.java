package com.kevin.ceep.model;

import java.io.Serializable;
import java.util.Date;

public class ProdutoVendido implements Serializable {
    private String id;
    private String nomeProduto;
    private String dataVenda;
    private String nomePersonagem;
    private int quantidadeProduto;
    private int valorProduto;
    public ProdutoVendido() {}
    public ProdutoVendido(String id, String nomeProduto, String dataVenda, String nomePersonagem, int quantidadeProduto, int valorProduto) {
        this.id = id;
        this.nomeProduto = nomeProduto;
        this.dataVenda = dataVenda;
        this.nomePersonagem = nomePersonagem;
        this.quantidadeProduto = quantidadeProduto;
        this.valorProduto = valorProduto;
    }
    public String getId() {
        return id;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public String getDataVenda() {
        return dataVenda;
    }

    public String getNomePersonagem() {
        return nomePersonagem;
    }

    public int getQuantidadeProduto() {
        return quantidadeProduto;
    }

    public int getValorProduto() {
        return valorProduto;
    }


}
