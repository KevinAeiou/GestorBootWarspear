package com.kevin.ceep.model;

import java.io.Serializable;

public class TrabalhoProducao extends Trabalho implements Serializable {
    private String id;
    private String trabalhoId;
    private String tipo_licenca;
    private Integer estado;
    private Boolean recorrencia;

    public TrabalhoProducao(){}
    public TrabalhoProducao(String id, String trabalhoId, String tipoLicenca, Integer estado, Boolean recorrencia) {
        this.id = id;
        this.trabalhoId = trabalhoId;
        this.tipo_licenca = tipoLicenca;
        this.estado = estado;
        this.recorrencia = recorrencia;
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

    public String getTrabalhoId() {
        return trabalhoId;
    }

    @Override
    public String getId() {
        return id;
    }
}
