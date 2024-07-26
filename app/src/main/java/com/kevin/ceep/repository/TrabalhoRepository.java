package com.kevin.ceep.repository;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_TRABALHO;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.model.Trabalho;

import java.util.ArrayList;
import java.util.Comparator;

public class TrabalhoRepository {
    private final DatabaseReference minhaReferencia;
    private final MutableLiveData<Resource<ArrayList<Trabalho>>> trabalhosEncontrados;

    public TrabalhoRepository() {
        this.minhaReferencia = FirebaseDatabase.getInstance().getReference(CHAVE_LISTA_TRABALHO);
        this.trabalhosEncontrados = new MutableLiveData<>();
    }

    public LiveData<Resource<ArrayList<Trabalho>>> pegaTodosTrabalhos() {
        minhaReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Trabalho> trabalhos = new ArrayList<>();
                for (DataSnapshot dn:dataSnapshot.getChildren()){
                    Trabalho trabalho = dn.getValue(Trabalho.class);
                    if (trabalho != null){
                        trabalhos.add(trabalho);
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    trabalhos.sort(Comparator.comparing(Trabalho::getProfissao).thenComparing(Trabalho::getRaridade).thenComparing(Trabalho::getNivel).thenComparing(Trabalho::getNome));
                }
                trabalhosEncontrados.setValue(new Resource<>(trabalhos, null));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Resource<ArrayList<Trabalho>> resourceAtual = trabalhosEncontrados.getValue();
                Resource<ArrayList<Trabalho>> resourceCriado;
                if (resourceAtual != null) {
                    resourceCriado = new Resource<>(resourceAtual.dado, databaseError.getMessage());
                } else {
                    resourceCriado = new Resource<>(null, databaseError.getMessage());
                }
                trabalhosEncontrados.setValue(resourceCriado);
            }
        });
        return trabalhosEncontrados;
    }

    public void modificaTrabalho(Trabalho trabalhoModificado) {
        minhaReferencia.child(trabalhoModificado.getId()).setValue(trabalhoModificado);
    }

    public void salvaNovoTrabalho(Trabalho novoTrabalho) {
        minhaReferencia.child(novoTrabalho.getId()).setValue(novoTrabalho);
    }

    public void excluiTrabalho(Trabalho trabalhoRecebido) {
        minhaReferencia.child(trabalhoRecebido.getId()).removeValue();
    }
}
