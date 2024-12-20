package com.kevin.ceep.repository;

import static com.kevin.ceep.db.contracts.EstoqueDbContract.EstoqueEntry.COLUMN_NAME_ID;
import static com.kevin.ceep.db.contracts.EstoqueDbContract.EstoqueEntry.COLUMN_NAME_ID_PERSONAGEM;
import static com.kevin.ceep.db.contracts.EstoqueDbContract.EstoqueEntry.COLUMN_NAME_ID_TRABALHO;
import static com.kevin.ceep.db.contracts.EstoqueDbContract.EstoqueEntry.COLUMN_NAME_QUANTIDADE;
import static com.kevin.ceep.db.contracts.EstoqueDbContract.EstoqueEntry.TABLE_NAME;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_ESTOQUE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.utilitario.Utilitario.comparaString;
import static com.kevin.ceep.utilitario.Utilitario.geraIdAleatorio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
import com.kevin.ceep.db.contracts.EstoqueDbContract.EstoqueEntry;
import com.kevin.ceep.model.TrabalhoEstoque;
import com.kevin.ceep.model.TrabalhoProducao;

import java.util.ArrayList;
import java.util.Objects;

public class TrabalhoEstoqueRepository {
    private final DatabaseReference minhaReferencia;
    private final SQLiteDatabase dbLeitura, dbModificacao;
    private final String idPersonagem;
    private final MutableLiveData<Resource<ArrayList<TrabalhoEstoque>>> trabalhosEstoqueEncontrados;

    public TrabalhoEstoqueRepository(Context context, String personagemID) {
        this.idPersonagem = personagemID;
        String usuarioID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        this.minhaReferencia = FirebaseDatabase.getInstance().getReference(CHAVE_USUARIOS).child(usuarioID).child(CHAVE_LISTA_PERSONAGEM)
                .child(personagemID).child(CHAVE_LISTA_ESTOQUE);
        DbHelper dbHelper = DbHelper.getInstance(context);
        this.dbLeitura = dbHelper.getReadableDatabase();
        this.dbModificacao = dbHelper.getWritableDatabase();
        this.trabalhosEstoqueEncontrados = new MutableLiveData<>();
    }

