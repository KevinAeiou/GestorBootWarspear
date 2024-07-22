package com.kevin.ceep.model;

import static com.kevin.ceep.utilitario.Utilitario.comparaString;
import static com.kevin.ceep.utilitario.Utilitario.limpaString;

import android.content.res.Resources;
import android.util.Log;

import com.kevin.ceep.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Trabalho implements Serializable {

    private String id;
    private String nome;
    private String nomeProducao;
    private String profissao;
    private String raridade;
    private String trabalhoNecessario;
    private Integer nivel;
    private Integer experiencia;
    public Trabalho(){}

    public Trabalho(String id, String nome, String nomeProducao, String profissao, String raridade, String trabalhoNecessario, Integer nivel, Integer experiencia) {
        this.id = id;
        this.nome = nome;
        this.nomeProducao = nomeProducao;
        this.profissao = profissao;
        this.raridade = raridade;
        this.trabalhoNecessario = trabalhoNecessario;
        this.nivel = nivel;
        this.experiencia = experiencia;
    }

    public String getNome() {
        return nome;
    }
    public String getNomeProducao() {
        return nomeProducao;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public Integer getNivel() {
        return nivel;
    }

    public String getRaridade() {
        return raridade;
    }

    public String getId() {
        return id;
    }

    public Integer getExperiencia() {
        return experiencia;
    }

    public String getTrabalhoNecessario() {
        return trabalhoNecessario;
    }

    public void setTrabalhoNecessario(String trabalhoNecessario) {
        this.trabalhoNecessario = trabalhoNecessario;
    }

    public boolean ehProducaoDeRecursos() {
        ArrayList<String> listaProducaoRecursos = new ArrayList<>();
        List<String> listaRecursos = recuperaListaRecursos();
        for (String nomeRecurso : listaRecursos) {
            listaProducaoRecursos.add(nomeRecurso);
        }
        return listaProducaoRecursos.contains(limpaString(nomeProducao));
    }

    private List<String> recuperaListaRecursos() {
        return Arrays.asList(Resources.getSystem().getStringArray(R.array.producaoRecursos));
    }
}