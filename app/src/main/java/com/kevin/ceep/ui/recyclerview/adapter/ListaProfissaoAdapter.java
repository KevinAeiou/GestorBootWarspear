package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
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
            String expMaximo = defineBarraExperiencia(profissao);
            nome_profissao.setText(profissao.getNome());
            experiencia_profissao.setText(expMaximo);
        }

        @NonNull
        private String defineBarraExperiencia(Profissao profissao) {
            String barraExperiencia="";
            if (profissao.getExperiencia()<20){
                barraExperiencia=profissao.getExperiencia()+"/"+"20";
            } else if (profissao.getExperiencia()<200) {
                barraExperiencia=(profissao.getExperiencia()-20)+"/"+"179";
            } else if (profissao.getExperiencia()<540) {
                barraExperiencia=(profissao.getExperiencia()-200)+"/"+"339";
            } else if (profissao.getExperiencia()<1250) {
                barraExperiencia=(profissao.getExperiencia()-540)+"/"+"709";
            } else if (profissao.getExperiencia()<2550) {
                barraExperiencia=(profissao.getExperiencia()-1250)+"/"+"1299";
            } else if (profissao.getExperiencia()<4700) {
                barraExperiencia=(profissao.getExperiencia()-2550)+"/"+"2149";
            } else if (profissao.getExperiencia()<7990) {
                barraExperiencia=(profissao.getExperiencia()-4700)+"/"+"3289";
            } else if (profissao.getExperiencia()<12770) {
                barraExperiencia=(profissao.getExperiencia()-7990)+"/"+"4779";
            } else if (profissao.getExperiencia()<19440) {
                barraExperiencia=(profissao.getExperiencia()-12770)+"/"+"6669";
            } else if (profissao.getExperiencia()<28440) {
                barraExperiencia=(profissao.getExperiencia()-19440)+"/"+"8999";
            } else if (profissao.getExperiencia()<40270) {
                barraExperiencia=(profissao.getExperiencia()-28440)+"/"+"11829";
            } else if (profissao.getExperiencia()<55450) {
                barraExperiencia=(profissao.getExperiencia()-40270)+"/"+"15179";
            } else if (profissao.getExperiencia()<74570) {
                barraExperiencia=(profissao.getExperiencia()-55450)+"/"+"19119";
            } else if (profissao.getExperiencia()<98250) {
                barraExperiencia=(profissao.getExperiencia()-74570)+"/"+"23679";
            } else if (profissao.getExperiencia()<127180) {
                barraExperiencia=(profissao.getExperiencia()-98250)+"/"+"28929";
            } else if (profissao.getExperiencia()<156110) {
                barraExperiencia=(profissao.getExperiencia()-127180)+"/"+"28929";
            } else if (profissao.getExperiencia()<185040) {
                barraExperiencia=(profissao.getExperiencia()-156110)+"/"+"28929";
            } else if (profissao.getExperiencia()<215001) {
                barraExperiencia=(profissao.getExperiencia()-185040)+"/"+"29959";
            } else if (profissao.getExperiencia()<245000) {
                barraExperiencia=(profissao.getExperiencia()-215001)+"/"+"29999";
            } else if (profissao.getExperiencia()<300000) {
                barraExperiencia=(profissao.getExperiencia()-245000)+"/"+"54999";
            } else if (profissao.getExperiencia()<375000) {
                barraExperiencia=(profissao.getExperiencia()-300000)+"/"+"74999";
            } else if (profissao.getExperiencia()<470000) {
                barraExperiencia=(profissao.getExperiencia()-375000)+"/"+"94999";
            } else if (profissao.getExperiencia()<585000) {
                barraExperiencia=(profissao.getExperiencia()-470000)+"/"+"114999";
            } else if (profissao.getExperiencia()<720000) {
                barraExperiencia=(profissao.getExperiencia()-585000)+"/"+"134999";
            } else if (profissao.getExperiencia()<875000) {
                barraExperiencia=(profissao.getExperiencia()-720000)+"/"+"154999";
            } else if (profissao.getExperiencia()<105000) {
                barraExperiencia=(profissao.getExperiencia()-875000)+"/"+"174999";
            }
            return barraExperiencia;
        }

    }
}
