package com.kevin.ceep.repository;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_TRABALHO;
import static com.kevin.ceep.utilitario.Utilitario.comparaString;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.db.DbHelper;
import com.kevin.ceep.db.contracts.TrabalhoDbContract.TrabalhoEntry;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoProducao;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class TrabalhoRepository {
    private final DatabaseReference minhaReferencia;
    private final DbHelper dbHelper;
    private final MutableLiveData<Resource<ArrayList<Trabalho>>> trabalhosEncontrados;

    public TrabalhoRepository(Context context) {
        this.minhaReferencia = FirebaseDatabase.getInstance().getReference(CHAVE_LISTA_TRABALHO);
        this.dbHelper = new DbHelper(context);
        this.trabalhosEncontrados = new MutableLiveData<>();
    }
    public LiveData<Resource<Void>> modificaTrabalho(Trabalho trabalhoModificado) {
        MutableLiveData<Resource<Void>> liveData = new  MutableLiveData<>();
        minhaReferencia.child(trabalhoModificado.getId()).setValue(trabalhoModificado).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(TrabalhoEntry.COLUMN_NAME_NOME, trabalhoModificado.getNome());
                values.put(TrabalhoEntry.COLUMN_NAME_NOME_PRODUCAO, trabalhoModificado.getNomeProducao());
                values.put(TrabalhoEntry.COLUMN_NAME_EXPERIENCIA, trabalhoModificado.getExperiencia());
                values.put(TrabalhoEntry.COLUMN_NAME_NIVEL, trabalhoModificado.getNivel());
                values.put(TrabalhoEntry.COLUMN_NAME_PROFISSAO, trabalhoModificado.getProfissao());
                values.put(TrabalhoEntry.COLUMN_NAME_RARIDADE, trabalhoModificado.getRaridade());
                values.put(TrabalhoEntry.COLUMN_NAME_TRABALHO_NECESSARIO, trabalhoModificado.getTrabalhoNecessario());
                String selection = TrabalhoEntry.COLUMN_NAME_ID + " LIKE ?";
                String[] selectionArgs = {trabalhoModificado.getId()};
                long newRowId = db.update(TrabalhoEntry.TABLE_NAME, values, selection, selectionArgs);
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

    public LiveData<Resource<Void>> adicionaTrabalho(Trabalho trabalho) {
        MutableLiveData<Resource<Void>> liveData = new  MutableLiveData<>();
        minhaReferencia.child(trabalho.getId()).setValue(trabalho).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(TrabalhoEntry.COLUMN_NAME_ID, trabalho.getId());
                values.put(TrabalhoEntry.COLUMN_NAME_NOME, trabalho.getNome());
                values.put(TrabalhoEntry.COLUMN_NAME_NOME_PRODUCAO, trabalho.getNomeProducao());
                values.put(TrabalhoEntry.COLUMN_NAME_EXPERIENCIA, trabalho.getExperiencia());
                values.put(TrabalhoEntry.COLUMN_NAME_NIVEL, trabalho.getNivel());
                values.put(TrabalhoEntry.COLUMN_NAME_PROFISSAO, trabalho.getProfissao());
                values.put(TrabalhoEntry.COLUMN_NAME_RARIDADE, trabalho.getRaridade());
                values.put(TrabalhoEntry.COLUMN_NAME_TRABALHO_NECESSARIO, trabalho.getTrabalhoNecessario());
                long newRowId = db.insert(TrabalhoEntry.TABLE_NAME, null, values);
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

    public LiveData<Resource<Void>> removeTrabalho(Trabalho trabalhoRecebido) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        minhaReferencia.child(trabalhoRecebido.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String selection = TrabalhoEntry.COLUMN_NAME_ID + " LIKE ?";
                String[] selectionArgs = {trabalhoRecebido.getId()};
                db.delete(TrabalhoEntry.TABLE_NAME, selection, selectionArgs);
                liveData.setValue(new Resource<>(null, null));
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).toString()));
            }
        });
        return liveData;
    }

    public Trabalho retornaTrabalhoPorId(ArrayList<Trabalho> trabalhos, TrabalhoProducao trabalhoModificado) {
        for (Trabalho trabalho : trabalhos) {
            if (comparaString(trabalho.getId(), trabalhoModificado.getIdTrabalho())) {
                return trabalho;
            }
        }
        return null;
    }

    public LiveData<Resource<ArrayList<Trabalho>>> pegaTodosTrabalhos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TrabalhoEntry.TABLE_NAME,
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
                    cursor.getString(1), //nome
                    cursor.getString(2), //nomeProducao
                    cursor.getString(5), //profissao
                    cursor.getString(6), //raridade
                    cursor.getString(7), //trabalhoNecessario
                    cursor.getInt(4), //nivel
                    cursor.getInt(3) //experiencia
            );
            trabalho.setId(cursor.getString(0));
            trabalhos.add(trabalho);
        }
        cursor.close();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            trabalhos.sort(Comparator.comparing(Trabalho::getProfissao).thenComparing(Trabalho::getRaridade).thenComparing(Trabalho::getNivel).thenComparing(Trabalho::getNome));
        }
        trabalhosEncontrados.setValue(new Resource<>(trabalhos, null));
        sincronizaTrabalhos(trabalhos);
        return trabalhosEncontrados;
    }

    public LiveData<Resource<Void>> sincronizaTrabalhos(ArrayList<Trabalho> trabalhosBanco) {
        ArrayList<Trabalho> trabalhosServidor = new ArrayList<>();
        MutableLiveData<Resource<Void>> liveData = new  MutableLiveData<>();
        minhaReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                trabalhosServidor.clear();
                Log.d("onDataChange", "Lista de trabalhos foi modificada!");
                for (DataSnapshot dn:snapshot.getChildren()){
                    Trabalho trabalho = dn.getValue(Trabalho.class);
                    trabalhosServidor.add(trabalho);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    String selection = TrabalhoEntry.COLUMN_NAME_ID + " LIKE ?";
                    String[] selectionArgs = {trabalho.getId()};
                    Cursor cursor = db.query(
                            TrabalhoEntry.TABLE_NAME,
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
                        Log.d("onDataChange", trabalho.getNome()+" não encontrado no banco!");
                        SQLiteDatabase db2 = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(TrabalhoEntry.COLUMN_NAME_ID, trabalho.getId());
                        values.put(TrabalhoEntry.COLUMN_NAME_NOME, trabalho.getNome());
                        values.put(TrabalhoEntry.COLUMN_NAME_NOME_PRODUCAO, trabalho.getNomeProducao());
                        values.put(TrabalhoEntry.COLUMN_NAME_EXPERIENCIA, trabalho.getExperiencia());
                        values.put(TrabalhoEntry.COLUMN_NAME_NIVEL, trabalho.getNivel());
                        values.put(TrabalhoEntry.COLUMN_NAME_PROFISSAO, trabalho.getProfissao());
                        values.put(TrabalhoEntry.COLUMN_NAME_RARIDADE, trabalho.getRaridade());
                        values.put(TrabalhoEntry.COLUMN_NAME_TRABALHO_NECESSARIO, trabalho.getTrabalhoNecessario());
                        long newRowId = db2.insert(TrabalhoEntry.TABLE_NAME, null, values);
                        if (newRowId == -1) {
                            liveData.setValue(new Resource<>(null, "Erro ao adicionar "+trabalho.getNome()+" ao banco"));
                        } else {
                            liveData.setValue(new Resource<>(null, null));
                        }
                    } else if (contadorLinhas == 1) {
                        SQLiteDatabase db2 = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(TrabalhoEntry.COLUMN_NAME_NOME, trabalho.getNome());
                        values.put(TrabalhoEntry.COLUMN_NAME_NOME_PRODUCAO, trabalho.getNomeProducao());
                        values.put(TrabalhoEntry.COLUMN_NAME_EXPERIENCIA, trabalho.getExperiencia());
                        values.put(TrabalhoEntry.COLUMN_NAME_NIVEL, trabalho.getNivel());
                        values.put(TrabalhoEntry.COLUMN_NAME_PROFISSAO, trabalho.getProfissao());
                        values.put(TrabalhoEntry.COLUMN_NAME_RARIDADE, trabalho.getRaridade());
                        values.put(TrabalhoEntry.COLUMN_NAME_TRABALHO_NECESSARIO, trabalho.getTrabalhoNecessario());
                        String selection2 = TrabalhoEntry.COLUMN_NAME_ID + " LIKE ?";
                        String[] selectionArgs2 = {trabalho.getId()};
                        long newRowId = db2.update(TrabalhoEntry.TABLE_NAME, values, selection2, selectionArgs2);
                        if (newRowId == -1) {
                            liveData.setValue(new Resource<>(null, "Erro ao modificar trabalho a lista"));
                        } else {
                            liveData.setValue(new Resource<>(null, null));
                            Log.d("onDataChange", trabalho.getNome()+" foi modificado no banco!");
                        }
                    }
                    cursor.close();
                }
                Log.d("trabalhosSobrando", "tamanho lista banco " +trabalhosBanco.size());
                Log.d("trabalhosSobrando", "tamanho lista servidor " +trabalhosServidor.size());
                ArrayList<Trabalho> novaLista = new ArrayList<>();
                for (Trabalho trabalhoBanco : trabalhosBanco) {
                    for (Trabalho trabalhoServidor : trabalhosServidor) {
                        if (trabalhoServidor.getId().equals(trabalhoBanco.getId())) {
                            novaLista.add(trabalhoBanco);
                        }
                    }
                }
                trabalhosBanco.removeAll(novaLista);
                Log.d("trabalhosSobrando", "tamanho lista banco " +trabalhosBanco.size());
                for (Trabalho trabalhoBanco : trabalhosBanco) {
                    Log.d("trabalhosSobrando", trabalhoBanco.getNome() + " sobrando!");
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    String selection = TrabalhoEntry.COLUMN_NAME_ID + " LIKE ?";
                    String[] selectionArgs = {trabalhoBanco.getId()};
                    db.delete(TrabalhoEntry.TABLE_NAME, selection, selectionArgs);
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
