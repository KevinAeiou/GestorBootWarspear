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

    public LiveData<Resource<Void>> salvaNovoTrabalho(Trabalho novoTrabalho) {
        if (novoTrabalho.getId() == null) {
            return trabalhoRepository.salvaNovoTrabalho(novoTrabalho);
        } else {
            return trabalhoRepository.modificaTrabalho(novoTrabalho);
        }
    }
    public LiveData<Resource<Void>> salvaNovoTrabalhoDb(Trabalho novoTrabalho) {
        if (novoTrabalho.getId() == null) {
            return trabalhoRepository.salvaNovoTrabalhoDb(novoTrabalho);
        } else {
            return trabalhoRepository.modificaTrabalho(novoTrabalho);
        }
    }
    public LiveData<Resource<Void>> excluiTrabalhoEspecificoServidor(Trabalho trabalhoRecebido) {
        return trabalhoRepository.excluiTrabalho(trabalhoRecebido);
    }
    public LiveData<Resource<ArrayList<Trabalho>>> pegaTodosTrabalhos() {
        return trabalhoRepository.pegaTodosTrabalhos();
    }
    public LiveData<Resource<ArrayList<Trabalho>>> pegaTodosTrabalhosDb() {
        return trabalhoRepository.pegaTodosTrabalhosDb();
    }
    public Trabalho retornaTrabalhoPorChaveNome(ArrayList<Trabalho> trabalhos, TrabalhoProducao trabalhoModificado) {
        return trabalhoRepository.retornaTrabalhoPorChaveNome(trabalhos, trabalhoModificado);
    }
}
