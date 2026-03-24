package com.kevin.ceep.ui.viewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kevin.ceep.repository.ProfissaoPersonagemRepository;
import com.kevin.ceep.ui.viewModel.ProfissaoPersonagemViewModel;

public class ProfissaoPersonagemViewModelFactory implements ViewModelProvider.Factory {
    private final String idPersonagem;

    public ProfissaoPersonagemViewModelFactory(String idPersonagem) {
        this.idPersonagem= idPersonagem;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfissaoPersonagemViewModel.class)) {
            return (T) new ProfissaoPersonagemViewModel(
                ProfissaoPersonagemRepository.getInstance(idPersonagem)
            );
        }
        throw new IllegalArgumentException("Classe ViewModel desconhecida");
    }
}
