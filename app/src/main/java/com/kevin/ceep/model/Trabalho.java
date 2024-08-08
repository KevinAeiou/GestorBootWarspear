package com.kevin.ceep.model;

import static com.kevin.ceep.utilitario.Utilitario.limpaString;

import java.io.Serializable;
import java.util.ArrayList;
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

    public void setId(String novoId) {
        this.id = novoId;
    }

    public Integer getExperiencia() {
        return experiencia;
    }

    public String getTrabalhoNecessario() {
        return trabalhoNecessario;
    }

    public boolean ehProducaoDeRecursos() {
        ArrayList<String> listaProducaoRecursos = new ArrayList<>(List.of(
                "melhorarlicencacomum","licençadeproducaodoaprendiz","grandecolecaoderecursoscomuns",
                "grandecolecaoderecursosavancados","coletaemmassaderecursosavancados","melhoriadaessenciacomum",
                "melhoriadasubstanciacomum","melhoriadocatalizadorcomum","melhoriadaessenciacomposta",
                "melhoriadasubtanciacomposta","melhoriadocatalizadoramplificado","criaresferadoaprendiz",
                "produzindoavarinhademadeira","produzindocabecadocajadodejade","produzindocabecadecajadodeonix",
                "criaresferadoneofito","produzindoavarinhadeaço","extracaodelascas",
                "manipulacaodelascas","fazermodoaprendiz","preparandolascasdequartzo",
                "manipulacaodemineriodecobre","fazermodoprincipiante","adquirirtesouradoaprendiz",
                "produzindofioresistente","fazendotecidodelinho","fazendotecidodecetim",
                "comprartesouradoprincipiante","produzindofiogrosso","adquirirfacadoaprendiz",
                "recebendoescamasdaserpente","concluindocouroresistente","adquirirfacadoprincipiante",
                "recebendoescamasdolagarto","curtindocourogrosso","adquirirmarretaodoaprendiz",
                "forjandoplacasdecobre","fazendoplacasdebronze","adquirirmarretaodoprincipiante",
                "forjandoplacasdeferro","fazendoaneisdeaco","adquirirmoldedoaprendiz",
                "extracaodepepitasdecobre","recebendogemadassombras","adquirirmoldedoprincipiante",
                "extracaodepepitasdeprata","recebendogemadaluz","adquirirpincadoaprendiz",
                "extracaodejadebruta","recebendoenergiainicial","adquirirpinçasdoprincipiante",
                "extracaodeonixextraordinaria","recebendoeterinicial","adquirirfuradordoaprendiz",
                "produzindotecidodelicado","extracaodesubstanciainstável","adquirirfuradordoprincipiante",
                "produzindotecidodenso","extracaodesubstanciaestável","recebendofibradebronze",
                "recebendoprata","recebendoinsigniadeestudante","recebendofibradeplatina",
                "recebendoambar","recebendodistintivodeaprendiz"
        ));
        return listaProducaoRecursos.contains(limpaString(nomeProducao));
    }
}