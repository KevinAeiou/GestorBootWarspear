package com.kevin.ceep.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TrabalhoTest {
    private final Trabalho ANEL_COMUM1 = new Trabalho("1", "Anél de jade bruta", "Anel de jade bruta","Anéis","comum","",10,30);
    private final Trabalho PRODUCAO_RECURSOS = new Trabalho("1", "melhorarlicençacomum", "melhorarlicençacomum","Anéis","raro","",5,30);

    @Test
    public void deve_RetornarFalso_QuandoVerificarSeEhProducaoDeRecursos() {
        boolean verificacao = ANEL_COMUM1.ehProducaoDeRecursos();
        assertEquals(false, verificacao);
    }
    @Test
    public void deve_RetornarVerdadeiro_QuandoVerificarSeEhProducaoDeRecursos() {
        boolean verificacao = PRODUCAO_RECURSOS.ehProducaoDeRecursos();
        assertEquals(false, verificacao);
    }

}