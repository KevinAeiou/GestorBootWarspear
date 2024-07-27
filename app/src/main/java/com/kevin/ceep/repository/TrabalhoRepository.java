package com.kevin.ceep.repository;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_TRABALHO;
import static com.kevin.ceep.utilitario.Utilitario.geraIdAleatorio;

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
import java.util.Objects;

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

    public LiveData<Resource<Void>> modificaTrabalho(Trabalho trabalhoModificado) {
        MutableLiveData<Resource<Void>> liveData = new  MutableLiveData<>();
        minhaReferencia.child(trabalhoModificado.getId()).setValue(trabalhoModificado).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveData.setValue(new Resource<>(null, null));
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).toString()));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Void>> salvaNovoTrabalho(Trabalho novoTrabalho) {
        MutableLiveData<Resource<Void>> liveData = new  MutableLiveData<>();
        String novoId = geraIdAleatorio();
        novoTrabalho.setId(novoId);
        minhaReferencia.child(novoTrabalho.getId()).setValue(novoTrabalho).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveData.setValue(new Resource<>(null, null));
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).toString()));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Void>> excluiTrabalho(Trabalho trabalhoRecebido) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        minhaReferencia.child(trabalhoRecebido.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveData.setValue(new Resource<>(null, null));
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).toString()));
            }
        });
        return liveData;
    }
}
