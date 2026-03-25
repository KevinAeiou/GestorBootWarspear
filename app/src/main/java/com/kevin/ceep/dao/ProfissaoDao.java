package com.kevin.ceep.dao;

import static com.kevin.ceep.db.contracts.ProfissaoDbContract.ProfissaoEntry.COLUMN_NAME_ID;
import static com.kevin.ceep.db.contracts.ProfissaoDbContract.ProfissaoEntry.COLUMN_NAME_NOME;
import static com.kevin.ceep.db.contracts.ProfissaoDbContract.ProfissaoEntry.TABLE_PROFISSOES;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kevin.ceep.db.DbHelper;
import com.kevin.ceep.model.ProfissaoBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfissaoDao {
    private final SQLiteDatabase db;

    public ProfissaoDao(Context context) {
        DbHelper dbHelper = DbHelper.getInstance(context);

        this.db = dbHelper.getWritableDatabase();
    }

    public Map<String, String> recuperaMapaProfissoes() {
        Map<String, String> mapa = new HashMap<>();

        Cursor cursor = db.query(
                TABLE_PROFISSOES,
                new String[]{COLUMN_NAME_ID, COLUMN_NAME_NOME},
                null,null,null,null,null
        );

        while (cursor.moveToNext()) {
            mapa.put(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NOME))
            );
        }

        cursor.close();

        return mapa;
    }

    public void substituirTodas(List<ProfissaoBase> profissoes) {
        db.beginTransaction();
        try {
            db.delete(TABLE_PROFISSOES, null, null);

            for (ProfissaoBase profissao : profissoes) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME_ID, profissao.getId());
                values.put(COLUMN_NAME_NOME, profissao.getNome());

                db.insert(TABLE_PROFISSOES, null, values);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
