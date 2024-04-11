package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.kevin.ceep.R;
import com.kevin.ceep.model.ProfissaoTrabalho;
import com.kevin.ceep.model.Trabalho;

import java.util.List;

public class ListaProfissaoTrabalhoAdapter extends RecyclerView.Adapter<ListaProfissaoTrabalhoAdapter.ProfissaoTrabalhoViewHolder> {
    private List<ProfissaoTrabalho> profissoes;
    private List<Trabalho> trabalhos;
    private final Context context;

    public ListaProfissaoTrabalhoAdapter(List<ProfissaoTrabalho> profissaoTrabalhos, Context context) {
        this.profissoes = profissaoTrabalhos;
        this.context = context;
    }
    public void setListaFiltrada(List<ProfissaoTrabalho> listaFiltrada) {
        this.profissoes = listaFiltrada;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ProfissaoTrabalhoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_profissao_trabalho, parent, false);
        return new ProfissaoTrabalhoViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaProfissaoTrabalhoAdapter.ProfissaoTrabalhoViewHolder holder, int position) {
        ProfissaoTrabalho profissaoTrabalho = profissoes.get(position);
        holder.vincula(profissaoTrabalho);
        configuraRecyclerViewExpancivel(holder, profissaoTrabalho);
    }

    private void configuraRecyclerViewExpancivel(@NonNull ProfissaoTrabalhoViewHolder holder, ProfissaoTrabalho profissaoTrabalho) {
        boolean isExpandable = profissaoTrabalho.isExpandable();
        holder.constraintLayoutItemProfissaoTrabalho.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
        if (isExpandable) {
            holder.shapeableImageViewItemProfissaoTrabalho.setImageResource(R.drawable.ic_cima);
        } else {
            holder.shapeableImageViewItemProfissaoTrabalho.setImageResource(R.drawable.ic_baixo);
        }
        ListaTrabalhoEspecificoAdapter trabalhoEspecificoAdapter = new ListaTrabalhoEspecificoAdapter(context, trabalhos);
        holder.recyclerViewExpansivelItemProfissaoTrabalho.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerViewExpansivelItemProfissaoTrabalho.setHasFixedSize(true);
        holder.recyclerViewExpansivelItemProfissaoTrabalho.setAdapter(trabalhoEspecificoAdapter);
        holder.linearLayoutItemProfissaoTrabalho.setOnClickListener(view -> {
            profissaoTrabalho.setExpandable(!profissaoTrabalho.isExpandable());
            trabalhos = profissaoTrabalho.getTrabalhos();
            notifyItemChanged(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return profissoes.size();
    }
    public void remove(int posicao) {
        if (posicao < 0 || posicao >= profissoes.size()) {
            return;
        }
        profissoes.remove(posicao);
        notifyItemRemoved(posicao);
        notifyItemRangeChanged(posicao, profissoes.size());
        notifyDataSetChanged();
    }
    public void limpaLista() {
        profissoes.clear();
        notifyDataSetChanged();
    }

    public class ProfissaoTrabalhoViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayoutCompat linearLayoutItemProfissaoTrabalho;
        private final ConstraintLayout constraintLayoutItemProfissaoTrabalho;
        private final RecyclerView recyclerViewExpansivelItemProfissaoTrabalho;
        private final MaterialTextView txtNomeItemProfissaoTrabalho;
        private final ShapeableImageView shapeableImageViewItemProfissaoTrabalho;
        public ProfissaoTrabalhoViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutItemProfissaoTrabalho = itemView.findViewById(R.id.linearLayoutItemProfissaoTrabalho);
            constraintLayoutItemProfissaoTrabalho = itemView.findViewById(R.id.constraintLayoutExpansivelItemProfissaoTrabalho);
            recyclerViewExpansivelItemProfissaoTrabalho = itemView.findViewById(R.id.recyclerViewItemProfissaoTrabalho);
            txtNomeItemProfissaoTrabalho = itemView.findViewById(R.id.txtProfissaoItemProfissaoTrabalho);
            shapeableImageViewItemProfissaoTrabalho = itemView.findViewById(R.id.imgExpandeItemProfissaoTrabalho);
        }

        public void vincula(ProfissaoTrabalho profissaoTrabalho) {
            preencheCampos(profissaoTrabalho);
        }

        private void preencheCampos(ProfissaoTrabalho profissaoTrabalho) {
            txtNomeItemProfissaoTrabalho.setText(profissaoTrabalho.getNome());
        }
    }
}
