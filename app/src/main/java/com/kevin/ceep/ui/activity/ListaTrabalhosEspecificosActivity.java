package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PROFISSAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_RARIDADE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoEspecificoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ListaTrabalhosEspecificosActivity extends AppCompatActivity {

    private ListaTrabalhoEspecificoAdapter trabalhoAdapter;
    private ProgressDialog progressDialog;
    private TextInputEditText edtNovoTrabalho,edtNovoNivelTrabalho;
    private AppCompatButton botaoNovoTrabalho;
    private RecyclerView recyclerView;
    private List<Trabalho> trabalhos;
    private String profissao, raridade, trabalho, nivel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_trabalhos_especificos);
        setTitle(CHAVE_TITULO_TRABALHO);

        mostraDialogodeProresso();
        recebeDadosIntent();

        edtNovoTrabalho = findViewById(R.id.edtNovoTrabalhoEspecifico);
        edtNovoNivelTrabalho = findViewById(R.id.edtNovoNivelTrabalhoEspecifico);
        botaoNovoTrabalho = findViewById(R.id.botaoNovoTrabalhoEspecifico);

        atualizaListaTrabalhoEspecifico();

        configuraEditTrabaho();
        configuraBotaoInsereTrabalho();
        configuraDeslizeItem();
        configuraSwipeRefreshLayout();
        Log.i(TAG_ACTIVITY,"onCreateListaTrabalhosEspecificos");
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
                trabalhoAdapter.remove(posicaoDeslize);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void removeTrabalhoLista(int posicaoDeslize) {
        String idTrabalho = trabalhos.get(posicaoDeslize).getId();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhaReferencia = database.getReference(CHAVE_LISTA_TRABALHO);
        minhaReferencia.child(idTrabalho).removeValue();
    }

    private void configuraBotaoInsereTrabalho() {
        botaoNovoTrabalho.setOnClickListener(view -> {
            adicionaNovoTrabalho();
            atualizaListaTrabalhoEspecifico();
            edtNovoTrabalho.setText(null);
            botaoNovoTrabalho.setVisibility(View.GONE);
        });
    }

    private void adicionaNovoTrabalho() {
        String novoId = geraIdAleatorio();
        Trabalho novoTrabalho = new Trabalho(novoId,trabalho,profissao,"",raridade, 0, Integer.parseInt(nivel));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhaReferencia = database.getReference("Lista_trabalhos");

        minhaReferencia.child(novoId).setValue(novoTrabalho);
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

    private void configuraEditTrabaho() {
        edtNovoTrabalho.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                trabalho = edtNovoTrabalho.getText().toString();
                nivel = edtNovoNivelTrabalho.getText().toString();
                verificaNomeTrabalho(trabalho,nivel);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtNovoNivelTrabalho.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                trabalho = edtNovoTrabalho.getText().toString();
                nivel = edtNovoNivelTrabalho.getText().toString();
                verificaNomeTrabalho(trabalho,nivel);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void verificaNomeTrabalho(String novoTrabalho,String novoNivelTrabalho) {
        if (!novoTrabalho.isEmpty()&!novoNivelTrabalho.isEmpty()){
            botaoNovoTrabalho.setVisibility(View.VISIBLE);
        }else{
            botaoNovoTrabalho.setVisibility(View.GONE);
        }
    }

    private void configuraSwipeRefreshLayout() {
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutTrabalhosEspecificos);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            atualizaListaTrabalhoEspecifico();
        });
    }

    private void atualizaListaTrabalhoEspecifico() {
        List<Trabalho> todosTrabalhos = pegaTodosTrabalhos();
        configuraRecyclerView(todosTrabalhos);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
        Log.i(TAG_ACTIVITY,"onStopListaTrabalhosEspecificos");
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_NOME_PROFISSAO)) {
            Profissao personagemRecebido = (Profissao) dadosRecebidos
                    .getSerializableExtra(CHAVE_NOME_RARIDADE);
            Profissao profissaoRecebido = (Profissao) dadosRecebidos
                    .getSerializableExtra(CHAVE_NOME_PROFISSAO);
            raridade=personagemRecebido.getNome();
            profissao=profissaoRecebido.getNome();
        }
    }

    private void mostraDialogodeProresso(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();
    }

    private List<Trabalho> pegaTodosTrabalhos() {
        trabalhos = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Lista_trabalhos");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dn:dataSnapshot.getChildren()){
                    Trabalho trabalho = dn.getValue(Trabalho.class);
                    if (trabalho.getProfissao().equals(profissao) && trabalho.getRaridade().equals(raridade)){
                        trabalhos.add(trabalho);
                    }
                }
                trabalhoAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return trabalhos;
    }

    private void configuraRecyclerView(List<Trabalho> todosTrabalhos) {
        recyclerView = findViewById(R.id.listaTrabalhoEspecificoRecyclerView);
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

                Intent dadosRecebidos = getIntent();
                Intent iniciaTrabalhosActivity =
                        new Intent(ListaTrabalhosEspecificosActivity.this,
                                ConfirmaTrabalhoActivity.class);

                if (dadosRecebidos.hasExtra(CHAVE_NOME_PROFISSAO)) {
                    iniciaTrabalhosActivity.putExtra(CHAVE_NOME_TRABALHO, trabalho);
                    String personagemId = (String) dadosRecebidos.
                            getSerializableExtra(CHAVE_NOME_PERSONAGEM);
                    iniciaTrabalhosActivity.putExtra(CHAVE_NOME_PERSONAGEM, personagemId);
                    startActivity(iniciaTrabalhosActivity,
                            ActivityOptions.makeSceneTransitionAnimation(ListaTrabalhosEspecificosActivity.this).toBundle());
                }
            }
        });
    }
}