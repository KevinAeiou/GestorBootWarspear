package com.kevin.ceep.dao;

import android.util.Log;

import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.repository.TrabalhoProducaoRepository;

import java.util.ArrayList;
public class TrabalhoProducaoDAO {
    private TrabalhoProducaoRepository trabalhoProducaoRepository;
    private final String personagemID;
    private final ArrayList<TrabalhoProducao> trabalhosProducao = new ArrayList<>();

    public TrabalhoProducaoDAO(String personagemID) {
        this.personagemID = personagemID;
        trabalhoProducaoRepository = new TrabalhoProducaoRepository(personagemID);
    }

    public boolean modificaTrabalhoProducaoServidor(TrabalhoProducao trabalhoModificado) {
        Log.d("segundoPlano", "TrabalhoProducaoDAO: trabalhoProducaoDAO");
        return trabalhoProducaoRepository.modificaTrabalhoProducaoServidor(trabalhoModificado);
    }
}
