package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.ceep.R;
import com.kevin.ceep.model.ProfissaoBase;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListenerProfissao;

import java.util.ArrayList;

public class ListaProfissaoAdapter extends RecyclerView.Adapter<
    ListaProfissaoAdapter.ProfissaoViewHolder
> {
    private final ArrayList<ProfissaoBase> profissoes;
    private final Context context;
    private static OnItemClickListenerProfissao onItemClickListener;

    public ListaProfissaoAdapter(ArrayList<ProfissaoBase> profissoes, Context context) {
        this.profissoes = profissoes;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListenerProfissao onItemClickListener) {
        ListaProfissaoAdapter.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ProfissaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
            .inflate(R.layout.item_profissao, parent, false);
        return new ProfissaoViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfissaoViewHolder holder, int position) {
        ProfissaoBase profissao = profissoes.get(position);
        holder.vincula(profissao);
    }

    @Override
    public int getItemCount() {
        return profissoes.size();
    }

    public void atualiza(ArrayList<ProfissaoBase> profissoesAtualizadas) {
        DiffUtil.DiffResult diffResult= DiffUtil.calculateDiff(
            new ListaProfissaoAdapter.ItemDiffCallback(profissoes, profissoesAtualizadas)
        );
        profissoes.clear();
        profissoes.addAll(profissoesAtualizadas);
        diffResult.dispatchUpdatesTo(this);
    }

    public void limpaLista() {
        atualiza(new ArrayList<>());
    }

    public static class ProfissaoViewHolder extends RecyclerView.ViewHolder {
        private final TextView nome_profissao;
        private ProfissaoBase profissao;

        public ProfissaoViewHolder(@NonNull View itemView) {
            super(itemView);

            nome_profissao = itemView.findViewById(R.id.itemNomeProfissao);
            itemView.setOnClickListener(view ->
                onItemClickListener.onItemClick(profissao, getAdapterPosition())
            );
        }

        public void vincula(ProfissaoBase profissao) {
            this.profissao = profissao;
            preencheCampo(profissao);
        }

        private void preencheCampo(ProfissaoBase profissaoPersonagem) {
            nome_profissao.setText(profissaoPersonagem.getNome());
        }
    }

    private static class ItemDiffCallback extends DiffUtil.Callback {
        private final ArrayList<ProfissaoBase> listaAntiga;
        private final ArrayList<ProfissaoBase> listaNova;
        public ItemDiffCallback(ArrayList<ProfissaoBase> listaAntiga, ArrayList<ProfissaoBase> listaNova) {
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
