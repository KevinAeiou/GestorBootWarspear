package com.kevin.ceep.dao;

import java.util.ArrayList;
import java.util.Arrays;

import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.repository.TrabalhoRepository;

public class TrabalhoDAO {

    private final static ArrayList<Trabalho> trabalhos = new ArrayList<>();
    private TrabalhoRepository trabalhoRepository;

    public TrabalhoDAO() {
        trabalhoRepository = new TrabalhoRepository();
    }

    public ArrayList<Trabalho> todos() {
        return trabalhoRepository.pegaTodosTrabalho();
    }

    public void insere(Trabalho... notas) {
        TrabalhoDAO.trabalhos.addAll(Arrays.asList(notas));
    }

    public void altera(int posicao, Trabalho nota) {
        trabalhos.set(posicao, nota);
    }

    public void remove(int posicao) {
        trabalhos.remove(posicao);
    }
    public void removeTodos() {
        trabalhos.clear();
    }

    public void modificaTrabalho(Trabalho trabalhoModificado) {
        trabalhoRepository.modificaTrabalho(trabalhoModificado);
    }

    public void salvaNovoTrabalho(Trabalho novoTrabalho) {
        trabalhoRepository.salvaNovoTrabalho(novoTrabalho);
    }

    public void excluiTrabalhoEspecificoServidor(Trabalho trabalhoRecebido) {
        trabalhoRepository.excluiTrabalho(trabalhoRecebido);
    }
}
