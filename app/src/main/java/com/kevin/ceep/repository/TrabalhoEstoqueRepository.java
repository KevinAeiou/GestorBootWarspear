package com.kevin.ceep.repository;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_ESTOQUE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.utilitario.Utilitario.comparaString;
import static com.kevin.ceep.utilitario.Utilitario.geraIdAleatorio;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class TrabalhoEstoqueRepository {
    private final DatabaseReference minhaReferencia;
    private final MutableLiveData<Resource<ArrayList<TrabalhoEstoque>>> trabalhosEstoqueEncontrados;

    public TrabalhoEstoqueRepository(String personagemID) {
        String usuarioID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        this.minhaReferencia = FirebaseDatabase.getInstance().getReference(CHAVE_USUARIOS).child(usuarioID).child(CHAVE_LISTA_PERSONAGEM)
                .child(personagemID).child(CHAVE_LISTA_ESTOQUE);
        this.trabalhosEstoqueEncontrados = new MutableLiveData<>();
    }

    public void modificaQuantidadeTrabalhoNecessarioNoEstoque(TrabalhoProducao trabalhoProducao) {
        String[] listaTrabalhosNecessarios = trabalhoProducao.getTrabalhoNecessario().split(",");
        minhaReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            TrabalhoEstoque trabalho = dn.getValue(TrabalhoEstoque.class);
                            if (trabalho != null) {
                                for (String nome: listaTrabalhosNecessarios) {
                                    if (comparaString(trabalho.getNome(), nome)) {
                                        int novaQuantidade = trabalho.getQuantidade() - 1;
                                        if (novaQuantidade < 0) {
                                            novaQuantidade = 0;
                                        }
                                        trabalho.setQuantidade(novaQuantidade);
                                        modificaQuantidadeTrabalhoEspecificoNoEstoque(trabalho);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public LiveData<Resource<Void>> modificaQuantidadeTrabalhoEspecificoNoEstoque(TrabalhoEstoque trabalho) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        if (trabalho.getId() != null) {
            minhaReferencia.child(trabalho.getId()).child("quantidade").setValue(trabalho.getQuantidade()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    liveData.setValue(new Resource<>(null, null));
                } else if (task.isCanceled()) {
                    liveData.setValue(new Resource<>(null, task.getException().getMessage()));
                }
            });
        }
        return liveData;
    }
    public LiveData<Resource<ArrayList<TrabalhoEstoque>>> pegaTodosTrabalhosEstoque() {
        minhaReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<TrabalhoEstoque> trabalhosEstoque = new ArrayList<>();
                for (DataSnapshot dn:dataSnapshot.getChildren()){
                    TrabalhoEstoque trabalho = dn.getValue(TrabalhoEstoque.class);
                    if (trabalho.getProfissao() == null) {
                        trabalho.setProfissao("");
                    }
                    trabalhosEstoque.add(trabalho);
                }
                if (!trabalhosEstoque.isEmpty()) {
                    trabalhosEstoque.sort(Comparator.comparing(TrabalhoEstoque::getProfissao).thenComparing(TrabalhoEstoque::getRaridade).thenComparing(TrabalhoEstoque::getNivel).thenComparing(TrabalhoEstoque::getNome));
                }
                trabalhosEstoqueEncontrados.setValue(new Resource<>(trabalhosEstoque, null));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Resource<ArrayList<TrabalhoEstoque>> resourceAtual = trabalhosEstoqueEncontrados.getValue();
                Resource<ArrayList<TrabalhoEstoque>> resourceCriado;
                if (resourceAtual != null) {
                    resourceCriado = new Resource<>(resourceAtual.dado, databaseError.getMessage());
                } else {
                    resourceCriado = new Resource<>(null, databaseError.getMessage());
                }
                trabalhosEstoqueEncontrados.setValue(resourceCriado);
            }
        });
        return trabalhosEstoqueEncontrados;
    }

    public TrabalhoEstoque defineNovoTrabalhoEstoque(TrabalhoProducao trabalhoConcluido) {
        return new TrabalhoEstoque(
                null,
                trabalhoConcluido.getNome(),
                trabalhoConcluido.getProfissao(),
                trabalhoConcluido.getRaridade(),
                trabalhoConcluido.getNivel(),
                1,
                null);
    }
    public TrabalhoEstoque retornaTrabalhoEspecificoEstoque(ArrayList<TrabalhoEstoque> trabalhosEstoque, String nomeTrabalho) {
        for (TrabalhoEstoque trabalhoEstoque : trabalhosEstoque) {
            if (comparaString(trabalhoEstoque.getNome(), nomeTrabalho)) {
                return trabalhoEstoque;
            }
        }
        return null;
    }

    public LiveData<Resource<Void>> salvaNovoTrabalhoEstoque(TrabalhoEstoque novoTrabalho) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        String novoId = geraIdAleatorio();
        novoTrabalho.setId(novoId);
        minhaReferencia.child(novoTrabalho.getId()).setValue(novoTrabalho).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveData.setValue(new Resource<>(null, null));
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, task.getException().getMessage()));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Void>> deletaTrabalhoEstoque(TrabalhoEstoque trabalhoRemovido) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        minhaReferencia.child(trabalhoRemovido.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveData.setValue(new Resource<>(null, null));
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, task.getException().getMessage()));
            }
        });
        return liveData;
    }
}
