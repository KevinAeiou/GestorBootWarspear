package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kevin.ceep.R;
import com.kevin.ceep.model.TrabalhoEstoque;
import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.List;

public class ListaTrabalhoEstoqueAdapter extends RecyclerView.Adapter<ListaTrabalhoEstoqueAdapter.TrabalhoEstoqueViewHolder> {

    private List<TrabalhoEstoque> trabalhosEstoque;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ListaTrabalhoEstoqueAdapter(List<TrabalhoEstoque> trabalhosEstoque, Context context) {
        this.trabalhosEstoque = trabalhosEstoque;
        this.context = context;
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
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

    public void altera(int posicao, TrabalhoEstoque trabalhoEstoque){
        trabalhosEstoque.set(posicao,trabalhoEstoque);
        notifyDataSetChanged();
    }
    public class TrabalhoEstoqueViewHolder extends RecyclerView.ViewHolder{
        private final MaterialCardView cardTrabalho;
        private final TextView nomeTrabalho;
        private final TextView profissaoTrabalho;
        private final TextView nivelTrabalho;
        private final EditText quantidadeTrabalho;
        private TrabalhoEstoque trabalhoEstoque;
        private ImageButton botaoMenosUm;
        private ImageButton botaoMaisUm;


        public TrabalhoEstoqueViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTrabalho = itemView.findViewById(R.id.itemCardTrabalhoEstoque);
            nomeTrabalho = itemView.findViewById(R.id.itemNomeTrabalhoEstoque);
            profissaoTrabalho = itemView.findViewById(R.id.itemProfissaoTrabalhoEstoque);
            nivelTrabalho = itemView.findViewById(R.id.itemNivelTrabalhoEstoque);
            quantidadeTrabalho = itemView.findViewById(R.id.itemEdtQuantidadeTrabalhoEstoque);
            botaoMenosUm = itemView.findViewById(R.id.itemBotaoMenosUm);
            botaoMaisUm = itemView.findViewById(R.id.itemBotaoMaisUm);
            botaoMenosUm.setOnClickListener(v -> onItemClickListener.onItemClick(trabalhoEstoque,getAdapterPosition(), R.id.itemBotaoMenosUm));
            botaoMaisUm.setOnClickListener(v -> onItemClickListener.onItemClick(trabalhoEstoque,getAdapterPosition(), R.id.itemBotaoMaisUm));

        }
        public void vincula(TrabalhoEstoque trabalhoEstoque){
            this.trabalhoEstoque = trabalhoEstoque;
            preencheCampos(trabalhoEstoque);
        }
        private void preencheCampos(TrabalhoEstoque trabalhoEstoque) {
                nomeTrabalho.setText(trabalhoEstoque.getNome());
                profissaoTrabalho.setText(trabalhoEstoque.getProfissao());
                nivelTrabalho.setText("Nível "+trabalhoEstoque.getNivel());
                quantidadeTrabalho.setText(String.valueOf(trabalhoEstoque.getQuantidade()));
        }
    }

}
