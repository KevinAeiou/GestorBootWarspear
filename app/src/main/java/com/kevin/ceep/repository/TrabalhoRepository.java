package com.kevin.ceep.repository;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_TRABALHO;

import android.os.Build;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.ui.recyclerview.adapter.ListaTrabalhoEspecificoNovaProducaoAdapter;

import java.util.ArrayList;
import java.util.Comparator;

public class TrabalhoRepository {
    private DatabaseReference minhaReferencia;

    public TrabalhoRepository() {
        this.minhaReferencia = FirebaseDatabase.getInstance().getReference(CHAVE_LISTA_TRABALHO);
    }

    public void pegaTodosTrabalhos(ListaTrabalhoEspecificoNovaProducaoAdapter listaTrabalhoEspecificoAdapter) {
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
                listaTrabalhoEspecificoAdapter.setListaFiltrada(todosTrabalhos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
