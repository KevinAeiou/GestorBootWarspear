package com.kevin.ceep.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kevin.ceep.model.ProdutoVendido;
import com.kevin.ceep.repository.ProdutosVendidosRepository;
import com.kevin.ceep.repository.Resource;

import java.util.ArrayList;

public class ProdutosVendidosViewModel extends ViewModel {
    private final ProdutosVendidosRepository repository;

    public ProdutosVendidosViewModel(ProdutosVendidosRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<ArrayList<ProdutoVendido>>> pegaTodosProdutosVendidos() {
        return repository.pegaTodosProdutosVendidos();
    }

    public LiveData<Resource<Void>> deletaProduto(ProdutoVendido trabalhoRemovido) {
        return repository.deletaProduto(trabalhoRemovido);
    }
}
