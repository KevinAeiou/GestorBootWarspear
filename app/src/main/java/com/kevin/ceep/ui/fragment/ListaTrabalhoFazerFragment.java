package com.kevin.ceep.ui.fragment;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Objects;

public class ListaTrabalhoFazerFragment extends Fragment {

    private RecyclerView recyclerView;

    public ListaTrabalhoFazerFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i(TAG_ACTIVITY,"onAttachFragmento1");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG_ACTIVITY,"onCreateFragmento1");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG_ACTIVITY,"onCreatedViewFragmento1");
        View view = inflater.inflate(R.layout.fragment_lista_trabalho_fazer, container, false);

        recyclerView = view.findViewById(R.id.listaTrabalhoFazerRecyclerView);
        List<Trabalho> todosTrabalhos = pegaTodosTrabalhos();
        configuraRecyclerView(todosTrabalhos);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Trabalho> todosTrabalhos = pegaTodosTrabalhos();
        configuraRecyclerView(todosTrabalhos);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG_ACTIVITY,"onStartFragmento1");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG_ACTIVITY,"onResumeFragmento1");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG_ACTIVITY,"onPauseFragmento1");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG_ACTIVITY,"onStopFragmento1");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG_ACTIVITY,"onDestroyViewFragmento1");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG_ACTIVITY,"onDestroyFragmento1");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG_ACTIVITY,"onDettachFragmento1");
    }

    private void configuraRecyclerView(List<Trabalho> todosTrabalhos) {
        Log.i(TAG_ACTIVITY,"configuraRecyclerView");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        configuraAdapter(todosTrabalhos, recyclerView);
    }

    private void configuraAdapter(List<Trabalho> todosTrabalhos, RecyclerView listaTrabalho) {
        Log.i(TAG_ACTIVITY,"configuraAdapter");
        ListaTrabalhoAdapter trabalhoAdapter = new ListaTrabalhoAdapter(getActivity(),todosTrabalhos);
        listaTrabalho.setAdapter(trabalhoAdapter);
    }

    private List<Trabalho> pegaTodosTrabalhos() {
        String nomePersonagem = getArguments().getString(CHAVE_NOME_PERSONAGEM);
        Log.i(TAG_ACTIVITY,"PegouAListaTrabalho");
        List<Trabalho> trabalhos = new ArrayList<>();
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
                            if (trabalho.getEstado()==0){
                                trabalhos.add(trabalho);
                            }
                        }
                        Log.d("CHAVE_LISTA_TRABALHO0", String.valueOf(trabalhos.size()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return trabalhos;
    }
}