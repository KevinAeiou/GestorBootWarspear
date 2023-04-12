package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_TRABALHO;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.dao.NotaDAO;
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class ListaTrabalhosActivity extends AppCompatActivity {

    private ListaTrabalhoAdapter trabalhoAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private List<Trabalho> trabalhos;
    private SearchView busca;
    private String usuarioId, nomePersonagem;
    private Integer estado = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_trabalhos);
        setTitle(CHAVE_TITULO_TRABALHO);

        recebeDadosIntent();

        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        atualizaListaTrabalho();

        configuraCampoPesquisa();
        configuraBotaoInsereTrabalho();
        configuraDeslizeItem();
        configuraSwipeRefreshLayout();
        Log.i(TAG_ACTIVITY,"onCreateListaTrabalho");
    }

    private void configuraCampoPesquisa() {
        busca = findViewById(R.id.buscaTrabalho);
        busca.clearFocus();
        busca.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String textoBusca) {
                listaBusca(textoBusca);
                return true;
            }
        });
    }

    private void listaBusca(String textoBusca) {
        List<Trabalho> listaFiltro = new ArrayList<>();
        for (Trabalho trabalho:trabalhos){
            if (trabalho.getNome().toLowerCase().contains(textoBusca.toLowerCase())){
                listaFiltro.add(trabalho);
            }
        }
        if (listaFiltro.isEmpty()){
            Toast.makeText(this,"Nada encontrado...",Toast.LENGTH_SHORT).show();
        }else{
            trabalhoAdapter.setListaFiltrada(listaFiltro);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clasifica_lista_trabalho,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.itemMenuTodos:
                estado=3;
                atualizaListaTrabalho();
                break;
            case R.id.itemMenuProduzir:
                estado=0;
                atualizaListaTrabalho();
                break;
            case R.id.itemMenuProduzindo:
                estado=1;
                atualizaListaTrabalho();
                break;
            case R.id.itemMenuConcluido:
                estado=2;
                atualizaListaTrabalho();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
        mostraDialogodeProresso();
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
                int posicaoDeslize = viewHolder.getAdapterPosition();
                ListaTrabalhoAdapter trabalhoAdapter = (ListaTrabalhoAdapter)recyclerView.getAdapter();
                removeTrabalhoLista(posicaoDeslize);
                trabalhoAdapter.remove(posicaoDeslize);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void removeTrabalhoLista(int swipePosicao) {
        String idTrabalho = trabalhos.get(swipePosicao).getId();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhareferencia = database.getReference(CHAVE_USUARIOS);
        Log.d("Remove",idTrabalho);
        minhareferencia.child(usuarioId).child("Lista_personagem").
                child(nomePersonagem).child("Lista_desejo").
                child(idTrabalho).removeValue();
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_NOME_PERSONAGEM)){
            nomePersonagem = (String) dadosRecebidos.getSerializableExtra(CHAVE_NOME_PERSONAGEM);
        }
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
        Log.d("USUARIO", usuarioId);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(CHAVE_USUARIOS);
        databaseReference.child(usuarioId).child(CHAVE_PERSONAGEM).
                child(nomePersonagem).child(CHAVE_LISTA_DESEJO).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        trabalhos.clear();
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            Trabalho trabalho = dn.getValue(Trabalho.class);
                            if (estado==3){
                                trabalhos.add(trabalho);
                            }else if (trabalho.getEstado()==estado){
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
                /*addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            Trabalho trabalho = dn.getValue(Trabalho.class);
                            if (estado==3){
                                trabalhos.add(trabalho);
                            }else if (trabalho.getEstado()==estado){
                                trabalhos.add(trabalho);
                            }
                        }
                        trabalhoAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/
        return trabalhos;
    }

    private void configuraRecyclerView(List<Trabalho> todosTrabalhos) {
        recyclerView = findViewById(R.id.listaTrabalhoRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        configuraAdapter(todosTrabalhos, recyclerView);
    }

    private void configuraAdapter(List<Trabalho> todosTrabalhos, RecyclerView listaTrabalhos) {
        trabalhoAdapter = new ListaTrabalhoAdapter(this,todosTrabalhos);
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
                Intent iniciaTrabalhoEspecificoActivity=
                        new Intent(getApplicationContext(),TrabalhoEspecificoActivity.class);
                iniciaTrabalhoEspecificoActivity.putExtra(CHAVE_NOTA,CODIGO_REQUISICAO_ALTERA_NOTA);
                iniciaTrabalhoEspecificoActivity.putExtra(CHAVE_NOME_TRABALHO,trabalho);
                iniciaTrabalhoEspecificoActivity.putExtra(CHAVE_NOME_PERSONAGEM,nomePersonagem);
                startActivity(iniciaTrabalhoEspecificoActivity,
                        ActivityOptions.makeSceneTransitionAnimation(ListaTrabalhosActivity.this).toBundle());
            }
        });
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