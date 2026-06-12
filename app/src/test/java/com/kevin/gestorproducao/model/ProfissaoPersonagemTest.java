package com.kevin.gestorproducao.model;

import static org.junit.Assert.assertEquals;

import org.testng.annotations.Test;

public class ProfissaoPersonagemTest {
    private final ProfissaoPersonagem BRACELETES = new ProfissaoPersonagem();
    private final ProfissaoPersonagem CAPOTES = new ProfissaoPersonagem();
    private final ProfissaoPersonagem ANEIS = new ProfissaoPersonagem();
    private final ProfissaoPersonagem AMULETOS = new ProfissaoPersonagem();

    @Test
    public void deve_RetornarNivelUm_QuandoXpAtualIgualADezenove() {
        int nivel = BRACELETES.getNivel();
        assertEquals(1, nivel);
    }
    @Test
    public void deve_RetornarXpMaximoVinte_QuandoNivelUm() {
        int xpMaximo = BRACELETES.getXpMaximo();
        assertEquals(20, xpMaximo);
    }

    @Test
    public void deve_RetornarDezenove_QuandoXpAtualIgualADezenove() {
        int xpRestante = BRACELETES.getXpRestante(1,1);
        assertEquals(19, xpRestante);
    }
    @Test
    public void deve_RetornarNivelDois_QuandoXpAtualIgualACentoENoventaENove() {
        int nivel = CAPOTES.getNivel();
        assertEquals(2, nivel);
    }
    @Test
    public void deve_RetornarDuzentos_QuandoNivelDois() {
        int xpMaximo = CAPOTES.getXpMaximo();
        assertEquals(200, xpMaximo);
    }

    @Test
    public void deve_RetornarCentoESententaEOito_QuandoXpAtualIgualACentoENoventaENove() {
        int xpRestante = CAPOTES.getXpRestante(2,1);
        assertEquals(178, xpRestante);
    }
    @Test
    public void deve_RetornarNivelVinteESeis_QuandoXpAtualIgualACentoEOitocentoETrintaMilEUm() {
        int nivel = AMULETOS.getNivel();
        assertEquals(26, nivel);
    }
    @Test
    public void deve_RetornarNivelVinteESeis_QuandoXpAtualIgualACentoEOitocentoETrintaMil() {
        int nivel = ANEIS.getNivel();
        assertEquals(26, nivel);
    }
    @Test
    public void deve_RetornarXpMaximoOitocentosETrintaMil_QuandoNivelVinteESeis() {
        int xpMaximo = ANEIS.getXpMaximo();
        assertEquals(830000, xpMaximo);
    }

    @Test
    public void deve_RetornarZero_QuandoXpAtualIgualAOitocentoETrintaMil() {
        int xpRestante = ANEIS.getXpRestante(26,0);
        assertEquals(0, xpRestante);
    }
}