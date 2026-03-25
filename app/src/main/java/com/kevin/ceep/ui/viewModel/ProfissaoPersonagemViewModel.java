package com.kevin.ceep.ui.viewModel;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.kevin.ceep.model.ProfissaoPersonagem;
import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.repository.ProfissaoPersonagemRepository;
import com.kevin.ceep.repository.Resource;
import com.kevin.ceep.ui.viewModel.singleEvent.SingleLiveEvent;

import java.util.ArrayList;

public class ProfissaoPersonagemViewModel extends ViewModel {
    private final ProfissaoPersonagemRepository repository;
    private final SingleLiveEvent<Resource<Void>> insercaoResultado = new SingleLiveEvent<>();
    private final SingleLiveEvent<Resource<Void>> modificacaoResultado = new SingleLiveEvent<>();
    private final SingleLiveEvent<Resource<ArrayList<ProfissaoPersonagem>>> recuperacaoProfissoesPersonagem = new SingleLiveEvent<>();
    public ProfissaoPersonagemViewModel(ProfissaoPersonagemRepository repository) {
        this.repository = repository;
    }

    public SingleLiveEvent<Resource<Void>> getInsercaoResultado() {
        return insercaoResultado;
    }

    public SingleLiveEvent<Resource<Void>> getModificacaoResultado() {
        return modificacaoResultado;
    }
    public SingleLiveEvent<Resource<ArrayList<ProfissaoPersonagem>>> getRecuperacaoProfissoesPersonagem() {
        return recuperacaoProfissoesPersonagem;
    }
    public ProfissaoPersonagem retornaProfissaoModificada(
            ArrayList<ProfissaoPersonagem> profissoes,
            TrabalhoProducao trabalhoModificado
    ) {
        return repository.retornaProfissaoModificada(profissoes,trabalhoModificado);
    }

    public void recuperaProfissoesPersonagem() {
        Observer<? super Resource<ArrayList<ProfissaoPersonagem>>> observer = new Observer<
            Resource<ArrayList<ProfissaoPersonagem>>
        >() {
            @Override
            public void onChanged(Resource<ArrayList<ProfissaoPersonagem>> resultado) {
                recuperacaoProfissoesPersonagem.setValue(resultado);
                repository.recuperaProfissoesPersonagem().removeObserver(this);
            }
        };
        repository.recuperaProfissoesPersonagem().observeForever(observer);
    }

    public void modificaExperienciaProfissao(
            ProfissaoPersonagem profissaoPersonagem
    ) {
        Observer<? super Resource<Void>> observer = new Observer<Resource<Void>>() {
            @Override
            public void onChanged(Resource<Void> resultado) {
                modificacaoResultado.setValue(resultado);
                repository.modificaProfissaoPersonagem(profissaoPersonagem).removeObserver(this);
            }
        };
        repository.modificaProfissaoPersonagem(profissaoPersonagem).observeForever(observer);
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
