package com.kevin.ceep.repository;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.model.Personagem;

import java.util.ArrayList;
import java.util.Objects;

public class PersonagemRepository {
    DatabaseReference minhaReferencia;
    private final MutableLiveData<Resource<ArrayList<Personagem>>> personagensEncontrados;

    public PersonagemRepository() {
        String usuarioID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        this.minhaReferencia = FirebaseDatabase.getInstance().getReference(CHAVE_USUARIOS)
                .child(usuarioID).child(CHAVE_LISTA_PERSONAGEM);
        this.personagensEncontrados = new MutableLiveData<>();
    }

    public LiveData<Resource<ArrayList<Personagem>>> pegaTodosPersonagens() {
        minhaReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Personagem> personagens = new ArrayList<>();
                for (DataSnapshot dn:snapshot.getChildren()){
                    Personagem personagem = dn.getValue(Personagem.class);
                    if (personagem != null){
                        personagens.add(personagem);
                    }
                }
                personagensEncontrados.setValue(new Resource<>(personagens, null));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Resource<ArrayList<Personagem>> resourceAtual = personagensEncontrados.getValue();
                Resource<ArrayList<Personagem>> resourceCriado;
                if (resourceAtual != null) {
                    resourceCriado = new Resource<>(resourceAtual.dado, error.getMessage());
                } else {
                    resourceCriado = new Resource<>(null, error.getMessage());
                }
                personagensEncontrados.setValue(resourceCriado);
            }
        });
        return personagensEncontrados;
    }
}
