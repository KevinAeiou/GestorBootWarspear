package com.kevin.ceep.ui.recyclerview.adapter.listener;

import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoEstoque;

public interface OnItemClickListener {

    void onItemClick(Profissao profissao, int posicao);
    void onItemClick(Trabalho trabalho, int adapterPosition);
    void onItemClick(TrabalhoEstoque trabalhoEstoque, int adapterPosition, int botaoId);
}
