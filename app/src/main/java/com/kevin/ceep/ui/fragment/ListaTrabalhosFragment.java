package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_ALTERA_TRABALHO;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.ui.activity.ListaRaridadeActivity;
import com.kevin.ceep.ui.activity.TrabalhoEspecificoActivity;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoProducaoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class ListaTrabalhosFragment extends Fragment {
    private static final String TAG="MainActivity";
    ActivityResultLauncher<Intent> activityLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(TAG,"onActivityResult");
                if (result.getResultCode()==1){
                    Intent intent=result.getData();
                    if (intent!=null){

                    }
                }
            }
    );
    private DatabaseReference databaseReference;
    private ListaTrabalhoProducaoAdapter trabalhoAdapter;
    private RecyclerView recyclerView;
    private List<TrabalhoProducao> trabalhos;
    private String usuarioId, personagemId;
    public ListaTrabalhosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle dadosRecebidos = getArguments();
        if (dadosRecebidos != null) {
            if (dadosRecebidos.containsKey(CHAVE_PERSONAGEM)){
                personagemId = getArguments().getString(CHAVE_PERSONAGEM);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista_trabalhos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializaComponentes();
        atualizaListaTrabalho(view);
        configuraSwipeRefreshLayout(view);
        configuraBotaoInsereTrabalho(view);
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
                int posicaoDeslize = viewHolder.getAdapterPosition();
                ListaTrabalhoProducaoAdapter trabalhoAdapter = (ListaTrabalhoProducaoAdapter) recyclerView.getAdapter();
                removeTrabalhoLista(posicaoDeslize);
                if (trabalhoAdapter != null) {
                    trabalhoAdapter.remove(posicaoDeslize);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void removeTrabalhoLista(int swipePosicao) {
        String idTrabalho = trabalhos.get(swipePosicao).getId();
        databaseReference.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                child(personagemId).child(CHAVE_LISTA_DESEJO).
                child(idTrabalho).removeValue();
    }
    private void configuraSwipeRefreshLayout(View view) {
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutTrabalhos);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            atualizaListaTrabalho(view);
        });
    }
    private void configuraBotaoInsereTrabalho(View view) {
        FloatingActionButton botaoInsereTrabaho = view.findViewById(R.id.floatingActionButton);
        botaoInsereTrabaho.setOnClickListener(v -> vaiParaRaridadeActivity());
    }
    private void vaiParaRaridadeActivity() {
        Intent iniciaListaRaridade =
                new Intent(getContext(),
                        ListaRaridadeActivity.class);
        iniciaListaRaridade.putExtra(CHAVE_PERSONAGEM, personagemId);
        startActivity(iniciaListaRaridade,
                ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        //startActivityForResult(iniciaFormularioNota, CODIGO_REQUISICAO_INSERE_NOTA);
    }

    private void inicializaComponentes() {
        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(CHAVE_USUARIOS);
    }
    private void atualizaListaTrabalho(View view) {
        List<TrabalhoProducao> todosTrabalhos = pegaTodosTrabalhos();
        configuraRecyclerView(todosTrabalhos, view);
    }
    private void configuraRecyclerView(List<TrabalhoProducao> todosTrabalhos, View view) {
        recyclerView = view.findViewById(R.id.listaTrabalhoRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        configuraAdapter(todosTrabalhos, recyclerView);
    }
    private void configuraAdapter(List<TrabalhoProducao> todosTrabalhos, RecyclerView listaTrabalhos) {
        trabalhoAdapter = new ListaTrabalhoProducaoAdapter(getContext(),todosTrabalhos);
        listaTrabalhos.setAdapter(trabalhoAdapter);
        trabalhoAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Profissao profissao, int posicao) {

            }

            @Override
            public void onItemClick(Personagem personagem, int posicao) {

            }

            @Override
            public void onItemClick(Trabalho trabalho, int adapterPosition) {
                vaiParaTrabalhoEspecificoActivity(trabalho);
            }

            @Override
            public void onItemClick(Raridade raridade, int adapterPosition) {

            }
        });
    }
    private void vaiParaTrabalhoEspecificoActivity(Trabalho trabalho) {
        Intent iniciaTrabalhoEspecificoActivity=
                new Intent(getActivity(), TrabalhoEspecificoActivity.class);
        iniciaTrabalhoEspecificoActivity.putExtra(CHAVE_TRABALHO, CODIGO_REQUISICAO_ALTERA_TRABALHO);
        iniciaTrabalhoEspecificoActivity.putExtra(CHAVE_NOME_TRABALHO, trabalho);
        iniciaTrabalhoEspecificoActivity.putExtra(CHAVE_PERSONAGEM, personagemId);
        activityLauncher.launch(iniciaTrabalhoEspecificoActivity);
    }
    private List<TrabalhoProducao> pegaTodosTrabalhos() {
        trabalhos = new ArrayList<>();
        Log.d("USUARIO", usuarioId);
        databaseReference.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                child(personagemId).child(CHAVE_LISTA_DESEJO).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        trabalhos.clear();
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            TrabalhoProducao trabalho = dn.getValue(TrabalhoProducao.class);
                            trabalhos.add(trabalho);
                        }
                        trabalhoAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return trabalhos;
    }
}