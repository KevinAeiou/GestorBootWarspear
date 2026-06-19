package com.kevin.gestorproducao.repository;

import static com.kevin.gestorproducao.repository.TrabalhoProducaoRepository.destroyInstance;
import static com.kevin.gestorproducao.ui.activity.Constantes.CHAVE_USUARIOS2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kevin.gestorproducao.model.Usuario;
import com.kevin.gestorproducao.repository.helper.FirebaseTimeoutHelper;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FirebaseAuthRepository {
    private static FirebaseAuthRepository instancia;
    private final FirebaseAuth minhaInstancia;
    private final DatabaseReference minhaReferencia;
    private final Executor backgroundExecutor = Executors.newFixedThreadPool(2);

    public FirebaseAuthRepository() {
        this.minhaInstancia = FirebaseAuth.getInstance();
        this.minhaReferencia = FirebaseDatabase.getInstance().getReference(CHAVE_USUARIOS2);
    }

    public static FirebaseAuthRepository getInstance() {
        if (instancia == null) {
            destroyInstance();
            instancia = new FirebaseAuthRepository();
        }
        return instancia;
    }

    public LiveData<Resource<Void>> autenticarUsuario(Usuario usuario) {

        return FirebaseTimeoutHelper.execute(callback -> minhaInstancia
            .signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
            .addOnCompleteListener(backgroundExecutor, task -> {
                if (task.isSuccessful()) {
                    callback.sucesso(null);
                    return;
                }

                callback.erro(
                    recuperaErro(
                        task.getException(),
                        "Erro desconhecido ao autenticar usuário"
                    )
                );
            })
        );
    }

    public LiveData<Resource<Void>> criaUsuario(Usuario usuario) {

        return FirebaseTimeoutHelper.execute(callback -> minhaInstancia
            .createUserWithEmailAndPassword(usuario.getEmail(),usuario.getSenha())
            .addOnCompleteListener(backgroundExecutor, task -> {
                if (task.isSuccessful()){
                    callback.sucesso(null);
                    return;
                }

                String erroString;
                try{
                    throw Objects.requireNonNull(task.getException());
                }catch (FirebaseAuthWeakPasswordException e){
                    erroString = "A senha deve conter no mínimo 8 caracteres!";
                } catch (FirebaseAuthUserCollisionException e){
                    erroString = "Conta já cadastrada!";
                }catch (FirebaseAuthInvalidCredentialsException e) {
                    erroString = "Email inválido!";
                }catch (FirebaseNetworkException e) {
                    erroString = "Erro de conexão! Tente novamente.";
                }catch (Exception e) {
                    erroString = "Erro ao cadastrar usuário: " + e.getMessage();
                }

                callback.erro(erroString);
            })
        );
    }

    public LiveData<Resource<Void>> insereUsuario(Usuario usuario) {

        return FirebaseTimeoutHelper.execute(callback -> minhaReferencia
            .child(usuario.getId())
            .setValue(usuario)
            .addOnCompleteListener(backgroundExecutor, task -> {
                if (task.isSuccessful()) {
                    callback.sucesso(null);
                    return;
                }

                callback.erro(
                    recuperaErro(
                        task.getException(),
                        "Erro desconhecido ao salvar dados do usuário"
                    )
                );
            })
        );
    }

    private String recuperaErro(Exception exception, String erro) {
        return exception == null ? erro : exception.getMessage();
    }

    public LiveData<Resource<Void>> recuperaSenha(String email) {

        return FirebaseTimeoutHelper.execute(callback -> minhaInstancia
            .sendPasswordResetEmail(email)
            .addOnCompleteListener(backgroundExecutor, task -> {
                if (task.isSuccessful()) {
                    callback.sucesso(null);
                    return;
                }

                callback.erro(
                    recuperaErro(
                        task.getException(),
                        "Erro desconhecido ao recuperar a senha"
                    )
                );
            })
        );
    }

    public LiveData<Resource<Usuario>> recuperaUsuarioAtual() {

        if (minhaInstancia.getCurrentUser() == null) {
            MutableLiveData<Resource<Usuario>> liveData = new MutableLiveData<>();
            liveData.postValue(
                new Resource<>(null, "Usuário não autenticado")
            );

            return liveData;
        }

        String uid = minhaInstancia.getCurrentUser().getUid();

        return FirebaseTimeoutHelper.execute(callback -> minhaReferencia
            .child(uid).get().addOnCompleteListener(backgroundExecutor, task -> {
                if (task.isSuccessful()) {
                    Usuario usuario = task.getResult().getValue(Usuario.class);

                    callback.sucesso(usuario);
                    return;
                }

                callback.erro(
                    recuperaErro(
                        task.getException(),
                        "Erro desconhecido ao recuperar usuário"
                    )
                );
            })
        );
    }
}
