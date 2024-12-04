package com.kevin.ceep.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kevin.ceep.db.contracts.TrabalhoDbContract;

public class TrabalhoDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "autoProducao.db";

    public TrabalhoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TrabalhoDbContract.TrabalhoEntry.TABLE_NAME + " (" +
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_ID + " VARCHAR(30) PRIMARY KEY," +
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NOME + " TEXT," +
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NOME_PRODUCAO + " TEXT," +
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_EXPERIENCIA + " INTEGER," +
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_NIVEL + " INTEGER," +
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_PROFISSAO + " TEXT," +
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_RARIDADE + " TEXT," +
                TrabalhoDbContract.TrabalhoEntry.COLUMN_NAME_TRABALHO_NECESSARIO + " TEXT" +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrabalhoDbContract.TrabalhoEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
