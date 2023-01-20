package com.kevin.ceep.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.kevin.ceep.model.Trabalho;

public class NotaDAO {

    private final static ArrayList<Trabalho> notas = new ArrayList<>();

    public List<Trabalho> todos() {
        return (List<Trabalho>) notas.clone();
    }

    public void insere(Trabalho... notas) {
        NotaDAO.notas.addAll(Arrays.asList(notas));
    }

    public void altera(int posicao, Trabalho nota) {
        notas.set(posicao, nota);
    }

    public void remove(int posicao) {
        notas.remove(posicao);
    }

    public void troca(int posicaoInicio, int posicaoFim) {
        Collections.swap(notas, posicaoInicio, posicaoFim);
    }

    public void removeTodos() {
        notas.clear();
    }
}
