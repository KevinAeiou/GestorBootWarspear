package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.ceep.R;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.ui.recyclerview.adapter.listener.AddTextChangedListenerInterface;

import java.util.ArrayList;
import java.util.List;

public class ListaProfissaoAdapter extends RecyclerView.Adapter<ListaProfissaoAdapter.ProfissaoViewHolder> {

    private List<Profissao> profissoes;
    private final Context context;
    private AddTextChangedListenerInterface addTextChangedListener;

    public ListaProfissaoAdapter(Context context, List<Profissao> profissao) {
        this.profissoes = profissao;
        this.context = context;
    }

    public void setAddTextChangedListener(AddTextChangedListenerInterface addTextChangedListener) {
        this.addTextChangedListener = addTextChangedListener;
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

    public void atualiza(ArrayList<Profissao> profissoes) {
        this.profissoes = profissoes;
        notifyDataSetChanged();
    }

    public void limpaLista() {
        profissoes.clear();
        notifyDataSetChanged();
    }
    public class ProfissaoViewHolder extends RecyclerView.ViewHolder {

        private final TextView nome_profissao;
        private final EditText experiencia_profissao;
        private final TextView nivelProfissao;
        private final CardView cardProfissao;
        private Profissao profissao;
        public ProfissaoViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_profissao = itemView.findViewById(R.id.itemNomeProfissao);
            experiencia_profissao = itemView.findViewById(R.id.itemExperienciaProfissao);
            nivelProfissao = itemView.findViewById(R.id.itemNivelProfissao);
            cardProfissao = itemView.findViewById(R.id.cardViewProfissao);
            experiencia_profissao.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    addTextChangedListener.afterTextChangedMeu(profissao, experiencia_profissao.getText());
                }
            });
        }
        public void vincula(Profissao profissao) {
            this.profissao = profissao;
            preencheCampo(profissao);
        }
        private void preencheCampo(Profissao profissao) {
            experiencia_profissao.setText(String.valueOf(profissao.getExperiencia()));
            nome_profissao.setText(profissao.getNome());
            if (profissao.isPrioridade()) {
                cardProfissao.setCardBackgroundColor(ContextCompat.getColor(context,R.color.cor_background_feito));
            }
            nivelProfissao.setText(context.getString(R.string.stringNivelValor, profissao.getNivel()));
        }
    }
}
