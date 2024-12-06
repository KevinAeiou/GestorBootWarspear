package com.kevin.ceep.repository;

import static com.kevin.ceep.db.contracts.PersoagemDbContract.PersonagemEntry.COLUMN_NAME_EMAIL;
import static com.kevin.ceep.db.contracts.PersoagemDbContract.PersonagemEntry.COLUMN_NAME_ESPACO_PRODUCAO;
import static com.kevin.ceep.db.contracts.PersoagemDbContract.PersonagemEntry.COLUMN_NAME_ESTADO;
import static com.kevin.ceep.db.contracts.PersoagemDbContract.PersonagemEntry.COLUMN_NAME_ID;
import static com.kevin.ceep.db.contracts.PersoagemDbContract.PersonagemEntry.COLUMN_NAME_NOME;
import static com.kevin.ceep.db.contracts.PersoagemDbContract.PersonagemEntry.COLUMN_NAME_SENHA;
import static com.kevin.ceep.db.contracts.PersoagemDbContract.PersonagemEntry.COLUMN_NAME_USO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.db.DbHelper;
import com.kevin.ceep.db.contracts.PersoagemDbContract;
import com.kevin.ceep.model.Personagem;

import java.util.ArrayList;
import java.util.Objects;

public class PersonagemRepository {
    private final DatabaseReference minhaReferencia;
    private final DbHelper dbHelper;
    private final String usuarioID;
    private final MutableLiveData<Resource<ArrayList<Personagem>>> personagensEncontrados;

    public PersonagemRepository(Context context) {
        this.usuarioID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        this.minhaReferencia = FirebaseDatabase.getInstance().getReference(CHAVE_USUARIOS)
                .child(usuarioID).child(CHAVE_LISTA_PERSONAGEM);
        this.dbHelper = new DbHelper(context);
        this.personagensEncontrados = new MutableLiveData<>();
    }

