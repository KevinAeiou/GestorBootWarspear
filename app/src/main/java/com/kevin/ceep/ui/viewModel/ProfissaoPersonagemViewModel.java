package com.kevin.ceep.ui.viewModel;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.repository.ProfissaoPersonagemRepository;
import com.kevin.ceep.repository.Resource;
import com.kevin.ceep.ui.viewModel.singleEvent.SingleLiveEvent;

import java.util.ArrayList;

public class ProfissaoPersonagemViewModel extends ViewModel {
    private final ProfissaoPersonagemRepository repository;
    private final SingleLiveEvent<Resource<Void>> insercaoResultado = new SingleLiveEvent<>();
    private final SingleLiveEvent<Resource<Void>> modificacaoResultado = new SingleLiveEvent<>();
    private final SingleLiveEvent<Resource<ArrayList<Profissao>>> recuperacaoProfissoesPersonagem = new SingleLiveEvent<>();
    public ProfissaoPersonagemViewModel(ProfissaoPersonagemRepository repository) {
        this.repository = repository;
    }

    public SingleLiveEvent<Resource<Void>> getInsercaoResultado() {
        return insercaoResultado;
    }

    public SingleLiveEvent<Resource<Void>> getModificacaoResultado() {
        return modificacaoResultado;
    }
    public SingleLiveEvent<Resource<ArrayList<Profissao>>> getRecuperacaoProfissoesPersonagem() {
        return recuperacaoProfissoesPersonagem;
    }
    public Profissao retornaProfissaoModificada(
            ArrayList<Profissao> profissoes,
            TrabalhoProducao trabalhoModificado
    ) {
        return repository.retornaProfissaoModificada(profissoes,trabalhoModificado);
    }

    public void recuperaProfissoesPersonagem() {
        Observer<? super Resource<ArrayList<Profissao>>> observer = new Observer<
            Resource<ArrayList<Profissao>>
        >() {
            @Override
            public void onChanged(Resource<ArrayList<Profissao>> resultado) {
                recuperacaoProfissoesPersonagem.setValue(resultado);
                repository.recuperaProfissoesPersonagem().removeObserver(this);
            }
        };
        repository.recuperaProfissoesPersonagem().observeForever(observer);
    }

    public void modificaExperienciaProfissao(
            Profissao profissao
    ) {
        Observer<? super Resource<Void>> observer = new Observer<Resource<Void>>() {
            @Override
            public void onChanged(Resource<Void> resultado) {
                modificacaoResultado.setValue(resultado);
                repository.modificaProfissaoPersonagem(profissao).removeObserver(this);
            }
        };
        repository.modificaProfissaoPersonagem(profissao).observeForever(observer);
    }

    public void insereProfissoes() {
        Observer<? super Resource<Void>> observer = new Observer<Resource<Void>>() {
            @Override
            public void onChanged(Resource<Void> resultado) {
                insercaoResultado.setValue(resultado);
                repository.insereNovasProfissoes().removeObserver(this);
            }
        };
        repository.insereNovasProfissoes().observeForever(observer);
    }

    public void removeOuvinte() {
        repository.removeOuvinte();
    }
}
