package com.kevin.ceep.repository;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_TRABALHO;

import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.model.Trabalho;

import java.util.ArrayList;
import java.util.Comparator;

public class TrabalhoRepository {
    private DatabaseReference minhaReferencia;

    public TrabalhoRepository() {
        FirebaseDatabase meuBanco = FirebaseDatabase.getInstance();
        this.minhaReferencia = meuBanco.getReference(CHAVE_LISTA_TRABALHO);
    }

    public ArrayList<Trabalho> pegaTodosTrabalho() {
        ArrayList<Trabalho> todosTrabalhos = new ArrayList<>();
        minhaReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todosTrabalhos.clear();
                for (DataSnapshot dn:dataSnapshot.getChildren()){
                    Trabalho trabalho = dn.getValue(Trabalho.class);
                    if (trabalho != null){
                        todosTrabalhos.add(trabalho);
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    todosTrabalhos.sort(Comparator.comparing(Trabalho::getProfissao).thenComparing(Trabalho::getRaridade).thenComparing(Trabalho::getNivel).thenComparing(Trabalho::getNome));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Snackbar.make(binding.constraintLayoutProfissoesTrabalhos, "Erro ao carregar dados: "+ databaseError, Snackbar.LENGTH_LONG).show();
            }
        });
        return todosTrabalhos;
    }

    public void modificaTrabalho(Trabalho trabalhoModificado) {
        minhaReferencia.child(trabalhoModificado.getId()).setValue(trabalhoModificado);
    }

    public void salvaNovoTrabalho(Trabalho novoTrabalho) {
        minhaReferencia.child(novoTrabalho.getId()).setValue(novoTrabalho);
    }
}
