package com.kevin.ceep.repository;

import static com.kevin.ceep.repository.TrabalhoProducaoRepository.destroyInstance;
import static com.kevin.ceep.ui.activity.Constantes.CHAVE_LISTA_PROFISSOES;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.dao.ProfissaoDao;
import com.kevin.ceep.model.Profissao;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProfissaoRepository {
    private static volatile ProfissaoRepository instancia;
    private final DatabaseReference referenciaListaProfissoes;
    private ValueEventListener ouvinteListaProfissoes;
    private final ProfissaoDao profissaoDao;
    private final Executor backGroundExecutor = Executors.newFixedThreadPool(2);

    public ProfissaoRepository(Context context) {
        FirebaseDatabase meuBanco= FirebaseDatabase.getInstance();
        this.referenciaListaProfissoes = meuBanco.getReference(CHAVE_LISTA_PROFISSOES);
        this.profissaoDao = new ProfissaoDao(context);
    }

    public static synchronized ProfissaoRepository getInstance(Context context) {
        if (instancia == null) {
            destroyInstance();

            instancia = new ProfissaoRepository(context);
        }

        return instancia;
    }

    public LiveData<Resource<ArrayList<Profissao>>> recuperaProfissoes() {
        MutableLiveData<Resource<ArrayList<Profissao>>> profissoesRecuperadas = new MutableLiveData<>();

        ouvinteListaProfissoes = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Profissao> profissoes = new ArrayList<>();

                for (DataSnapshot dn : snapshot.getChildren()) {
                    Profissao profissao = dn.getValue(Profissao.class);
                    assert profissao != null;
                    profissoes.add(profissao);
                }

                profissoesRecuperadas.postValue(new Resource<>(profissoes, null));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                profissoesRecuperadas.postValue(new Resource<>(null, error.getMessage()));
            }
        };

        referenciaListaProfissoes.addValueEventListener(ouvinteListaProfissoes);
        return profissoesRecuperadas;
    }

    public Map<String, String> recuperaMapaProfissoesLocal() {
        return profissaoDao.recuperaMapaProfissoes();
    }

    public LiveData<Resource<Void>> sincronizaProfissoes() {
        ArrayList<Profissao> profissoesServidor = new ArrayList<>();
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();

        ouvinteListaProfissoes = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profissoesServidor.clear();

                for (DataSnapshot dn : snapshot.getChildren()) {
                    Profissao profissao = dn.getValue(Profissao.class);
                    if (profissao != null) {
                        profissoesServidor.add(profissao);
                    }
                }

                backGroundExecutor.execute(() -> {
                    try {
                        profissaoDao.substituirTodas(profissoesServidor);

                        liveData.postValue(new Resource<>(null, null));

                    } catch (Exception e) {
                        liveData.postValue(new Resource<>(null, e.getMessage()));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                liveData.setValue(new Resource<>(null, error.getMessage()));
            }
        };

        referenciaListaProfissoes.addListenerForSingleValueEvent(ouvinteListaProfissoes);
        return liveData;
    }

    public void removeOuvinte() {
        if (referenciaListaProfissoes != null && ouvinteListaProfissoes != null) {
            referenciaListaProfissoes.removeEventListener(ouvinteListaProfissoes);
        }
    }
}
