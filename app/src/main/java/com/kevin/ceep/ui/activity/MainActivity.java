package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_REQUISICAO;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.databinding.ActivityMainBinding;
import com.kevin.ceep.model.Personagem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private NavController controlador;
    private DrawerLayout drawerLayout;
    private List<Personagem> personagens;
    private String idPersonagemRecebido;
    private NavigationView navigationView;
    private Personagem personagemSelecionado;
    private TextView txtCabecalhoNome, txtCabecalhoEstado, txtCabecalhoUso, txtCabecalhoEspacoProducao;
    private int itemNavegacao;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        inicializaComponentes();

        setSupportActionBar(binding.appBarMain.toolbar);

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.listaTrabalhosProducao, R.id.listaEstoque, R.id.listaProdutosVendidos, R.id.listaProfissoes)
                .setOpenableLayout(drawerLayout)
                .build();
        controlador = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        controlador.addOnDestinationChangedListener((navController,
                                                     navDestination,
                                                     bundle) -> {
                if (bundle != null) {
                    if (bundle.containsKey(CHAVE_PERSONAGEM)){
                        idPersonagemRecebido = bundle.getString(CHAVE_PERSONAGEM);
                    }
                }
                Log.d("controlador",
                "onDestinationChanged: "+navDestination.getLabel());
        });
        NavigationUI.setupActionBarWithNavController(this, controlador, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, controlador);
        configuraToogle();
        configuraToolbar();
        recebeDadosIntent();
        pegaTodosPersonagens();
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(controlador, appBarConfiguration)
                || super.onSupportNavigateUp();
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
        drawerLayout = binding.drawerLayout;
        navigationView = binding.navegacaoView;
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
        }
    }

    private void vaiParaListaTodosTrabalhos() {
        Intent iniciaVaiParaListaTodosTrabalhos = new Intent(getApplicationContext(), ListaTodosTrabalhosActivity.class);
        startActivity(iniciaVaiParaListaTodosTrabalhos);
    }

    private void vaiParaAtributosPersonagem(int codigoRequisicao) {
        Intent iniciaVaiParaAtributosPersonagem = new Intent(getApplicationContext(), AtributosPersonagemActivity.class);
        iniciaVaiParaAtributosPersonagem.putExtra(CHAVE_PERSONAGEM, personagemSelecionado);
        iniciaVaiParaAtributosPersonagem.putExtra(CHAVE_REQUISICAO, codigoRequisicao);
        startActivity(iniciaVaiParaAtributosPersonagem);
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        idPersonagemRecebido = null;
        if (dadosRecebidos.hasExtra(CHAVE_PERSONAGEM)){
            idPersonagemRecebido = (String) dadosRecebidos.getSerializableExtra(CHAVE_PERSONAGEM);
        }
    }
    private void vaiParaEntraActivity() {
        Intent vaiParaEntraActivity = new Intent(getApplicationContext(),
                EntrarUsuarioActivity.class);
        startActivity(vaiParaEntraActivity, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }
}
