package com.kevin.ceep.repository;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kevin.ceep.model.TrabalhoProducao;

import java.util.Objects;

public class TrabalhoProducaoRepository {
    private DatabaseReference minhaReferencia;
    private final String usuarioID, personagemID;
    private DatabaseReference minhaReferenciaUsuario;

    public TrabalhoProducaoRepository(String personagemID) {
        this.personagemID = personagemID;
        usuarioID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        minhaReferenciaUsuario = FirebaseDatabase.getInstance().getReference(CHAVE_USUARIOS);
    }

    public boolean modificaTrabalhoProducaoServidor(TrabalhoProducao trabalhoModificado) {
        final boolean[] confirmacao = {false};
        minhaReferenciaUsuario.child(usuarioID).child(CHAVE_LISTA_PERSONAGEM)
                .child(personagemID).child(CHAVE_LISTA_DESEJO)
                .child(trabalhoModificado.getId()).setValue(trabalhoModificado).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        confirmacao[0] = true;
                        Log.d("segundoPlano", "modificaTrabalhoProducaoServidor: Sucesso");
                    } else if (task.isCanceled()) {
                        confirmacao[0] = false;
                        Log.d("segundoPlano", "modificaTrabalhoProducaoServidor: Falha");
                    }
                });
        Log.d("segundoPlano", "modificaTrabalhoProducaoServidor: retornou: "+confirmacao[0]);
        return confirmacao[0];
    }
}
