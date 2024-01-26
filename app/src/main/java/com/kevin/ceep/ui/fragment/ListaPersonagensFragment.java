package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.kevin.ceep.ui.activity.AtributosPersonagemActivity;
import com.kevin.ceep.ui.recyclerview.adapter.ListaPersonagemAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaPersonagensFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaPersonagensFragment extends Fragment {

    ActivityResultLauncher<Intent> activityLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d("activityResultLauncher","onActivityResult");
                if (result.getResultCode()==1){
                    Intent intent=result.getData();
                    if (intent!=null){

                    }
                }
            }
    );
    private ListaPersonagemAdapter personagemAdapter;
    private List<Personagem> personagens;
    private FirebaseAuth minhaAutenticacao;
    private RecyclerView recyclerView;
    private DatabaseReference minhaReferencia;
    private String usuarioId;

    public ListaPersonagensFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListaPersonagensFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaPersonagensFragment newInstance(String param1, String param2) {
        ListaPersonagensFragment fragment = new ListaPersonagensFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lista_personagens, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializaComponentes(view);
        atualizaListaPersonagem();
        configuraSwipeRefreshLayout(view);
        configuraBotaoCadastraPersonagem(view);
    }

    private void configuraBotaoCadastraPersonagem(View view) {
        FloatingActionButton botaoCadastraPersonagem = view.findViewById(R.id.botaoAFlutuantePersonagem);
        botaoCadastraPersonagem.setOnClickListener(view1 -> vaiParaAtributosPersonagemActivity(new Personagem()));
    }

    private void inicializaComponentes(View view) {
        minhaAutenticacao = FirebaseAuth.getInstance();
        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        minhaReferencia = database.getReference(CHAVE_USUARIOS);
        recyclerView = view.findViewById(R.id.listaPersonagensRecyclerViewFragment);
    }
    private void atualizaListaPersonagem() {
        List<Personagem> todosPersonagens = pegaTodosPersonagens();
        configuraRecyclerView(todosPersonagens);
    }
    private void configuraRecyclerView(List<Personagem> todosPersonagens) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Collections.sort(todosPersonagens, (p1, p2) -> p1.getNome().compareToIgnoreCase(p2.getNome()));
        configuraAdapter(todosPersonagens,recyclerView);
    }
    private void configuraAdapter(List<Personagem> todosPersonagens, RecyclerView recyclerView) {
        personagemAdapter = new ListaPersonagemAdapter(getContext(),todosPersonagens);
        recyclerView.setAdapter(personagemAdapter);

        personagemAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Profissao profissao, int posicao) {

            }

            @Override
            public void onItemClick(Personagem personagem, int posicao) {
                vaiParaAtributosPersonagemActivity(personagem);
            }

            @Override
            public void onItemClick(Trabalho trabalho, int adapterPosition) {

            }

            @Override
            public void onItemClick(Raridade raridade, int adapterPosition) {

            }
        });
    }
    private void vaiParaAtributosPersonagemActivity(Personagem personagem) {
        Log.d("PERSONAGEM", personagem.getId());
        Intent iniciaAtributosPersonagemActivity = new Intent(getContext(), AtributosPersonagemActivity.class);
        iniciaAtributosPersonagemActivity.putExtra(CHAVE_PERSONAGEM,
                personagem);
        activityLauncher.launch(iniciaAtributosPersonagemActivity);
    }
    private void configuraSwipeRefreshLayout(View view) {
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutPersonagemFragment);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            atualizaListaPersonagem();
        });
    }
    private List<Personagem> pegaTodosPersonagens() {
        personagens = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Log.d("PERSONAGEMINICIO", String.valueOf(personagens.size()));
        DatabaseReference databaseReference = database.getReference(CHAVE_USUARIOS);
        databaseReference.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        personagens.clear();
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            Personagem personagem = dn.getValue(Personagem.class);
                            personagens.add(personagem);
                        }
                        personagemAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        Log.d("PERSONAGEMFIM", String.valueOf(personagens.size()));
        return personagens;
    }
}