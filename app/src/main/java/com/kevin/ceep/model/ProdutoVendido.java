package com.kevin.ceep.model;

import java.io.Serializable;

public class ProdutoVendido implements Serializable {
    private String id;
    private String nome;
    private String data;
    private String personagem;
    private int quantidade;
    private int valor;
    public ProdutoVendido() {}
    public ProdutoVendido(String id, String nome, String data, String personagem, int quantidade, int valor) {
        this.id = id;
        this.nome = nome;
        this.data = data;
        this.personagem = personagem;
        this.quantidade = quantidade;
        this.valor = valor;
    }
    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getData() {
        return data;
    }

    public String getPersonagem() {
        return personagem;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public int getValor() {
        return valor;
    }


}
