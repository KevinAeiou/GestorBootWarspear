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
        return trabalhos.size();
    }

    public class TrabalhoEspecificoViewHolder extends RecyclerView.ViewHolder {

        private final TextView nome_trabalho_especifico;
        private final TextView profissao_trabalho_especifico;
        private final TextView nivel_trabalho_especifico;
        private Trabalho trabalho;

        public TrabalhoEspecificoViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_trabalho_especifico = itemView.findViewById(R.id.itemNomeTrabaloEspecifico);
            profissao_trabalho_especifico = itemView.findViewById(R.id.itemProfissaoTrabalhoEspecifico);
            nivel_trabalho_especifico = itemView.findViewById(R.id.itemNivelTrabaloEspecifico);
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
            nivel_trabalho_especifico.setTextColor(Color.parseColor("#00ff00"));
        }

        private void confiuraCorNomeTrabalo(Trabalho trabalho) {
            String raridade = trabalho.getRaridade();
            if (raridade.equals("Comum")){
                nome_trabalho_especifico.setTextColor(Color.parseColor("#B8D8E0"));
            }else if (raridade.equals("Raro")){
                nome_trabalho_especifico.setTextColor(Color.parseColor("#ff66ff"));
            }else if (raridade.equals("Especial")){
                nome_trabalho_especifico.setTextColor(Color.parseColor("#ff6666"));
            }
        }
    }
}
