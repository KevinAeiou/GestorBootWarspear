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

import java.util.List;

public class ListaProdutosVendidosAdapter extends RecyclerView.Adapter<ListaProdutosVendidosAdapter.ProdutosVendidosViewHolder>{
    private List<ProdutoVendido> listaProdutosVendidos;
    private final Context context;
    public ListaProdutosVendidosAdapter(List<ProdutoVendido> listaProdutosVendidos, Context context) {
        this.listaProdutosVendidos = listaProdutosVendidos;
        this.context = context;
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
        notifyDataSetChanged();
    }
    public void limpaLista() {
        listaProdutosVendidos.clear();
        notifyDataSetChanged();
    }
    public class ProdutosVendidosViewHolder extends RecyclerView.ViewHolder{
        private final TextView itemNomeProduto;
        private final TextView itemDataProduto;
        private final TextView itemValorProduto;
        private final TextView itemQuantidadeProduto;
        public ProdutosVendidosViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNomeProduto = itemView.findViewById(R.id.itemNomeProdutoVendido);
            itemDataProduto = itemView.findViewById(R.id.itemDataProdutoVendido);
            itemValorProduto = itemView.findViewById(R.id.itemDataProdutoVendido);
            itemQuantidadeProduto = itemView.findViewById(R.id.itemQuantidadeProdutoVendido);
        }
        public void vincula(ProdutoVendido produtoVendido) {
            preencheCampos(produtoVendido);
        }

        private void preencheCampos(ProdutoVendido produtoVendido) {
            itemNomeProduto.setText(produtoVendido.getNomeProduto());
            itemDataProduto.setText(produtoVendido.getDataVenda());
            itemValorProduto.setText(produtoVendido.getValorProduto()+" ouros");
            itemQuantidadeProduto.setText("x "+produtoVendido.getQuantidadeProduto());
        }
    }
}
