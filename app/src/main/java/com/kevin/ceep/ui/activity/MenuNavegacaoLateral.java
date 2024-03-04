package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_TRABALHO;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.kevin.ceep.R;
import com.kevin.ceep.ui.fragment.ListaEstoqueFragment;
import com.kevin.ceep.ui.fragment.ListaPersonagensFragment;
import com.kevin.ceep.ui.fragment.ListaTrabalhosFragment;

public class MenuNavegacaoLateral extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private String personagemRecebido;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_navegacao_lateral);
        setTitle(CHAVE_TITULO_TRABALHO);
        int itemNavegacao = R.id.nav_personagem;
        itemNavegacao = recebeDadosIntent(itemNavegacao);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navegacao_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.bringToFront();
        Log.d("menuNavegacao", "Trouxe para frente.");
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, R.string.abre_menu_navegacao, R.string.fecha_menu_navegacao);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        mostraFragmentSelecionado(itemNavegacao);
        // navigationView.setCheckedItem(itemNavegacao);
        Log.d("menuNavegacao", "Definiu item: " + itemNavegacao);
    }

    private void mostraFragmentSelecionado(int itemNavegacao) {
        FragmentManager gerenciadorDeFragmento = getSupportFragmentManager();
        Fragment fragmentoSelecionado = null;
        Bundle argumento = new Bundle();
        switch (itemNavegacao){
            case R.id.nav_lista_personagem:
                break;
            case R.id.nav_personagem:
                fragmentoSelecionado = new ListaPersonagensFragment();
                Log.d("menuNavegacao", "Clicou item: personagens");
                break;
            case R.id.nav_trabalhos:
                fragmentoSelecionado = new ListaTrabalhosFragment();
                argumento.putString(CHAVE_PERSONAGEM, personagemRecebido);
                fragmentoSelecionado.setArguments(argumento);
                gerenciadorDeFragmento.setFragmentResult(CHAVE_PERSONAGEM, argumento);
                Log.d("menuNavegacao", "Fragmento selecionado: "+fragmentoSelecionado);
                Log.d("menuNavegacao", "Clicou item: trabalhos");
                break;
            case R.id.nav_estoque:
                fragmentoSelecionado = new ListaEstoqueFragment();
                Log.d("menuNavegacao", "Clicou item: estoque");
                break;
            case R.id.nav_sair:
                FirebaseAuth.getInstance().signOut();
                vaiParaEntraActivity();
                break;
        }
        if (fragmentoSelecionado != null){
            reposicionaFragmento(fragmentoSelecionado, gerenciadorDeFragmento);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private int recebeDadosIntent(int itemNavegacao) {
        Intent dadosRecebidos = getIntent();
        personagemRecebido = null;
        if (dadosRecebidos.hasExtra(CHAVE_PERSONAGEM)){
            personagemRecebido = (String) dadosRecebidos.getSerializableExtra(CHAVE_PERSONAGEM);
            Log.d("menuNavegacao", "String id personagem recebido: "+personagemRecebido);
            if (personagemRecebido != null){
                itemNavegacao = R.id.nav_trabalhos;
            }
        }

        return itemNavegacao;
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
        item.setChecked(true);
        Log.d("menuNavegacao", "Definiu item checado como True.");
        mostraFragmentSelecionado(item.getItemId());
        return true;
    }

    private void vaiParaEntraActivity() {
        Intent vaiParaEntraActivity = new Intent(getApplicationContext(),
                EntrarUsuarioActivity.class);
        startActivity(vaiParaEntraActivity, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
    private void reposicionaFragmento(Fragment fragmento, FragmentManager gerenciadorDeFragmento){
        FragmentTransaction transicaoDeFragmento = gerenciadorDeFragmento.beginTransaction();
        transicaoDeFragmento.replace(R.id.frameLayout, fragmento);
        transicaoDeFragmento.commit();
    }

}
