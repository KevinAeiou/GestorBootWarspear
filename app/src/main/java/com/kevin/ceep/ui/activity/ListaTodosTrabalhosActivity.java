package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.Utilitario.comparaString;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.databinding.ActivityListaTodosTrabalhosBinding;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.ProfissaoTrabalho;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoEstoque;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTodosTrabalhosAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoEspecificoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListaTodosTrabalhosActivity extends AppCompatActivity {
    private ActivityListaTodosTrabalhosBinding binding;
    private ListaTodosTrabalhosAdapter listaTodosTrabalhosAdapter;
    private ListaTrabalhoEspecificoAdapter listaTrabalhoEspecificoAdapter;
    private FloatingActionButton botaoNovoTrabalho;
    private RecyclerView meuRecycler;
    private List<ProfissaoTrabalho> profissoesTrabalhos;
    private List<Trabalho> todosTrabalhos;
    private ProgressBar indicadorProgresso;
    private DatabaseReference minhaReferencia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaTodosTrabalhosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        inicializaComponentes();
        atualizaListaProfissoesTrabalhos();

        configuraBotaoCadastraNovoTrabalho();
        configuraSwipeRefreshLayout();
        Log.i(TAG_ACTIVITY,"onCreateListaTrabalhosEspecificos");
        }
    private void inicializaComponentes() {
        setTitle(CHAVE_TITULO_TRABALHO);
        botaoNovoTrabalho = binding.floatingButtonProfissoesTrabalhos;
        indicadorProgresso = binding.indicadorProgressoProfissoesTrabalhos;
        meuRecycler = binding.recyclerViewProfissoesTrabalhos;
        FirebaseDatabase meuBanco = FirebaseDatabase.getInstance();
        minhaReferencia = meuBanco.getReference(CHAVE_LISTA_TRABALHO);

    }
    private void configuraBotaoCadastraNovoTrabalho() {
        botaoNovoTrabalho.setOnClickListener(view -> vaiParaCadastraNovoTrabalhoActivity());
    }

    private void vaiParaCadastraNovoTrabalhoActivity() {
        Intent iniciaVaiParaCadastraNovoTrabalho = new Intent(getApplicationContext(),
                        TrabalhoEspecificoActivity.class);
        iniciaVaiParaCadastraNovoTrabalho.putExtra(CHAVE_TRABALHO, CODIGO_REQUISICAO_INSERE_TRABALHO);
        startActivity(iniciaVaiParaCadastraNovoTrabalho);
    }

    private void configuraSwipeRefreshLayout() {
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshProfissoesTrabalhos);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            atualizaListaProfissoesTrabalhos();
        });
    }

    private void atualizaListaProfissoesTrabalhos() {
        pegaTodosTrabalhos();
        List<ProfissaoTrabalho> profissoesTrabalhos = new ArrayList<>();
        configuraRecyclerView(profissoesTrabalhos);
    }

    private void filtraTrabalhosProfissao() {
        profissoesTrabalhos = new ArrayList<>();
        for (Trabalho trabalho : todosTrabalhos) {
            if (profissoesTrabalhos.isEmpty()){
                ArrayList<Trabalho> listaTrabalhosProfissao = new ArrayList<>();
                listaTrabalhosProfissao.add(trabalho);
                ProfissaoTrabalho profissaoTrabalho = new ProfissaoTrabalho(trabalho.getProfissao(), listaTrabalhosProfissao);
                profissoesTrabalhos.add(profissaoTrabalho);
            } else {
                if (!profissaoExiste(trabalho)) {
                    ArrayList<Trabalho> listaTrabalhosProfissao = new ArrayList<>();
                    listaTrabalhosProfissao.add(trabalho);
                    ProfissaoTrabalho profissaoTrabalho = new ProfissaoTrabalho(trabalho.getProfissao(), listaTrabalhosProfissao);
                    profissoesTrabalhos.add(profissaoTrabalho);
                }
            }
        }
        indicadorProgresso.setVisibility(View.GONE);
        listaTodosTrabalhosAdapter.setListaFiltrada(profissoesTrabalhos);
    }

    private boolean profissaoExiste(Trabalho trabalho) {
        for (ProfissaoTrabalho profissaoTrabalho : profissoesTrabalhos) {
            if (comparaString(profissaoTrabalho.getNome(), trabalho.getProfissao())) {
                profissaoTrabalho.getTrabalhos().add(trabalho);
                return true;
            }
        }
        return false;
    }

    private void pegaTodosTrabalhos() {
        todosTrabalhos = new ArrayList<>();
        minhaReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todosTrabalhos.clear();
                for (DataSnapshot dn:dataSnapshot.getChildren()){
                    Trabalho trabalho = dn.getValue(Trabalho.class);
                    if (trabalho != null){
                        todosTrabalhos.add(trabalho);
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    todosTrabalhos.sort(Comparator.comparing(Trabalho::getProfissao).thenComparing(Trabalho::getRaridade).thenComparing(Trabalho::getNivel).thenComparing(Trabalho::getNome));
                }
                filtraTrabalhosProfissao();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(binding.constraintLayoutProfissoesTrabalhos, "Erro ao carregar dados: "+ databaseError, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void configuraRecyclerView(List<ProfissaoTrabalho> profissoesTrabalhos) {
        meuRecycler.setHasFixedSize(true);
        meuRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        configuraAdapter(profissoesTrabalhos, meuRecycler);
    }

    private void configuraAdapter(List<ProfissaoTrabalho> profissoesTrabalhos, RecyclerView listaTrabalhos) {
        listaTodosTrabalhosAdapter = new ListaTodosTrabalhosAdapter(profissoesTrabalhos, getApplicationContext());
        listaTrabalhos.setAdapter(listaTodosTrabalhosAdapter);
    }
    @Override
    protected void onStop() {
        super.onStop();
        binding = null;
    }
}