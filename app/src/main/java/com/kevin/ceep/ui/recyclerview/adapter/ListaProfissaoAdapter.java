package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.ceep.R;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ListaProfissaoAdapter extends RecyclerView.Adapter<ListaProfissaoAdapter.ProfissaoViewHolder> {

    private List<Profissao> profissoes;
    private final Context context;
    private OnItemClickListener onItemClickListener;

    public ListaProfissaoAdapter(Context context, List<Profissao> profissao) {
        this.profissoes = profissao;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
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

    public void setLista(ArrayList<Profissao> profissoes) {
        this.profissoes = profissoes;
        notifyDataSetChanged();
    }

    public void limpaLista() {
        profissoes.clear();
        notifyDataSetChanged();
    }

    public class ProfissaoViewHolder extends RecyclerView.ViewHolder {

        private final TextView nome_profissao;
        private final TextView experiencia_profissao;
        private final TextView nivelProfissao;
        private final CardView cardProfissao;
        private Profissao profissao;

        public ProfissaoViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_profissao = itemView.findViewById(R.id.itemNomeProfissao);
            experiencia_profissao = itemView.findViewById(R.id.itemExperienciaProfissao);
            nivelProfissao = itemView.findViewById(R.id.itemNivelProfissao);
            cardProfissao = itemView.findViewById(R.id.cardViewProfissao);

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(profissao, getAdapterPosition()));
        }

        public void vincula(Profissao profissao) {
            this.profissao = profissao;
            preencheCampo(profissao);
        }

        private void preencheCampo(Profissao profissao) {
            defineBarraExperiencia(profissao);
            nome_profissao.setText(profissao.getNome());
            if (profissao.isPrioridade()) {
                cardProfissao.setCardBackgroundColor(ContextCompat.getColor(context,R.color.cor_background_feito));
            }
            nivelProfissao.setText(context.getString(R.string.stringNivelValor, profissao.getNivel()));
        }

        private void defineBarraExperiencia(Profissao profissao) {
            int xpNecessario = 0;
            int xpRestante = 0;
            int xpMaximo = 0;
            int xpAtual = profissao.getExperiencia();

            ArrayList<Integer> xpNiveis = new ArrayList<>();
            xpNiveis.add(20);
            xpNiveis.add(200);
            xpNiveis.add(540);
            xpNiveis.add(1250);
            xpNiveis.add(2550);
            xpNiveis.add(4700);
            xpNiveis.add(7990);
            xpNiveis.add(12770);
            xpNiveis.add(19440);
            xpNiveis.add(28440);
            xpNiveis.add(40270);
            xpNiveis.add(55450);
            xpNiveis.add(74570);
            xpNiveis.add(98250);
            xpNiveis.add(127180);
            xpNiveis.add(156110);
            xpNiveis.add(185040);
            xpNiveis.add(215000);
            xpNiveis.add(245000);
            xpNiveis.add(300000);
            xpNiveis.add(375000);
            xpNiveis.add(470000);
            xpNiveis.add(585000);
            xpNiveis.add(705000);
            xpNiveis.add(830000);
            xpNiveis.add(1050000);

            for (int i=0; i<xpNiveis.size();i++){
                if (i==0 && xpAtual<xpNiveis.get(i)){
                    xpMaximo = xpNiveis.get(i);
                    xpNecessario = xpNiveis.get(i);
                    xpRestante = xpAtual;
                }else if (i>=1 && xpAtual>=xpNiveis.get(i-1) && xpAtual<xpNiveis.get(i)){
                    xpMaximo = xpNiveis.get(i);
                    xpNecessario = xpMaximo - xpNiveis.get(i-1);
                    xpRestante = xpNecessario-(xpAtual-xpNiveis.get(i-1));
                }
            }
            int porcentagem = 100 - ((100 * xpRestante) / xpNecessario);
            String exp = xpAtual+"/"+xpMaximo+"/"+porcentagem+"%";
            experiencia_profissao.setText(exp);
        }

    }
}
