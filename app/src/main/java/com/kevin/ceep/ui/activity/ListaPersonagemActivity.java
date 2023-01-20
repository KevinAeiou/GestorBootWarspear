package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_CONFIRMA_CADASTRO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.Raridade;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.ui.activity.ListaTrabalhosActivity;
import com.kevin.ceep.ui.recyclerview.adapter.ListaRaridadeAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ListaPersonagemActivity extends AppCompatActivity {

    private ListaRaridadeAdapter personagemAdapter;
    private ProgressDialog progressDialog;
    private TextInputEditText edtNovoPersonagem;
    private AppCompatButton botaoNovoPersonagem;
    private FirebaseAuth minhaAutenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_personagem);
        setTitle(CHAVE_TITULO_PERSONAGEM);

        minhaAutenticacao = FirebaseAuth.getInstance();
        edtNovoPersonagem = findViewById(R.id.edtNovoPersonagem);

        recebeDadosIntent();
        configuraDialogoProgresso();

        configuraSwipeRefreshLayout();
        configuraEditPersonagem();
        configuraBotaoDeslogaUsuario();
        configuraBotaoInserePersonagem();
        Log.i(TAG_ACTIVITY,"onCreateListaPersonagem");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG_ACTIVITY,"onResumeListaPersonagem");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG_ACTIVITY,"onPauseListaPersonagem");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG_ACTIVITY,"onStopListaPersonagem");
    }

    private void configuraBotaoDeslogaUsuario() {
        TextView txtDeslogaUsuario = findViewById(R.id.txtSairUsuario);
        txtDeslogaUsuario.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            vaiParaEntraActivity();
            finish();
        });
    }

    private void vaiParaEntraActivity() {
        Intent vaiParaEntraActivity = new Intent(getApplicationContext(),
                EntrarUsuarioActivity.class);
        startActivity(vaiParaEntraActivity, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_CONFIRMA_CADASTRO)) {
            final Toast toast = configuraToastCustomizado();
            toast.show();
        }
    }

    @NonNull
    private Toast configuraToastCustomizado() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_customizado,
                findViewById(R.id.toastCustomizadoLayout));
        final Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        return toast;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = minhaAutenticacao.getCurrentUser();
        minhaAutenticacao.updateCurrentUser(currentUser);
        List<Raridade> todosPersonagens = pegaTodosPersonagens();
        configuraRecyclerView(todosPersonagens);
        Log.i(TAG_ACTIVITY,"onStartListaPersonagem");
    }

    private void configuraEditPersonagem() {
        edtNovoPersonagem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String novoPersonagem = edtNovoPersonagem.getText().toString();
                verificaNomePersonagem(novoPersonagem);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void verificaNomePersonagem(String novoPersonagem) {
        if (!novoPersonagem.isEmpty()){
            botaoNovoPersonagem.setEnabled(true);
        }else{
            botaoNovoPersonagem.setEnabled(false);
        }
    }

    private void configuraBotaoInserePersonagem() {
        botaoNovoPersonagem = findViewById(R.id.botaoNovoPersonagem);
        botaoNovoPersonagem.setOnClickListener(view -> {
            adicionaNovoPersonagem();
            atualizaListaTrabalho();
            edtNovoPersonagem.setText(null);
            botaoNovoPersonagem.setEnabled(false);
        });
    }

    private void adicionaNovoPersonagem() {

        String personagem = edtNovoPersonagem.getText().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhaReferencia = database.getReference("Usuarios");
        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String novoId = geraIdAleatorio();

        minhaReferencia.child(usuarioId).child("Lista_personagem").child(novoId).child("nome").setValue(personagem);
        minhaReferencia.child(usuarioId).child("Lista_personagem").child(novoId).child("id").setValue(novoId);
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

    private void configuraSwipeRefreshLayout() {
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutPersonagem);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            atualizaListaTrabalho();
        });
    }

    private void atualizaListaTrabalho() {
        List<Raridade> todosPersonagens = pegaTodosPersonagens();
        configuraRecyclerView(todosPersonagens);
    }

    private void configuraDialogoProgresso() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();
    }

    private void configuraRecyclerView(List<Raridade> todosPersonagens) {
        RecyclerView recyclerView = findViewById(R.id.listaPersonagensRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        configuraAdapter(todosPersonagens,recyclerView);
    }

    private void configuraAdapter(List<Raridade> todosPersonagens, RecyclerView recyclerView) {
        personagemAdapter = new ListaRaridadeAdapter(this,todosPersonagens);
        recyclerView.setAdapter(personagemAdapter);
        personagemAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Profissao profissao, int posicao) {

            }

            @Override
            public void onItemClick(Raridade personagem, int posicao) {
                Log.d("PERSONAGEM", personagem.getId());
                Intent iniciaListaTrabalhoActivity =
                        new Intent(getApplicationContext(),
                                ListaTrabalhosActivity.class);
                iniciaListaTrabalhoActivity.putExtra(CHAVE_NOME_PERSONAGEM,
                        personagem.getId());
                startActivity(iniciaListaTrabalhoActivity,
                        ActivityOptions.makeSceneTransitionAnimation(
                                ListaPersonagemActivity.this).toBundle());
            }

            @Override
            public void onItemClick(Trabalho trabalho, int adapterPosition) {

            }
        });
    }

    private List<Raridade> pegaTodosPersonagens() {
        List<Raridade> personagens = new ArrayList<>();
        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Log.d("USUARIO", usuarioId);
        DatabaseReference databaseReference = database.getReference("Usuarios");
        databaseReference.child(usuarioId).child("Lista_personagem").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            Raridade personagem = dn.getValue(Raridade.class);
                            personagens.add(personagem);
                        }
                        personagemAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return personagens;
    }
}