package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_ESTOQUE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_ESTOQUE;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.kevin.ceep.R;
import com.kevin.ceep.databinding.FragmentListaEstoqueBinding;
import com.kevin.ceep.databinding.FragmentListaProfissoesBinding;
import com.kevin.ceep.model.ProdutoVendido;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoEstoque;
import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.repository.TrabalhoEstoqueRepository;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoEspecificoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoEstoqueAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoProducaoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;
import com.kevin.ceep.ui.viewModel.TrabalhoEstoqueViewModel;
import com.kevin.ceep.ui.viewModel.factory.TrabalhoEstoqueViewModelFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListaEstoqueFragment extends Fragment {
    private FragmentListaEstoqueBinding binding;
    private ListaTrabalhoEstoqueAdapter trabalhoEstoqueAdapter;
    private RecyclerView recyclerView;
    private ArrayList<TrabalhoEstoque> todosTrabalhosEstoque;
    private String personagemId;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar indicadorDeProgresso;
    private TrabalhoEstoqueViewModel trabalhoEstoqueViewModel;
    public ListaEstoqueFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle argumento = getArguments();
        if (argumento != null) {
            if (argumento.containsKey(CHAVE_PERSONAGEM)){
                personagemId = argumento.getString(CHAVE_PERSONAGEM);
                Log.d("fragmentoEstoque", "ID do personagem recebido: "+personagemId);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListaEstoqueBinding.inflate(inflater, container, false);
        requireActivity().setTitle(CHAVE_TITULO_ESTOQUE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializaComponentes();
        configuraRecyclerView();
        configuraDeslizeItem();
        configuraSwipeRefreshLayout();
    }
    private void configuraSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(()->{
            if (personagemId != null){
                atualizaListaEstoque();
            }
        });
    }

    private void atualizaListaEstoque() {
        trabalhoEstoqueViewModel.pegaTodosTrabalhosEstoque().observe(getViewLifecycleOwner(), resultado -> {
            todosTrabalhosEstoque = resultado.getDado();
            if (todosTrabalhosEstoque != null) {
                indicadorDeProgresso.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                if (todosTrabalhosEstoque.isEmpty()) {
                    Snackbar.make(binding.getRoot(), "Estoque vazio", Snackbar.LENGTH_LONG).show();
                } else {
                    trabalhoEstoqueAdapter.atualiza(todosTrabalhosEstoque);
                }
            } else if (resultado.getErro() != null) {
                Snackbar.make(binding.getRoot(), "Erro: "+resultado.getErro(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void inicializaComponentes() {
        recyclerView = binding.listaTrabalhoEstoqueRecyclerView;
        swipeRefreshLayout = binding.swipeRefreshLayoutTrabalhosEstoque;
        indicadorDeProgresso = binding.indicadorProgressoListaEstoqueFragment;
        todosTrabalhosEstoque = new ArrayList<>();
        TrabalhoEstoqueViewModelFactory trabalhoEstoqueViewModelFactory = new TrabalhoEstoqueViewModelFactory(new TrabalhoEstoqueRepository(personagemId));
        trabalhoEstoqueViewModel = new ViewModelProvider(this, trabalhoEstoqueViewModelFactory).get(TrabalhoEstoqueViewModel.class);
    }
    private void configuraRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        configuraAdapter(todosTrabalhosEstoque, recyclerView);
    }
    private void configuraAdapter(List<TrabalhoEstoque> todosTrabalhosEstoque, RecyclerView listaTrabalhos) {
        trabalhoEstoqueAdapter = new ListaTrabalhoEstoqueAdapter(todosTrabalhosEstoque,getContext());
        listaTrabalhos.setAdapter(trabalhoEstoqueAdapter);
        trabalhoEstoqueAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Trabalho trabalho, int adapterPosition) {

            }

            @Override
            public void onItemClick(ListaTrabalhoEspecificoAdapter trabalhoEspecificoAdapter) {

            }

            @Override
            public void onItemClick(TrabalhoEstoque trabalhoEstoque, int adapterPosition, int botaoId) {
                alteraQuantidade(trabalhoEstoque, adapterPosition, botaoId);
            }

            @Override
            public void onItemClick(ProdutoVendido produtoVendido) {

            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void alteraQuantidade(TrabalhoEstoque trabalhoEstoqueModificado, int adapterPosition, int botaoId) {
        int novaQuantidade = trabalhoEstoqueModificado.getQuantidade();
        switch (botaoId) {
            case R.id.itemBotaoMenosUm:
                if (trabalhoEstoqueModificado.getQuantidade() > 0) {
                    novaQuantidade -= 1;
                }
                break;
            case R.id.itemBotaoMaisUm:
                novaQuantidade += 1;
                break;
            case R.id.itemBotaoMenosCinquenta:
                if (trabalhoEstoqueModificado.getQuantidade() > 0) {
                    novaQuantidade -= 50;
                    if (novaQuantidade < 0) {
                        novaQuantidade = 0;
                    }
                }
                break;
            case R.id.itemBotaoMaisCinquenta:
                novaQuantidade += 50;
                break;
        }
        trabalhoEstoqueModificado.setQuantidade(novaQuantidade);
        trabalhoEstoqueViewModel.modificaQuantidadeTrabalhoEspecificoNoEstoque(trabalhoEstoqueModificado).observe(this, resultado -> {
            if (resultado.getErro() != null) {
                Snackbar.make(binding.getRoot(),resultado.getErro(),Snackbar.LENGTH_SHORT).show();
            } else {
                trabalhoEstoqueAdapter.altera(adapterPosition,trabalhoEstoqueModificado);
            }
        });
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
                ListaTrabalhoEstoqueAdapter trabalhoAdapter = (ListaTrabalhoEstoqueAdapter) recyclerView.getAdapter();
                if (trabalhoAdapter != null) {
                    TrabalhoEstoque trabalhoremovido = todosTrabalhosEstoque.get(itemPosicao);
                    trabalhoAdapter.remove(itemPosicao);
                    Snackbar snackbarDesfazer = Snackbar.make(binding.getRoot(), trabalhoremovido.getNome()+ " excluido", Snackbar.LENGTH_LONG);
                    snackbarDesfazer.addCallback(new Snackbar.Callback(){
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            if (event != DISMISS_EVENT_ACTION){
                                removeTrabalhoDoBanco(trabalhoremovido);
                                removeTrabalhoDaLista(trabalhoremovido);
                            }
                        }
                    });
                    snackbarDesfazer.setAction(getString(R.string.stringDesfazer), v -> trabalhoEstoqueAdapter.adiciona(trabalhoremovido, itemPosicao));
                    snackbarDesfazer.show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void removeTrabalhoDaLista(TrabalhoEstoque trabalhoRemovido) {
        todosTrabalhosEstoque.remove(trabalhoRemovido);
    }

    private void removeTrabalhoDoBanco(TrabalhoEstoque trabalhoremovido) {
        trabalhoEstoqueViewModel.deletaTrabalhoEstoque(trabalhoremovido).observe(this, resultadoRemoveTrabalho -> {
            if (resultadoRemoveTrabalho.getErro() != null) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        atualizaListaEstoque();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}