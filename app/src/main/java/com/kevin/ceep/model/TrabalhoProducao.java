package com.kevin.ceep.model;

import static com.kevin.ceep.utilitario.Utilitario.geraIdAleatorio;

import java.io.Serializable;

public class TrabalhoProducao extends Trabalho implements Serializable {
    private String idTrabalho;
    private String tipo_licenca;
    private Integer estado;
    private Boolean recorrencia;

    public TrabalhoProducao() {
        super();
        super.setId(geraIdAleatorio());
    }
    public Boolean getRecorrencia() {
        return recorrencia;
    }

    public Integer getEstado() {
        return estado;
    }

    public String getTipo_licenca() {
        return tipo_licenca;
    }

    public void setTipo_licenca(String tipo_licenca) {
        this.tipo_licenca = tipo_licenca;
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

    public void setIdTrabalho(String idTrabalho) {
        this.idTrabalho = idTrabalho;
    }
}
