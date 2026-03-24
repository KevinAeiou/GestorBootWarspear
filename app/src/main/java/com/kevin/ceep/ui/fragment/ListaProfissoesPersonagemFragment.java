package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.Constantes.CHAVE_ID_PERSONAGEM;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kevin.ceep.databinding.FragmentListaProfissoesPersonagemBinding;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.repository.PersonagemRepository;
import com.kevin.ceep.ui.recyclerview.adapter.ListaProfissaoPersonagemAdapter;
import com.kevin.ceep.ui.viewModel.ComponentesVisuais;
import com.kevin.ceep.ui.viewModel.EstadoAppViewModel;
import com.kevin.ceep.ui.viewModel.PersonagemViewModel;
import com.kevin.ceep.ui.viewModel.ProfissaoPersonagemViewModel;
import com.kevin.ceep.ui.viewModel.TrabalhoProducaoViewModel;
import com.kevin.ceep.ui.viewModel.factory.PersonagemViewModelFactory;
import com.kevin.ceep.ui.viewModel.factory.ProfissaoPersonagemViewModelFactory;
import com.kevin.ceep.ui.viewModel.factory.TrabalhoProducaoViewModelFactory;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ListaProfissoesPersonagemFragment
        extends BaseFragment<FragmentListaProfissoesPersonagemBinding> {
    private ListaProfissaoPersonagemAdapter listaProfissaoPersonagemAdapter;
    private String idPersonagem;
    private ArrayList<Profissao> todasProfissoes;
    private ArrayList<TrabalhoProducao> producao;
    private RecyclerView meuRecycler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar indicadorProgresso;
    private ProfissaoPersonagemViewModel profissaoPersonagemViewModel;
    private PersonagemViewModel personagemViewModel;
    private TrabalhoProducaoViewModel trabalhoProducaoViewModel;

    public ListaProfissoesPersonagemFragment() {
    }
    @Override
    protected FragmentListaProfissoesPersonagemBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentListaProfissoesPersonagemBinding.inflate(
                inflater,
                container,
                false
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configuraComponentesVisuais();
        inicializaComponentes();
        configuraRecyclerView();
        configuraSwipeRefreshLayout();
        configuraPersonagemSelecionado();
    }

    private void configuraComponentesVisuais() {
        EstadoAppViewModel estadoAppViewModel = new ViewModelProvider(requireActivity()).get(EstadoAppViewModel.class);
        ComponentesVisuais componentesVisuais = new ComponentesVisuais();
        componentesVisuais.appBar = true;
        componentesVisuais.menuNavegacaoLateral = true;
        componentesVisuais.menuNavegacaoInferior = true;
        estadoAppViewModel.componentes.setValue(componentesVisuais);
    }

    private void configuraPersonagemSelecionado() {
        personagemViewModel.pegaPersonagemSelecionado().observe(getViewLifecycleOwner(), resultadoPegaPersonagem -> {
            if (resultadoPegaPersonagem == null) return;
            atualizarViewModel(resultadoPegaPersonagem.getId());
            TrabalhoProducaoViewModelFactory trabalhoProducaoViewModelFactory = new TrabalhoProducaoViewModelFactory(resultadoPegaPersonagem.getId());
            trabalhoProducaoViewModel = new ViewModelProvider(
                    this,
                    trabalhoProducaoViewModelFactory
            ).get(resultadoPegaPersonagem.getId(), TrabalhoProducaoViewModel.class);
            trabalhoProducaoViewModel.getTrabalhosProducao().observe(
                    getViewLifecycleOwner(),
                    resultadoRecuperaProducao -> {
                        if (resultadoRecuperaProducao.getDado() != null) {
                            producao = resultadoRecuperaProducao.getDado();
                        }
                        if (resultadoRecuperaProducao.getErro() == null) {
                            return;
                        }
                        mostraMensagem(resultadoRecuperaProducao.getErro());
                    });
            trabalhoProducaoViewModel.recuperaTrabalhosProducao();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void configuraRecyclerView() {
        meuRecycler.setHasFixedSize(true);
        meuRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        configuraAdapter(meuRecycler);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void configuraAdapter(RecyclerView meuRecycler) {
        listaProfissaoPersonagemAdapter = new ListaProfissaoPersonagemAdapter(getContext(), todasProfissoes);
        meuRecycler.setAdapter(listaProfissaoPersonagemAdapter);
        configuraCliqueItemProfissao();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void configuraCliqueItemProfissao() {
        listaProfissaoPersonagemAdapter.setOnItemClickListener(
            (profissao, adapterPosition) -> {
                ArrayList<TrabalhoProducao> producaoFiltrada = producao.stream()
                        .filter(trabalho ->
                            trabalho.getProfissao().equals(profissao.getNome()))
                        .collect(Collectors.toCollection(ArrayList::new));
                ProfissaoFragment profissaoFragment = new ProfissaoFragment();
                Bundle argumento = new Bundle();
                argumento.putString(CHAVE_ID_PERSONAGEM, idPersonagem);
                argumento.putSerializable("profissao", profissao);
                argumento.putSerializable("producao", producaoFiltrada);
                profissaoFragment.setArguments(argumento);
                profissaoFragment.show(getChildFragmentManager(), "profissaoFragment");
            }
        );
    }

    private void recuperaProfissoes() {
        profissaoPersonagemViewModel.getRecuperacaoProfissoesPersonagem().observe(
            getViewLifecycleOwner(),
            resultadoTodasProfissoes -> {
                if (resultadoTodasProfissoes.getDado() != null) {
                    todasProfissoes = resultadoTodasProfissoes.getDado();
                    if (todasProfissoes.isEmpty()) {
                        profissaoPersonagemViewModel.getInsercaoResultado().observe(
                            getViewLifecycleOwner(),
                            resultadoInsereProfissoes -> {
                                if (resultadoInsereProfissoes.getErro() != null) {
                                    mostraMensagem("Erro: "+resultadoTodasProfissoes.getErro());
                                }
                            }
                        );

                        profissaoPersonagemViewModel.insereProfissoes();
                    }
                    indicadorProgresso.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    listaProfissaoPersonagemAdapter.atualiza(todasProfissoes);
                }
                if (resultadoTodasProfissoes.getErro() != null) {
                    mostraMensagem("Erro: "+resultadoTodasProfissoes.getErro());
                }
            }
        );

        profissaoPersonagemViewModel.recuperaProfissoesPersonagem();
    }
    private void configuraSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            listaProfissaoPersonagemAdapter.limpaLista();
            if (idPersonagem.isEmpty()) return;
            recuperaProfissoes();
        });
    }
    private void inicializaComponentes() {
        idPersonagem= "";
        todasProfissoes = new ArrayList<>();
        producao = new ArrayList<>();
        meuRecycler = binding.recyclerViewListaProfissoesPersonagemFragment;
        swipeRefreshLayout = binding.swipeRefreshLayoutListaProfissoesPersonagemFragment;
        indicadorProgresso = binding.indicadorProgressoListaProfissoesPersonagemFragment;
        PersonagemViewModelFactory personagemViewModelFactory = new PersonagemViewModelFactory(PersonagemRepository.getInstance());
        personagemViewModel = new ViewModelProvider(requireActivity(), personagemViewModelFactory).get(PersonagemViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        recuperaProfissoes();
    }

    private void atualizarViewModel(String novoIdPersonagem) {
        if (idPersonagem.equals(novoIdPersonagem)) return;
        idPersonagem= novoIdPersonagem;
        ProfissaoPersonagemViewModelFactory profissaoPersonagemViewModelFactory = new ProfissaoPersonagemViewModelFactory(
            idPersonagem
        );
        profissaoPersonagemViewModel = new ViewModelProvider(
            this,
                profissaoPersonagemViewModelFactory
        ).get(idPersonagem, ProfissaoPersonagemViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeOuvinteProfissao();
        removeOuvintePersonagem();
        binding = null;
    }

    private void removeOuvintePersonagem() {
        if (personagemViewModel == null) return;
        personagemViewModel.removeOuvinte();
    }

    private void removeOuvinteProfissao() {
        if (profissaoPersonagemViewModel == null) return;
        profissaoPersonagemViewModel.removeOuvinte();
    }
}