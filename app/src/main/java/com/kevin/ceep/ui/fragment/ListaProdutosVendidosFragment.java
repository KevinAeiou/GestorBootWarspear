package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_VENDAS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializaComponentes();
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
                            produtosVendidos.add(produtoVendido);
                        }
                        // produtosVendidos.sort(Comparator.comparing(ProdutoVendido::getNome));
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