package com.kevin.ceep.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kevin.ceep.databinding.FragmentListaProfissoesBinding;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.ui.recyclerview.adapter.ListaProfissaoAdapter;
import com.kevin.ceep.ui.viewModel.ComponentesVisuais;
import com.kevin.ceep.ui.viewModel.EstadoAppViewModel;
import com.kevin.ceep.ui.viewModel.ProfissaoViewModel;
import com.kevin.ceep.ui.viewModel.factory.ProfissaoViewModelFactory;

import java.util.ArrayList;

public class ListaProfissoesFragment extends BaseFragment<FragmentListaProfissoesBinding> {

    private ListaProfissaoAdapter listaProfissaoAdapter;
    private ArrayList<Profissao> profissoes;
    private RecyclerView meuRecycler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar indicadorProgresso;
    private ProfissaoViewModel profissaoViewModel;

    public ListaProfissoesFragment() {}

    @Override
    protected FragmentListaProfissoesBinding inflateBinding(
        LayoutInflater inflater,
        ViewGroup container
    ) {
        return FragmentListaProfissoesBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configuraComponentesVisuais();
        inicializaComponentes();
        configuraRecyclerView();
        configuraSwipeRefreshLayout();
    }

    private void configuraComponentesVisuais() {
        EstadoAppViewModel estadoAppViewModel = new ViewModelProvider(requireActivity())
            .get(EstadoAppViewModel.class);
        ComponentesVisuais componentesVisuais = new ComponentesVisuais();
        componentesVisuais.appBar = true;
        componentesVisuais.menuNavegacaoInferior = false;
        componentesVisuais.menuNavegacaoLateral = true;

        estadoAppViewModel.componentes.setValue(componentesVisuais);
    }

    private void inicializaComponentes() {
        profissoes = new ArrayList<>();
        meuRecycler = binding.recyclerViewListaProfissoesFragment;
        swipeRefreshLayout = binding.swipeRefreshLayoutListaProfissoesFragment;
        indicadorProgresso = binding.indicadorProgressoListaProfissoesFragment;
        ProfissaoViewModelFactory profissaoViewModelFactory = new ProfissaoViewModelFactory(
            getContext()
        );
        profissaoViewModel = new ViewModelProvider(
            this,
            profissaoViewModelFactory
        ).get(ProfissaoViewModel.class);
    }

    private void configuraRecyclerView() {
        meuRecycler.setHasFixedSize(true);
        meuRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        configuraAdapter();
    }

    private void configuraAdapter() {
        listaProfissaoAdapter = new ListaProfissaoAdapter(profissoes, getContext());
        meuRecycler.setAdapter(listaProfissaoAdapter);

        configuraCliqueItemProfissao();
    }

    private void configuraCliqueItemProfissao() {
        listaProfissaoAdapter.setOnItemClickListener(
            (profissao, adapterPosition) -> {
                mostraMensagem("Item: " + profissao.getNome());
            });
    }

    private void configuraSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            listaProfissaoAdapter.limpaLista();
            sincronizaProfissoes();
            recuperaProfissoes();
        });
    }

    private void sincronizaProfissoes() {
        profissaoViewModel.getSincronizacaoResultado().observe(
            getViewLifecycleOwner(),
            resultadoSincroniza -> {
                if (resultadoSincroniza.getErro() != null) {
                    mostraMensagem("Erro: " + resultadoSincroniza.getErro());
                }
            }
        );

        profissaoViewModel.sincronizaProfissoes();
    }

    private void recuperaProfissoes() {
        profissaoViewModel.getRecuperacaoProfissoes().observe(
            getViewLifecycleOwner(),
            resultadoProfissoes -> {
                if (resultadoProfissoes.getDado() != null) {
                    indicadorProgresso.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    profissoes = resultadoProfissoes.getDado();
                    listaProfissaoAdapter.atualiza(profissoes);
                }
                if (resultadoProfissoes.getErro() != null) {
                    mostraMensagem("Erro" + resultadoProfissoes.getErro());
                }
            }
        );

        profissaoViewModel.recuperaProfissoes();
    }

    @Override
    public void onResume() {
        super.onResume();

        recuperaProfissoes();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        removeOuvinteProfissao();
        binding = null;
    }

    private void removeOuvinteProfissao() {
        if (profissaoViewModel == null) return;

        profissaoViewModel.removeOuvinte();
    }

}