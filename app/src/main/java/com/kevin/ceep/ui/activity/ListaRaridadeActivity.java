package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_RARIDADE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_RARIDADE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kevin.ceep.R;
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.Raridade;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.ui.recyclerview.adapter.ListaRaridadeAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ListaRaridadeActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_raridade);
        setTitle(CHAVE_TITULO_RARIDADE);

        mostraDialogodeProgresso();
        atualizarListaRaridade();
        configuraSwipeRefreshLayout();
    }

    private void configuraSwipeRefreshLayout() {
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutRaridades);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            atualizarListaRaridade();
        });
    }

    private void atualizarListaRaridade() {
        List<Raridade> todasRaridades = pegaTodasRaridades();
        configuraRecyclerView(todasRaridades);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
        Log.i(TAG_ACTIVITY,"onStopListaRaridade");
    }

    private void configuraRecyclerView(List<Raridade> todasRaridades) {
        RecyclerView recyclerView = findViewById(R.id.listaRaridadeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressDialog.dismiss();
        configuraAdapter(todasRaridades,recyclerView);
    }

    private void configuraAdapter(List<Raridade> todasRaridades, RecyclerView listaRaridades) {
        ListaRaridadeAdapter raridadeAdapter = new ListaRaridadeAdapter(this, todasRaridades);
        listaRaridades.setAdapter(raridadeAdapter);
        raridadeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Raridade raridade, int posicao) {
                String personagemId = recebeDadosIntent();
                Intent iniciaProfissoesActivity =
                        new Intent(ListaRaridadeActivity.this,
                                ListaProfissoesActivity.class);
                iniciaProfissoesActivity.putExtra(CHAVE_PERSONAGEM,personagemId);
                iniciaProfissoesActivity.putExtra(CHAVE_NOME_RARIDADE, raridade);
                startActivity(iniciaProfissoesActivity,
                        ActivityOptions.makeSceneTransitionAnimation(ListaRaridadeActivity.this).toBundle());
            }

            @Override
            public void onItemClick(Profissao profissao, int posicao) {

            }

            @Override
            public void onItemClick(Personagem personagem, int posicao) {
            }

            @Override
            public void onItemClick(Trabalho trabalho, int adapterPosition) {
            }
        });
    }

    private String recebeDadosIntent() {
        String nomePersonagem = "";
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_PERSONAGEM)){
            nomePersonagem = (String) dadosRecebidos.getSerializableExtra(CHAVE_PERSONAGEM);
        }
        return nomePersonagem;
    }

    private List<Raridade> pegaTodasRaridades() {
        List<Raridade> raridades = new ArrayList<>();
        raridades.add(new Raridade("Comum"));
        raridades.add(new Raridade("Melhorado"));
        raridades.add(new Raridade("Raro"));
        raridades.add(new Raridade("Especial"));
        return raridades;
    }

    private void mostraDialogodeProgresso(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();
    }
}