package com.kevin.ceep.repository;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_TRABALHO;
import static com.kevin.ceep.utilitario.Utilitario.comparaString;
import static com.kevin.ceep.utilitario.Utilitario.geraIdAleatorio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.db.TrabalhoDbHelper;
import com.kevin.ceep.db.contracts.TrabalhoDbContract;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoProducao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class TrabalhoRepository {
    private final DatabaseReference minhaReferencia;
    private final TrabalhoDbHelper trabalhoDbHelper;
    private final MutableLiveData<Resource<ArrayList<Trabalho>>> trabalhosEncontrados;
    private final MutableLiveData<Resource<ArrayList<Trabalho>>> trabalhosEncontradosDb;

    public TrabalhoRepository(Context context) {
        this.minhaReferencia = FirebaseDatabase.getInstance().getReference(CHAVE_LISTA_TRABALHO);
        this.trabalhosEncontrados = new MutableLiveData<>();
        this.trabalhoDbHelper = new TrabalhoDbHelper(context);
        this.trabalhosEncontradosDb = new MutableLiveData<>();
    }

    public LiveData<Resource<ArrayList<Trabalho>>> pegaTodosTrabalhos() {
        minhaReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Trabalho> trabalhos = new ArrayList<>();
                for (DataSnapshot dn:dataSnapshot.getChildren()){
                    Trabalho trabalho = dn.getValue(Trabalho.class);
                    if (trabalho != null){
                        trabalhos.add(trabalho);
                    }
                }
                trabalhos.sort(Comparator.comparing(Trabalho::getProfissao).thenComparing(Trabalho::getRaridade).thenComparing(Trabalho::getNivel).thenComparing(Trabalho::getNome));
                trabalhosEncontrados.setValue(new Resource<>(trabalhos, null));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Resource<ArrayList<Trabalho>> resourceAtual = trabalhosEncontrados.getValue();
                Resource<ArrayList<Trabalho>> resourceCriado;
                if (resourceAtual != null) {
                    resourceCriado = new Resource<>(resourceAtual.dado, databaseError.getMessage());
                } else {
                    resourceCriado = new Resource<>(null, databaseError.getMessage());
                }
                trabalhosEncontrados.setValue(resourceCriado);
            }
        });
        return trabalhosEncontrados;
    }

    public LiveData<Resource<Void>> modificaTrabalho(Trabalho trabalhoModificado) {
        MutableLiveData<Resource<Void>> liveData = new  MutableLiveData<>();
        minhaReferencia.child(trabalhoModificado.getId()).setValue(trabalhoModificado).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveData.setValue(new Resource<>(null, null));
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).toString()));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Void>> salvaNovoTrabalho(Trabalho novoTrabalho) {
        MutableLiveData<Resource<Void>> liveData = new  MutableLiveData<>();
        String novoId = geraIdAleatorio();
        novoTrabalho.setId(novoId);
        minhaReferencia.child(novoTrabalho.getId()).setValue(novoTrabalho).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveData.setValue(new Resource<>(null, null));
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).toString()));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Void>> excluiTrabalho(Trabalho trabalhoRecebido) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        minhaReferencia.child(trabalhoRecebido.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveData.setValue(new Resource<>(null, null));
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).toString()));
            }
        });
        return liveData;
    }

    public Trabalho retornaTrabalhoPorChaveNome(ArrayList<Trabalho> trabalhos,TrabalhoProducao trabalhoModificado) {
        for (Trabalho trabalho : trabalhos) {
            if (comparaString(trabalho.getNome(), trabalhoModificado.getNome())) {
                return trabalho;
            }
        }
        return null;
    }

    public LiveData<Resource<ArrayList<Trabalho>>> pegaTodosTrabalhosDb() {
        SQLiteDatabase db = trabalhoDbHelper.getReadableDatabase();
        String[] projection = {
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_ID,
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NOME,
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NOME_PRODUCAO,
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_EXPERIENCIA,
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NIVEL,
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_PROFISSAO,
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_RARIDADE,
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_TRABALHO_NECESSARIO
        };
        Cursor cursor = db.query(
                TrabalhoDbContract.TrabalhoEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
            );
        ArrayList<Trabalho> trabalhos = new ArrayList<>();
        while(cursor.moveToNext()) {
            Trabalho trabalho = new Trabalho(
                    cursor.getString(0), //id
                    cursor.getString(1), //nome
                    cursor.getString(2), //nomeProducao
                    cursor.getString(5), //profissao
                    cursor.getString(6), //raridade
                    cursor.getString(7), //trabalhoNecessario
                    cursor.getInt(4), //nivel
                    cursor.getInt(3) //experiencia
            );
            trabalhos.add(trabalho);
            Log.d("trabalho", "trabalhoEncontrado: " + trabalho.getId() + trabalho.getNome() +trabalho.getNomeProducao() + trabalho.getRaridade());
        }
        cursor.close();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            trabalhos.sort(Comparator.comparing(Trabalho::getProfissao).thenComparing(Trabalho::getRaridade).thenComparing(Trabalho::getNivel).thenComparing(Trabalho::getNome));
        }
        trabalhosEncontradosDb.setValue(new Resource<>(trabalhos, null));
        return trabalhosEncontradosDb;
    }
    public LiveData<Resource<Void>> salvaNovoTrabalhoDb(Trabalho novoTrabalho) {
        MutableLiveData<Resource<Void>> liveData = new  MutableLiveData<>();
        String novoId = geraIdAleatorio();
        novoTrabalho.setId(novoId);

        SQLiteDatabase db = trabalhoDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_ID, novoTrabalho.getId());
        values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NOME, novoTrabalho.getNome());
        values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NOME_PRODUCAO, novoTrabalho.getExperiencia());
        values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NIVEL, novoTrabalho.getNivel());
        values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_PROFISSAO, novoTrabalho.getProfissao());
        values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_RARIDADE, novoTrabalho.getRaridade());
        values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_TRABALHO_NECESSARIO, novoTrabalho.getTrabalhoNecessario());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TrabalhoDbContract.TrabalhoEntry.TABLE_NAME, null, values);
        if (newRowId == -1) {
            liveData.setValue(new Resource<>(null, "Erro ao adicionar novo trabalho a lista"));
        } else {
            liveData.setValue(new Resource<>(null, null));
        }

        return liveData;
    }
}
