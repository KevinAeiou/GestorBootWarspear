package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.ui.fragment.ListaEstoqueFragment;
import com.kevin.ceep.ui.fragment.ListaTrabalhosFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuNavegacaoLateral extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private List<Personagem> personagens;
    private String personagemRecebido;
    private NavigationView navigationView;
    private Personagem personagemSelecionado;
    private int itemNavegacao;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_navegacao_lateral);

        inicializaComponentes();

        configuraToolbar();

        navigationView.bringToFront();
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, R.string.abre_menu_navegacao, R.string.fecha_menu_navegacao);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        Menu menuNavigation = navigationView.getMenu();
        MenuItem menuPersonagens = menuNavigation.findItem(R.id.nav_lista_personagem);
        SubMenu subItens = menuPersonagens.getSubMenu();
        pegaTodosPersonagens(subItens);

        navigationView.setCheckedItem(itemNavegacao);
        Log.d("menuNavegacao", "Definiu item: " + itemNavegacao);
    }

    private void configuraToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        personagemSelecionado = null;
    }

    private void inicializaComponentes() {
        setTitle(CHAVE_TITULO_TRABALHO);
        itemNavegacao = R.id.nav_trabalhos;
        itemNavegacao = recebeDadosIntent(itemNavegacao);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navegacao_view);
    }

    private void atualizaPersonagemSelecionado() {
        if (personagemSelecionado != null) {
            View cabecalho = navigationView.getHeaderView(0);
            TextView txtCabecalhoNome = cabecalho.findViewById(R.id.txtCabecalhoNomePersonagem);
            TextView txtCabecalhoEstado = cabecalho.findViewById(R.id.txtCabecalhoEstadoPersonagem);
            TextView txtCabecalhoUso = cabecalho.findViewById(R.id.txtCabecalhoUsoPersonagem);
            TextView txtCabecalhoEspacoProducao = cabecalho.findViewById(R.id.txtCabecalhoEspacoProducaoPersonagem);
            txtCabecalhoNome.setText(personagemSelecionado.getNome());
            txtCabecalhoEstado.setText("Estado: "+personagemSelecionado.getEstado());
            txtCabecalhoUso.setText("Uso: "+personagemSelecionado.getUso());
            txtCabecalhoEspacoProducao.setText("Espaço de produção: "+personagemSelecionado.getEspacoProducao());
            personagemRecebido = personagemSelecionado.getId();
            mostraFragmentSelecionado(itemNavegacao);
        }
    }

    private void mostraFragmentSelecionado(int itemNavegacao) {
        FragmentManager gerenciadorDeFragmento = getSupportFragmentManager();
        Fragment fragmentoSelecionado = null;
        Bundle argumento = new Bundle();
        switch (itemNavegacao){
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
                argumento.putString(CHAVE_PERSONAGEM, personagemRecebido);
                fragmentoSelecionado.setArguments(argumento);
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
        personagemSelecionado = personagens.get(item.getOrder());
        item.setChecked(true);
        Log.d("menuNavegacao", "Item selecionado: "+item);
        atualizaPersonagemSelecionado();
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
    private void pegaTodosPersonagens(SubMenu subItens) {
        personagens = new ArrayList<>();
        String usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(CHAVE_USUARIOS);
        databaseReference.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        personagens.clear();
                        subItens.clear();
                        int indice = 0;
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            Personagem personagem = dn.getValue(Personagem.class);
                            personagens.add(personagem);
                            subItens.add(0, indice, indice, personagem.getNome());
                            indice += 1;
                        }
                        if (personagemSelecionado == null) {
                            MenuItem itemMenu = subItens.getItem(0);
                            Log.d("subMenu", "Item personagem: "+itemMenu.getTitle());
                            personagemSelecionado = personagens.get(itemMenu.getOrder());
                            atualizaPersonagemSelecionado();
                        }
                        // indicadorProgresso.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
