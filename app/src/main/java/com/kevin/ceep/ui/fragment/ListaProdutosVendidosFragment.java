package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_VENDAS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_ESTOQUE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.databinding.FragmentListaProdutosVendidosBinding;
import com.kevin.ceep.model.ProdutoVendido;
import com.kevin.ceep.ui.recyclerview.adapter.ListaProdutosVendidosAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoProducaoAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class ListaProdutosVendidosFragment extends Fragment {
    private FragmentListaProdutosVendidosBinding binding;
    private ListaProdutosVendidosAdapter produtosVendidosAdapter;
    private String usuarioId, personagemId;
    private ArrayList<ProdutoVendido> produtosVendidos;
    private RecyclerView meuRecycler;
    private DatabaseReference minhaReferencia;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar indicadorProgresso;

    public ListaProdutosVendidosFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle argumento = getArguments();
        if (argumento != null) {
            if (argumento.containsKey(CHAVE_PERSONAGEM)){
                personagemId = argumento.getString(CHAVE_PERSONAGEM);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListaProdutosVendidosBinding.inflate(inflater, container, false);
        requireActivity().setTitle("Produtos vendidos");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializaComponentes();
        atualizaListaProdutosVendidos();
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
                                removeTrabalhoLista(produtoVendidoRemovido);
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
    private void removeTrabalhoLista(ProdutoVendido trabalhoRemovido) {
        minhaReferencia.child(usuarioId).child(CHAVE_LISTA_VENDAS).
                child(trabalhoRemovido.getId()).removeValue();
    }
    private void configuraSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            produtosVendidosAdapter.limpaLista();
            if (personagemId != null){
                pegaTodosProdutosVendidos();
            }
        });
    }
    private void atualizaListaProdutosVendidos() {
        ArrayList<ProdutoVendido> listaProdutosVendidos = pegaTodosProdutosVendidos();
        configuraRecyclerView(listaProdutosVendidos);
    }

    private ArrayList<ProdutoVendido> pegaTodosProdutosVendidos() {
        produtosVendidos = new ArrayList<>();
        minhaReferencia.child(usuarioId).child(CHAVE_LISTA_VENDAS)
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        produtosVendidos.clear();
                        for (DataSnapshot dn : dataSnapshot.getChildren()){
                            ProdutoVendido produtoVendido = dn.getValue(ProdutoVendido.class);
                            if (produtoVendido.getNomePersonagem().equals(personagemId)) {
                                produtosVendidos.add(produtoVendido);
                            }
                        }
                        produtosVendidos.sort(Comparator.comparing(ProdutoVendido::getDataVenda).thenComparing(ProdutoVendido::getNomeProduto).reversed());
                        indicadorProgresso.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        produtosVendidosAdapter.setListaFiltrada(produtosVendidos);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return produtosVendidos;
    }

    private void configuraRecyclerView(ArrayList<ProdutoVendido> produtosVendidos) {
        meuRecycler.setHasFixedSize(true);
        meuRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        configuraAdapter(produtosVendidos, meuRecycler);
    }

    private void configuraAdapter(ArrayList<ProdutoVendido> produtosVendidos, RecyclerView meuRecycler) {
        produtosVendidosAdapter = new ListaProdutosVendidosAdapter(produtosVendidos, getContext());
        meuRecycler.setAdapter(produtosVendidosAdapter);
    }

    private void inicializaComponentes() {
        produtosVendidos = new ArrayList<>();
        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        meuRecycler = binding.recyclerViewListaProdutosVendidos;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        minhaReferencia = database.getReference(CHAVE_USUARIOS);
        swipeRefreshLayout = binding.swipeRefreshLayoutProdutosVendidos;
        indicadorProgresso = binding.indicadorProgressoListaProdutosVendidosFragment;
    }

    @Override
    public void onStop() {
        super.onStop();
        binding = null;
    }
}