package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PROFISSAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_PROFISSAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.kevin.ceep.databinding.FragmentListaProfissoesBinding;
import com.kevin.ceep.model.ProdutoVendido;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoEstoque;
import com.kevin.ceep.ui.recyclerview.adapter.ListaProfissaoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoEspecificoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.Objects;

public class ListaProfissoesFragment extends Fragment {
    private FragmentListaProfissoesBinding binding;
    private ListaProfissaoAdapter listaProfissaoAdapter;
    private String usuarioId, personagemId;
    private ArrayList<Profissao> profissoes;
    private RecyclerView meuRecycler;
    private DatabaseReference minhaReferencia;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar indicadorProgresso;


    public ListaProfissoesFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle argumento = getArguments();
        if (argumento != null) {
            if (argumento.containsKey(CHAVE_PERSONAGEM)) {
                personagemId = argumento.getString(CHAVE_PERSONAGEM);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListaProfissoesBinding.inflate(inflater, container, false);
        requireActivity().setTitle(CHAVE_TITULO_PROFISSAO);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializaComponentes();
        atualizaListaProfissoes();
        configuraSwipeRefreshLayout();
    }

    private void atualizaListaProfissoes() {
        ArrayList<Profissao> profissoes = pegaTodasProfissoes();
        configuraRecyclerView(profissoes);
    }

    private void configuraRecyclerView(ArrayList<Profissao> profissoes) {
        meuRecycler.setHasFixedSize(true);
        meuRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        configuraAdapter(profissoes, meuRecycler);
    }

    private void configuraAdapter(ArrayList<Profissao> profissoes, RecyclerView meuRecycler) {
        listaProfissaoAdapter = new ListaProfissaoAdapter(getContext(), profissoes);
        meuRecycler.setAdapter(listaProfissaoAdapter);
        listaProfissaoAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Profissao profissao, int adapterPosition) {
                Snackbar.make(binding.getRoot(), profissao.getNome(), Snackbar.LENGTH_LONG).show();
            }

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

            }
        });
    }

    private ArrayList<Profissao> pegaTodasProfissoes() {
        minhaReferencia
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        profissoes.clear();
                        for (DataSnapshot dn : dataSnapshot.getChildren()) {
                            Profissao profissao = dn.getValue(Profissao.class);
                            assert profissao != null;
                            profissoes.add(profissao);
                        }
                        indicadorProgresso.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        listaProfissaoAdapter.setLista(profissoes);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Snackbar.make(binding.getRoot(), "Falha ao carregar lista de profissões", Snackbar.LENGTH_LONG).show();
                    }
                });
        return profissoes;
    }
    private void configuraSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            listaProfissaoAdapter.limpaLista();
            if (personagemId != null){
                pegaTodasProfissoes();
            }
        });
    }
    private void inicializaComponentes() {
        profissoes = new ArrayList<>();
        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        meuRecycler = binding.recyclerViewListaProfissoesFragment;
        FirebaseDatabase meuBanco = FirebaseDatabase.getInstance();
        minhaReferencia = meuBanco.getReference(CHAVE_USUARIOS)
                .child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                .child(personagemId)
                .child(CHAVE_LISTA_PROFISSAO);
        swipeRefreshLayout = binding.swipeRefreshLayoutListaProfissoesFragment;
        indicadorProgresso = binding.indicadorProgressoListaProfissoesFragment;
    }

    @Override
    public void onStop() {
        super.onStop();
        binding = null;
    }
}