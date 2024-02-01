package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_ALTERA_TRABALHO;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.Raridade;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoEstoque;
import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.ui.activity.ListaRaridadeActivity;
import com.kevin.ceep.ui.activity.TrabalhoEspecificoActivity;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoProducaoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class ListaTrabalhosFragment extends Fragment {
    private static final String TAG="MainActivity";
    ActivityResultLauncher<Intent> activityLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(TAG,"onActivityResult");
                if (result.getResultCode()==1){
                    Intent intent=result.getData();
                    if (intent!=null){

                    }
                }
            }
    );
    private DatabaseReference databaseReference;
    private ListaTrabalhoProducaoAdapter trabalhoAdapter;
    private RecyclerView recyclerView;
    private List<TrabalhoProducao> trabalhos;
    private String usuarioId, personagemId;
    private List<Personagem> personagens;
    private Menu itemMenuPersonagem;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearProgressIndicator indicadorProgresso;
    private ChipGroup grupoChipFiltro;
    public ListaTrabalhosFragment() {
        // Required empty public constructor
    }
    public static ListaTrabalhosFragment novaInstanciaListaPersonagensFragment(String personagemId) {
        ListaTrabalhosFragment fragment = new ListaTrabalhosFragment();
        Bundle argumento = new Bundle();
        argumento.putString(CHAVE_PERSONAGEM, personagemId);
        //argumento.putString(ARG_PARAM2, param2);
        fragment.setArguments(argumento);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        /*getParentFragmentManager().setFragmentResultListener(CHAVE_PERSONAGEM, this, (requestKey, result) -> {
            personagemId = result.getString(CHAVE_PERSONAGEM);
            Log.d("personagemID1", "personagemID definido como: "+personagemId);
        });*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista_trabalhos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializaComponentes(view);
        getParentFragmentManager().setFragmentResultListener(CHAVE_PERSONAGEM, this, (requestKey, result) -> {
            personagemId = result.getString(CHAVE_PERSONAGEM);
            atualizaListaTrabalho();
        });
        configuraSwipeRefreshLayout();
        configuraBotaoInsereTrabalho(view);
        configuraDeslizeItem();
        grupoChipFiltro.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d("chipSelecionado", "Chip selecionado: "+checkedId);
            int estado = -1;
            switch (checkedId){
                case (R.id.chipFiltroTodos):
                    estado = -1;
                    break;
                case (R.id.chipFiltroProduzir):
                    estado = 0;
                    break;
                case (R.id.chipFiltroProduzindo):
                    estado = 1;
                    break;
                case (R.id.chipFiltroPronto):
                    estado = 2;
                    break;
            }
            List<TrabalhoProducao> listaFiltrada = filtroListaChip(estado, trabalhos);
            if (listaFiltrada.isEmpty()) {
                // if no item is added in filtered list we are
                // displaying a toast message as no data found.
                Snackbar.make(getView(), "Nem um resultado encontrado!", Snackbar.LENGTH_LONG).show();
            } else {
                // at last we are passing that filtered
                // list to our adapter class.
                trabalhoAdapter.setListaFiltrada(listaFiltrada);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_personagem, menu);
        itemMenuPersonagem = menu;
        MenuItem itemBusca = menu.findItem(R.id.itemMenuBusca);
        SearchView busca = (SearchView) itemBusca.getActionView();
        busca.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtroLista(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    private List<TrabalhoProducao> filtroListaChip(int estado, List<TrabalhoProducao> todosTrabalhos) {
        // creating a new array list to filter our data.
        List<TrabalhoProducao> listaFiltrada = new ArrayList<>();
        if (estado == -1){
            listaFiltrada = todosTrabalhos;
        }else {
            // running a for loop to compare elements.
            for (TrabalhoProducao item : todosTrabalhos) {
                // checking if the entered string matched with any item of our recycler view.
                if (item.getEstado() == estado) {
                    // if the item is matched we are
                    // adding it to our filtered list.
                    listaFiltrada.add(item);
                }
            }
        }
        return listaFiltrada;
    }

    private void filtroLista(String newText) {
        // creating a new array list to filter our data.
        List<TrabalhoProducao> listaFiltrada = new ArrayList<>();

        // running a for loop to compare elements.
        for (TrabalhoProducao item : trabalhos) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getNome().toLowerCase().contains(newText.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                listaFiltrada.add(item);
            }
        }
        if (listaFiltrada.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Snackbar.make(getView(),"Nem um resultado encontrado!", Snackbar.LENGTH_LONG).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            trabalhoAdapter.setListaFiltrada(listaFiltrada);
        }
    }
    private void configuraDeslizeItem() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int posicaoDeslize = viewHolder.getAdapterPosition();
                ListaTrabalhoProducaoAdapter trabalhoAdapter = (ListaTrabalhoProducaoAdapter) recyclerView.getAdapter();
                removeTrabalhoLista(posicaoDeslize);
                if (trabalhoAdapter != null) {
                    trabalhoAdapter.remove(posicaoDeslize);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void removeTrabalhoLista(int swipePosicao) {
        String idTrabalho = trabalhos.get(swipePosicao).getId();
        databaseReference.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                child(personagemId).child(CHAVE_LISTA_DESEJO).
                child(idTrabalho).removeValue();
    }
    private void configuraSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (personagemId != null){
                atualizaListaTrabalho();
            }
        });
    }
    private void configuraBotaoInsereTrabalho(View view) {
        FloatingActionButton botaoInsereTrabaho = view.findViewById(R.id.floatingActionButton);
        botaoInsereTrabaho.setOnClickListener(v -> vaiParaRaridadeActivity());
    }
    private void vaiParaRaridadeActivity() {
        Intent iniciaListaRaridade =
                new Intent(getContext(),
                        ListaRaridadeActivity.class);
        iniciaListaRaridade.putExtra(CHAVE_PERSONAGEM, personagemId);
        startActivity(iniciaListaRaridade,
                ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        //startActivityForResult(iniciaFormularioNota, CODIGO_REQUISICAO_INSERE_NOTA);
    }

    private void inicializaComponentes(View view) {
        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        recyclerView = view.findViewById(R.id.listaTrabalhoRecyclerView);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(CHAVE_USUARIOS);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutTrabalhos);
        indicadorProgresso = view.findViewById(R.id.indicadorProgressoListaTrabalhosFragment);
        grupoChipFiltro = view.findViewById(R.id.chipGrupId);

    }
    private void atualizaListaTrabalho() {
        List<TrabalhoProducao> todosTrabalhos = pegaTodosTrabalhos();
        int chipId = grupoChipFiltro.getCheckedChipId();
        int estado = -1;
        if (chipId == R.id.chipFiltroTodos){
            estado = -1;
        }else if (chipId == R.id.chipFiltroProduzir){
            estado = 0;
        }else if (chipId == R.id.chipFiltroProduzindo){
            estado = 1;
        }else if (chipId == R.id.chipFiltroPronto){
            estado = 2;
        }
        List<TrabalhoProducao> listaFiltrada = filtroListaChip(estado,todosTrabalhos);
        Log.d("listaFiltrada1", "Tamanho da lista filtrada: "+ listaFiltrada.size());
        if (listaFiltrada.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Snackbar.make(getView(), "Nem um resultado encontrado!", Snackbar.LENGTH_LONG).show();
        }
        configuraRecyclerView(listaFiltrada);
    }
    private void configuraRecyclerView(List<TrabalhoProducao> listaFiltrada) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        configuraAdapter(listaFiltrada, recyclerView);
    }
    private void configuraAdapter(List<TrabalhoProducao> listaFiltrada, RecyclerView listaTrabalhos) {
        trabalhoAdapter = new ListaTrabalhoProducaoAdapter(getContext(),listaFiltrada);
        listaTrabalhos.setAdapter(trabalhoAdapter);
        trabalhoAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Profissao profissao, int posicao) {

            }

            @Override
            public void onItemClick(Personagem personagem, int posicao) {

            }

            @Override
            public void onItemClick(Trabalho trabalho, int adapterPosition) {
                vaiParaTrabalhoEspecificoActivity(trabalho);
            }

            @Override
            public void onItemClick(Raridade raridade, int adapterPosition) {

            }

            @Override
            public void onItemClick(TrabalhoEstoque trabalhoEstoque, int adapterPosition, int botaoId) {

            }
        });
    }
    private void vaiParaTrabalhoEspecificoActivity(Trabalho trabalho) {
        Intent iniciaTrabalhoEspecificoActivity=
                new Intent(getActivity(), TrabalhoEspecificoActivity.class);
        iniciaTrabalhoEspecificoActivity.putExtra(CHAVE_TRABALHO, CODIGO_REQUISICAO_ALTERA_TRABALHO);
        iniciaTrabalhoEspecificoActivity.putExtra(CHAVE_NOME_TRABALHO, trabalho);
        iniciaTrabalhoEspecificoActivity.putExtra(CHAVE_PERSONAGEM, personagemId);
        activityLauncher.launch(iniciaTrabalhoEspecificoActivity);
    }
    private List<TrabalhoProducao> pegaTodosTrabalhos() {
        trabalhos = new ArrayList<>();
        databaseReference.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                child(personagemId).child(CHAVE_LISTA_DESEJO).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        trabalhos.clear();
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            TrabalhoProducao trabalho = dn.getValue(TrabalhoProducao.class);
                            trabalhos.add(trabalho);
                        }
                        trabalhoAdapter.notifyDataSetChanged();
                        indicadorProgresso.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return trabalhos;
    }
}