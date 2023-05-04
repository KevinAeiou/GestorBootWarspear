package com.kevin.ceep.ui.recyclerview.adapter;

import static com.kevin.ceep.R.drawable.card_ativo_background;
import static com.kevin.ceep.R.drawable.card_inativo_background;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.ceep.R;
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.List;

public class ListaPersonagemAdapter extends RecyclerView.Adapter<ListaPersonagemAdapter.RaridadeViewHolder> {

    private List<Personagem> personagems;
    private Context context;
    private OnItemClickListener onItemClickListener;
    public ListaPersonagemAdapter(Context context, List<Personagem> personagems) {
        this.personagems = personagems;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RaridadeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_personagem,parent,false);
        return new RaridadeViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull RaridadeViewHolder holder, int position) {
        Personagem personagem = personagems.get(position);
        holder.vincula(personagem);
    }

    @Override
    public int getItemCount() {
        return personagems.size();
    }

    public void remove(int position) {
        if (position < 0 || position >= personagems.size()) {
            return;
        }
        personagems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, personagems.size());
        notifyDataSetChanged();
    }

    public class RaridadeViewHolder extends RecyclerView.ViewHolder{

        private final TextView nome_personagem;
        private final CardView card_personagem;
        private Personagem personagem;

        public RaridadeViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_personagem = itemView.findViewById(R.id.itemNomePersonagem);
            card_personagem=itemView.findViewById(R.id.cardViewPersonagem);

            itemView.setOnClickListener(view -> onItemClickListener.onItemClick(personagem, getAdapterPosition()));
        }

        public void vincula(Personagem personagem){
            this.personagem = personagem;
            preencheCampo(personagem);
        }

        private void preencheCampo(Personagem personagem) {
            nome_personagem.setText(personagem.getNome());
            //nome_personagem.setTextColor(ContextCompat.getColor(context,R.color.cor_texto_raridade_comum));
            if (personagem.getEstado()==1){
                card_personagem.setCardBackgroundColor(ContextCompat.getColor(context,R.color.cor_background_escuro));
            }else{
                card_personagem.setCardBackgroundColor(ContextCompat.getColor(context,R.color.cor_texto_licenca_iniciante));
            }
        }
    }
}
