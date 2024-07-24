package com.kevin.ceep.dao;

import androidx.lifecycle.MutableLiveData;

import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.repository.TrabalhoProducaoRepository;

public class TrabalhoProducaoDAO{
    private final TrabalhoProducaoRepository trabalhoProducaoRepository;

    public TrabalhoProducaoDAO(String personagemID){
        this.trabalhoProducaoRepository = new TrabalhoProducaoRepository(personagemID);
    }

    public MutableLiveData<Boolean> modificaTrabalhoProducaoServidor(TrabalhoProducao trabalhoProducao) {
        return trabalhoProducaoRepository.modificaTrabalhoProducaoServidor(trabalhoProducao);
    }
}
