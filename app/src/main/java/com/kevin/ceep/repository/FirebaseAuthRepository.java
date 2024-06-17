package com.kevin.ceep.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.rpc.context.AttributeContext;

public class FirebaseAuthRepository {
    private FirebaseAuth minhaInstancia;

    public FirebaseAuthRepository(FirebaseAuth minhaInstancia) {
        this.minhaInstancia = minhaInstancia;
    }

    public boolean autenticarUsuario(String email, String senha) {
        final boolean[] confirmacao = {false};
        minhaInstancia.signInWithEmailAndPassword(email,senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        confirmacao[0] = true;
                    } else {
                        confirmacao[0] = false;
                        Exception exception = task.getException();
                        if (exception != null) {}
                    }
                });
        return confirmacao[0];
    }
}
