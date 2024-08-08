package com.kevin.ceep.repository;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_VENDAS;
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
import com.kevin.ceep.model.ProdutoVendido;
import com.kevin.ceep.model.Trabalho;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class ProdutosVendidosRepository {
    private final DatabaseReference minhaReferencia;
    private MutableLiveData<Resource<ArrayList<ProdutoVendido>>> todosProdutosVendidosEncontrados;

    public ProdutosVendidosRepository(String personagemID) {
        String usuarioID = Objects.requireNonNull(FirebaseAuth.getInstance().getUid());
        this.minhaReferencia = FirebaseDatabase.getInstance().getReference(CHAVE_USUARIOS)
                .child(usuarioID).child(CHAVE_LISTA_PERSONAGEM).child(personagemID)
                .child(CHAVE_LISTA_VENDAS);
        this.todosProdutosVendidosEncontrados = new MutableLiveData<>();
    }

    public LiveData<Resource<ArrayList<ProdutoVendido>>> pegaTodosProdutosVendidos() {
        minhaReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ProdutoVendido> produtosVendidos = new ArrayList<>();
                for (DataSnapshot dn: snapshot.getChildren()) {
                    ProdutoVendido produtoVendido = dn.getValue(ProdutoVendido.class);
                    if (produtoVendido != null) {
                        produtosVendidos.add(produtoVendido);
                    }
                }
                produtosVendidos.sort(Comparator.comparing(ProdutoVendido::getDataVenda).thenComparing(ProdutoVendido::getNomeProduto).reversed());
                todosProdutosVendidosEncontrados.setValue(new Resource<>(produtosVendidos, null));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Resource<ArrayList<ProdutoVendido>> resourceAtual = todosProdutosVendidosEncontrados.getValue();
                Resource<ArrayList<ProdutoVendido>> resourceCriado;
                if (resourceAtual != null) {
                    resourceCriado = new Resource<>(resourceAtual.dado, error.getMessage());
                } else {
                    resourceCriado = new Resource<>(null, error.getMessage());
                }
                todosProdutosVendidosEncontrados.setValue(resourceCriado);
            }
        });
        return todosProdutosVendidosEncontrados;
    }

    public LiveData<Resource<Void>> deletaProduto(ProdutoVendido trabalhoRemovido) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        minhaReferencia.child(trabalhoRemovido.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveData.setValue(new Resource<>(null, null));
            } else if (task.isCanceled()) {
                Exception exception = task.getException();
                if (exception != null) {
                    liveData.setValue(new Resource<>(null, exception.getMessage()));
                }
            }
        });
        return liveData;
    }
}
