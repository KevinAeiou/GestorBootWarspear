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

public class ListaTrabalhoEspecificoAdapter extends RecyclerView.Adapter<ListaTrabalhoEspecificoAdapter.TrabalhoEspecificoViewHolder> {

    private List<Trabalho> trabalhos;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ListaTrabalhoEspecificoAdapter(Context context,List<Trabalho> trabalho) {
        this.trabalhos = trabalho;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setListaFiltrada(List<Trabalho> listaFiltrada){
        this.trabalhos=listaFiltrada;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrabalhoEspecificoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_trabalho_especifico,parent,false);
        return new TrabalhoEspecificoViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull TrabalhoEspecificoViewHolder holder, int position) {
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

    public void remove(int posicao){
        if (posicao<0 || posicao>=trabalhos.size()){
            return;
        }
        trabalhos.remove(posicao);
        notifyItemRemoved(posicao);
        notifyItemRangeChanged(posicao,trabalhos.size());
        notifyDataSetChanged();
    }

    public class TrabalhoEspecificoViewHolder extends RecyclerView.ViewHolder {

        private final TextView nome_trabalho_especifico;
        private final TextView profissao_trabalho_especifico;
        private final TextView nivel_trabalho_especifico;
        private final TextView experienciaTrabalhoEspecifico;
        private final TextView raridadeTrabalhoEspecifico;
        private Trabalho trabalho;

        public TrabalhoEspecificoViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_trabalho_especifico = itemView.findViewById(R.id.itemNomeTrabaloEspecifico);
            profissao_trabalho_especifico = itemView.findViewById(R.id.itemProfissaoTrabalhoEspecifico);
            nivel_trabalho_especifico = itemView.findViewById(R.id.itemNivelTrabaloEspecifico);
            experienciaTrabalhoEspecifico = itemView.findViewById(R.id.itemExperienciaTrabaloEspecifico);
            raridadeTrabalhoEspecifico = itemView.findViewById(R.id.itemRaridadeTrabalhoEspecifico);
            itemView.setOnClickListener(view -> {
                onItemClickListener.onItemClick(trabalho,getAdapterPosition());
            });
        }
        public void vincula(Trabalho trabalho){
            this.trabalho = trabalho;
            preencheCampo(trabalho);
        }

        private void preencheCampo(Trabalho trabalho) {
            nome_trabalho_especifico.setText(trabalho.getNome());
            confiuraCorNomeTrabalo(trabalho);
            profissao_trabalho_especifico.setText(trabalho.getProfissao());
            profissao_trabalho_especifico.setTextColor(Color.WHITE);
            nivel_trabalho_especifico.setText(String.valueOf(trabalho.getNivel()));
            nivel_trabalho_especifico.setTextColor(ContextCompat.getColor(context,R.color.cor_texto_nivel));
            experienciaTrabalhoEspecifico.setText("Exp "+trabalho.getExperiencia());
            raridadeTrabalhoEspecifico.setText(trabalho.getRaridade());
        }

        private void confiuraCorNomeTrabalo(Trabalho trabalho) {
            String raridade = trabalho.getRaridade();
            if (raridade.equals("Comum")){
                nome_trabalho_especifico.setTextColor(ContextCompat.getColor(context,R.color.cor_texto_raridade_comum));
            } else if (raridade.equals("Melhorado")) {
                nome_trabalho_especifico.setTextColor(ContextCompat.getColor(context,R.color.cor_texto_raridade_melhorado));
            } else if (raridade.equals("Raro")){
                nome_trabalho_especifico.setTextColor(ContextCompat.getColor(context,R.color.cor_texto_raridade_raro));
            }else if (raridade.equals("Especial")){
                nome_trabalho_especifico.setTextColor(ContextCompat.getColor(context,R.color.cor_texto_raridade_especial));
            }
        }
    }
}
