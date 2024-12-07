package com.kevin.ceep.dao;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.repository.Resource;
import com.kevin.ceep.repository.TrabalhoProducaoRepository;

public class TrabalhoProducaoDAO{
    private final TrabalhoProducaoRepository trabalhoProducaoRepository;

    public TrabalhoProducaoDAO(Context context, String personagemID){
        this.trabalhoProducaoRepository = new TrabalhoProducaoRepository(context, personagemID);
    }

    public LiveData<Resource<Void>> modificaTrabalhoProducaoServidor(TrabalhoProducao trabalhoProducao) {
        return trabalhoProducaoRepository.modificaTrabalhoProducao(trabalhoProducao);
    }
}
