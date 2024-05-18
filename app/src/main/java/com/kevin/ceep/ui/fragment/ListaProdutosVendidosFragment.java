package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kevin.ceep.R;
import com.kevin.ceep.databinding.FragmentListaProdutosVendidosBinding;
import com.kevin.ceep.model.ProdutoVendido;

import java.util.ArrayList;
import java.util.Objects;

public class ListaProdutosVendidosFragment extends Fragment {
    private FragmentListaProdutosVendidosBinding binding;
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
        configuraRecyclerView(produtosVendidos);
    }

    private void configuraRecyclerView(ArrayList<ProdutoVendido> produtosVendidos) {
        meuRecycler.setHasFixedSize(true);
        meuRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        configuraAdapter(produtosVendidos, meuRecycler);
    }

    private void configuraAdapter(ArrayList<ProdutoVendido> produtosVendidos, RecyclerView meuRecycler) {

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