package com.kevin.ceep.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.repository.Resource;
import com.kevin.ceep.repository.TrabalhoProducaoRepository;

import java.util.ArrayList;

public class TrabalhoProducaoViewModel extends ViewModel {
    private final TrabalhoProducaoRepository repository;

    public TrabalhoProducaoViewModel(TrabalhoProducaoRepository repository) {
        this.repository = repository;
    }
    public LiveData<Resource<Void>> modificaTrabalhoProducaoServidor(TrabalhoProducao trabalhoModificado) {
        return repository.modificaTrabalhoProducaoServidor(trabalhoModificado);
    }
    public LiveData<Resource<Void>> salvaNovoTrabalhoProducao(TrabalhoProducao novoTrabalho) {
        return repository.salvaNovoTrabalhoProducao(novoTrabalho);
    }
    public LiveData<Resource<Void>> deletaTrabalhoProducao(TrabalhoProducao trabalhoDeletado) {
        return repository.deletaTrabalhoProducao(trabalhoDeletado);
    }

    public LiveData<Resource<ArrayList<TrabalhoProducao>>> pegaTodosTrabalhosProducao() {
        return repository.pegaTodosTrabalhosProducao();
    }
}
