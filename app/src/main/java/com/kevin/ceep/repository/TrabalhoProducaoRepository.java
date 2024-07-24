package com.kevin.ceep.repository;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kevin.ceep.model.TrabalhoProducao;

import java.util.Objects;

public class TrabalhoProducaoRepository {
    private final DatabaseReference minhaReferenciaListaDeDesejos;

    public TrabalhoProducaoRepository(String personagemID) {
        String usuarioID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        this.minhaReferenciaListaDeDesejos = FirebaseDatabase.getInstance().getReference(CHAVE_USUARIOS)
                .child(usuarioID).child(CHAVE_LISTA_PERSONAGEM)
                .child(personagemID).child(CHAVE_LISTA_DESEJO);
    }

    public MutableLiveData<Boolean> modificaTrabalhoProducaoServidor(TrabalhoProducao trabalhoModificado) {
        MutableLiveData<Boolean> confirmacao = new MutableLiveData<>(false);
        minhaReferenciaListaDeDesejos.child(trabalhoModificado.getId()).setValue(trabalhoModificado).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        confirmacao.setValue(true);
                        Log.d("segundoPlano", "modificaTrabalhoProducaoServidor: Sucesso");
                    } else if (task.isCanceled()) {
                        confirmacao.setValue(false);
                        Log.d("segundoPlano", "modificaTrabalhoProducaoServidor: Falha");
                    }
                });
        Log.d("segundoPlano", "modificaTrabalhoProducaoServidor: retornou: "+ confirmacao.getValue());
        return confirmacao;
    }
}
