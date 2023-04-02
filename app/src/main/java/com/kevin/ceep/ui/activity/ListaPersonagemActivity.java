package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_CONFIRMA_CADASTRO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PROFISSAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.ui.recyclerview.adapter.ListaPersonagemAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListaPersonagemActivity extends AppCompatActivity {

    private ListaPersonagemAdapter personagemAdapter;
    private List<Personagem> personagens;
    private ProgressDialog progressDialog;
    private TextInputLayout txtEmailPersonagem, txtSenhaPersonagem;
    private TextInputEditText edtNovoPersonagem, edtEmailPersonagem,edtSenhaPersonagem;
    private AppCompatButton botaoNovoPersonagem;
    private FirebaseAuth minhaAutenticacao;
    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private DatabaseReference minhaReferencia;
    private SwitchCompat switchBotao;
    private String usuarioId,nomePersonagem,emailPersonagem,senhaPersonagem;
    private String[] mensagens={"Carregando dados...","Erro de conexão..."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_personagem);
        setTitle(CHAVE_TITULO_PERSONAGEM);

        inicializaComponentes();

        configuraCampoNovoPersonagem();

        configuraBotaoDeslogaUsuario();
        configuraBotaoNovoPersonagem();
        configuraDeslizeItem();
        configuraSwipeRefreshLayout();
        Log.i(TAG_ACTIVITY,"onCreateListaPersonagem");
    }

    private void inicializaComponentes() {
        minhaAutenticacao = FirebaseAuth.getInstance();
        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        minhaReferencia = database.getReference(CHAVE_USUARIOS);
        recyclerView = findViewById(R.id.listaPersonagensRecyclerView);
        txtEmailPersonagem = findViewById(R.id.txtEmailPersonagem);
        txtSenhaPersonagem = findViewById(R.id.txtSenhaPersonagem);
        edtNovoPersonagem = findViewById(R.id.edtNovoPersonagem);
        edtEmailPersonagem = findViewById(R.id.edtEmailPersonagem);
        edtSenhaPersonagem = findViewById(R.id.edtSenhaPersonagem);
        switchBotao =findViewById(R.id.itemSwitchButton);
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
                removePersonagemLista(posicaoDeslize);
                personagemAdapter.remove(posicaoDeslize);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void removePersonagemLista(int swipePosicao) {
        String idPersonagem = personagens.get(swipePosicao).getId();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhareferencia = database.getReference(CHAVE_USUARIOS);
        Log.d("Remove",idPersonagem);
        minhareferencia.child(usuarioId).child(CHAVE_PERSONAGEM).
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
            //configuraDialogoProgresso(1);
            //vaiParaEntraActivity();
            //finish();
            //progressDialog.dismiss();
        }
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
        recebeDadosIntent();
        atualizaListaPersonagem();
        Log.i(TAG_ACTIVITY,"onStartListaPersonagem");
    }

    private void configuraCampoNovoPersonagem() {
        edtNovoPersonagem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nomePersonagem = edtNovoPersonagem.getText().toString();
                emailPersonagem = edtEmailPersonagem.getText().toString();
                senhaPersonagem = edtSenhaPersonagem.getText().toString();

                if(verificaEdtPersonagem(nomePersonagem,emailPersonagem,senhaPersonagem)){
                    if(verificaEmailValido(emailPersonagem)&!nomePersonagem.isEmpty()&!senhaPersonagem.isEmpty()){
                        habilitaBotaoRecuperaSenha();
                    }else{
                        botaoNovoPersonagem.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtEmailPersonagem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                nomePersonagem = edtNovoPersonagem.getText().toString();
                emailPersonagem = edtEmailPersonagem.getText().toString();
                senhaPersonagem = edtSenhaPersonagem.getText().toString();

                if(verificaEdtPersonagem(nomePersonagem,emailPersonagem,senhaPersonagem)){
                    if(verificaEmailValido(emailPersonagem)&!nomePersonagem.isEmpty()&!senhaPersonagem.isEmpty()){
                        habilitaBotaoRecuperaSenha();
                    }else{
                        botaoNovoPersonagem.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtSenhaPersonagem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                nomePersonagem = edtNovoPersonagem.getText().toString();
                emailPersonagem = edtEmailPersonagem.getText().toString();
                senhaPersonagem = edtSenhaPersonagem.getText().toString();

                if(verificaEdtPersonagem(nomePersonagem,emailPersonagem,senhaPersonagem)){
                    if(verificaEmailValido(emailPersonagem)&!nomePersonagem.isEmpty()&!senhaPersonagem.isEmpty()){
                        habilitaBotaoRecuperaSenha();
                    }else{
                        botaoNovoPersonagem.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private boolean verificaEmailValido(String email) {
        if(configuraEditEmail(!email.isEmpty()) & configuraEditEmail(Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            return true;
        }else {
            configuraMenssagemAjuda(email);
        }
        return false;
    }

    private void habilitaBotaoRecuperaSenha() {
        txtEmailPersonagem.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#007FFF")));
        txtEmailPersonagem.setBoxStrokeColor(Color.parseColor("#007FFF"));
        txtEmailPersonagem.setHelperTextEnabled(false);
        botaoNovoPersonagem.setEnabled(true);
    }

    private void configuraMenssagemAjuda(String email) {

        if (!configuraEditEmail(Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            txtEmailPersonagem.setHelperText("Por favor, informe um email válido!");
        }
        if (!configuraEditEmail(!email.isEmpty()) & email.length()<1){
            txtEmailPersonagem.setHelperText("Campo requerido!");
        }
    }

    @SuppressLint("NewApi")
    private boolean configuraEditEmail(boolean email) {
        if (!email){
            txtEmailPersonagem.setHintTextColor(ColorStateList.valueOf(getColor(R.color.cor_background_bordo)));
            txtEmailPersonagem.setBoxStrokeColor(Color.parseColor("#A71500"));
            txtEmailPersonagem.setHelperTextColor(ColorStateList.valueOf(getColor(R.color.cor_background_bordo)));
            txtEmailPersonagem.setHelperTextEnabled(true);
            botaoNovoPersonagem.setEnabled(false);
            return false;
        }
        return true;
    }

    private Boolean verificaEdtPersonagem(String novoPersonagem, String emailPersonagem, String senhaPersonagem) {
        if (!novoPersonagem.isEmpty()||!emailPersonagem.isEmpty()||!senhaPersonagem.isEmpty()){
            txtEmailPersonagem.setVisibility(View.VISIBLE);
            txtSenhaPersonagem.setVisibility(View.VISIBLE);
            botaoNovoPersonagem.setVisibility(View.VISIBLE);
            return true;
        }else {
            txtEmailPersonagem.setVisibility(View.GONE);
            txtSenhaPersonagem.setVisibility(View.GONE);
            botaoNovoPersonagem.setVisibility(View.GONE);
        }
        return false;
    }

    private void configuraBotaoNovoPersonagem() {
        botaoNovoPersonagem = findViewById(R.id.botaoNovoPersonagem);
        botaoNovoPersonagem.setOnClickListener(view -> {
            adicionaNovoPersonagem();
            atualizaListaPersonagem();
            edtNovoPersonagem.setText(null);
            edtEmailPersonagem.setText(null);
            edtSenhaPersonagem.setText(null);
            botaoNovoPersonagem.setEnabled(false);
        });
    }

    private void adicionaNovoPersonagem() {

        String novoIdPersonagem = geraIdAleatorio();

        Personagem personagem = new Personagem(novoIdPersonagem,nomePersonagem,emailPersonagem,senhaPersonagem,0);

        minhaReferencia.child(usuarioId).child(CHAVE_PERSONAGEM).child(novoIdPersonagem).setValue(personagem);
        adicionaNovaListaProfissoes(novoIdPersonagem);
    }

    private void adicionaNovaListaProfissoes(String idPersonagem) {

        String[] profissoes = getResources().getStringArray(R.array.profissoes);

        for (int i = 0; i< profissoes.length; i++){
            String novoIdProfissao = geraIdAleatorio();
            Profissao profissao = new Profissao(profissoes[i]);
            minhaReferencia.child(usuarioId)
                    .child(CHAVE_PERSONAGEM)
                    .child(idPersonagem)
                    .child(CHAVE_LISTA_PROFISSAO)
                    .child(i+novoIdProfissao)
                    .setValue(profissao);
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
        if(infConexao!=null && infConexao.isConnectedOrConnecting()){
            return true;
        }
        return false;
    }

    private void configuraRecyclerView(List<Personagem> todosPersonagens) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Collections.sort(todosPersonagens, (p1, p2) -> p1.getNome().compareToIgnoreCase(p2.getNome()));
        configuraAdapter(todosPersonagens,recyclerView);
    }

    private void configuraAdapter(List<Personagem> todosPersonagens, RecyclerView recyclerView) {
        personagemAdapter = new ListaPersonagemAdapter(this,todosPersonagens);
        progressDialog.dismiss();
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

    public List<Personagem> atualizaEstadoPersonagem(Personagem raridade,int estado) {
        personagens = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference personagemReferencia = database.getReference(CHAVE_USUARIOS);
        personagemReferencia.child(usuarioId).child(CHAVE_PERSONAGEM).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            Personagem personagem = dn.getValue(Personagem.class);
                            if (personagem.getId().equals(raridade.getId())){
                                personagemReferencia.child(usuarioId).child(CHAVE_PERSONAGEM).
                                        child(personagem.getId()).child("estado").setValue(estado);
                            }
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

    private List<Personagem> pegaTodosPersonagens() {
        personagens = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Log.d("PERSONAGEM", String.valueOf(personagens));
        DatabaseReference databaseReference = database.getReference(CHAVE_USUARIOS);
        databaseReference.child(usuarioId).child(CHAVE_PERSONAGEM).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
        Log.d("PERSONAGEM", String.valueOf(personagens));
        return personagens;
    }
}