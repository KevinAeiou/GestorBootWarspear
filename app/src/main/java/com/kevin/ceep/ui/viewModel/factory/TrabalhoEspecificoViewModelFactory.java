package com.kevin.ceep.ui.viewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kevin.ceep.repository.TrabalhoRepository;
import com.kevin.ceep.ui.viewModel.TrabalhoEspecificoViewModel;

public class TrabalhoEspecificoViewModelFactory implements ViewModelProvider.Factory {
    private final TrabalhoRepository repository;
    public TrabalhoEspecificoViewModelFactory(TrabalhoRepository repository) {
        this.repository = repository;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TrabalhoEspecificoViewModel(repository);
    }
}
