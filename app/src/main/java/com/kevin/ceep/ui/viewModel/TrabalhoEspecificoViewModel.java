package com.kevin.ceep.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.repository.Resource;
import com.kevin.ceep.repository.TrabalhoEstoqueRepository;
import com.kevin.ceep.repository.TrabalhoProducaoRepository;
import com.kevin.ceep.repository.TrabalhoRepository;

public class TrabalhoEspecificoViewModel extends ViewModel {
    private final TrabalhoRepository trabalhoRepository;
    private final TrabalhoProducaoRepository trabalhoProducaoRepository;
    private final TrabalhoEstoqueRepository trabalhoEstoqueRepository;

    public TrabalhoEspecificoViewModel(TrabalhoRepository trabalhoRepository, TrabalhoProducaoRepository trabalhoProducaoRepository, TrabalhoEstoqueRepository trabalhoEstoqueRepository) {
        this.trabalhoRepository = trabalhoRepository;
        this.trabalhoProducaoRepository = trabalhoProducaoRepository;
        this.trabalhoEstoqueRepository = trabalhoEstoqueRepository;
    }

    public LiveData<Resource<Void>> salvaNovoTrabalho(Trabalho novoTrabalho) {
        if (novoTrabalho.getId() == null) {
            return trabalhoRepository.salvaNovoTrabalho(novoTrabalho);
        } else {
            return trabalhoRepository.modificaTrabalho(novoTrabalho);
        }
    }

    public LiveData<Resource<Void>> excluiTrabalhoEspecificoServidor(Trabalho trabalhoRecebido) {
        return trabalhoRepository.excluiTrabalho(trabalhoRecebido);
    }

    public LiveData<Resource<Void>> modificaTrabalhoProducaoServidor(TrabalhoProducao trabalhoModificado) {
        return trabalhoProducaoRepository.modificaTrabalhoProducaoServidor(trabalhoModificado);
    }

    public LiveData<Resource<Void>> modificaQuantidadeTrabalhoNecessarioNoEstoque(TrabalhoProducao trabalhoModificado) {
        return trabalhoEstoqueRepository.modificaQuantidadeTrabalhoNecessarioNoEstoque(trabalhoModificado);
    }
}
