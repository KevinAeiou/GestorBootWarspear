package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.ceep.R;
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.List;

public class ListaProfissaoAdapter extends RecyclerView.Adapter<ListaProfissaoAdapter.ProfissaoViewHolder> {

    private List<Profissao> profissoes;
    private Context context;
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
        private final TextView nivel_profissao;
        private Profissao profissao;

        public ProfissaoViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_profissao = itemView.findViewById(R.id.itemNomeProfissao);
            experiencia_profissao = itemView.findViewById(R.id.itemExperienciaProfissao);
            nivel_profissao = itemView.findViewById(R.id.itemNivelProfissao);

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(profissao,getAdapterPosition()));
        }

        public void vincula(Profissao profissao) {
            this.profissao = profissao;
            preencheCampo(profissao);
        }

        private void preencheCampo(Profissao profissao) {
            Integer expMaximo = defineExperienciaMaxima(profissao);
            int restante=expMaximo-profissao.getExperiencia();
            String exp= expMaximo+"-"+profissao.getExperiencia()+"="+restante;
            nome_profissao.setText(profissao.getNome());
            experiencia_profissao.setText(exp);
        }

        @NonNull
        private Integer defineExperienciaMaxima(Profissao profissao) {
            Integer expMaximo=0;
            if (profissao.getExperiencia()<20){
                expMaximo=20;
                nivel_profissao.setText("1");
            } else if (profissao.getExperiencia()<200) {
                expMaximo=200;
                nivel_profissao.setText("2");
            } else if (profissao.getExperiencia()<540) {
                expMaximo=1250;
                nivel_profissao.setText("3");
            } else if (profissao.getExperiencia()<2550) {
                expMaximo=2550;
                nivel_profissao.setText("4");
            } else if (profissao.getExperiencia()<4700) {
                expMaximo=4700;
                nivel_profissao.setText("5");
            } else if (profissao.getExperiencia()<7990) {
                expMaximo=7990;
                nivel_profissao.setText("6");
            } else if (profissao.getExperiencia()<12770) {
                expMaximo=12770;
                nivel_profissao.setText("7");
            } else if (profissao.getExperiencia()<19440) {
                expMaximo=19440;
                nivel_profissao.setText("8");
            } else if (profissao.getExperiencia()<28440) {
                expMaximo=28440;
                nivel_profissao.setText("9");
            } else if (profissao.getExperiencia()<40270) {
                expMaximo=40270;
                nivel_profissao.setText("10");
            } else if (profissao.getExperiencia()<55450) {
                expMaximo=55450;
                nivel_profissao.setText("11");
            } else if (profissao.getExperiencia()<74570) {
                expMaximo=74570;
                nivel_profissao.setText("12");
            } else if (profissao.getExperiencia()<98250) {
                expMaximo=98250;
                nivel_profissao.setText("13");
            } else if (profissao.getExperiencia()<127180) {
                expMaximo=127180;
                nivel_profissao.setText("14");
            } else if (profissao.getExperiencia()<156110) {
                expMaximo=156110;
                nivel_profissao.setText("15");
            } else if (profissao.getExperiencia()<185040) {
                expMaximo=185040;
                nivel_profissao.setText("16");
            } else if (profissao.getExperiencia()<215001) {
                expMaximo=215001;
                nivel_profissao.setText("17");
            } else if (profissao.getExperiencia()<245000) {
                expMaximo=245000;
                nivel_profissao.setText("18");
            } else if (profissao.getExperiencia()<300000) {
                expMaximo=300000;
                nivel_profissao.setText("19");
            } else if (profissao.getExperiencia()<375000) {
                expMaximo=375000;
                nivel_profissao.setText("20");
            } else if (profissao.getExperiencia()<470000) {
                expMaximo=470000;
                nivel_profissao.setText("21");
            } else if (profissao.getExperiencia()<585000) {
                expMaximo=585000;
                nivel_profissao.setText("22");
            } else if (profissao.getExperiencia()<720000) {
                expMaximo=720000;
                nivel_profissao.setText("23");
            } else if (profissao.getExperiencia()<875000) {
                expMaximo=875000;
                nivel_profissao.setText("24");
            } else if (profissao.getExperiencia()<105000) {
                expMaximo = 105000;
                nivel_profissao.setText("25");
            }else
                nivel_profissao.setText("26");
            return expMaximo;
        }

    }
}
