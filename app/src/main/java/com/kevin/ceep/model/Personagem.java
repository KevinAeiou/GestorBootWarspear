package com.kevin.ceep.model;

import java.io.Serializable;

public class Personagem implements Serializable {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private Integer estado;
    private Integer uso;

    public Personagem(){}

    public Personagem(String id, String nome, String email, String senha, Integer estado, Integer uso) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.estado = estado;
        this.uso = uso;
    }

    public String getNome() {
        return nome;
    }

    public String getId() {
        return id;
    }

    public Integer getEstado() {
        return estado;
    }

    public String getSenha() {
        return senha;
    }

    public String getEmail() {
        return email;
    }

    public Integer getUso() {
        return uso;
    }
}
