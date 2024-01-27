package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_TRABALHO;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.kevin.ceep.ui.fragment.ListaEstoqueFragment;
import com.kevin.ceep.ui.fragment.ListaPersonagensFragment;
import com.kevin.ceep.ui.fragment.ListaTrabalhosFragment;
import com.kevin.ceep.R;

public class MenuNavegacaoLateral extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_navegacao_lateral);
        setTitle(CHAVE_TITULO_TRABALHO);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navegacao_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.bringToFront();
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, R.string.abre_menu_navegacao, R.string.fecha_menu_navegacao);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_trabalhos);
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
                reposicionaFragmento(new ListaTrabalhosFragment());
                break;
            case R.id.nav_personagem:
                reposicionaFragmento(new ListaPersonagensFragment());
                break;
            case R.id.nav_estoque:
                reposicionaFragmento(new ListaEstoqueFragment());
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
