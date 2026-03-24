package com.kevin.ceep.ui.viewModel;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.repository.ProfissaoRepository;
import com.kevin.ceep.repository.Resource;
import com.kevin.ceep.ui.viewModel.singleEvent.SingleLiveEvent;

import java.util.ArrayList;

public class ProfissaoViewModel extends ViewModel {
    private final ProfissaoRepository repository;
    private final SingleLiveEvent<Resource<ArrayList<Profissao>>> recuperacaoProfissoes = new SingleLiveEvent<>();
    private final SingleLiveEvent<Resource<Void>> sincronizacaoResultado = new SingleLiveEvent<>();


    public ProfissaoViewModel(ProfissaoRepository repository) {
        this.repository = repository;
    }

    public SingleLiveEvent<Resource<Void>> getSincronizacaoResultado() {
        return sincronizacaoResultado;
    }

    public SingleLiveEvent<Resource<ArrayList<Profissao>>> getRecuperacaoProfissoes() {
        return recuperacaoProfissoes;
    }

    public void recuperaProfissoes() {
        Observer<? super Resource<ArrayList<Profissao>>> observer = new Observer<
                Resource<ArrayList<Profissao>>
                >() {
            @Override
            public void onChanged(Resource<ArrayList<Profissao>> resultado) {
                recuperacaoProfissoes.setValue(resultado);
                repository.recuperaProfissoes().removeObserver(this);
            }
        };

        repository.recuperaProfissoes().observeForever(observer);
    }

    public void sincronizaProfissoes() {
        Observer<? super Resource<Void>> observer = new Observer<Resource<Void>>() {

            @Override
            public void onChanged(Resource<Void> resultado) {
                sincronizacaoResultado.setValue(resultado);
                repository.sincronizaProfissoes().removeObserver(this);
            }
        };

        repository.sincronizaProfissoes().observeForever(observer);
    }

    public void removeOuvinte() {
        repository.removeOuvinte();
    }

}
