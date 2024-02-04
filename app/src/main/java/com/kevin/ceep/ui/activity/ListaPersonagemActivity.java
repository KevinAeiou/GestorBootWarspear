package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PROFISSAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.kevin.ceep.ui.recyclerview.adapter.ListaPersonagemAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ListaPersonagemActivity extends AppCompatActivity {

    private ListaPersonagemAdapter personagemAdapter;
    private List<Personagem> personagens;
    private ProgressDialog progressDialog;
    private FirebaseAuth minhaAutenticacao;
    private RecyclerView recyclerView;
    private DatabaseReference minhaReferencia;
    private String usuarioId;
    private final String[] mensagens={"Carregando dados...","Erro de conexão..."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_personagem);

        inicializaComponentes();
        atualizaListaPersonagem();

        configuraDeslizeItem();
        configuraSwipeRefreshLayout();
        Log.i(TAG_ACTIVITY,"onCreateListaPersonagem");
    }

    private void inicializaComponentes() {
        setTitle(CHAVE_TITULO_PERSONAGEM);
        minhaAutenticacao = FirebaseAuth.getInstance();
        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        minhaReferencia = database.getReference(CHAVE_USUARIOS);
        recyclerView = findViewById(R.id.listaPersonagensRecyclerView);
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
                ListaPersonagemAdapter personagemAdapter = (ListaPersonagemAdapter) recyclerView.getAdapter();
                Personagem personagemDeletado = personagens.get(viewHolder.getAdapterPosition());
                if (personagemAdapter != null) {
                    personagemAdapter.remove(posicaoDeslize);
                }
                if (personagemAdapter != null) {
                    personagemAdapter.notifyItemRemoved(posicaoDeslize);
                }

                configuraSnackBar(posicaoDeslize, personagemAdapter, personagemDeletado);
                //removePersonagemLista(posicaoDeslize);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void configuraSnackBar(int posicaoDeslize, ListaPersonagemAdapter personagemAdapter, Personagem personagemDeletado) {
        Snackbar.make(recyclerView, personagemDeletado.getNome(), Snackbar.LENGTH_LONG).setAction("Desfazer", v -> {
            // adding on click listener to our action of snack bar.
            // below line is to add our item to array list with a position.
            personagens.add(posicaoDeslize, personagemDeletado);

            // below line is to notify item is
            // added to our adapter class.
            personagemAdapter.notifyItemInserted(posicaoDeslize);
        }).show();
    }

    private void removePersonagemLista(int swipePosicao) {
        String idPersonagem = personagens.get(swipePosicao).getId();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhareferencia = database.getReference(CHAVE_USUARIOS);
        Log.d("Remove",idPersonagem);
        minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                child(idPersonagem).removeValue();
    }

    private void atualizaListaPersonagem() {
        configuraDialogoProgresso(0);
        if (vericaConexaoInternet()) {
            List<Personagem> todosPersonagens = pegaTodosPersonagens();
            personagens.clear();
            configuraRecyclerView(todosPersonagens);
        }else{
            progressDialog.dismiss();
            Toast.makeText(this,"Erro na conexão...",Toast.LENGTH_LONG).show();
            //configuraDialogoProgresso(1);
            //vaiParaEntraActivity();
            //finish();
            //progressDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = minhaAutenticacao.getCurrentUser();
        if (currentUser != null) {
            minhaAutenticacao.updateCurrentUser(currentUser);
        }
        Log.i(TAG_ACTIVITY,"onStartListaPersonagem");
    }
    private void adicionaNovaListaProfissoes(String idPersonagem) {

        String[] profissoes = getResources().getStringArray(R.array.profissoes);

        for (int i = 0; i< profissoes.length; i++){
            String novoIdProfissao = geraIdAleatorio();
            Profissao profissao = new Profissao(profissoes[i], 0, false);
            minhaReferencia.child(usuarioId)
                    .child(CHAVE_LISTA_PERSONAGEM)
                    .child(idPersonagem)
                    .child(CHAVE_LISTA_PROFISSAO)
                    .child(i+novoIdProfissao)
                    .setValue(profissao);
        }

    }

    static String geraIdAleatorio() {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(28);

        for (int i = 0; i < 28; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    private void configuraSwipeRefreshLayout() {
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutPersonagem);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            atualizaListaPersonagem();
        });
    }

    private void configuraDialogoProgresso(int posicaoMensagem) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(mensagens[posicaoMensagem]);
        progressDialog.show();
    }

    private Boolean vericaConexaoInternet(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo infConexao = cm.getActiveNetworkInfo();
        return infConexao != null && infConexao.isConnectedOrConnecting();
    }

    private void configuraRecyclerView(List<Personagem> todosPersonagens) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Collections.sort(todosPersonagens, (p1, p2) -> p1.getNome().compareToIgnoreCase(p2.getNome()));
        configuraAdapter(todosPersonagens,recyclerView);
    }

    private void configuraAdapter(List<Personagem> todosPersonagens, RecyclerView recyclerView) {
        personagemAdapter = new ListaPersonagemAdapter(this,todosPersonagens);
        recyclerView.setAdapter(personagemAdapter);

        personagemAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Profissao profissao, int posicao) {

            }

            @Override
            public void onItemClick(Personagem personagem, int posicao) {
                Log.d("PERSONAGEM", personagem.getId());
                Intent iniciaListaTrabalhoActivity =
                        new Intent(getApplicationContext(),
                                ListaTrabalhosActivity.class);
                iniciaListaTrabalhoActivity.putExtra(CHAVE_PERSONAGEM,
                        personagem.getId());
                startActivity(iniciaListaTrabalhoActivity,
                        ActivityOptions.makeSceneTransitionAnimation(
                                ListaPersonagemActivity.this).toBundle());
            }

            @Override
            public void onItemClick(Trabalho trabalho, int adapterPosition) {

            }

            @Override
            public void onItemClick(Raridade raridade, int adapterPosition) {
                
            }

            @Override
            public void onItemClick(TrabalhoEstoque trabalhoEstoque, int adapterPosition, int botaoId) {

            }
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
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        Log.d("PERSONAGEMFIM", String.valueOf(personagens.size()));
        return personagens;
    }
}