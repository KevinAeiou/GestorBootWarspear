package com.kevin.ceep.repository;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_TRABALHO;
import static com.kevin.ceep.utilitario.Utilitario.comparaString;
import static com.kevin.ceep.utilitario.Utilitario.geraIdAleatorio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.NonNull;
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

    public TrabalhoRepository(Context context) {
        this.minhaReferencia = FirebaseDatabase.getInstance().getReference(CHAVE_LISTA_TRABALHO);
        this.trabalhoDbHelper = new TrabalhoDbHelper(context);
        this.trabalhosEncontrados = new MutableLiveData<>();
    }
    public LiveData<Resource<Void>> modificaTrabalho(Trabalho trabalhoModificado) {
        MutableLiveData<Resource<Void>> liveData = new  MutableLiveData<>();
        minhaReferencia.child(trabalhoModificado.getId()).setValue(trabalhoModificado).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SQLiteDatabase db = trabalhoDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NOME, trabalhoModificado.getNome());
                values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NOME_PRODUCAO, trabalhoModificado.getNomeProducao());
                values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_EXPERIENCIA, trabalhoModificado.getExperiencia());
                values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NIVEL, trabalhoModificado.getNivel());
                values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_PROFISSAO, trabalhoModificado.getProfissao());
                values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_RARIDADE, trabalhoModificado.getRaridade());
                values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_TRABALHO_NECESSARIO, trabalhoModificado.getTrabalhoNecessario());
                String selection = TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_ID + " LIKE ?";
                String[] selectionArgs = {trabalhoModificado.getId()};
                long newRowId = db.update(TrabalhoDbContract.TrabalhoEntry.TABLE_NAME, values, selection, selectionArgs);
                if (newRowId == -1) {
                    liveData.setValue(new Resource<>(null, "Erro ao adicionar novo trabalho a lista"));
                } else {
                    liveData.setValue(new Resource<>(null, null));
                }
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).toString()));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Void>> adicionaTrabalho(Trabalho novoTrabalho) {
        MutableLiveData<Resource<Void>> liveData = new  MutableLiveData<>();
        String novoId = geraIdAleatorio();
        novoTrabalho.setId(novoId);
        minhaReferencia.child(novoTrabalho.getId()).setValue(novoTrabalho).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SQLiteDatabase db = trabalhoDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_ID, novoTrabalho.getId());
                values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NOME, novoTrabalho.getNome());
                values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NOME_PRODUCAO, novoTrabalho.getNomeProducao());
                values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_EXPERIENCIA, novoTrabalho.getExperiencia());
                values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NIVEL, novoTrabalho.getNivel());
                values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_PROFISSAO, novoTrabalho.getProfissao());
                values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_RARIDADE, novoTrabalho.getRaridade());
                values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_TRABALHO_NECESSARIO, novoTrabalho.getTrabalhoNecessario());
                long newRowId = db.insert(TrabalhoDbContract.TrabalhoEntry.TABLE_NAME, null, values);
                if (newRowId == -1) {
                    liveData.setValue(new Resource<>(null, "Erro ao adicionar novo trabalho a lista"));
                } else {
                    liveData.setValue(new Resource<>(null, null));
                }
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
                SQLiteDatabase db = trabalhoDbHelper.getWritableDatabase();
                String selection = TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_ID + " LIKE ?";
                String[] selectionArgs = {trabalhoRecebido.getId()};
                db.delete(TrabalhoDbContract.TrabalhoEntry.TABLE_NAME, selection, selectionArgs);
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

    public LiveData<Resource<ArrayList<Trabalho>>> pegaTodosTrabalhos() {
        SQLiteDatabase db = trabalhoDbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TrabalhoDbContract.TrabalhoEntry.TABLE_NAME,
                null,
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
        }
        cursor.close();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            trabalhos.sort(Comparator.comparing(Trabalho::getProfissao).thenComparing(Trabalho::getRaridade).thenComparing(Trabalho::getNivel).thenComparing(Trabalho::getNome));
        }
        trabalhosEncontrados.setValue(new Resource<>(trabalhos, null));
        return trabalhosEncontrados;
    }

    public LiveData<Resource<Void>> sincronizaTrabalhos() {
        ArrayList<Trabalho> trabalhos = new ArrayList<>();
        MutableLiveData<Resource<Void>> liveData = new  MutableLiveData<>();
        minhaReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dn:snapshot.getChildren()){
                    Trabalho trabalho = dn.getValue(Trabalho.class);
                    trabalhos.add(trabalho);
                }
                for (Trabalho trabalho : trabalhos) {
                    SQLiteDatabase db = trabalhoDbHelper.getReadableDatabase();
                    String selection = TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_ID + " LIKE ?";
                    String[] selectionArgs = {trabalho.getId()};
                    Cursor cursor = db.query(
                            TrabalhoDbContract.TrabalhoEntry.TABLE_NAME,
                            null,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            null
                    );
                    int contadorLinhas = 0;
                    while(cursor.moveToNext()) {
                        contadorLinhas += 1;
                    }
                    if (contadorLinhas == 0) {
                        SQLiteDatabase db2 = trabalhoDbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_ID, trabalho.getId());
                        values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NOME, trabalho.getNome());
                        values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NOME_PRODUCAO, trabalho.getNomeProducao());
                        values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_EXPERIENCIA, trabalho.getExperiencia());
                        values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NIVEL, trabalho.getNivel());
                        values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_PROFISSAO, trabalho.getProfissao());
                        values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_RARIDADE, trabalho.getRaridade());
                        values.put(TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_TRABALHO_NECESSARIO, trabalho.getTrabalhoNecessario());
                        long newRowId = db2.insert(TrabalhoDbContract.TrabalhoEntry.TABLE_NAME, null, values);
                        if (newRowId == -1) {
                            liveData.setValue(new Resource<>(null, "Erro ao adicionar "+trabalho.getNome()+" ao banco"));
                        } else {
                            liveData.setValue(new Resource<>(null, null));
                        }
                    }
                    cursor.close();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                liveData.setValue(new Resource<>(null, error.getMessage()));
            }
        });
        return liveData;
    }
}
