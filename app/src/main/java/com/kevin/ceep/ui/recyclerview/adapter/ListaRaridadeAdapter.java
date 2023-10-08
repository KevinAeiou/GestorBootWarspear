package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.ceep.R;
import com.kevin.ceep.model.Raridade;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.List;

public class ListaRaridadeAdapter extends RecyclerView.Adapter<ListaRaridadeAdapter.RaridadeViewHolder> {

    private final List<Raridade> raridades;
    private final Context context;
    private OnItemClickListener onItemClickListener;

    public ListaRaridadeAdapter(Context context, List<Raridade> raridade) {
        this.raridades = raridade;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RaridadeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_raridade,parent,false);
        return new ListaRaridadeAdapter.RaridadeViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaRaridadeAdapter.RaridadeViewHolder holder, int position) {
        Raridade raridade = raridades.get(position);
        holder.vincula(raridade);
    }

    @Override
    public int getItemCount() {
        return raridades.size();
    }

    public class RaridadeViewHolder extends RecyclerView.ViewHolder {

        private final TextView nomeRaridade;
        private Raridade raridade;

        public RaridadeViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeRaridade = itemView.findViewById(R.id.itemNomeRaridade);

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(raridade,getAdapterPosition()));
        }

        public void vincula(Raridade raridade) {
            this.raridade = raridade;
            preencheCampo(raridade);
        }

        private void preencheCampo(Raridade raridade) {
            nomeRaridade.setText(raridade.getNome());
            configuraCorRaridade(raridade);
        }

        private void configuraCorRaridade(Raridade raridade) {
            if (raridade.getNome().equals("Raro")||raridade.getNome().equals("Melhorado")){
                nomeRaridade.setTextColor(ContextCompat.getColor(context,R.color.cor_texto_raridade_raro));
            }else if (raridade.getNome().equals("Especial")){
                nomeRaridade.setTextColor(ContextCompat.getColor(context,R.color.cor_texto_raridade_especial));
            }else{
                nomeRaridade.setTextColor(ContextCompat.getColor(context,R.color.cor_texto_raridade_comum));
            }
        }
    }
}
