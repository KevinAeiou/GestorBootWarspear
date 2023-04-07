package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.ceep.R;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.List;

public class ListaTrabalhoAdapter extends RecyclerView.Adapter<ListaTrabalhoAdapter.TrabalhoViewHolder> {

    private List<Trabalho> trabalhos;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ListaTrabalhoAdapter(Context context,List<Trabalho> trabalho) {
        this.trabalhos = trabalho;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setListaFiltrada(List<Trabalho> listaFiltrada){
        this.trabalhos=listaFiltrada;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrabalhoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_trabalho, parent, false);
        return new TrabalhoViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull TrabalhoViewHolder holder, int position) {
        Trabalho trabalho = trabalhos.get(position);
        holder.vincula(trabalho);
    }

    @Override
    public int getItemCount() {
        return trabalhos.size();
    }

    public void altera(int posicao, Trabalho nota) {
        trabalhos.set(posicao, nota);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if (position < 0 || position >= trabalhos.size()) {
            return;
        }
        trabalhos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,trabalhos.size());
        notifyDataSetChanged();
    }

    public class TrabalhoViewHolder extends RecyclerView.ViewHolder{

        private final CardView cardview_trabalho;
        private final TextView nome_trabalho;
        private final TextView tipo_licenca;
        private final TextView profissao_trabalho;
        private final TextView nivel_trabalho;
        private Trabalho trabalho;

        public TrabalhoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardview_trabalho = itemView.findViewById(R.id.itemCardViewTrabalho);
            nome_trabalho = itemView.findViewById(R.id.itemNomeTrabalho);
            tipo_licenca = itemView.findViewById(R.id.itemTipoLicenca);
            profissao_trabalho = itemView.findViewById(R.id.itemProfissaoTrabalho);
            nivel_trabalho = itemView.findViewById(R.id.itemNivelTrabalho);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(trabalho, getAdapterPosition()));
        }
        public void vincula(Trabalho trabalho) {
            this.trabalho = trabalho;
            preencheCampo(trabalho);
        }
        private void preencheCampo(Trabalho trabalho) {
            nome_trabalho.setText(trabalho.getNome());
            configuraCorNomeTrabalho(trabalho);
            tipo_licenca.setText(trabalho.getTipo_licenca());
            configuraCorLicencaTrabalho(trabalho);
            profissao_trabalho.setText(trabalho.getProfissao());
            profissao_trabalho.setTextColor(Color.WHITE);
            nivel_trabalho.setText(String.valueOf(trabalho.getNivel()));
            nivel_trabalho.setTextColor(Color.parseColor("#00ff00"));
            configuraCorCardViewTrabalho(trabalho);
        }

        private void configuraCorCardViewTrabalho(Trabalho trabalho) {
            Integer estado = trabalho.getEstado();
            if (estado==0){
                cardview_trabalho.setCardBackgroundColor(Color.parseColor("#6DB5CA"));
            }else if (estado==1){
                cardview_trabalho.setCardBackgroundColor(Color.parseColor("#6d87ca"));
            }else if (estado==2){
                cardview_trabalho.setCardBackgroundColor(Color.parseColor("#b5ca6d"));
            }
        }

        private void configuraCorLicencaTrabalho(Trabalho trabalho) {
            String licenca = trabalho.getTipo_licenca();
            if (licenca.equals("Licença de produção do iniciante")){
                tipo_licenca.setTextColor(Color.parseColor("#FCF5EF"));
            }else if (licenca.equals("Licença de produção do aprendiz")){
                tipo_licenca.setTextColor(Color.parseColor("#66ff66"));
            }else{
                tipo_licenca.setTextColor(Color.parseColor("#ffcc00"));
            }
        }

        private void configuraCorNomeTrabalho(Trabalho trabalho) {
            String raridade = trabalho.getRaridade();
            if (raridade.equals("Comum")){
                nome_trabalho.setTextColor(Color.parseColor("#B8D8E0"));
            } else if (raridade.equals("Raro")){
                nome_trabalho.setTextColor(Color.parseColor("#ff66ff"));
            }else{
                nome_trabalho.setTextColor(Color.parseColor("#ff6666"));
            }
        }
    }
    public void adiciona(Trabalho trabalho){
        trabalhos.add(trabalho);
        notifyDataSetChanged();
    }
}
