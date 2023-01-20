package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.ceep.R;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.List;

public class ListaProfissaoAdapter extends RecyclerView.Adapter<ListaProfissaoAdapter.ProfissaoViewHolder> {

    private List<Profissao> profissoes;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ListaProfissaoAdapter(Context context, List<Profissao> profissao) {
        this.profissoes = profissao;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ProfissaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_profissao,parent,false);
        return new ProfissaoViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfissaoViewHolder holder, int position) {
        Profissao profissao = profissoes.get(position);
        holder.vincula(profissao);
    }

    @Override
    public int getItemCount() {
        return profissoes.size();
    }

    public class ProfissaoViewHolder extends RecyclerView.ViewHolder {

        private final TextView nome_profissao;
        private Profissao profissao;

        public ProfissaoViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_profissao = itemView.findViewById(R.id.itemNomeProfissao);

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(profissao,getAdapterPosition()));
        }

        public void vincula(Profissao profissao) {
            this.profissao = profissao;
            preencheCampo(profissao);
        }

        private void preencheCampo(Profissao profissao) {
            nome_profissao.setText(profissao.getNome());
            nome_profissao.setTextColor(Color.WHITE);
        }
    }

    public void adiciona(Profissao profissao){
        profissoes.add(profissao);
        notifyDataSetChanged();
    }
}
