package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.ceep.R;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.List;

public class ListaProfissaoAdapter extends RecyclerView.Adapter<ListaProfissaoAdapter.ProfissaoViewHolder> {

    private final List<Profissao> profissoes;
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

    public class ProfissaoViewHolder extends RecyclerView.ViewHolder {

        private final TextView nome_profissao;
        private final TextView experiencia_profissao;
        private Profissao profissao;

        public ProfissaoViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_profissao = itemView.findViewById(R.id.itemNomeProfissao);
            experiencia_profissao = itemView.findViewById(R.id.itemExperienciaProfissao);

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(profissao,getAdapterPosition()));
        }

        public void vincula(Profissao profissao) {
            this.profissao = profissao;
            preencheCampo(profissao);
        }

        private void preencheCampo(Profissao profissao) {
            Integer expMaximo = defineExperienciaMaxima(profissao);
            int porcentagem = (100*profissao.getExperiencia())/expMaximo;
            String exp = profissao.getExperiencia()+"/"+expMaximo+"/"+porcentagem+"%";
            nome_profissao.setText(profissao.getNome());
            experiencia_profissao.setText(exp);
        }

        @NonNull
        private Integer defineExperienciaMaxima(Profissao profissao) {
            int expMaximo=0;
            if (profissao.getExperiencia()<=20){
                expMaximo=20;
            } else if (profissao.getExperiencia()<=200 && profissao.getExperiencia()>20) {
                expMaximo=200;
            } else if (profissao.getExperiencia()<=540 && profissao.getExperiencia()>200) {
                expMaximo=540;
            } else if (profissao.getExperiencia()<=1250 && profissao.getExperiencia()>540) {
                expMaximo=1250;
            } else if (profissao.getExperiencia()<=2550 && profissao.getExperiencia()>1250) {
                expMaximo=2550;
            } else if (profissao.getExperiencia()<=4700 && profissao.getExperiencia()>2550) {
                expMaximo=4700;
            } else if (profissao.getExperiencia()<=7990 && profissao.getExperiencia()>4700) {
                expMaximo=7990;
            } else if (profissao.getExperiencia()<=12770 && profissao.getExperiencia()>7990) {
                expMaximo=12770;
            } else if (profissao.getExperiencia()<=19440 && profissao.getExperiencia()>12770) {
                expMaximo=19440;
            } else if (profissao.getExperiencia()<=28440 && profissao.getExperiencia()>19440) {
                expMaximo=28440;
            } else if (profissao.getExperiencia()<=40270 && profissao.getExperiencia()>28440) {
                expMaximo=40270;
            } else if (profissao.getExperiencia()<=55450 && profissao.getExperiencia()>40270) {
                expMaximo=55450;
            } else if (profissao.getExperiencia()<=74570 && profissao.getExperiencia()>55450) {
                expMaximo=74570;
            } else if (profissao.getExperiencia()<=98250 && profissao.getExperiencia()>74570) {
                expMaximo=98250;
            } else if (profissao.getExperiencia()<=127180 && profissao.getExperiencia()>98250) {
                expMaximo=127180;
            } else if (profissao.getExperiencia()<=156110 && profissao.getExperiencia()>127180) {
                expMaximo=156110;
            } else if (profissao.getExperiencia()<=185040 && profissao.getExperiencia()>156110) {
                expMaximo=185040;
            } else if (profissao.getExperiencia()<=215001 && profissao.getExperiencia()>185040) {
                expMaximo=215001;
            } else if (profissao.getExperiencia()<=245000 && profissao.getExperiencia()>215001) {
                expMaximo=245000;
            } else if (profissao.getExperiencia()<=300000 && profissao.getExperiencia()>245000) {
                expMaximo=300000;
            } else if (profissao.getExperiencia()<=375000 && profissao.getExperiencia()>300000) {
                expMaximo=375000;
            } else if (profissao.getExperiencia()<=470000 && profissao.getExperiencia()>375000) {
                expMaximo=470000;
            } else if (profissao.getExperiencia()<=585000 && profissao.getExperiencia()>470000) {
                expMaximo=585000;
            } else if (profissao.getExperiencia()<=720000 && profissao.getExperiencia()>585000) {
                expMaximo=720000;
            } else if (profissao.getExperiencia()<=875000 && profissao.getExperiencia()>720000) {
                expMaximo=875000;
            }else if (profissao.getExperiencia()<=1050000 && profissao.getExperiencia()>875000) {
                expMaximo = 1050000;
            }
            Log.d("CHAVE_EXP", String.valueOf(expMaximo));
            return expMaximo;
        }

    }
}
