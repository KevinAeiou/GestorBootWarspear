package com.kevin.ceep.model;

import java.io.Serializable;

public class TrabalhoProducao extends Trabalho implements Serializable {
    private String tipo_licenca;
    private Integer estado;
    private Boolean recorrencia;

    public TrabalhoProducao(){}
    public TrabalhoProducao(String tipoLicenca, Integer estado,Boolean recorrencia) {
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
}
