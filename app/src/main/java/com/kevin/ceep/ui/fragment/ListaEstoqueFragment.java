package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_ESTOQUE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_ESTOQUE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.Raridade;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoEstoque;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoEstoqueAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListaEstoqueFragment extends Fragment {
    private ListaTrabalhoEstoqueAdapter trabalhoEstoqueAdapter;
    private DatabaseReference minhaReferencia;
    private RecyclerView recyclerView;
    private List<TrabalhoEstoque> trabalhos;
    private List<Personagem> personagens;
    private String usuarioId, personagemId;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Menu itemMenuPersonagem;// Variavel provisoria
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
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_personagem, menu);
        itemMenuPersonagem = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        for (Personagem personagem: personagens){
            Log.d("listaPersonagens", personagem.getNome());
            if (personagem.getNome().equals(item.getTitle().toString())){
                personagemId = personagem.getId();
                atualizaListaEstoque();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
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
    private List<Personagem> pegaTodosPersonagens() {
        Log.d("listaPersonagens", "Entrou na funçao pegaTodosPersonagens");
        personagens = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(CHAVE_USUARIOS);
        databaseReference.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        personagens.clear();
                        Log.d("listaPersonagens", "Limpou a lista de personagens");
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            Personagem personagem = dn.getValue(Personagem.class);
                            personagens.add(personagem);
                            if (personagem != null) {
                                Log.d("listaPersonagens", "Personagem adicionado: " + personagem.getNome());
                            }
                            itemMenuPersonagem.add(personagem != null ? personagem.getNome() : null);
                        }
                        personagemId = personagens.get(0).getId();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("listaPersonagens", "Erro ao definir lista de personagens.");
                    }

                });
        Log.d("listaPersonagens", "Saiu da funçao pegaTodosPersonagens");
        return personagens;
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
            public void onItemClick(Profissao profissao, int posicao) {

            }

            @Override
            public void onItemClick(Personagem personagem, int posicao) {

            }

            @Override
            public void onItemClick(Trabalho trabalho, int adapterPosition) {

            }

            @Override
            public void onItemClick(Raridade raridade, int adapterPosition) {

            }

            @Override
            public void onItemClick(TrabalhoEstoque trabalhoEstoque, int adapterPosition, int botaoId) {
                alteraQuantidade(trabalhoEstoque, adapterPosition, botaoId);
            }
        });
    }

    private void alteraQuantidade(TrabalhoEstoque trabalhoEstoque, int adapterPosition, int botaoId) {
        if (botaoId == R.id.itemBotaoMenosUm){
            minhaReferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemId).child(CHAVE_LISTA_ESTOQUE)
                    .child(trabalhoEstoque.getId()).child("quantidade")
                    .setValue(trabalhoEstoque.getQuantidade()-1, ((databaseError, databaseReference) -> {
                        if (databaseError != null){
                            Snackbar.make(layoutFragmentoEstoque,databaseError.getMessage(),Snackbar.LENGTH_SHORT).show();
                        } else {
                            TrabalhoEstoque trabalhoAlterado = new TrabalhoEstoque(
                                    trabalhoEstoque.getId(),
                                    trabalhoEstoque.getNome(),
                                    trabalhoEstoque.getQuantidade()-1);
                            Snackbar.make(layoutFragmentoEstoque,"Quantidade alterada!",Snackbar.LENGTH_SHORT).show();
                            trabalhoEstoqueAdapter.altera(adapterPosition,trabalhoAlterado);
                        }
                    }));
        }else if (botaoId == R.id.itemBotaoMaisUm){
            minhaReferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemId).child(CHAVE_LISTA_ESTOQUE)
                    .child(trabalhoEstoque.getId()).child("quantidade")
                    .setValue(trabalhoEstoque.getQuantidade() + 1, (databaseError, databaseReference) -> {
                        if (databaseError != null){
                            Snackbar.make(layoutFragmentoEstoque,databaseError.getMessage(),Snackbar.LENGTH_SHORT).show();
                        } else {
                            TrabalhoEstoque trabalhoAlterado = new TrabalhoEstoque(
                                    trabalhoEstoque.getId(),
                                    trabalhoEstoque.getNome(),
                                    trabalhoEstoque.getQuantidade()+1);
                            Snackbar.make(layoutFragmentoEstoque,"Quantidade alterada!",Snackbar.LENGTH_SHORT).show();
                            trabalhoEstoqueAdapter.altera(adapterPosition,trabalhoAlterado);
                        }
                    });
        }
    }

    private List<TrabalhoEstoque> pegaTodosTrabalhosEstoque() {
        trabalhos = new ArrayList<>();
        Log.d("fragmentoEstoque","ID do usuario: "+usuarioId);
        minhaReferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                child(personagemId).child(CHAVE_LISTA_ESTOQUE).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        trabalhos.clear();
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            TrabalhoEstoque trabalho = dn.getValue(TrabalhoEstoque.class);
                            trabalhos.add(trabalho);
                        }
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