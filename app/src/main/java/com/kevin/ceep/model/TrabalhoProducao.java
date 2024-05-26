package com.kevin.ceep.model;

import java.io.Serializable;

public class TrabalhoProducao extends Trabalho implements Serializable {
    private String tipo_licenca;
    private Integer estado;
    private Boolean recorrencia;

    public TrabalhoProducao(){}
    public TrabalhoProducao(String id,String nome,String nomeProducao,String profissao,String raridade, String trabalhoNecessario, Integer nivel,Integer experiencia,String tipoLicenca, Integer estado,Boolean recorrencia) {
        super(id,nome, nomeProducao,profissao,raridade, trabalhoNecessario, nivel,experiencia);
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

}
