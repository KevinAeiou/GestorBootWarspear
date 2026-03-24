package com.kevin.ceep.ui.viewModel.factory;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kevin.ceep.repository.TrabalhoProducaoRepository;
import com.kevin.ceep.ui.viewModel.TrabalhoProducaoViewModel;

public class TrabalhoProducaoViewModelFactory implements ViewModelProvider.Factory {
    private final String idPersonagem;
    private final Context context;

    public TrabalhoProducaoViewModelFactory(Context context, String idPersonagem) {
        this.idPersonagem = idPersonagem;
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TrabalhoProducaoViewModel.class)) {
            return (T) new TrabalhoProducaoViewModel(TrabalhoProducaoRepository.getInstance(context, idPersonagem));
        }
        throw new IllegalArgumentException("Classe ViewModel desconhecida");
    }
}
