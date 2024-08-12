package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TRABALHO;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.kevin.ceep.R;
import com.kevin.ceep.databinding.FragmentListaProdutosVendidosBinding;
import com.kevin.ceep.model.ProdutoVendido;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoEstoque;
import com.kevin.ceep.repository.ProdutosVendidosRepository;
import com.kevin.ceep.ui.activity.AtributosProdutoVendidoActivity;
import com.kevin.ceep.ui.recyclerview.adapter.ListaProdutosVendidosAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoEspecificoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;
import com.kevin.ceep.ui.viewModel.ProdutosVendidosViewModel;
import com.kevin.ceep.ui.viewModel.factory.ProdutosVendidosViewModelFactory;

import java.util.ArrayList;

public class ListaProdutosVendidosFragment extends Fragment {
    private FragmentListaProdutosVendidosBinding binding;
    private ListaProdutosVendidosAdapter produtosVendidosAdapter;
    private String personagemId;
    private ArrayList<ProdutoVendido> produtosVendidos;
    private RecyclerView meuRecycler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar indicadorProgresso;
    private ProdutosVendidosViewModel produtosVendidosViewModel;

    public ListaProdutosVendidosFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recebeDadosIntent();
    }

    private void recebeDadosIntent() {
        Bundle argumento = getArguments();
        if (argumento != null) {
            if (argumento.containsKey(CHAVE_PERSONAGEM)) {
                personagemId = argumento.getString(CHAVE_PERSONAGEM);
                if (personagemId != null) {
                    ProdutosVendidosViewModelFactory produtosVendidosViewModelFactory = new ProdutosVendidosViewModelFactory(new ProdutosVendidosRepository(personagemId));
                    produtosVendidosViewModel = new ViewModelProvider(this, produtosVendidosViewModelFactory).get(ProdutosVendidosViewModel.class);
                }
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListaProdutosVendidosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializaComponentes();
        configuraRecyclerView();
        configuraSwipeRefreshLayout();
        configuraDeslizeItem();
    }
    private void configuraDeslizeItem() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int itemPosicao = viewHolder.getAdapterPosition();
                produtosVendidosAdapter = (ListaProdutosVendidosAdapter) meuRecycler.getAdapter();
                if (produtosVendidosAdapter != null) {
                    ProdutoVendido produtoVendidoRemovido = produtosVendidos.get(itemPosicao);
                    produtosVendidosAdapter.remove(itemPosicao);
                    Snackbar snackbarDesfazer = Snackbar.make(binding.getRoot(), produtoVendidoRemovido.getNomeProduto()+ " excluido", Snackbar.LENGTH_LONG);
                    snackbarDesfazer.addCallback(new Snackbar.Callback(){
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            if (event != DISMISS_EVENT_ACTION){
                                removeTrabalhoDoBanco(produtoVendidoRemovido);
                                removeTrabalhoDaLista(produtoVendidoRemovido);
                            }
                        }
                    });
                    snackbarDesfazer.setAction(getString(R.string.stringDesfazer), v -> produtosVendidosAdapter.adiciona(produtoVendidoRemovido, itemPosicao));
                    snackbarDesfazer.show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(meuRecycler);
    }

    private void removeTrabalhoDaLista(ProdutoVendido produtoVendidoRemovido) {
        produtosVendidos.remove(produtoVendidoRemovido);
    }

    private void removeTrabalhoDoBanco(ProdutoVendido trabalhoRemovido) {
        produtosVendidosViewModel.deletaProduto(trabalhoRemovido).observe(getViewLifecycleOwner(), resultadoRemoveTrabalho -> {
            if (resultadoRemoveTrabalho.getErro() != null) {
                Snackbar.make(binding.getRoot(), "Erro: "+resultadoRemoveTrabalho.getErro(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
    private void configuraSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            produtosVendidosAdapter.limpaLista();
            if (personagemId != null){
                pegaTodosProdutosVendidos();
            }
        });
    }

    private void pegaTodosProdutosVendidos() {
        produtosVendidosViewModel.pegaTodosProdutosVendidos().observe(getViewLifecycleOwner(), resultadoTodosProdutos -> {
            if (resultadoTodosProdutos.getDado() != null) {
                produtosVendidos = resultadoTodosProdutos.getDado();
                indicadorProgresso.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                produtosVendidosAdapter.atualiza(produtosVendidos);
            }
            if (resultadoTodosProdutos.getErro() != null) {
                Snackbar.make(binding.getRoot(), "Erro: "+resultadoTodosProdutos.getErro(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void configuraRecyclerView() {
        meuRecycler.setHasFixedSize(true);
        meuRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        configuraAdapter(meuRecycler);
    }

    private void configuraAdapter(RecyclerView meuRecycler) {
        produtosVendidosAdapter = new ListaProdutosVendidosAdapter(produtosVendidos, getContext());
        meuRecycler.setAdapter(produtosVendidosAdapter);
        produtosVendidosAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Trabalho trabalho, int adapterPosition) {

            }

            @Override
            public void onItemClick(ListaTrabalhoEspecificoAdapter trabalhoEspecificoAdapter) {

            }

            @Override
            public void onItemClick(TrabalhoEstoque trabalhoEstoque, int adapterPosition, int botaoId) {

            }

            @Override
            public void onItemClick(ProdutoVendido produtoVendido) {
                vaiParaAtributoProdutoVendido(produtoVendido);
            }
        });
    }

    private void vaiParaAtributoProdutoVendido(ProdutoVendido produtoVendido) {
        Intent iniciaVaiParaAtributosProdutoVendido = new Intent(getContext(), AtributosProdutoVendidoActivity.class);
        iniciaVaiParaAtributosProdutoVendido.putExtra(CHAVE_TRABALHO, produtoVendido);
        iniciaVaiParaAtributosProdutoVendido.putExtra(CHAVE_PERSONAGEM, personagemId);
        startActivity(iniciaVaiParaAtributosProdutoVendido);
    }

    private void inicializaComponentes() {
        produtosVendidos = new ArrayList<>();
        meuRecycler = binding.recyclerViewListaProdutosVendidos;
        swipeRefreshLayout = binding.swipeRefreshLayoutProdutosVendidos;
        indicadorProgresso = binding.indicadorProgressoListaProdutosVendidosFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (personagemId != null) {
            pegaTodosProdutosVendidos();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        binding = null;
    }
}