package com.kevin.ceep.model;

import java.io.Serializable;

public class Personagem implements Serializable {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private boolean estado;
    private boolean  uso;
    private int espacoProducao;

    public Personagem(){}

    public Personagem(String id, String nome, String email, String senha, boolean estado, boolean uso, int espacoProducao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.estado = estado;
        this.uso = uso;
        this.espacoProducao = espacoProducao;
    }

    public String getNome() {
        return nome;
    }

    public String getId() {
        return id;
    }

    public boolean getEstado() {
        return estado;
    }

    public String getSenha() {
        return senha;
    }

    public String getEmail() {
        return email;
    }

    public boolean getUso() {
        return uso;
    }

    public int getEspacoProducao() {
        return espacoProducao;
    }
    public void setId(String id) {
        this.id = id;
    }
}
