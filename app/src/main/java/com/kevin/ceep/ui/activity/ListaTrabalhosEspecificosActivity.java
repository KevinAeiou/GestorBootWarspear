package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.ListaTrabalhosActivity.removerAcentos;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PROFISSAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_RARIDADE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.databinding.ActivityListaTrabalhosEspecificosBinding;
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.Raridade;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoEstoque;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoEspecificoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListaTrabalhosEspecificosActivity extends AppCompatActivity {
    private ActivityListaTrabalhosEspecificosBinding binding;
    private ListaTrabalhoEspecificoAdapter trabalhoAdapter;
    private Profissao profissaoRecebido;
    private Raridade raridadeRecebido;
    private FloatingActionButton botaoNovoTrabalho;
    private RecyclerView recyclerView;
    private List<Trabalho> trabalhos;
    private String personagemId;
    private CircularProgressIndicator indicadorCircular;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaTrabalhosEspecificosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(CHAVE_TITULO_TRABALHO);

        recebeDadosIntent();
        inicializaComponentes();
        atualizaListaTodosTrabalho();

        configuraBotaoInsereTrabalho();
        configuraDeslizeItem();
        configuraSwipeRefreshLayout();
        Log.i(TAG_ACTIVITY,"onCreateListaTrabalhosEspecificos");
        }
    private void inicializaComponentes() {
        botaoNovoTrabalho = binding.listaTodosTrabalhosfloatingActionButton;
        indicadorCircular = binding.listaTodosTrabalhosIndicadorProgresso;
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
                ListaTrabalhoEspecificoAdapter trabalhoAdapter = (ListaTrabalhoEspecificoAdapter) recyclerView.getAdapter();
                removeTrabalhoLista(posicaoDeslize);
                if (trabalhoAdapter != null) {
                    trabalhoAdapter.remove(posicaoDeslize);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    private void listaBusca(String textoBusca) {
        List<Trabalho> listaFiltro = new ArrayList<>();
        for (Trabalho trabalho:trabalhos){
            if (removerAcentos(trabalho.getNome().toLowerCase()).contains(removerAcentos(textoBusca.toLowerCase()))){
                listaFiltro.add(trabalho);
            }
        }
        if (listaFiltro.isEmpty()){
            Toast.makeText(this,"Nada encontrado...",Toast.LENGTH_SHORT).show();
        }else{
            trabalhoAdapter.setListaFiltrada(listaFiltro);
        }
    }

    private void removeTrabalhoLista(int posicaoDeslize) {
        String idTrabalho = trabalhos.get(posicaoDeslize).getId();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhaReferencia = database.getReference(CHAVE_LISTA_TRABALHO);
        minhaReferencia.child(idTrabalho).removeValue();
    }

    private void configuraBotaoInsereTrabalho() {
        botaoNovoTrabalho.setOnClickListener(view -> vaiParaTrabalhoEspecificoActivity());
    }

    private void vaiParaTrabalhoEspecificoActivity() {
        Intent cadastraNovoTrabalho=
                new Intent(getApplicationContext(),
                        TrabalhoEspecificoActivity.class);
        cadastraNovoTrabalho.putExtra(CHAVE_TRABALHO, CODIGO_REQUISICAO_INSERE_TRABALHO);
        cadastraNovoTrabalho.putExtra(CHAVE_PERSONAGEM, personagemId);
        cadastraNovoTrabalho.putExtra(CHAVE_NOME_PROFISSAO,profissaoRecebido);
        cadastraNovoTrabalho.putExtra(CHAVE_NOME_RARIDADE,raridadeRecebido);
        startActivity(cadastraNovoTrabalho,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private void configuraSwipeRefreshLayout() {
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.listaTodosTrabalhosSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            atualizaListaTodosTrabalho();
        });
    }

    private void atualizaListaTodosTrabalho() {
        List<Trabalho> todosTrabalhos = pegaTodosTrabalhos();
        configuraRecyclerView(todosTrabalhos);
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_NOME_PROFISSAO)) {
            raridadeRecebido = (Raridade) dadosRecebidos
                    .getSerializableExtra(CHAVE_NOME_RARIDADE);
            profissaoRecebido = (Profissao) dadosRecebidos
                    .getSerializableExtra(CHAVE_NOME_PROFISSAO);
            personagemId = (String) dadosRecebidos.
                    getSerializableExtra(CHAVE_PERSONAGEM);
        }
    }

    private List<Trabalho> pegaTodosTrabalhos() {
        trabalhos = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Lista_trabalhos");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trabalhos.clear();
                for (DataSnapshot dn:dataSnapshot.getChildren()){
                    Trabalho trabalho = dn.getValue(Trabalho.class);
                    if (trabalho != null){
                        trabalhos.add(trabalho);
                    }
                }
                trabalhos.sort(Comparator.comparing(Trabalho::getProfissao).thenComparing(Trabalho::getNivel).thenComparing(Trabalho::getNome));
                trabalhoAdapter.notifyDataSetChanged();
                indicadorCircular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return trabalhos;
    }

    private void configuraRecyclerView(List<Trabalho> todosTrabalhos) {
        recyclerView = binding.listaTodosTrabalhosRecyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        configuraAdapter(todosTrabalhos, recyclerView);
    }

    private void configuraAdapter(List<Trabalho> todosTrabalhos, RecyclerView listaTrabalhos) {
        trabalhoAdapter = new ListaTrabalhoEspecificoAdapter(this,todosTrabalhos);
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

                Intent iniciaTrabalhosActivity =
                        new Intent(ListaTrabalhosEspecificosActivity.this,
                                ConfirmaTrabalhoActivity.class);

                iniciaTrabalhosActivity.putExtra(CHAVE_NOME_TRABALHO, trabalho);
                iniciaTrabalhosActivity.putExtra(CHAVE_PERSONAGEM, personagemId);
                startActivity(iniciaTrabalhosActivity,
                        ActivityOptions.makeSceneTransitionAnimation(ListaTrabalhosEspecificosActivity.this).toBundle());

            }

            @Override
            public void onItemClick(Raridade raridade, int adapterPosition) {

            }

            @Override
            public void onItemClick(TrabalhoEstoque trabalhoEstoque, int adapterPosition, int botaoId) {

            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        binding = null;
    }
}