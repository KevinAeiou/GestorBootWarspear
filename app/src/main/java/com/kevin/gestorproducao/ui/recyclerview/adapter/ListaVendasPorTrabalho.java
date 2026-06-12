package com.kevin.gestorproducao.ui.recyclerview.adapter;

import static com.kevin.gestorproducao.utilitario.Utilitario.formatarTimestamp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.gestorproducao.R;
import com.kevin.gestorproducao.model.TrabalhoVendido;
import com.kevin.gestorproducao.ui.recyclerview.adapter.listener.OnItemClickListenerTrabalhoVendido;
import com.kevin.gestorproducao.utilitario.Formatador;

import java.util.List;

public class ListaVendasPorTrabalho
    extends BaseListAdapter<TrabalhoVendido, ListaVendasPorTrabalho.VendasPorTrabalhoViewHolder>
{
    private final Context context;
    private OnItemClickListenerTrabalhoVendido onItemClickListener;

    public ListaVendasPorTrabalho(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListenerTrabalhoVendido onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public VendasPorTrabalhoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.item_venda_por_trabalho,
            parent,
            false
        );
        return new VendasPorTrabalhoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendasPorTrabalhoViewHolder holder, int position) {
        holder.vincula(getItem(position));
    }

    @Override
    protected DiffUtil.Callback getDiffCallback(
        List<TrabalhoVendido> antiga,
        List<TrabalhoVendido> nova
    ) {
        return new DiffUtil.Callback() {
            @Override public int getOldListSize() { return antiga.size(); }
            @Override public int getNewListSize() { return nova.size(); }

            @Override
            public boolean areItemsTheSame(int oldPos, int newPos) {
                return antiga.get(oldPos).getId().equals(nova.get(newPos).getId());
            }

            @Override
            public boolean areContentsTheSame(int oldPos, int newPos) {
                return antiga.get(oldPos).equals(nova.get(newPos));
            }
        };
    }

    public class VendasPorTrabalhoViewHolder extends RecyclerView.ViewHolder {
        private TrabalhoVendido trabalho;
        private final TextView nome;
        private final TextView valor;
        private final TextView nivel;
        private final TextView criadoEm;

        public VendasPorTrabalhoViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.itemNomeVendaPorTrabalho);
            valor = itemView.findViewById(R.id.itemValorTrabalhoVendido);
            nivel = itemView.findViewById(R.id.itemNivelVendaPorTrabalho);
            criadoEm = itemView.findViewById(R.id.itemCriadoEmVendaPorTrabalho);

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(trabalho));
        }

        void vincula(TrabalhoVendido venda) {
            this.trabalho = venda;
            nome.setText(venda.getNome());
            valor.setText(context.getString(
                R.string.stringOuroValor,
                Formatador.formatarMilhar(venda.getValor())
            ));
            nivel.setText(context.getString(
                R.string.stringNivelValor,
                venda.getNivel())
            );
            criadoEm.setText(formatarTimestamp(venda.getCriadoEm()));
        }
    }
}