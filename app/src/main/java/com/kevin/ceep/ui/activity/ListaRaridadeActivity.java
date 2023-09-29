package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_RARIDADE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_RARIDADE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kevin.ceep.R;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.ui.recyclerview.adapter.ListaPersonagemAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.ListaProfissaoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ListaRaridadeActivity extends AppCompatActivity {

    private ListaProfissaoAdapter raridadeAdapter;
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
        List<Profissao> todasRaridades = pegaTodasRaridades();
        configuraRecyclerView(todasRaridades);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
        Log.i(TAG_ACTIVITY,"onStopListaRaridade");
    }

    private void configuraRecyclerView(List<Profissao> todasRaridades) {
        RecyclerView recyclerView = findViewById(R.id.listaRaridadeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressDialog.dismiss();
        configuraAdapter(todasRaridades,recyclerView);
    }

    private void configuraAdapter(List<Profissao> todasRaridades, RecyclerView listaRaridades) {
        raridadeAdapter = new ListaProfissaoAdapter(this, todasRaridades);
        listaRaridades.setAdapter(raridadeAdapter);
        raridadeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Profissao raridade, int posicao) {
                String personagemId = recebeDadosIntent();
                Intent iniciaProfissoesActivity =
                        new Intent(ListaRaridadeActivity.this,
                                ListaProfissoesActivity.class);
                iniciaProfissoesActivity.putExtra(CHAVE_NOME_PERSONAGEM,personagemId);
                iniciaProfissoesActivity.putExtra(CHAVE_NOME_RARIDADE, raridade);
                startActivity(iniciaProfissoesActivity,
                        ActivityOptions.makeSceneTransitionAnimation(ListaRaridadeActivity.this).toBundle());
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
        if (dadosRecebidos.hasExtra(CHAVE_NOME_PERSONAGEM)){
            nomePersonagem = (String) dadosRecebidos.getSerializableExtra(CHAVE_NOME_PERSONAGEM);
        }
        Log.d("PERSONAGEM", nomePersonagem);
        return nomePersonagem;
    }

    private List<Profissao> pegaTodasRaridades() {
        List<Profissao> raridades = new ArrayList<>();
        raridades.add(new Profissao("Comum"));
        raridades.add(new Profissao("Melhorado"));
        raridades.add(new Profissao("Raro"));
        raridades.add(new Profissao("Especial"));
        return raridades;
    }

    private void mostraDialogodeProgresso(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();
    }


}