    public void modificaQuantidadeTrabalhoNecessarioNoEstoque(TrabalhoProducao trabalhoProducao) {
        String[] listaTrabalhosNecessarios = trabalhoProducao.getTrabalhoNecessario().split(",");
        minhaReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            TrabalhoEstoque trabalho = dn.getValue(TrabalhoEstoque.class);
                            if (trabalho != null) {
                                for (String nome: listaTrabalhosNecessarios) {
                                    if (comparaString(trabalho.getNome(), nome)) {
                                        int novaQuantidade = trabalho.getQuantidade() - 1;
                                        if (novaQuantidade < 0) {
                                            novaQuantidade = 0;
                                        }
                                        trabalho.setQuantidade(novaQuantidade);
                                        modificaTrabalhoEstoque(trabalho);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    public LiveData<Resource<Void>> modificaTrabalhoEstoque(TrabalhoEstoque trabalho) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        if (trabalho.getId() != null) {
            minhaReferencia.child(trabalho.getId()).child(COLUMN_NAME_QUANTIDADE).setValue(trabalho.getQuantidade()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ContentValues values = new ContentValues();
                    dbModificacao.beginTransaction();
                    values.put(COLUMN_NAME_QUANTIDADE, trabalho.getQuantidade());
                    String selection = EstoqueEntry.COLUMN_NAME_ID + " LIKE ?";
                    String[] selectionArgs = {trabalho.getId()};
                    long newRowId = dbModificacao.update(TABLE_NAME, values, selection, selectionArgs);
                    if (newRowId == -1) {
                        liveData.setValue(new Resource<>(null, "Erro ao modificar trabalho produção no banco"));
                    } else {
                        liveData.setValue(new Resource<>(null, null));
                    }
                } else if (task.isCanceled()) {
                    liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).getMessage()));
                }
            });
        }
        return liveData;
    }
    public LiveData<Resource<ArrayList<TrabalhoEstoque>>> pegaTodosTrabalhosEstoque() {
        String sql = "SELECT Lista_estoque.id, Lista_estoque.idTrabalho, Lista_estoque.idPersonagem, trabalhos.nome, trabalhos.nomeProducao, trabalhos.profissao, trabalhos.raridade, trabalhos.trabalhoNecessario, trabalhos.nivel, trabalhos.experiencia, Lista_estoque.quantidade\n" +
                "FROM Lista_estoque\n" +
                "INNER JOIN trabalhos\n" +
                "ON Lista_estoque.idTrabalho == trabalhos.id\n" +
                "WHERE Lista_estoque.idPersonagem == ? " +
                "ORDER BY trabalhos.profissao, trabalhos.raridade, trabalhos.nivel";
        String[] selectionArgs = {idPersonagem};
        Cursor cursor = dbLeitura.rawQuery(
                sql,
                selectionArgs
        );
        ArrayList<TrabalhoEstoque> trabalhosEstoque = new ArrayList<>();
        while (cursor.moveToNext()) {
            TrabalhoEstoque trabalhoEstoque = new TrabalhoEstoque();
            trabalhoEstoque.setId(cursor.getString(0));
            trabalhoEstoque.setTrabalhoId(cursor.getString(1));
            trabalhoEstoque.setNome(cursor.getString(3));
            trabalhoEstoque.setNomeProducao(cursor.getString(4));
            trabalhoEstoque.setProfissao(cursor.getString(5));
            trabalhoEstoque.setRaridade(cursor.getString(6));
            trabalhoEstoque.setTrabalhoNecessario(cursor.getString(7));
            trabalhoEstoque.setNivel(cursor.getInt(8));
            trabalhoEstoque.setExperiencia(cursor.getInt(9));
            trabalhoEstoque.setQuantidade(cursor.getInt(10));
            trabalhosEstoque.add(trabalhoEstoque);
        }
        cursor.close();
        trabalhosEstoqueEncontrados.setValue(new Resource<>(trabalhosEstoque, null));
        return trabalhosEstoqueEncontrados;
    }
    public TrabalhoEstoque retornaTrabalhoEspecificoEstoque(ArrayList<TrabalhoEstoque> trabalhosEstoque, String nomeTrabalho) {
        for (TrabalhoEstoque trabalhoEstoque : trabalhosEstoque) {
            if (comparaString(trabalhoEstoque.getNome(), nomeTrabalho)) {
                return trabalhoEstoque;
            }
        }
        return null;
    }

    public LiveData<Resource<Void>> adicionaTrabalhoEstoque(TrabalhoEstoque novoTrabalho) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        String novoId = geraIdAleatorio();
        novoTrabalho.setId(novoId);
        minhaReferencia.child(novoTrabalho.getId()).setValue(novoTrabalho).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME_ID, novoTrabalho.getId());
                values.put(COLUMN_NAME_ID_PERSONAGEM, idPersonagem);
                values.put(COLUMN_NAME_ID_TRABALHO, novoTrabalho.getTrabalhoId());
                values.put(COLUMN_NAME_QUANTIDADE, novoTrabalho.getQuantidade());
                long novaLinha = dbModificacao.insert(TABLE_NAME, null, values);
                if (novaLinha == -1) {
                    liveData.setValue(new Resource<>(null, "Erro ao adicionar novo trabalho no estoque"));
                } else {
                    liveData.setValue(new Resource<>(null, null));
                }
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).getMessage()));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Void>> removeTrabalhoEstoque(TrabalhoEstoque trabalhoRemovido) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        minhaReferencia.child(trabalhoRemovido.getId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String selection = COLUMN_NAME_ID + " LIKE ?";
                String[] selectionArgs = {trabalhoRemovido.getId()};
                long linhaRemovida = dbModificacao.delete(TABLE_NAME, selection, selectionArgs);
                if (linhaRemovida == -1) {
                    liveData.setValue(new Resource<>(null, "Erro ao remover trabalho do estoque"));
                } else {
                    liveData.setValue(new Resource<>(null, null));
                }
            } else if (task.isCanceled()) {
                liveData.setValue(new Resource<>(null, Objects.requireNonNull(task.getException()).getMessage()));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Void>> sincronizaEstoque() {
        ArrayList<TrabalhoEstoque> trabalhosEstoqueServidor = new ArrayList<>();
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        minhaReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dn:dataSnapshot.getChildren()){
                    TrabalhoEstoque trabalho = dn.getValue(TrabalhoEstoque.class);
                    trabalhosEstoqueServidor.add(trabalho);
                    String selection = COLUMN_NAME_ID + " LIKE ?";
                    String[] selectionArgs = {Objects.requireNonNull(trabalho).getId()};
                    Cursor cursor = dbLeitura.query(
                            TABLE_NAME,
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
                    cursor.close();
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_NAME_ID, trabalho.getId());
                    values.put(COLUMN_NAME_ID_PERSONAGEM, idPersonagem);
                    values.put(COLUMN_NAME_ID_TRABALHO, trabalho.getTrabalhoId());
                    values.put(COLUMN_NAME_QUANTIDADE, trabalho.getQuantidade());
                    if (contadorLinhas == 0) {
                        dbModificacao.insert(TABLE_NAME, null, values);
                    } else if (contadorLinhas == 1) {
                        selection = COLUMN_NAME_ID + " LIKE ?";
                        selectionArgs = new String[]{trabalho.getId()};
                        dbModificacao.update(TABLE_NAME, values, selection, selectionArgs);
                    }
                }
                String selection = "SELECT id " +
                        "FROM Lista_estoque " +
                        "WHERE idPersonagem == ?";
                String[] selectionArgs = {idPersonagem};
                Cursor cursor = dbLeitura.rawQuery(
                        selection,
                        selectionArgs
                );
                ArrayList<TrabalhoEstoque> trabalhosEstoqueBanco = new ArrayList<>();
                while (cursor.moveToNext()) {
                    TrabalhoEstoque trabalhoEstoque = new TrabalhoEstoque();
                    trabalhoEstoque.setId(cursor.getString(0));
                    trabalhosEstoqueBanco.add(trabalhoEstoque);
                }
                cursor.close();
                ArrayList<TrabalhoEstoque> novaLista = new ArrayList<>();
                for (TrabalhoEstoque trabalhoBanco : trabalhosEstoqueBanco) {
                    for (TrabalhoEstoque trabalhoServidor : trabalhosEstoqueServidor) {
                        if (trabalhoBanco.getId().equals(trabalhoServidor.getId())) {
                            novaLista.add(trabalhoBanco);
                        }
                    }
                }
                trabalhosEstoqueBanco.removeAll(novaLista);
                for (TrabalhoEstoque trabalhoEstoque : trabalhosEstoqueBanco) {
                    String selection2 = COLUMN_NAME_ID + " LIKE ?";
                    String[] selectionArgs2 = {trabalhoEstoque.getId(), idPersonagem};
                    dbModificacao.delete(TABLE_NAME, selection2, selectionArgs2);
                }
                liveData.setValue(new Resource<>(null, null));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                liveData.setValue(new Resource<>(null, databaseError.getMessage()));
            }
        });
        return liveData;
    }
}
