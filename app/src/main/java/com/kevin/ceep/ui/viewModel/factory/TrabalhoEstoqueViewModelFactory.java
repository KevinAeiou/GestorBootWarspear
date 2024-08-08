package com.kevin.ceep.ui.viewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kevin.ceep.repository.TrabalhoEstoqueRepository;
import com.kevin.ceep.ui.viewModel.TrabalhoEstoqueViewModel;

public class TrabalhoEstoqueViewModelFactory implements ViewModelProvider.Factory {
    private final TrabalhoEstoqueRepository repository;

    public TrabalhoEstoqueViewModelFactory(TrabalhoEstoqueRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TrabalhoEstoqueViewModel(repository);
    }
}
