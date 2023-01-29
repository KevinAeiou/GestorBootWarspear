package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PROFISSAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PROFISSAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_RARIDADE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_PROFISSAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.ui.recyclerview.adapter.ListaProfissaoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListaProfissoesActivity extends AppCompatActivity {

    private ListaProfissaoAdapter profissaoAdapter;
    private ProgressDialog progressDialog;
    private String personagemId,usuarioId;
    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private DatabaseReference minhaReferencia;
    private List<Profissao> todasProfissoes;
    private Boolean CHAVE_ATUALIZA_LISTA_PROFISSOES = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_profissoes);
        setTitle(CHAVE_TITULO_PROFISSAO);

        recyclerView = findViewById(R.id.listaProfissoesRecyclerView);
        database = FirebaseDatabase.getInstance();
        minhaReferencia = database.getReference(CHAVE_USUARIOS);
        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recebeDadosIntent();
        mostraDialogodeProgresso();

        todasProfissoes = pegaTodasProfissoes();
        configuraRecyclerView();
        configuraDeslizeItem();
    }

    private void configuraDeslizeItem() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
                |ItemTouchHelper.DOWN|ItemTouchHelper.START|ItemTouchHelper.END,0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int daqui = viewHolder.getAdapterPosition();
                int praca = target.getAdapterPosition();
                Collections.swap(todasProfissoes,daqui,praca);
                recyclerView.getAdapter().notifyItemMoved(daqui,praca);
                CHAVE_ATUALIZA_LISTA_PROFISSOES = true;
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void atualizaListaProfissoes() {
        limpaListaProfissoes();
        for (int i = 0; i<todasProfissoes.size(); i++){
            String novoIdProfissao = geraIdAleatorio();
            minhaReferencia.child(usuarioId)
                    .child(CHAVE_PERSONAGEM)
                    .child(personagemId)
                    .child(CHAVE_LISTA_PROFISSAO)
                    .child(i+novoIdProfissao)
                    .setValue(todasProfissoes.get(i));
        }
    }

    private String geraIdAleatorio() {
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

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_NOME_PERSONAGEM)){
            personagemId = (String) dadosRecebidos
                    .getSerializableExtra(CHAVE_NOME_PERSONAGEM);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (CHAVE_ATUALIZA_LISTA_PROFISSOES){
            atualizaListaProfissoes();
        }
        finish();
        Log.i(TAG_ACTIVITY,"onStopListaProfissoes");
    }

    private void limpaListaProfissoes() {
        minhaReferencia.child(usuarioId)
                .child(CHAVE_PERSONAGEM)
                .child(personagemId)
                .child(CHAVE_LISTA_PROFISSAO)
                .removeValue();
    }

    private void mostraDialogodeProgresso(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();
    }

    private List<Profissao> pegaTodasProfissoes(){
        List<Profissao> profissoes = new ArrayList<>();

        minhaReferencia.child(usuarioId).child(CHAVE_PERSONAGEM).child(personagemId).child(CHAVE_LISTA_PROFISSAO).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            Profissao profissao = dn.getValue((Profissao.class));
                            profissoes.add(profissao);
                        }

                        profissaoAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return profissoes;
    }

    private void configuraRecyclerView(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressDialog.dismiss();
        configuraAdapter(todasProfissoes,recyclerView);
    }

    private void configuraAdapter(List<Profissao> todasProfissoes, RecyclerView listaProfissoes) {
        profissaoAdapter = new ListaProfissaoAdapter(this,todasProfissoes);
        listaProfissoes.setAdapter(profissaoAdapter);
        profissaoAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Profissao profissao, int posicao) {
                Intent dadosRecebidos = getIntent();
                if (dadosRecebidos.hasExtra(CHAVE_NOME_RARIDADE)){
                    Profissao raridade = (Profissao) dadosRecebidos
                            .getSerializableExtra(CHAVE_NOME_RARIDADE);
                    Intent iniciaTrabalhosActivity =
                            new Intent(ListaProfissoesActivity.this,
                                    ListaTrabalhosEspecificosActivity.class);
                    iniciaTrabalhosActivity.putExtra(CHAVE_NOME_PERSONAGEM,personagemId);
                    iniciaTrabalhosActivity.putExtra(CHAVE_NOME_PROFISSAO,profissao);
                    iniciaTrabalhosActivity.putExtra(CHAVE_NOME_RARIDADE, raridade);
                    startActivity(iniciaTrabalhosActivity,
                            ActivityOptions.makeSceneTransitionAnimation(ListaProfissoesActivity.this).toBundle());
                }
            }

            @Override
            public void onItemClick(Personagem personagem, int posicao) {

            }

            @Override
            public void onItemClick(Trabalho trabalho, int adapterPosition) {

            }
        });
    }
}
