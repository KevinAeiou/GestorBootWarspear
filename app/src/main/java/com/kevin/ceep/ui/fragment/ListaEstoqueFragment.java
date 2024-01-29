package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_ESTOQUE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.model.TrabalhoEstoque;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoEstoqueAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListaEstoqueFragment extends Fragment {
    private ListaTrabalhoEstoqueAdapter trabalhoEstoqueAdapter;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private List<TrabalhoEstoque> trabalhos;
    private List<Personagem> personagens;
    private String usuarioId, personagemId;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Menu itemMenuPersonagem;// Variavel provisoria


    public ListaEstoqueFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param personagemId Parameter 1.
     * @return A new instance of fragment EstoqueFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaEstoqueFragment novaInstanciaListaEstoque(String personagemId) {
        ListaEstoqueFragment fragment = new ListaEstoqueFragment();
        Bundle argumentos = new Bundle();
        argumentos.putString(CHAVE_PERSONAGEM, personagemId);
        fragment.setArguments(argumentos);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle argumento = getArguments();
        if (argumento != null) {
            if (argumento.containsKey(CHAVE_PERSONAGEM)){
                personagemId = argumento.toString();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lista_estoque, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializaComponentes(view);
        List<Personagem> todosPersonagens = pegaTodosPersonagens();
        if (todosPersonagens.size() > 0){
            atualizaListaEstoque();
        }
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
        databaseReference = database.getReference(CHAVE_USUARIOS);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutTrabalhosEstoque);
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
                            Log.d("listaPersonagens", "Personagem adicionado: " + personagem.getNome());
                            itemMenuPersonagem.add(personagem.getNome());
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
    }
    private List<TrabalhoEstoque> pegaTodosTrabalhosEstoque() {
        trabalhos = new ArrayList<>();
        Log.d("pegaEstoque",personagemId);
        databaseReference.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return trabalhos;
    }

    /*private void listaBusca(String textoBusca) {
        List<Trabalho> listaFiltro = new ArrayList<>();
        for (Trabalho trabalho:trabalhos){
            if (removerAcentos(trabalho.getNome().toLowerCase()).contains(removerAcentos(textoBusca.toLowerCase()))){
                listaFiltro.add(trabalho);
            }
        }
        if (listaFiltro.isEmpty()){
            Snackbar.make(getView().findViewById(R.id.frameLayout),"Nada encontrado...",Snackbar.LENGTH_SHORT).show();
        }else{
            trabalhoEstoqueAdapter.setListaFiltrada(listaFiltro);
        }
    }*/
}