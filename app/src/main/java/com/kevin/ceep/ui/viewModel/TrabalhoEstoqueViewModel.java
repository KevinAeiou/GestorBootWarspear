package com.kevin.ceep.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kevin.ceep.model.TrabalhoEstoque;
import com.kevin.ceep.repository.Resource;
import com.kevin.ceep.repository.TrabalhoEstoqueRepository;

import java.util.ArrayList;

public class TrabalhoEstoqueViewModel extends ViewModel {
    private final TrabalhoEstoqueRepository repository;

    public TrabalhoEstoqueViewModel(TrabalhoEstoqueRepository repository) {
        this.repository = repository;
    }
    public LiveData<Resource<ArrayList<TrabalhoEstoque>>> pegaTodosTrabalhosEstoque() {
        return repository.pegaTodosTrabalhosEstoque();
    }

    public LiveData<Resource<Void>> modificaQuantidadeTrabalhoEspecificoNoEstoque(TrabalhoEstoque trabalhoEstoqueModificado) {
        return repository.modificaQuantidadeTrabalhoEspecificoNoEstoque(trabalhoEstoqueModificado);
    }

    public TrabalhoEstoque retornaTrabalhoEspecificoEstoque(ArrayList<TrabalhoEstoque> trabalhosEstoque, String nomeTrabalho) {
        return repository.retornaTrabalhoEspecificoEstoque(trabalhosEstoque, nomeTrabalho);
    }

    public LiveData<Resource<Void>> adicionaTrabalhoEstoque(TrabalhoEstoque trabalhoEstoqueEncontrado) {
        return repository.adicionaTrabalhoEstoque(trabalhoEstoqueEncontrado);
    }
    public LiveData<Resource<Void>> deletaTrabalhoEstoque(TrabalhoEstoque trabalhoRemovido) {
        return repository.removeTrabalhoEstoque(trabalhoRemovido);
    }

    public LiveData<Resource<Void>> sincronizaEstoque() {
        return repository.sincronizaEstoque();
    }
}
