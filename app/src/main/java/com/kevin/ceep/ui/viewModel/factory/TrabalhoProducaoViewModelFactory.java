package com.kevin.ceep.ui.viewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kevin.ceep.repository.TrabalhoProducaoRepository;
import com.kevin.ceep.ui.viewModel.TrabalhoProducaoViewModel;

public class TrabalhoProducaoViewModelFactory implements ViewModelProvider.Factory {
    private final TrabalhoProducaoRepository repository;

    public TrabalhoProducaoViewModelFactory(TrabalhoProducaoRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TrabalhoProducaoViewModel(repository);
    }
}
