package com.kevin.ceep.repository;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.model.TrabalhoEstoque;
import com.kevin.ceep.model.TrabalhoProducao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class TrabalhoProducaoRepository {
    private final DatabaseReference minhaReferenciaListaDeDesejos;
    private MutableLiveData<Resource<ArrayList<TrabalhoProducao>>> trabalhosProducaoEncontrados;

    public TrabalhoProducaoRepository(String personagemID) {
        String usuarioID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        this.minhaReferenciaListaDeDesejos = FirebaseDatabase.getInstance().getReference(CHAVE_USUARIOS)
                .child(usuarioID).child(CHAVE_LISTA_PERSONAGEM)
                .child(personagemID).child(CHAVE_LISTA_DESEJO);
        this.trabalhosProducaoEncontrados = new MutableLiveData<>();
    }

    public LiveData<Resource<Void>> modificaTrabalhoProducaoServidor(TrabalhoProducao trabalhoModificado) {
        MutableLiveData<Resource<Void>> confirmacao = new MutableLiveData<>();
        minhaReferenciaListaDeDesejos.child(trabalhoModificado.getId()).setValue(trabalhoModificado).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        confirmacao.setValue(new Resource<>(null, null));
                    } else if (task.isCanceled()) {
                        confirmacao.setValue(new Resource<>(null, task.getException().toString()));
                    }
                });
        return confirmacao;
    }

    public LiveData<Resource<Void>> salvaNovoTrabalhoProducao(TrabalhoProducao novoTrabalho) {
        MutableLiveData<Resource<Void>> liveDate = new MutableLiveData<>();
        minhaReferenciaListaDeDesejos.child(novoTrabalho.getId())
                .setValue(novoTrabalho).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        liveDate.setValue(new Resource<>(null, null));
                    } else if (task.isCanceled()) {
                        liveDate.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).getMessage()));
                    }
                });
        return liveDate;
    }
    public LiveData<Resource<Void>> deletaTrabalhoProducao(TrabalhoProducao trabalhoDeletado) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        minhaReferenciaListaDeDesejos.child(trabalhoDeletado.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveData.setValue(new Resource<>(null, null));
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, task.getException().getMessage()));
            }
        });
        return liveData;
    }

    public LiveData<Resource<ArrayList<TrabalhoProducao>>> pegaTodosTrabalhosProducao() {
        minhaReferenciaListaDeDesejos.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<TrabalhoProducao> trabalhosProducao = new ArrayList<>();
                for (DataSnapshot dn:snapshot.getChildren()){
                    TrabalhoProducao trabalho = dn.getValue(TrabalhoProducao.class);
                    trabalhosProducao.add(trabalho);
                }
                trabalhosProducao.sort(Comparator.comparing(TrabalhoProducao::getEstado).thenComparing(TrabalhoProducao::getProfissao).thenComparing(TrabalhoProducao::getRaridade).thenComparing(TrabalhoProducao::getNivel).thenComparing(TrabalhoProducao::getNome));
                trabalhosProducaoEncontrados.setValue(new Resource<>(trabalhosProducao, null));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Resource<ArrayList<TrabalhoProducao>> resourceAtual = trabalhosProducaoEncontrados.getValue();
                Resource<ArrayList<TrabalhoProducao>> resourceCriado;
                if (resourceAtual != null) {
                    resourceCriado = new Resource<>(resourceAtual.dado, error.getMessage());
                } else {
                    resourceCriado = new Resource<>(null, error.getMessage());
                }
                trabalhosProducaoEncontrados.setValue(resourceCriado);
            }
        });
        return trabalhosProducaoEncontrados;
    }
}
