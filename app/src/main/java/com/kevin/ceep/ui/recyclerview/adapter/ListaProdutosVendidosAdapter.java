package com.kevin.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kevin.ceep.R;
import com.kevin.ceep.model.ProdutoVendido;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.List;

public class ListaProdutosVendidosAdapter extends RecyclerView.Adapter<ListaProdutosVendidosAdapter.ProdutosVendidosViewHolder>{
    private List<ProdutoVendido> listaProdutosVendidos;
    private final Context context;
    private OnItemClickListener onItemClickListener;
    public ListaProdutosVendidosAdapter(List<ProdutoVendido> listaProdutosVendidos, Context context) {
        this.listaProdutosVendidos = listaProdutosVendidos;
        this.context = context;
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    public  void setListaFiltrada(List<ProdutoVendido> listaFiltrada) {
        this.listaProdutosVendidos = listaFiltrada;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ProdutosVendidosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_produto_vendido, parent, false);
        return new ProdutosVendidosViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaProdutosVendidosAdapter.ProdutosVendidosViewHolder holder, int posicao) {
        ProdutoVendido produtoVendido = listaProdutosVendidos.get(posicao);
        holder.vincula(produtoVendido);
    }

    @Override
    public int getItemCount() {
        return listaProdutosVendidos.size();
    }
    public void remove(int posicao){
        if (posicao < 0 || posicao >= listaProdutosVendidos.size()) {
            return;
        }
        listaProdutosVendidos.remove(posicao);
        notifyItemRemoved(posicao);
        notifyItemRangeChanged(posicao, listaProdutosVendidos.size());
    }
    public void limpaLista() {
        listaProdutosVendidos.clear();
        notifyDataSetChanged();
    }

    public void adiciona(ProdutoVendido produtoVendidoRemovido, int itemPosicao) {
        if (itemPosicao < 0 || itemPosicao > listaProdutosVendidos.size()){
            return;
        }
        listaProdutosVendidos.add(itemPosicao, produtoVendidoRemovido);
        notifyItemInserted(itemPosicao);
        notifyItemRangeChanged(itemPosicao, listaProdutosVendidos.size());
    }

    public class ProdutosVendidosViewHolder extends RecyclerView.ViewHolder{
        private final TextView itemNomeProduto;
        private final TextView itemDataProduto;
        private final TextView itemValorProduto;
        private final TextView itemQuantidadeProduto;
        private ProdutoVendido produtoVendido;
        public ProdutosVendidosViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNomeProduto = itemView.findViewById(R.id.itemNomeProdutoVendido);
            itemDataProduto = itemView.findViewById(R.id.itemDataProdutoVendido);
            itemValorProduto = itemView.findViewById(R.id.itemValorProdutoVendido);
            itemQuantidadeProduto = itemView.findViewById(R.id.itemQuantidadeProdutoVendido);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(produtoVendido));
        }
        public void vincula(ProdutoVendido produtoVendido) {
            this.produtoVendido = produtoVendido;
            preencheCampos(produtoVendido);
        }

        private void preencheCampos(ProdutoVendido produtoVendido) {
            itemNomeProduto.setText(produtoVendido.getNomeProduto());
            itemDataProduto.setText(produtoVendido.getDataVenda());
            itemValorProduto.setText(context.getString(R.string.stringOuroValor, produtoVendido.getValorProduto()));
            itemQuantidadeProduto.setText(context.getString(R.string.stringQuantidadeValor, produtoVendido.getQuantidadeProduto()));
        }
    }
}
