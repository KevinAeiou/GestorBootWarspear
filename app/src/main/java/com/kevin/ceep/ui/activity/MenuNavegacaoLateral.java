package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.ui.fragment.EstoqueFragment;
import com.kevin.ceep.ui.fragment.ListaTrabalhosFragment;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Personagem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuNavegacaoLateral extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ProgressDialog progressDialog;
    private List<Personagem> personagens;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private String usuarioId, personagemId;
    private Menu itemMenuPersonagem;
    private final String[] mensagens={"Carregando dados...","Erro de conexão..."};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_navegacao_lateral);
        setTitle(CHAVE_TITULO_TRABALHO);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navegacao_view);
        toolbar = findViewById(R.id.toolbar);
        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        personagens = new ArrayList<>();
        Log.d("listaPersonagens", "Criou ArrayList");
        Log.d("USUARIO", usuarioId);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.bringToFront();
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, R.string.abre_menu_navegacao, R.string.fecha_menu_navegacao);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_trabalhos);
    }

    private Boolean vericaConexaoInternet(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo infConexao = cm.getActiveNetworkInfo();
        return infConexao != null && infConexao.isConnectedOrConnecting();
    }

    private void configuraDialogoProgresso(int posicaoMensagem) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(mensagens[posicaoMensagem]);
        progressDialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_personagem, menu);
        configuraDialogoProgresso(0);
        itemMenuPersonagem = menu;
        SearchView viewBusca = (SearchView) menu.findItem(R.id.itemMenuBusca).getActionView();
        viewBusca.setQueryHint("Buscar");
        viewBusca.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });
        if (vericaConexaoInternet()) {
            Log.d("conexaoInternet", "Possui conexão.");
            pegaTodosPersonagens();

        }else{
            Log.d("conexaoInternet", "Não possui conexão.");
            progressDialog.dismiss();
            Toast.makeText(this,"Erro na conexão...",Toast.LENGTH_LONG).show();
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("itemMenuSelecionado", String.valueOf(item));
        Snackbar.make(findViewById(R.id.frameLayout), String.valueOf(item), Snackbar.LENGTH_LONG).show();
        Log.d("listaPersonagens", "Personagens na lista do menu:");
        for (Personagem personagem: personagens){
            Log.d("listaPersonagens", personagem.getNome());
            if (personagem.getNome().equals(item.getTitle().toString())){
                personagemId = personagem.getId();
                Log.d("itemMenuSelecionado", personagemId);
                Bundle argumentos = new Bundle();
                Log.d("itemMenuSelecionado33", personagemId);
                argumentos.putString(CHAVE_NOME_PERSONAGEM, personagemId);
                ListaTrabalhosFragment trabalhosFragment =  new ListaTrabalhosFragment();
                trabalhosFragment.setArguments(argumentos);
                reposicionaFragmento(trabalhosFragment);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void pegaTodosPersonagens() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(CHAVE_USUARIOS);
        databaseReference.child(usuarioId).child(CHAVE_PERSONAGEM).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        personagens.clear();
                        Log.d("listaPersonagens", "Limpou a lista de personagens");
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            Personagem personagem = dn.getValue(Personagem.class);
                            personagens.add(personagem);
                            Log.d("listaPersonagens", "Personagem adicionado: " + personagem.getNome());
                            itemMenuPersonagem.add(personagem.getNome());
                        }
                        progressDialog.dismiss();
                        Log.d("listaPersonagens", "Personagens na lista: ");
                        personagemId = personagens.get(0).getId();
                        for (Personagem personagem: personagens){
                            Log.d("listaPersonagens", personagem.getNome());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_trabalhos:
                Bundle argumentos = new Bundle();
                Log.d("itemMenuSelecionado33", personagemId);
                argumentos.putString(CHAVE_NOME_PERSONAGEM, personagemId);

                ListaTrabalhosFragment trabalhosFragment =  new ListaTrabalhosFragment();
                trabalhosFragment.setArguments(argumentos);
                reposicionaFragmento(trabalhosFragment);
                break;
            case R.id.nav_personagem:
                Intent vaiParaListaPersonagens =  new Intent(getApplicationContext(), ListaPersonagemActivity.class);
                startActivity(vaiParaListaPersonagens,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            case R.id.nav_estoque:
                reposicionaFragmento(new EstoqueFragment());
                break;
            case R.id.nav_sair:
                FirebaseAuth.getInstance().signOut();
                vaiParaEntraActivity();
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void vaiParaEntraActivity() {
        Intent vaiParaEntraActivity = new Intent(getApplicationContext(),
                EntrarUsuarioActivity.class);
        startActivity(vaiParaEntraActivity, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
    public int retornaContadorLinhaEsqueleto(Context context){
        int pxAltura = retornaAlturaDispositivo(context);
        int alturaLinhaEsqueleto = (int) getResources().getDimension(R.dimen.row_layout_height);
        return (int) Math.ceil(pxAltura/alturaLinhaEsqueleto);
    }
    public int retornaAlturaDispositivo(Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.heightPixels;
    }

    private void reposicionaFragmento(Fragment fragmento){
        FragmentManager gerenciadorDeFragmento = getSupportFragmentManager();
        FragmentTransaction transicaoDeFragmento = gerenciadorDeFragmento.beginTransaction();
        transicaoDeFragmento.replace(R.id.frameLayout, fragmento);
        transicaoDeFragmento.commit();

    }

}
