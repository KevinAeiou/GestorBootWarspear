package com.kevin.ceep.dao;

import java.util.ArrayList;
import java.util.Arrays;

import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.repository.FirebaseDatabaseRepository;

public class TrabalhoDAO {

    private final static ArrayList<Trabalho> trabalhos = new ArrayList<>();
    private static FirebaseDatabaseRepository minhaReferencia;

    public TrabalhoDAO() {
        TrabalhoDAO.minhaReferencia = new FirebaseDatabaseRepository();
    }

    public ArrayList<Trabalho> todos() {
        return minhaReferencia.pegaTodosTrabalho();
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
}
