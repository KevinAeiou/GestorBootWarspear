package com.kevin.ceep.dao;

import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.repository.TrabalhoEstoqueRepository;

public class TrabalhoEstoqueDAO {
    private TrabalhoEstoqueRepository trabalhoEstoqueRepository;

    public TrabalhoEstoqueDAO(String personagemID) {
        trabalhoEstoqueRepository = new TrabalhoEstoqueRepository(personagemID);
    }

    public void modificaQuantidadeTrabalhoNecessarioNoEstoque(TrabalhoProducao trabalhoProducao) {
        trabalhoEstoqueRepository.modificaQuantidadeTrabalhoNecessarioNoEstoque(trabalhoProducao);
    }

    public void modificaQuantidadeTrabalhoNoEstoque(TrabalhoProducao trabalhoConcluido) {
        trabalhoEstoqueRepository.modificaTrabalhoNoEstoque(trabalhoConcluido);
    }
}
