package com.kevin.ceep.ui.viewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kevin.ceep.repository.PersonagemRepository;
import com.kevin.ceep.ui.viewModel.PersonagemViewModel;

public class PersonagemViewModelFactory implements ViewModelProvider.Factory {
    private final PersonagemRepository repository;

    public PersonagemViewModelFactory(PersonagemRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PersonagemViewModel(repository);
    }
}
