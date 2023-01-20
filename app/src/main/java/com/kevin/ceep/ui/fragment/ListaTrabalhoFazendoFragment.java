package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListaTrabalhoFazendoFragment extends Fragment {

    private RecyclerView recyclerView;

    public ListaTrabalhoFazendoFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_trabalho_fazendo,container,false);
        recyclerView = view.findViewById(R.id.listaTrabalhoFazendoRecyclerView);
        List<Trabalho> todosTrabalhos = pegaTodosTrabalhos();
        configuraRecyclerView(todosTrabalhos);
        return view;
    }


    private void configuraRecyclerView(List<Trabalho> todosTrabalhos) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        configuraAdapter(todosTrabalhos, recyclerView);

    }

    @SuppressLint("NotifyDataSetChanged")
    private void configuraAdapter(List<Trabalho> todosTrabalhos, RecyclerView listaTrabalho) {
        ListaTrabalhoAdapter trabalhoAdapter = new ListaTrabalhoAdapter(getActivity(),todosTrabalhos);
        listaTrabalho.setAdapter(trabalhoAdapter);
        trabalhoAdapter.notifyDataSetChanged();
    }

    private List<Trabalho> pegaTodosTrabalhos() {
        List<Trabalho> trabalhos = new ArrayList<>();
        String nomePersonagem = getArguments().getString(CHAVE_NOME_PERSONAGEM);
        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhaReferencia = database.getReference(CHAVE_USUARIOS);
        minhaReferencia.child(usuarioId).child(CHAVE_PERSONAGEM).
                child(nomePersonagem).child(CHAVE_LISTA_DESEJO).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            Trabalho trabalho = dn.getValue(Trabalho.class);
                            if (trabalho.getEstado()==1){
                                trabalhos.add(trabalho);
                                Log.d("TRABALHO",trabalho.getNome());
                            }
                        }
                        Log.d("CHAVE_LISTA_TRABALHO1", String.valueOf(trabalhos.size()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return trabalhos;
    }
}