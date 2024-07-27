package com.kevin.ceep.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.repository.Resource;
import com.kevin.ceep.repository.TrabalhoRepository;

public class TrabalhoEspecificoViewModel extends ViewModel {
    private final TrabalhoRepository trabalhoRepository;
    public TrabalhoEspecificoViewModel(TrabalhoRepository trabalhoRepository) {
        this.trabalhoRepository = trabalhoRepository;
    }

    public LiveData<Resource<Void>> salvaNovoTrabalho(Trabalho novoTrabalho) {
        if (novoTrabalho.getId() == null) {
            return trabalhoRepository.salvaNovoTrabalho(novoTrabalho);
        } else {
            return trabalhoRepository.modificaTrabalho(novoTrabalho);
        }
    }
}
