package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kevin.ceep.R;
import com.kevin.ceep.model.TrabalhoEstoque;

import java.util.List;

public class ListaTrabalhoEstoqueAdapter extends RecyclerView.Adapter<ListaTrabalhoEstoqueAdapter.TrabalhoEstoqueViewHolder> {

    private List<TrabalhoEstoque> trabalhosEstoque;
    private Context context;

    public ListaTrabalhoEstoqueAdapter(List<TrabalhoEstoque> trabalhosEstoque, Context context) {
        this.trabalhosEstoque = trabalhosEstoque;
        this.context = context;
    }

    @NonNull
    @Override
    public TrabalhoEstoqueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_trabalho_estoque, parent, false);
        return new TrabalhoEstoqueViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull TrabalhoEstoqueViewHolder holder, int position) {
        TrabalhoEstoque trabalhoEstoque = trabalhosEstoque.get(position);
        holder.vincula(trabalhoEstoque);
    }

    @Override
    public int getItemCount() {
        return trabalhosEstoque.size();
    }

    public class TrabalhoEstoqueViewHolder extends RecyclerView.ViewHolder{
        private final MaterialCardView cardTrabalho;
        private final TextView nomeTrabalho;
        private final EditText quantidadeTrabalho;
        private TrabalhoEstoque trabalhoEstoque;


        public TrabalhoEstoqueViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTrabalho = itemView.findViewById(R.id.itemCardTrabalhoEstoque);
            nomeTrabalho = itemView.findViewById(R.id.itemNomeTrabalhoEstoque);
            quantidadeTrabalho = itemView.findViewById(R.id.itemQuantidadeTrabalhoEstoque);
            quantidadeTrabalho.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        public void vincula(TrabalhoEstoque trabalhoEstoque){
            this.trabalhoEstoque = trabalhoEstoque;
            preencheCampos(trabalhoEstoque);
        }
        private void preencheCampos(TrabalhoEstoque trabalhoEstoque) {
                nomeTrabalho.setText(trabalhoEstoque.getNome());
                quantidadeTrabalho.setText(String.valueOf(trabalhoEstoque.getQuantidade()));
        }
    }

}
