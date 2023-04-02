package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.ui.activity.ListaPersonagemActivity;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.List;

public class ListaPersonagemAdapter extends RecyclerView.Adapter<ListaPersonagemAdapter.RaridadeViewHolder> {

    private List<Personagem> personagems;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    public ListaPersonagemAdapter(Context context, List<Personagem> personagems) {
        this.personagems = personagems;
        this.context = context;
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener){
        this.onCheckedChangeListener=onCheckedChangeListener;
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
        private final SwitchCompat estado_personagem;
        private Personagem personagem;

        public RaridadeViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_personagem = itemView.findViewById(R.id.itemNomePersonagem);
            estado_personagem = itemView.findViewById(R.id.itemSwitchButton);
            estado_personagem.setOnCheckedChangeListener((compoundButton, b) -> {
                ListaPersonagemActivity personagemActivity = new ListaPersonagemActivity();
                if (b){
                    personagemActivity.atualizaEstadoPersonagem(personagem,1);
                }else{
                    personagemActivity.atualizaEstadoPersonagem(personagem,0);
                }
            });
            itemView.setOnClickListener(view -> onItemClickListener.onItemClick(personagem, getAdapterPosition()));
        }

        public void vincula(Personagem personagem){
            this.personagem = personagem;
            preencheCampo(personagem);
        }

        private void preencheCampo(Personagem personagem) {
            configuraEstadoPersonagem(personagem);
            nome_personagem.setText(personagem.getNome());
            nome_personagem.setTextColor(Color.parseColor("#FCF5EF"));
        }

        private void configuraEstadoPersonagem(Personagem personagem) {
            if (personagem.getEstado()==1){
                estado_personagem.setText("Ativo");
                estado_personagem.setChecked(true);
            }else{
                estado_personagem.setText("Inativo");
                estado_personagem.setChecked(false);
            }
        }
    }
}
