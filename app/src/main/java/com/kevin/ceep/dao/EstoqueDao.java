package com.kevin.ceep.dao;

import static com.kevin.ceep.db.contracts.EstoqueDbContract.EstoqueEntry.COLUMN_NAME_ID;
import static com.kevin.ceep.db.contracts.EstoqueDbContract.EstoqueEntry.COLUMN_NAME_ID_PERSONAGEM;
import static com.kevin.ceep.db.contracts.EstoqueDbContract.EstoqueEntry.COLUMN_NAME_ID_TRABALHO;
import static com.kevin.ceep.db.contracts.EstoqueDbContract.EstoqueEntry.COLUMN_NAME_QUANTIDADE;
import static com.kevin.ceep.db.contracts.EstoqueDbContract.EstoqueEntry.TABLE_ESTOQUE;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.kevin.ceep.db.DbHelper;
import com.kevin.ceep.model.TrabalhoEstoque;

import java.util.List;

public class EstoqueDao {
    private final SQLiteDatabase db;


    public EstoqueDao(Context context) {
        DbHelper db_helper = DbHelper.getInstance(context);

        this.db = db_helper.getWritableDatabase();
    }

    public void substituirTodas(List<TrabalhoEstoque> estoque, String id_personagem) {
        db.beginTransaction();

        try {
            db.delete(TABLE_ESTOQUE, null, null);

            for (TrabalhoEstoque trabalho : estoque) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME_ID, trabalho.getId());
                values.put(COLUMN_NAME_ID_TRABALHO, trabalho.getIdTrabalho());
                values.put(COLUMN_NAME_ID_PERSONAGEM, id_personagem);
                values.put(COLUMN_NAME_QUANTIDADE, trabalho.getQuantidade());

                db.insert(TABLE_ESTOQUE, null, values);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
