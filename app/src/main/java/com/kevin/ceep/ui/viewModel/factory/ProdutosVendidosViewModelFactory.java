package com.kevin.ceep.ui.viewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kevin.ceep.repository.ProdutosVendidosRepository;
import com.kevin.ceep.ui.viewModel.ProdutosVendidosViewModel;

public class ProdutosVendidosViewModelFactory implements ViewModelProvider.Factory {
    private ProdutosVendidosRepository repository;

    public ProdutosVendidosViewModelFactory(ProdutosVendidosRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ProdutosVendidosViewModel(repository);
    }
}
