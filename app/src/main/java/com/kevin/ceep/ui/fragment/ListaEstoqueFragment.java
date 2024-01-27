package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.ListaTrabalhosActivity.removerAcentos;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_ESTOQUE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoEspecificoAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoEstoqueAdapter;
import com.kevin.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListaEstoqueFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ListaTrabalhoEstoqueAdapter trabalhoEstoqueAdapter;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private List<TrabalhoEstoque> trabalhos;
    private String usuarioId, personagemId;

    public ListaEstoqueFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EstoqueFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaEstoqueFragment newInstance(String param1, String param2) {
        ListaEstoqueFragment fragment = new ListaEstoqueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle argumento = getArguments();
        if (argumento != null) {
            if (argumento.containsKey(CHAVE_PERSONAGEM)){
                personagemId = argumento.toString();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lista_estoque, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializaComponentes(view);
        atualizaListaEstoque();
    }
    private void inicializaComponentes(View view) {
        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        recyclerView = view.findViewById(R.id.listaTrabalhoEstoqueRecyclerView);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(CHAVE_USUARIOS);
    }

    private void atualizaListaEstoque() {
        List<TrabalhoEstoque> todosTrabalhosEstoque = pegaTodosTrabalhosEstoque();
        configuraRecyclerView(todosTrabalhosEstoque);
    }
    private void configuraRecyclerView(List<TrabalhoEstoque> todosTrabalhosEstoque) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        configuraAdapter(todosTrabalhosEstoque, recyclerView);
    }
    private void configuraAdapter(List<TrabalhoEstoque> todosTrabalhosEstoque, RecyclerView listaTrabalhos) {
        trabalhoEstoqueAdapter = new ListaTrabalhoEstoqueAdapter(todosTrabalhosEstoque,getContext());
        listaTrabalhos.setAdapter(trabalhoEstoqueAdapter);
    }
    private List<TrabalhoEstoque> pegaTodosTrabalhosEstoque() {
        trabalhos = new ArrayList<>();
        databaseReference.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                child(personagemId).child(CHAVE_LISTA_ESTOQUE).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        trabalhos.clear();
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            TrabalhoEstoque trabalho = dn.getValue(TrabalhoEstoque.class);
                            trabalhos.add(trabalho);
                        }
                        trabalhoEstoqueAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return trabalhos;
    }

    /*private void listaBusca(String textoBusca) {
        List<Trabalho> listaFiltro = new ArrayList<>();
        for (Trabalho trabalho:trabalhos){
            if (removerAcentos(trabalho.getNome().toLowerCase()).contains(removerAcentos(textoBusca.toLowerCase()))){
                listaFiltro.add(trabalho);
            }
        }
        if (listaFiltro.isEmpty()){
            Snackbar.make(getView().findViewById(R.id.frameLayout),"Nada encontrado...",Snackbar.LENGTH_SHORT).show();
        }else{
            trabalhoEstoqueAdapter.setListaFiltrada(listaFiltro);
        }
    }*/
}