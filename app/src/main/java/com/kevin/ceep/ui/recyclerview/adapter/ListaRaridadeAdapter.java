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
import com.kevin.ceep.model.Raridade;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.List;

public class ListaRaridadeAdapter extends RecyclerView.Adapter<ListaRaridadeAdapter.RaridadeViewHolder> {

    private List<Raridade> raridades;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ListaRaridadeAdapter(Context context,List<Raridade> raridades) {
        this.raridades = raridades;
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
        return new RaridadeViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull RaridadeViewHolder holder, int position) {
        Raridade raridade = raridades.get(position);
        holder.vincula(raridade);
    }

    @Override
    public int getItemCount() {
        return raridades.size();
    }

    public class RaridadeViewHolder extends RecyclerView.ViewHolder{

        private final TextView nome_raridade;
        private Raridade raridade;

        public RaridadeViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_raridade = itemView.findViewById(R.id.itemNomeRaridade);

            itemView.setOnClickListener(view -> onItemClickListener.onItemClick(raridade, getAdapterPosition()));
        }

        public void vincula(Raridade raridade){
            this.raridade = raridade;
            preencheCampo(raridade);
        }

        private void preencheCampo(Raridade raridade) {
            configuraCorRaridade(raridade);
            nome_raridade.setText(raridade.getNome());
        }

        private void configuraCorRaridade(Raridade raridade) {
            if (raridade.getNome().equals("Comum")){
                nome_raridade.setTextColor(Color.parseColor("#FCF5EF"));
            }else if (raridade.getNome().equals("Raro")){
                nome_raridade.setTextColor(Color.parseColor("#ff66ff"));
            }else if (raridade.getNome().equals("Especial")){
                nome_raridade.setTextColor(Color.parseColor("#ff6666"));
            }else{
                nome_raridade.setTextColor(Color.parseColor("#FCF5EF"));
            }
        }
    }
}
