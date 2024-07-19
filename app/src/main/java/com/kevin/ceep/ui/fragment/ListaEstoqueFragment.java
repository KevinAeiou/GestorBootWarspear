package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_ESTOQUE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_ESTOQUE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.model.ProdutoVendido;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoEstoque;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoEspecificoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoEstoqueAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ListaEstoqueFragment extends Fragment {
    private ListaTrabalhoEstoqueAdapter trabalhoEstoqueAdapter;
    private DatabaseReference minhaReferencia;
    private RecyclerView recyclerView;
    private List<TrabalhoEstoque> trabalhos;
    private String usuarioId, personagemId;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConstraintLayout layoutFragmentoEstoque;
    public ListaEstoqueFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        requireActivity().setTitle(CHAVE_TITULO_ESTOQUE);
        return inflater.inflate(R.layout.fragment_lista_estoque, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializaComponentes(view);
        atualizaListaEstoque();
        configuraSwipeRefreshLayout();
    }
    private void configuraSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(()->{
            if (personagemId != null){
                atualizaListaEstoque();
            }
        });
    }

    private void inicializaComponentes(View view) {
        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        recyclerView = view.findViewById(R.id.listaTrabalhoEstoqueRecyclerView);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        minhaReferencia = database.getReference(CHAVE_USUARIOS);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutTrabalhosEstoque);
        layoutFragmentoEstoque = view.findViewById(R.id.constraintLayoutFragmentoListaEstoque);
    }
    private void atualizaListaEstoque() {
        List<TrabalhoEstoque> todosTrabalhosEstoque = pegaTodosTrabalhosEstoque();
        configuraRecyclerView(todosTrabalhosEstoque);
    }
    private void configuraRecyclerView(List<TrabalhoEstoque> todosTrabalhosEstoque) {
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

    private void alteraQuantidade(TrabalhoEstoque trabalhoEstoque, int adapterPosition, int botaoId) {
        int novaQuantidade = trabalhoEstoque.getQuantidade();
        if (botaoId == R.id.itemBotaoMenosUm && trabalhoEstoque.getQuantidade() > 0){
            novaQuantidade -= 1;
        } else if (botaoId == R.id.itemBotaoMenosCinquenta && trabalhoEstoque.getQuantidade() > 0) {
            novaQuantidade -= 50;
            if (novaQuantidade < 0) {
                novaQuantidade = 0;
            }
        } else if (botaoId == R.id.itemBotaoMaisUm){
            novaQuantidade += 1;
        } else if (botaoId == R.id.itemBotaoMaisCinquenta) {
            novaQuantidade += 50;
        }
        int finalNovaQuantidade = novaQuantidade;
        minhaReferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemId).child(CHAVE_LISTA_ESTOQUE)
                    .child(trabalhoEstoque.getId()).child("quantidade")
                    .setValue(novaQuantidade, ((databaseError, databaseReference) -> {
                        if (databaseError != null){
                            Snackbar.make(layoutFragmentoEstoque,databaseError.getMessage(),Snackbar.LENGTH_SHORT).show();
                        } else {
                            TrabalhoEstoque trabalhoAlterado = new TrabalhoEstoque(
                                    trabalhoEstoque.getId(),
                                    trabalhoEstoque.getNome(),
                                    trabalhoEstoque.getNomeProducao(),
                                    trabalhoEstoque.getProfissao(),
                                    trabalhoEstoque.getRaridade(),
                                    trabalhoEstoque.getTrabalhoNecessario(),
                                    trabalhoEstoque.getNivel(),
                                    trabalhoEstoque.getExperiencia(),
                                    finalNovaQuantidade);
                            Snackbar.make(layoutFragmentoEstoque,"Quantidade alterada!",Snackbar.LENGTH_SHORT).show();
                            trabalhoEstoqueAdapter.altera(adapterPosition,trabalhoAlterado);
                        }
                    }));
    }

    private List<TrabalhoEstoque> pegaTodosTrabalhosEstoque() {
        trabalhos = new ArrayList<>();
        Log.d("fragmentoEstoque","ID do usuario: "+usuarioId);
        minhaReferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                child(personagemId).child(CHAVE_LISTA_ESTOQUE).
                addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        trabalhos.clear();
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            TrabalhoEstoque trabalho = dn.getValue(TrabalhoEstoque.class);
                            if (trabalho != null && trabalho.getNome().equals("Licença de produção do aprendiz")) {
                                trabalho.setProfissao("");
                            }
                            trabalhos.add(trabalho);
                        }
                        trabalhos.sort(Comparator.comparing(TrabalhoEstoque::getProfissao).thenComparing(Trabalho::getNivel).thenComparing(Trabalho::getRaridade).thenComparing(TrabalhoEstoque::getNome));
                        trabalhoEstoqueAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return trabalhos;
    }
}