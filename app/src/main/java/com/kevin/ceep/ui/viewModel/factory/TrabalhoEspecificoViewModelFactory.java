package com.kevin.ceep.ui.viewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kevin.ceep.repository.TrabalhoEstoqueRepository;
import com.kevin.ceep.repository.TrabalhoProducaoRepository;
import com.kevin.ceep.repository.TrabalhoRepository;
import com.kevin.ceep.ui.viewModel.TrabalhoEspecificoViewModel;

public class TrabalhoEspecificoViewModelFactory implements ViewModelProvider.Factory {
    private final TrabalhoRepository repository;
    private final TrabalhoProducaoRepository trabalhoProducaoRepository;
    private TrabalhoEstoqueRepository trabalhoEstoqueRepository;

    public TrabalhoEspecificoViewModelFactory(TrabalhoRepository repository, TrabalhoProducaoRepository trabalhoProducaoRepository, TrabalhoEstoqueRepository trabalhoEstoqueRepository) {
        this.repository = repository;
        this.trabalhoProducaoRepository = trabalhoProducaoRepository;
        this.trabalhoEstoqueRepository = trabalhoEstoqueRepository;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TrabalhoEspecificoViewModel(repository, trabalhoProducaoRepository, trabalhoEstoqueRepository);
    }
}
