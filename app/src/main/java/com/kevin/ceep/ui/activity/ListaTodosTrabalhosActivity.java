package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_TRABALHO;
import static com.kevin.ceep.utilitario.Utilitario.comparaString;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.kevin.ceep.R;
import com.kevin.ceep.databinding.ActivityListaTodosTrabalhosBinding;
import com.kevin.ceep.model.ProfissaoTrabalho;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.repository.TrabalhoRepository;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTodosTrabalhosAdapter;
import com.kevin.ceep.ui.viewModel.TrabalhoViewModel;
import com.kevin.ceep.ui.viewModel.factory.TrabalhoViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class ListaTodosTrabalhosActivity extends AppCompatActivity {
    private ActivityListaTodosTrabalhosBinding binding;
    private ListaTodosTrabalhosAdapter listaTodosTrabalhosAdapter;
    private FloatingActionButton botaoNovoTrabalho;
    private RecyclerView meuRecycler;
    private List<ProfissaoTrabalho> profissoesTrabalhos;
    private List<Trabalho> todosTrabalhos;
    private ProgressBar indicadorProgresso;
    private TrabalhoViewModel trabalhoViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaTodosTrabalhosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        inicializaComponentes();
        configuraRecyclerView(profissoesTrabalhos);
        configuraBotaoCadastraNovoTrabalho();
        configuraSwipeRefreshLayout();
        }
    private void inicializaComponentes() {
        setTitle(CHAVE_TITULO_TRABALHO);
        profissoesTrabalhos = new ArrayList<>();
        botaoNovoTrabalho = binding.floatingButtonProfissoesTrabalhos;
        indicadorProgresso = binding.indicadorProgressoProfissoesTrabalhos;
        meuRecycler = binding.recyclerViewProfissoesTrabalhos;
        TrabalhoViewModelFactory trabalhoViewModelFactory = new TrabalhoViewModelFactory(new TrabalhoRepository());
        trabalhoViewModel = new ViewModelProvider(this, trabalhoViewModelFactory).get(TrabalhoViewModel.class);
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
            pegaTodosTrabalhos();
        });
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
        listaTodosTrabalhosAdapter.atualiza(profissoesTrabalhos);
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
        trabalhoViewModel.pegaTodosTrabalhos().observe(this, arrayListResource -> {
            if (arrayListResource.getDado() != null) {
                todosTrabalhos = arrayListResource.getDado();
                filtraTrabalhosProfissao();
            } else {
                Snackbar.make(binding.getRoot(), "Erro: "+arrayListResource.getErro(), Snackbar.LENGTH_LONG).show();
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
    protected void onResume() {
        super.onResume();
        pegaTodosTrabalhos();
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding = null;
    }
}