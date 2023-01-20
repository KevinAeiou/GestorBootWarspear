package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PROFISSAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_RARIDADE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_PROFISSAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.kevin.ceep.model.Raridade;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.ui.recyclerview.adapter.ListaProfissaoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ListaProfissoesActivity extends AppCompatActivity {

    private ListaProfissaoAdapter profissaoAdapter;
    private ProgressDialog progressDialog;
    private String personagemId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_profissoes);
        setTitle(CHAVE_TITULO_PROFISSAO);

        recebeDadosIntent();
        mostraDialogodeProgresso();
        List<Profissao> todasProfissoes = pegaTodasProfissoes();
        configuraRecyclerView(todasProfissoes);
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
        finish();
        Log.i(TAG_ACTIVITY,"onStopListaProfissoes");
    }

    private void mostraDialogodeProgresso(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();
    }

    private List<Profissao> pegaTodasProfissoes(){
        List<Profissao> profissoes = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference minhareferencia = database.getReference("Usuarios");

        minhareferencia.child(usuarioId).child("Lista_personagem").child(personagemId).child("Lista_profissoes").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            Profissao profissao = dn.getValue((Profissao.class));
                            profissoes.add(profissao);
                        }
                        profissaoAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return profissoes;
    }

    private void configuraRecyclerView(List<Profissao> todasProfissoes){
        RecyclerView recyclerView = findViewById(R.id.listaProfissoesRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                    Raridade raridade = (Raridade) dadosRecebidos
                            .getSerializableExtra(CHAVE_NOME_RARIDADE);
                    Intent iniciaTrabalhosActivity =
                            new Intent(ListaProfissoesActivity.this,
                                    ListaTrabalhosEspecificosActivity.class);
                    iniciaTrabalhosActivity.putExtra(CHAVE_NOME_PERSONAGEM,personagemId);
                    iniciaTrabalhosActivity.putExtra(CHAVE_NOME_PROFISSAO,profissao);
                    iniciaTrabalhosActivity.putExtra(CHAVE_NOME_RARIDADE,raridade);
                    startActivity(iniciaTrabalhosActivity,
                            ActivityOptions.makeSceneTransitionAnimation(ListaProfissoesActivity.this).toBundle());
                }
                /*Toast.makeText(ListaProfissoesActivity.this,
                        profissao.getNome(),
                        Toast.LENGTH_SHORT).show();*/
            }

            @Override
            public void onItemClick(Raridade raridade, int posicao) {

            }

            @Override
            public void onItemClick(Trabalho trabalho, int adapterPosition) {

            }
        });
    }
}
