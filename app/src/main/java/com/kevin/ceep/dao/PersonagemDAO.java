package com.kevin.ceep.dao;

import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.repository.PersonagemRepository;

public class PersonagemDAO {
    private PersonagemRepository personagemRepository;

    public PersonagemDAO(String usuarioID, String personagemID) {
        personagemRepository = new PersonagemRepository(usuarioID, personagemID);
    }

    public void modificaQuantidadeTrabalhoNecessarioNoEstoque(TrabalhoProducao trabalhoProducao) {
        personagemRepository.modificaQuantidadeTrabalhoNecessarioNoEstoque(trabalhoProducao);
    }

    public void modificaQuantidadeTrabalhoNoEstoque(TrabalhoProducao trabalhoConcluido) {
        personagemRepository.modificaTrabalhoNoEstoque(trabalhoConcluido);
    }
}
