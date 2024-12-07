package com.kevin.ceep.model;

import static com.kevin.ceep.utilitario.Utilitario.geraIdAleatorio;

import java.io.Serializable;

public class TrabalhoProducao extends Trabalho implements Serializable {
    private String id;
    private String idTrabalho;
    private String idPersonagem;
    private String licenca;
    private Integer estado;
    private Boolean recorrencia;

    public TrabalhoProducao() {
        super();
        this.id = geraIdAleatorio();
    }
    public Boolean getRecorrencia() {
        return recorrencia;
    }

    public Integer getEstado() {
        return estado;
    }

    public String getLicenca() {
        return licenca;
    }

    public void setLicenca(String licenca) {
        this.licenca = licenca;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public void setRecorrencia(Boolean recorrencia) {
        this.recorrencia = recorrencia;
    }

    public String getIdTrabalho() {
        return idTrabalho;
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getIdPersonagem() {
        return idPersonagem;
    }

    public void setIdPersonagem(String idPersonagem) {
        this.idPersonagem = idPersonagem;
    }

    public void setIdTrabalho(String idTrabalho) {
        this.idTrabalho = idTrabalho;
    }
}
