package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_CONFIRMA_CADASTRO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_POSICAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_ALTERA_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.POSICAO_INVALIDA;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
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
import com.kevin.ceep.model.Raridade;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoProducaoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class ListaTrabalhosActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";
    ActivityResultLauncher<Intent> activityLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(TAG,"onActivityResult");
                if (result.getResultCode()==1){
                    Intent intent=result.getData();
                    if (intent!=null){

                    }
                }
            }
    );
    private DatabaseReference databaseReference;
    private ListaTrabalhoProducaoAdapter trabalhoAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private List<TrabalhoProducao> trabalhos;
    private Chip chipEstado;
    private Boolean isChecked=false;
    private String usuarioId, personagemId;
    private Integer estado=0;

    public LinearLayout skeletonLayout;
    public ShimmerLayout shimmerLayout;
    public LayoutInflater inflater;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_trabalhos);
        setTitle(CHAVE_TITULO_TRABALHO);

        recebeDadosIntent();
        inicializaComponentes();

        skeletonLayout = findViewById(R.id.skeletonLayout);
        shimmerLayout = findViewById(R.id.shimmerSkeleton);

        atualizaListaTrabalho();
        configuraEstadoPersonagem();
        configuraChipMudaEstadoPersonagem();
        configuraCampoPesquisa();
        configuraBotaoInsereTrabalho();
        configuraDeslizeItem();
        configuraSwipeRefreshLayout();
        Log.i(TAG_ACTIVITY,"onCreateListaTrabalho");
    }

    private void configuraChipMudaEstadoPersonagem() {
        chipEstado.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                chipEstado.setText(R.string.stringAtivo);
                modificaEstadoPersonagem(true);
            }else{
                chipEstado.setText(R.string.stringInativo);
                modificaEstadoPersonagem(false);
            }
        });
    }

    private void configuraEstadoPersonagem() {
        databaseReference.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                child(personagemId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Personagem personagem = dataSnapshot.getValue(Personagem.class);
                        assert personagem != null;
                        if (personagem.getEstado()){
                            Log.d("SWITCH","Estado do personagem é ativo.");
                            isChecked=true;
                            Log.d("SWITCH", "O valor de ischeched é: ."+ true);
                            chipEstado.setText(R.string.stringAtivo);
                            chipEstado.setChecked(isChecked);
                        }else{
                            Log.d("SWITCH","Estado do personagem é inativo.");
                            isChecked=false;
                            Log.d("SWITCH", "O valor de ischeched é: ."+ false);
                            chipEstado.setText(R.string.stringInativo);
                            chipEstado.setChecked(isChecked);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void inicializaComponentes() {
        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(CHAVE_USUARIOS);
        chipEstado=findViewById(R.id.chipEstadoPersonagem);
    }

    private void configuraCampoPesquisa() {
        SearchView busca = findViewById(R.id.buscaTrabalho);
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
        List<TrabalhoProducao> listaFiltro = new ArrayList<>();
        for (TrabalhoProducao trabalho:trabalhos){
            if (removerAcentos(trabalho.getNome().toLowerCase()).contains(removerAcentos(textoBusca.toLowerCase()))||
                    removerAcentos(trabalho.getProfissao().toLowerCase()).contains(removerAcentos(textoBusca.toLowerCase()))||
                    removerAcentos(trabalho.getTipo_licenca().toLowerCase()).contains(removerAcentos(textoBusca.toLowerCase()))){
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
        getMenuInflater().inflate(R.menu.menu_clasifica_lista_trabalho,
                menu);
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

    private void modificaEstadoPersonagem(boolean estadoPersonagem) {
        databaseReference.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                .child(personagemId).child("estado").setValue(estadoPersonagem);
        Log.d("SWITCH","Estado do personagem modificado para: "+estadoPersonagem);
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
        if (vericaConexaoInternet()) {
            List<TrabalhoProducao> todosTrabalhos = pegaTodosTrabalhos();
            configuraRecyclerView(todosTrabalhos);
        }else {
            progressDialog.dismiss();
            Toast.makeText(this,"Erro na conexão...",Toast.LENGTH_LONG).show();
        }
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
                ListaTrabalhoProducaoAdapter trabalhoAdapter = (ListaTrabalhoProducaoAdapter) recyclerView.getAdapter();
                removeTrabalhoLista(posicaoDeslize);
                if (trabalhoAdapter != null) {
                    trabalhoAdapter.remove(posicaoDeslize);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void removeTrabalhoLista(int swipePosicao) {
        String idTrabalho = trabalhos.get(swipePosicao).getId();
        databaseReference.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                child(personagemId).child(CHAVE_LISTA_DESEJO).
                child(idTrabalho).removeValue();
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_PERSONAGEM)){
            personagemId=(String) dadosRecebidos.getSerializableExtra(CHAVE_PERSONAGEM);
        }
        if (dadosRecebidos.hasExtra(CHAVE_CONFIRMA_CADASTRO)) {
            Boolean confirmaCadastro= (Boolean) dadosRecebidos.getSerializableExtra(CHAVE_CONFIRMA_CADASTRO);
            if (Boolean.TRUE.equals(confirmaCadastro)){
                final Toast toast = configuraToastCustomizado();
                toast.show();
                dadosRecebidos.removeExtra(CHAVE_CONFIRMA_CADASTRO);
            }
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

    private void mostraDialogodeProresso(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.stringCarregandoDados));
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
        iniciaListaRaridade.putExtra(CHAVE_PERSONAGEM, personagemId);
        startActivity(iniciaListaRaridade,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        //startActivityForResult(iniciaFormularioNota, CODIGO_REQUISICAO_INSERE_NOTA);
    }

    private List<TrabalhoProducao> pegaTodosTrabalhos() {
        trabalhos = new ArrayList<>();
        Log.d("USUARIO", usuarioId);
        databaseReference.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                child(personagemId).child(CHAVE_LISTA_DESEJO).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        trabalhos.clear();
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            TrabalhoProducao trabalho = dn.getValue(TrabalhoProducao.class);
                            filtraListaTrabalho(trabalho);
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

    private void filtraListaTrabalho(TrabalhoProducao trabalho) {
        if (estado==3){
            trabalhos.add(trabalho);
        }else if (trabalho.getEstado().equals(estado)){
            trabalhos.add(trabalho);
        }
    }

    private void configuraRecyclerView(List<TrabalhoProducao> todosTrabalhos) {
        recyclerView = findViewById(R.id.listaTrabalhoRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        configuraAdapter(todosTrabalhos, recyclerView);
    }

    private void configuraAdapter(List<TrabalhoProducao> todosTrabalhos, RecyclerView listaTrabalhos) {
        trabalhoAdapter = new ListaTrabalhoProducaoAdapter(this,todosTrabalhos);
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
                vaiParaTrabalhoEspecificoActivity(trabalho);
            }

            @Override
            public void onItemClick(Raridade raridade, int adapterPosition) {

            }
        });
    }

    private void vaiParaTrabalhoEspecificoActivity(Trabalho trabalho) {
        Intent iniciaTrabalhoEspecificoActivity=
                new Intent(getApplicationContext(),TrabalhoEspecificoActivity.class);
        iniciaTrabalhoEspecificoActivity.putExtra(CHAVE_TRABALHO, CODIGO_REQUISICAO_ALTERA_TRABALHO);
        iniciaTrabalhoEspecificoActivity.putExtra(CHAVE_NOME_TRABALHO, trabalho);
        iniciaTrabalhoEspecificoActivity.putExtra(CHAVE_PERSONAGEM, personagemId);
        activityLauncher.launch(iniciaTrabalhoEspecificoActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ehResultadoInsereNota(requestCode, data)) {
            if (resultadoOk(resultCode)){
                TrabalhoProducao notaRecebida = (TrabalhoProducao) Objects.requireNonNull(data).getSerializableExtra(CHAVE_TRABALHO);
                adiciona(notaRecebida);
            }
        }

        if (ehResultadoAlteraNota(requestCode, data)) {
            if (resultadoOk(resultCode)){
                TrabalhoProducao notaRecebida = (TrabalhoProducao) Objects.requireNonNull(data).getSerializableExtra(CHAVE_TRABALHO);
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

    private void altera(TrabalhoProducao trabalhoProducao, int posicao) {
        new NotaDAO().altera(posicao, trabalhoProducao);
        trabalhoAdapter.altera(posicao, trabalhoProducao);
    }

    private boolean ehPosicaoValida(int posicaoRecebida) {
        return posicaoRecebida > POSICAO_INVALIDA;
    }

    private boolean ehResultadoAlteraNota(int requestCode, @Nullable Intent data) {
        return ehCodigoRequisicaoAlteraNota(requestCode) &&
                temNota(data);
    }

    private boolean ehCodigoRequisicaoAlteraNota(int requestCode) {
        return requestCode == CODIGO_REQUISICAO_ALTERA_TRABALHO;
    }

    private void adiciona(TrabalhoProducao nota) {
        new NotaDAO().insere(nota);
        trabalhoAdapter.adiciona(nota);
    }

    private boolean ehResultadoInsereNota(int requestCode, @Nullable Intent data) {
        return ehCodigoRequisicaoInsereNota(requestCode)&&
                temNota(data);
    }

    private boolean temNota(@Nullable Intent data) {
        return Objects.requireNonNull(data).hasExtra(CHAVE_TRABALHO);
    }

    private boolean resultadoOk(int resultCode) {
        return resultCode == Activity.RESULT_OK;
    }

    private boolean ehCodigoRequisicaoInsereNota(int requestCode) {
        return requestCode == CODIGO_REQUISICAO_INSERE_TRABALHO;
    }

    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    private boolean vericaConexaoInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo infConexao = cm.getActiveNetworkInfo();
        return infConexao != null && infConexao.isConnectedOrConnecting();
    }
}