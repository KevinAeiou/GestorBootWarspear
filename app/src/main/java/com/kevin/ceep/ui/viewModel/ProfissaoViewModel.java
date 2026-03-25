package com.kevin.ceep.ui.viewModel;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kevin.ceep.model.ProfissaoBase;
import com.kevin.ceep.repository.ProfissaoRepository;
import com.kevin.ceep.repository.Resource;
import com.kevin.ceep.ui.viewModel.singleEvent.SingleLiveEvent;

import java.util.ArrayList;

public class ProfissaoViewModel extends ViewModel {
    private final ProfissaoRepository repository;
    private final SingleLiveEvent<Resource<ArrayList<ProfissaoBase>>> recuperacaoProfissoes = new SingleLiveEvent<>();
    private final SingleLiveEvent<Resource<Void>> sincronizacaoResultado = new SingleLiveEvent<>();
    private final SingleLiveEvent<Resource<Void>> modificacaoResultado = new SingleLiveEvent<>();

    public ProfissaoViewModel(ProfissaoRepository repository) {
        this.repository = repository;
    }

    public SingleLiveEvent<Resource<Void>> getSincronizacaoResultado() {
        return sincronizacaoResultado;
    }

    public SingleLiveEvent<Resource<ArrayList<ProfissaoBase>>> getRecuperacaoProfissoes() {
        return recuperacaoProfissoes;
    }

    public SingleLiveEvent<Resource<Void>> getModificacaoProfissao() {
        return modificacaoResultado;
    }

    public void recuperaProfissoes() {
        Observer<? super Resource<ArrayList<ProfissaoBase>>> observer = new Observer<
                Resource<ArrayList<ProfissaoBase>>
                >() {
            @Override
            public void onChanged(Resource<ArrayList<ProfissaoBase>> resultado) {
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

    public void modificaProfissao(ProfissaoBase profissao) {
        Observer<? super Resource<Void>> observer = new Observer<Resource<Void>>() {
            @Override
            public void onChanged(Resource<Void> resultado) {
                modificacaoResultado.setValue(resultado);
                repository.modificaProfissao(profissao).removeObserver(this);
            }
        };

        repository.modificaProfissao(profissao).observeForever(observer);
    }


    public void removeOuvinte() {
        repository.removeOuvinte();
    }

}
