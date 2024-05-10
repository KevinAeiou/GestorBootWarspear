package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.List;

public class ListaTrabalhoEspecificoNovaProducaoAdapter extends RecyclerView.Adapter<ListaTrabalhoEspecificoNovaProducaoAdapter.TrabalhoEspecificoNovaProducaoViewHolder> {
    private List<Trabalho> trabalhos;
    private final Context context;
    private OnItemClickListener onItemClickListener;

    public ListaTrabalhoEspecificoNovaProducaoAdapter(Context context, List<Trabalho> trabalhos) {
        this.trabalhos = trabalhos;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setListaFiltrada(List<Trabalho> listaFiltrada) {
        this.trabalhos = listaFiltrada;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public TrabalhoEspecificoNovaProducaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_trabalho_especifico,parent,false);
        return new TrabalhoEspecificoNovaProducaoViewHolder(viewCriada);
    }
    @Override
    public void onBindViewHolder(@NonNull TrabalhoEspecificoNovaProducaoViewHolder holder, int position) {
        Trabalho trabalho = trabalhos.get(position);
        holder.vincula(trabalho);
    }

    @Override
    public int getItemCount() {
        if (trabalhos == null){
            return 0;
        }
        return trabalhos.size();
    }
    public void limpaLista() {
        trabalhos.clear();
        notifyDataSetChanged();
    }
    public class TrabalhoEspecificoNovaProducaoViewHolder extends RecyclerView.ViewHolder {
        private final TextView nomeTrabalhoEspecifico;
        private final TextView profissaoTrabalhoEspecifico;
        private final TextView raridadeTrabalhoEspecifico;
        private final TextView trabalhoNecessarioTrabalhoEspecifico;
        private final TextView experienciaTrabalhoEspecifico;
        private final TextView nivelTrabalhoEspecifico;
        private Trabalho trabalhoEspecifico;

        public TrabalhoEspecificoNovaProducaoViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeTrabalhoEspecifico = itemView.findViewById(R.id.itemNomeTrabaloEspecifico);
            profissaoTrabalhoEspecifico = itemView.findViewById(R.id.itemProfissaoTrabalhoEspecifico);
            nivelTrabalhoEspecifico = itemView.findViewById(R.id.itemNivelTrabaloEspecifico);
            raridadeTrabalhoEspecifico = itemView.findViewById(R.id.itemRaridadeTrabalhoEspecifico);
            trabalhoNecessarioTrabalhoEspecifico = itemView.findViewById(R.id.itemTrabalhoNecessarioTrabalhoEspecifico);
            experienciaTrabalhoEspecifico = itemView.findViewById(R.id.itemExperienciaTrabaloEspecifico);
            itemView.setOnClickListener(v -> {
                onItemClickListener.onItemClick(trabalhoEspecifico, getAdapterPosition());
            });
        }
        public void vincula(Trabalho trabalho){
            this.trabalhoEspecifico = trabalho;
            preencheCampo(trabalho);
        }
        private void preencheCampo(Trabalho trabalho) {
            nomeTrabalhoEspecifico.setText(trabalho.getNome());
            confiuraCorNomeTrabalho(trabalho);
            profissaoTrabalhoEspecifico.setText(trabalho.getProfissao());
            profissaoTrabalhoEspecifico.setTextColor(Color.WHITE);
            nivelTrabalhoEspecifico.setText(String.valueOf(trabalho.getNivel()));
            nivelTrabalhoEspecifico.setTextColor(ContextCompat.getColor(context,R.color.cor_texto_nivel));
            experienciaTrabalhoEspecifico.setText("Exp "+trabalho.getExperiencia());
            raridadeTrabalhoEspecifico.setText(trabalho.getRaridade());
            String trabalhoNecessario = trabalho.getTrabalhoNecessario();
            if (trabalhoNecessario == null || trabalhoNecessario.isEmpty()) {
                trabalhoNecessarioTrabalhoEspecifico.setVisibility(View.GONE);
            } else {
                trabalhoNecessarioTrabalhoEspecifico.setText(trabalho.getTrabalhoNecessario());
            }
        }
        private void confiuraCorNomeTrabalho(Trabalho trabalho) {
            String raridade = trabalho.getRaridade();
            if (raridade.equals("Comum")){
                nomeTrabalhoEspecifico.setTextColor(ContextCompat.getColor(context,R.color.cor_texto_raridade_comum));
            } else if (raridade.equals("Melhorado")) {
                nomeTrabalhoEspecifico.setTextColor(ContextCompat.getColor(context,R.color.cor_texto_raridade_melhorado));
            } else if (raridade.equals("Raro")){
                nomeTrabalhoEspecifico.setTextColor(ContextCompat.getColor(context,R.color.cor_texto_raridade_raro));
            }else if (raridade.equals("Especial")){
                nomeTrabalhoEspecifico.setTextColor(ContextCompat.getColor(context,R.color.cor_texto_raridade_especial));
            }
        }
    }
}