    public LiveData<Resource<Void>> sincronizaPersonagens() {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        minhaReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dn:snapshot.getChildren()){
                    Personagem personagem = dn.getValue(Personagem.class);
                    if (personagem != null){
                        SQLiteDatabase db = dbHelper.getReadableDatabase();
                        String selection = PersoagemDbContract.PersonagemEntry.COLUMN_NAME_ID + " LIKE ?";
                        String[] selectionArgs = {personagem.getId()};
                        Cursor cursor = db.query(
                                PersoagemDbContract.PersonagemEntry.TABLE_NAME,
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
                            SQLiteDatabase db2 = dbHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(PersoagemDbContract.PersonagemEntry.COLUMN_NAME_ID, personagem.getId());
                            values.put(PersoagemDbContract.PersonagemEntry.COLUMN_NAME_ID_USUARIO, usuarioID);
                            values.put(COLUMN_NAME_NOME, personagem.getNome());
                            values.put(PersoagemDbContract.PersonagemEntry.COLUMN_NAME_EMAIL, personagem.getEmail());
                            values.put(PersoagemDbContract.PersonagemEntry.COLUMN_NAME_SENHA, personagem.getSenha());
                            values.put(COLUMN_NAME_ESTADO, personagem.getEstado());
                            values.put(COLUMN_NAME_USO, personagem.getUso());
                            values.put(COLUMN_NAME_ESPACO_PRODUCAO, personagem.getEspacoProducao());
                            long newRowId = db2.insert(PersoagemDbContract.PersonagemEntry.TABLE_NAME, null, values);
                            if (newRowId == -1) {
                                liveData.setValue(new Resource<>(null, "Erro ao adicionar "+personagem.getNome()+" ao banco"));
                            } else {
                                liveData.setValue(new Resource<>(null, null));
                            }
                        }
                        cursor.close();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return liveData;
    }
    public LiveData<Resource<ArrayList<Personagem>>> pegaTodosPersonagens() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = PersoagemDbContract.PersonagemEntry.COLUMN_NAME_ID_USUARIO + " LIKE ?";
        String[] selectionArgs = {usuarioID};
        Cursor cursor = db.query(
                PersoagemDbContract.PersonagemEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );
        ArrayList<Personagem> personagens = new ArrayList<>();
        while (cursor.moveToNext()) {
            boolean estado = cursor.getInt(5) == 1;
            boolean uso = cursor.getInt(6) == 1;
            Personagem personagem = new Personagem (
                    cursor.getString(0),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    estado,
                    uso,
                    cursor.getInt(7)
                    );
            personagens.add(personagem);
        }
        cursor.close();
        personagensEncontrados.setValue(new Resource<>(personagens, null));
        return personagensEncontrados;
    }
    public LiveData<Resource<Void>> modificaPersonagem(Personagem personagemModificado) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        minhaReferencia.child(personagemModificado.getId()).child(COLUMN_NAME_NOME).setValue(personagemModificado.getNome()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME_NOME, personagemModificado.getNome());
                String selection = COLUMN_NAME_ID + " LIKE ?";
                String[] selectionArgs = {personagemModificado.getId()};
                long newRowId = db.update(PersoagemDbContract.PersonagemEntry.TABLE_NAME, values, selection, selectionArgs);
                if (newRowId == -1) {
                    liveData.setValue(new Resource<>(null, "Erro ao modificar "+personagemModificado.getNome()+" no banco"));
                } else {
                    liveData.setValue(new Resource<>(null, null));
                }
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).toString()));
            }
        });
        minhaReferencia.child(personagemModificado.getId()).child(COLUMN_NAME_EMAIL).setValue(personagemModificado.getEmail()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME_EMAIL, personagemModificado.getEmail());
                String selection = COLUMN_NAME_ID + " LIKE ?";
                String[] selectionArgs = {personagemModificado.getId()};
                long newRowId = db.update(PersoagemDbContract.PersonagemEntry.TABLE_NAME, values, selection, selectionArgs);
                if (newRowId == -1) {
                    liveData.setValue(new Resource<>(null, "Erro ao modificar "+personagemModificado.getNome()+" no banco"));
                } else {
                    liveData.setValue(new Resource<>(null, null));
                }
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).toString()));
            }
        });
        minhaReferencia.child(personagemModificado.getId()).child(COLUMN_NAME_SENHA).setValue(personagemModificado.getSenha()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME_SENHA, personagemModificado.getSenha());
                String selection = COLUMN_NAME_ID + " LIKE ?";
                String[] selectionArgs = {personagemModificado.getId()};
                long newRowId = db.update(PersoagemDbContract.PersonagemEntry.TABLE_NAME, values, selection, selectionArgs);
                if (newRowId == -1) {
                    liveData.setValue(new Resource<>(null, "Erro ao modificar "+personagemModificado.getNome()+" no banco"));
                } else {
                    liveData.setValue(new Resource<>(null, null));
                }
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).toString()));
            }
        });
        minhaReferencia.child(personagemModificado.getId()).child(COLUMN_NAME_ESTADO).setValue(personagemModificado.getEstado()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME_ESTADO, personagemModificado.getEstado());
                String selection = COLUMN_NAME_ID + " LIKE ?";
                String[] selectionArgs = {personagemModificado.getId()};
                long newRowId = db.update(PersoagemDbContract.PersonagemEntry.TABLE_NAME, values, selection, selectionArgs);
                if (newRowId == -1) {
                    Log.d("modificaPersonagem", "Erro ao modificar estado para: "+personagemModificado.getEstado());
                    liveData.setValue(new Resource<>(null, "Erro ao modificar "+personagemModificado.getNome()+" no banco"));
                } else {
                    Log.d("modificaPersonagem", "Estado modificado para: "+personagemModificado.getEstado());
                    liveData.setValue(new Resource<>(null, null));
                }
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).toString()));
            }
        });
        minhaReferencia.child(personagemModificado.getId()).child(COLUMN_NAME_USO).setValue(personagemModificado.getUso()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME_USO, personagemModificado.getUso());
                String selection = COLUMN_NAME_ID + " LIKE ?";
                String[] selectionArgs = {personagemModificado.getId()};
                long newRowId = db.update(PersoagemDbContract.PersonagemEntry.TABLE_NAME, values, selection, selectionArgs);
                if (newRowId == -1) {
                    liveData.setValue(new Resource<>(null, "Erro ao modificar "+personagemModificado.getNome()+" no banco"));
                } else {
                    liveData.setValue(new Resource<>(null, null));
                }
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).toString()));
            }
        });
        minhaReferencia.child(personagemModificado.getId()).child(COLUMN_NAME_ESPACO_PRODUCAO).setValue(personagemModificado.getEspacoProducao()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME_ESPACO_PRODUCAO, personagemModificado.getEspacoProducao());
                String selection = COLUMN_NAME_ID + " LIKE ?";
                String[] selectionArgs = {personagemModificado.getId()};
                long newRowId = db.update(PersoagemDbContract.PersonagemEntry.TABLE_NAME, values, selection, selectionArgs);
                if (newRowId == -1) {
                    liveData.setValue(new Resource<>(null, "Erro ao modificar "+personagemModificado.getNome()+" no banco"));
                } else {
                    liveData.setValue(new Resource<>(null, null));
                }
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).toString()));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Void>> adicionaPersonagem(Personagem novoPersonagem) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        minhaReferencia.child(novoPersonagem.getId()).setValue(novoPersonagem).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(PersoagemDbContract.PersonagemEntry.COLUMN_NAME_ID, novoPersonagem.getId());
                values.put(PersoagemDbContract.PersonagemEntry.COLUMN_NAME_ID_USUARIO, usuarioID);
                values.put(COLUMN_NAME_NOME, novoPersonagem.getNome());
                values.put(PersoagemDbContract.PersonagemEntry.COLUMN_NAME_EMAIL, novoPersonagem.getEmail());
                values.put(PersoagemDbContract.PersonagemEntry.COLUMN_NAME_SENHA, novoPersonagem.getSenha());
                values.put(COLUMN_NAME_ESTADO, novoPersonagem.getEstado());
                values.put(COLUMN_NAME_USO, novoPersonagem.getUso());
                values.put(COLUMN_NAME_ESPACO_PRODUCAO, novoPersonagem.getEspacoProducao());
                long newRowId = db.insert(PersoagemDbContract.PersonagemEntry.TABLE_NAME, null, values);
                if (newRowId == -1) {
                    liveData.setValue(new Resource<>(null, "Erro ao inserir "+novoPersonagem.getNome()+" no banco"));
                } else {
                    liveData.setValue(new Resource<>(null, null));
                }
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).toString()));
            }
        });
        return liveData;
    }
}
