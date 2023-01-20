package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOTA;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_POSICAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_ALTERA_NOTA;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_NOTA;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.POSICAO_INVALIDA;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.dao.NotaDAO;
import com.kevin.ceep.databinding.ActivityListaTrabalhosBinding;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.ui.fragment.ListaTrabalhoFazendoFragment;
import com.kevin.ceep.ui.fragment.ListaTrabalhoFazerFragment;
import com.kevin.ceep.ui.fragment.ListaTrabalhoFeitoFragment;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListaTrabalhosActivity extends AppCompatActivity {

    private ListaTrabalhoAdapter trabalhoAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private List<Trabalho> trabalhos;
    private FirebaseAuth minhaAutenticacao;
    private String usuarioId, nomePersonagem;
    private Fragment listaTrabalhoFragmento;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_trabalhos);
        setTitle(CHAVE_TITULO_TRABALHO);

        mostraDialogodeProresso();
        nomePersonagem = recebeDadosIntent();

        minhaAutenticacao = FirebaseAuth.getInstance();
        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        recyclerView = findViewById(R.id.listaTrabalhoRecyclerView);
        atualizaListaTrabalho();

        configuraBotaoInsereTrabalho();
        configuraDeslizeItem();
        configuraSwipeRefreshLayout();
        Log.i(TAG_ACTIVITY,"onCreateListaTrabalho");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG_ACTIVITY,"onPauseListaTrabalho");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG_ACTIVITY,"onStopListaTrabalho");
    }

    private void configuraSwipeRefreshLayout() {
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutTrabalhos);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            atualizaListaTrabalho();
        });
    }

    private void atualizaListaTrabalho() {
        List<Trabalho> todosTrabalhos = pegaTodosTrabalhos();
        configuraRecyclerView(todosTrabalhos);
    }

    private void configuraDeslizeItem() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int swipePosicao = viewHolder.getAdapterPosition();
                ListaTrabalhoAdapter adapter = (ListaTrabalhoAdapter)recyclerView.getAdapter();
                removeTrabalhoLista(swipePosicao,nomePersonagem);
                adapter.remove(swipePosicao);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void removeTrabalhoLista(int swipePosicao,String nomePersonagem) {
        String idTrabalho = trabalhos.get(swipePosicao).getId();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhareferencia = database.getReference("Usuarios");
        Log.d("Remove",idTrabalho);
        minhareferencia.child(usuarioId).child("Lista_personagem").
                child(nomePersonagem).child("Lista_desejo").
                child(idTrabalho).removeValue();
    }

    private String recebeDadosIntent() {
        String nomePersonagem = new String();
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_NOME_PERSONAGEM)){
            nomePersonagem = (String) dadosRecebidos.getSerializableExtra(CHAVE_NOME_PERSONAGEM);
        }
        return nomePersonagem;
    }

    private void mostraDialogodeProresso(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();
    }

    private void configuraBotaoInsereTrabalho() {
        FloatingActionButton botaoInsereTrabaho = findViewById(R.id.floatingActionButton);
        botaoInsereTrabaho.setOnClickListener(v -> vaiParaRaridadeActivity());
    }

    private void vaiParaRaridadeActivity() {
        Intent iniciaListaRaridade =
                new Intent(ListaTrabalhosActivity.this,
                        ListaRaridadeActivity.class);
        iniciaListaRaridade.putExtra(CHAVE_NOME_PERSONAGEM,nomePersonagem);
        startActivity(iniciaListaRaridade,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        //startActivityForResult(iniciaFormularioNota, CODIGO_REQUISICAO_INSERE_NOTA);
    }

    private List<Trabalho> pegaTodosTrabalhos() {
        trabalhos = new ArrayList<>();
        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("USUARIO", usuarioId);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(CHAVE_USUARIOS);
        databaseReference.child(usuarioId).child(CHAVE_PERSONAGEM).
                child(nomePersonagem).child(CHAVE_LISTA_DESEJO).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            Trabalho trabalho = dn.getValue(Trabalho.class);
                            trabalhos.add(trabalho);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ehResultadoInsereNota(requestCode, data)) {
            if (resultadoOk(resultCode)){
                Trabalho notaRecebida = (Trabalho) data.getSerializableExtra(CHAVE_NOTA);
                adiciona(notaRecebida);
            }
        }

        if (ehResultadoAlteraNota(requestCode, data)) {
            if (resultadoOk(resultCode)){
                Trabalho notaRecebida = (Trabalho) data.getSerializableExtra(CHAVE_NOTA);
                int posicaoRecebida = data.getIntExtra(CHAVE_POSICAO, POSICAO_INVALIDA);
                if (ehPosicaoValida(posicaoRecebida)){
                    altera(notaRecebida, posicaoRecebida);
                }else {
                    Toast.makeText(this,
                            "Ocorreu um problema na alteração da nota.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void altera(Trabalho nota, int posicao) {
        new NotaDAO().altera(posicao, nota);
        trabalhoAdapter.altera(posicao, nota);
    }

    private boolean ehPosicaoValida(int posicaoRecebida) {
        return posicaoRecebida > POSICAO_INVALIDA;
    }

    private boolean ehResultadoAlteraNota(int requestCode, @Nullable Intent data) {
        return ehCodigoRequisicaoAlteraNota(requestCode) &&
                temNota(data);
    }

    private boolean ehCodigoRequisicaoAlteraNota(int requestCode) {
        return requestCode == CODIGO_REQUISICAO_ALTERA_NOTA;
    }

    private void adiciona(Trabalho nota) {
        new NotaDAO().insere(nota);
        trabalhoAdapter.adiciona(nota);
    }

    private boolean ehResultadoInsereNota(int requestCode, @Nullable Intent data) {
        return ehCodigoRequisicaoInsereNota(requestCode)&&
                temNota(data);
    }

    private boolean temNota(@Nullable Intent data) {
        return data.hasExtra(CHAVE_NOTA);
    }

    private boolean resultadoOk(int resultCode) {
        return resultCode == Activity.RESULT_OK;
    }

    private boolean ehCodigoRequisicaoInsereNota(int requestCode) {
        return requestCode == CODIGO_REQUISICAO_INSERE_NOTA;
    }

    private void configuraRecyclerView(List<Trabalho> todosTrabalhos) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        configuraAdapter(todosTrabalhos, recyclerView);
    }

    private void configuraAdapter(List<Trabalho> todosTrabalhos, RecyclerView listaTrabalhos) {
        trabalhoAdapter = new ListaTrabalhoAdapter(this,todosTrabalhos);
        listaTrabalhos.setAdapter(trabalhoAdapter);
    }
/*
    private void vaiParaFormularioNotaActivityAltera(Trabalho nota, int posicao) {
        Intent abreFormularioComNota = new Intent(ListaTrabalhosActivity.this,
                FormularioNotaActivity.class);
        abreFormularioComNota.putExtra(CHAVE_NOTA, nota);
        abreFormularioComNota.putExtra(CHAVE_POSICAO, posicao);
        startActivityForResult(abreFormularioComNota, CODIGO_REQUISICAO_ALTERA_NOTA);
    }
*/
}