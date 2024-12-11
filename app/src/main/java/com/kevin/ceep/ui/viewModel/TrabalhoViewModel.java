package com.kevin.ceep.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.repository.Resource;
import com.kevin.ceep.repository.TrabalhoRepository;

import java.util.ArrayList;

public class TrabalhoViewModel extends ViewModel {
    private final TrabalhoRepository trabalhoRepository;

    public TrabalhoViewModel(TrabalhoRepository trabalhoRepository) {
        this.trabalhoRepository = trabalhoRepository;
    }

    public LiveData<Resource<Void>> adicionaTrabalho(Trabalho novoTrabalho) {
        if (novoTrabalho.getId() == null) {
            return trabalhoRepository.adicionaTrabalho(novoTrabalho);
        } else {
            return trabalhoRepository.modificaTrabalho(novoTrabalho);
        }
    }
    public LiveData<Resource<Void>> excluiTrabalhoEspecificoServidor(Trabalho trabalhoRecebido) {
        return trabalhoRepository.removeTrabalho(trabalhoRecebido);
    }
    public LiveData<Resource<ArrayList<Trabalho>>> pegaTodosTrabalhos() {
        return trabalhoRepository.pegaTodosTrabalhos();
    }
    public Trabalho retornaTrabalhoPorChaveNome(ArrayList<Trabalho> trabalhos, TrabalhoProducao trabalhoModificado) {
        return trabalhoRepository.retornaTrabalhoPorId(trabalhos, trabalhoModificado);
    }

    public LiveData<Resource<Void>> sicronizaTrabalhos() {
        return trabalhoRepository.sincronizaTrabalhos();
    }
}
