package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_REQUISICAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_ALTERA_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_TRABALHO;

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
import com.google.android.material.progressindicator.CircularProgressIndicator;
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
    private String idPersonagemRecebido;
    private NavigationView navigationView;
    private Personagem personagemSelecionado;
    private CircularProgressIndicator indicadorProgresso;
    private TextView txtCabecalhoNome, txtCabecalhoEstado, txtCabecalhoUso, txtCabecalhoEspacoProducao;
    private int itemNavegacao;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_navegacao_lateral);

        inicializaComponentes();

        configuraToolbar();

        navigationView.bringToFront();
        configuraToogle();

        navigationView.setNavigationItemSelectedListener(this);
        pegaTodosPersonagens();

        navigationView.setCheckedItem(itemNavegacao);
        Log.d("menuNavegacao", "Definiu item: " + itemNavegacao);
    }

    private void configuraSubMenuPersonagem() {
        Menu menuNavigation = navigationView.getMenu();
        MenuItem menuPersonagens = menuNavigation.findItem(R.id.nav_lista_personagem);
        menuPersonagens.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SubMenu subItens = menuPersonagens.getSubMenu();
        subItens.clear();
        int indice = 0;
        for (Personagem personagem : personagens) {
            subItens.add(0, indice, indice, personagem.getNome());
            indice += 1;
        }
        SubMenu subMenuTeste = menuNavigation.addSubMenu(9,99,1,"Sub Menu teste");
        subMenuTeste.add(9,0,0,"Testee1");
        subMenuTeste.add(9,1,1,"Testee2");
        subMenuTeste.add(9,2,2,"Testee3");
        subMenuTeste.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        if (personagemSelecionado == null) {
            MenuItem itemMenu = subItens.getItem(0);
            personagemSelecionado = personagens.get(itemMenu.getOrder());
            atualizaPersonagemSelecionado();
        }
    }

    private void configuraToogle() {
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, R.string.abre_menu_navegacao, R.string.fecha_menu_navegacao);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();
    }

    private void configuraToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void inicializaComponentes() {
        setTitle(CHAVE_TITULO_TRABALHO);
        itemNavegacao = R.id.nav_trabalhos;
        itemNavegacao = recebeDadosIntent(itemNavegacao);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navegacao_view);
        indicadorProgresso = findViewById(R.id.indicadorProgressoMenuNavegacao);
        View cabecalho = navigationView.getHeaderView(0);
        txtCabecalhoNome = cabecalho.findViewById(R.id.txtCabecalhoNomePersonagem);
        txtCabecalhoEstado = cabecalho.findViewById(R.id.txtCabecalhoEstadoPersonagem);
        txtCabecalhoUso = cabecalho.findViewById(R.id.txtCabecalhoUsoPersonagem);
        txtCabecalhoEspacoProducao = cabecalho.findViewById(R.id.txtCabecalhoEspacoProducaoPersonagem);
        personagemSelecionado = null;
    }

    private void atualizaPersonagemSelecionado() {
        if (personagemSelecionado != null) {
            txtCabecalhoNome.setText(personagemSelecionado.getNome());
            txtCabecalhoEstado.setText("Estado: "+personagemSelecionado.getEstado());
            txtCabecalhoUso.setText("Uso: "+personagemSelecionado.getUso());
            txtCabecalhoEspacoProducao.setText("Espaço de produção: "+personagemSelecionado.getEspacoProducao());
            idPersonagemRecebido = personagemSelecionado.getId();
            mostraFragmentSelecionado(navigationView.getCheckedItem());
        }
    }

    private void mostraFragmentSelecionado(MenuItem itemNavegacao) {
        Fragment fragmentoSelecionado = null;
        Bundle argumento = new Bundle();
        argumento.putString(CHAVE_PERSONAGEM, idPersonagemRecebido);
        switch (itemNavegacao.getItemId()){
            case R.id.nav_trabalhos:
                fragmentoSelecionado = new ListaTrabalhosFragment();
                fragmentoSelecionado.setArguments(argumento);
                break;
            case R.id.nav_estoque:
                fragmentoSelecionado = new ListaEstoqueFragment();
                fragmentoSelecionado.setArguments(argumento);
                break;
            case R.id.nav_configuracao:
                vaiParaAtributosPersonagem(CODIGO_REQUISICAO_ALTERA_TRABALHO);
                break;
            case R.id.nav_novo_personagem:
                vaiParaAtributosPersonagem(CODIGO_REQUISICAO_INSERE_TRABALHO);
                break;
            case R.id.nav_sair:
                FirebaseAuth.getInstance().signOut();
                vaiParaEntraActivity();
                break;
        }
        if (fragmentoSelecionado != null) {
            reposicionaFragmento(fragmentoSelecionado);
        }
    }

    private void vaiParaAtributosPersonagem(int codigoRequisicao) {
        Intent iniciaVaiParaAtributosPersonagem = new Intent(getApplicationContext(), AtributosPersonagemActivity.class);
        iniciaVaiParaAtributosPersonagem.putExtra(CHAVE_PERSONAGEM, personagemSelecionado);
        iniciaVaiParaAtributosPersonagem.putExtra(CHAVE_REQUISICAO, codigoRequisicao);
        startActivity(iniciaVaiParaAtributosPersonagem);
    }

    private int recebeDadosIntent(int itemNavegacao) {
        Intent dadosRecebidos = getIntent();
        idPersonagemRecebido = null;
        if (dadosRecebidos.hasExtra(CHAVE_PERSONAGEM)){
            idPersonagemRecebido = (String) dadosRecebidos.getSerializableExtra(CHAVE_PERSONAGEM);
            Log.d("menuNavegacao", "String id personagem recebido: "+ idPersonagemRecebido);
            if (idPersonagemRecebido != null){
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
        Log.d("menuNavegacao", "Item selecionado: "+item);
        Log.d("menuNavegacao", "ID do menu do item selecionado: "+item.getGroupId());
        if (item.getGroupId() == 0) {
            personagemSelecionado = personagens.get(item.getOrder());
            Log.d("menuNavegacao", "Personagem selecionado: "+personagemSelecionado.getNome());
            atualizaPersonagemSelecionado();
        } else {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        item.setChecked(true);
        mostraFragmentSelecionado(item);
        return true;
    }

    private void vaiParaEntraActivity() {
        Intent vaiParaEntraActivity = new Intent(getApplicationContext(),
                EntrarUsuarioActivity.class);
        startActivity(vaiParaEntraActivity, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
    private void reposicionaFragmento(Fragment fragmento) {
        FragmentManager gerenciadorDeFragmento = getSupportFragmentManager();
        FragmentTransaction transicaoDeFragmento = gerenciadorDeFragmento.beginTransaction();
        transicaoDeFragmento.replace(R.id.frameLayout, fragmento);
        transicaoDeFragmento.commit();
    }
    private void pegaTodosPersonagens() {
        personagens = new ArrayList<>();
        String usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(CHAVE_USUARIOS);
        databaseReference.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        personagens.clear();
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            Personagem personagem = dn.getValue(Personagem.class);
                            personagens.add(personagem);
                        }
                        configuraSubMenuPersonagem();
                        indicadorProgresso.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }
}
