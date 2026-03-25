package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.ceep.R;
import com.kevin.ceep.model.ProfissaoPersonagem;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListenerProfissaoPersonagem;

import java.util.ArrayList;

public class ListaProfissaoPersonagemAdapter extends RecyclerView.Adapter<
    ListaProfissaoPersonagemAdapter.ProfissaoViewHolder
> {

    private final ArrayList<ProfissaoPersonagem> profissoes;
    private final Context context;
    private OnItemClickListenerProfissaoPersonagem onItemClickListener;

    public ListaProfissaoPersonagemAdapter(Context context, ArrayList<ProfissaoPersonagem> profissaoPersonagem) {
        this.profissoes = profissaoPersonagem;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListenerProfissaoPersonagem onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    @NonNull
    @Override
    public ProfissaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_profissao_personagem,parent,false);
        return new ProfissaoViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfissaoViewHolder holder, int position) {
        ProfissaoPersonagem profissaoPersonagem = profissoes.get(position);
        holder.vincula(profissaoPersonagem);
    }

    @Override
    public int getItemCount() {
        return profissoes.size();
    }

    public void atualiza(ArrayList<ProfissaoPersonagem> profissoesAtualizadas) {
        DiffUtil.DiffResult diffResult= DiffUtil.calculateDiff(new ItemDiffCallback(profissoes, profissoesAtualizadas));
        profissoes.clear();
        profissoes.addAll(profissoesAtualizadas);
        diffResult.dispatchUpdatesTo(this);
    }

    public void limpaLista() {
        atualiza(new ArrayList<>());
    }
    public class ProfissaoViewHolder extends RecyclerView.ViewHolder {

        private final TextView nome_profissao;
        private final TextView experiencia_profissao;
        private final TextView nivelProfissao;
        private final CardView cardProfissao;
        private ProfissaoPersonagem profissaoPersonagem;
        public ProfissaoViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_profissao = itemView.findViewById(R.id.itemNomeProfissaoPersonagem);
            experiencia_profissao = itemView.findViewById(R.id.itemExperienciaProfissaoPersonagem);
            nivelProfissao = itemView.findViewById(R.id.itemNivelProfissaoPersonagem);
            cardProfissao = itemView.findViewById(R.id.cardViewProfissaoPersonagem);
            itemView.setOnClickListener(view ->
                onItemClickListener.onItemClick(profissaoPersonagem, getAdapterPosition())
            );
        }
        public void vincula(ProfissaoPersonagem profissaoPersonagem) {
            this.profissaoPersonagem = profissaoPersonagem;
            preencheCampo(profissaoPersonagem);
        }
        private void preencheCampo(ProfissaoPersonagem profissaoPersonagem) {
            String barraExperiencia = profissaoPersonagem.getExperiencia() + " / " + profissaoPersonagem.getXpMaximo();
            experiencia_profissao.setText(barraExperiencia);
            nome_profissao.setText(profissaoPersonagem.getNome());
            nivelProfissao.setText(String.valueOf(profissaoPersonagem.getNivel()));
            configuraCardPrioridade(profissaoPersonagem);
        }

        private void configuraCardPrioridade(ProfissaoPersonagem profissaoPersonagem) {
            int cor = profissaoPersonagem.isPrioridade() ?
                ContextCompat.getColor(context, R.color.cor_background_feito) :
                ContextCompat.getColor(context, R.color.cor_background_card);
            GradientDrawable borda = new GradientDrawable();
            borda.setShape(GradientDrawable.RECTANGLE);
            borda.setCornerRadius(16f);
            borda.setStroke(8, cor);
            cardProfissao.setBackground(borda);
        }
    }
    private static class ItemDiffCallback extends DiffUtil.Callback {
        private final ArrayList<ProfissaoPersonagem> listaAntiga;
        private final ArrayList<ProfissaoPersonagem> listaNova;
        public ItemDiffCallback(ArrayList<ProfissaoPersonagem> listaAntiga, ArrayList<ProfissaoPersonagem> listaNova) {
            this.listaAntiga= listaAntiga;
            this.listaNova= listaNova;
        }

        @Override
        public int getOldListSize() {
            return listaAntiga.size();
        }

        @Override
        public int getNewListSize() {
            return listaNova.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return listaAntiga.get(oldItemPosition).getId().equals(listaNova.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return listaAntiga.get(oldItemPosition).equals(listaNova.get(newItemPosition));
        }
    }
}
