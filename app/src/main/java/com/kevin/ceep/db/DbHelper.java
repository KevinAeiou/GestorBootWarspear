package com.kevin.ceep.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.kevin.ceep.db.contracts.EstoqueDbContract.EstoqueEntry;
import com.kevin.ceep.db.contracts.PersoagemDbContract.PersonagemEntry;
import com.kevin.ceep.db.contracts.TrabalhoDbContract.TrabalhoEntry;
import com.kevin.ceep.db.contracts.TrabalhoProducaoContract.TrabalhoProducaoEntry;

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "autoProducao.db";
    private static DbHelper minhaInstancia = null;

    public static DbHelper getInstance(Context context) {
        if (minhaInstancia == null) {
            minhaInstancia = new DbHelper(context.getApplicationContext());
        }
        return minhaInstancia;
    }

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TrabalhoEntry.TABLE_NAME + " (" +
                TrabalhoEntry.COLUMN_NAME_ID + " VARCHAR(30) PRIMARY KEY," +
                TrabalhoEntry.COLUMN_NAME_NOME + " TEXT," +
                TrabalhoEntry.COLUMN_NAME_NOME_PRODUCAO + " TEXT," +
                TrabalhoEntry.COLUMN_NAME_EXPERIENCIA + " INTEGER," +
                TrabalhoEntry.COLUMN_NAME_NIVEL + " INTEGER," +
                TrabalhoEntry.COLUMN_NAME_PROFISSAO + " TEXT," +
                TrabalhoEntry.COLUMN_NAME_RARIDADE + " TEXT," +
                TrabalhoEntry.COLUMN_NAME_TRABALHO_NECESSARIO + " TEXT" +
                ")"
        );
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + PersonagemEntry.TABLE_NAME + " (" +
                PersonagemEntry.COLUMN_NAME_ID + " VARCHAR(30) PRIMARY KEY," +
                PersonagemEntry.COLUMN_NAME_ID_USUARIO + " VARCHAR(30)," +
                PersonagemEntry.COLUMN_NAME_NOME + " TEXT," +
                PersonagemEntry.COLUMN_NAME_EMAIL + " TEXT," +
                PersonagemEntry.COLUMN_NAME_SENHA + " TEXT," +
                PersonagemEntry.COLUMN_NAME_ESTADO + " BOOLEAN," +
                PersonagemEntry.COLUMN_NAME_USO + " BOOLEAN," +
                PersonagemEntry.COLUMN_NAME_ESPACO_PRODUCAO + " INTEGER" +
                ")"
        );
        sqLiteDatabase.execSQL(
                "CREATE TABLE " +
                TrabalhoProducaoEntry.TABLE_NAME +
                " (" +
                TrabalhoProducaoEntry.COLUMN_NAME_ID + " VARCHAR(30) PRIMARY KEY," +
                TrabalhoProducaoEntry.COLUMN_NAME_ID_TRABALHO + " VARCHAR(30)," +
                TrabalhoProducaoEntry.COLUMN_NAME_ID_PERSONAGEM + " VARCHAR(30)," +
                TrabalhoProducaoEntry.COLUMN_NAME_LICENCA + " TEXT," +
                TrabalhoProducaoEntry.COLUMN_NAME_ESTADO + " INTEGER," +
                TrabalhoProducaoEntry.COLUMN_NAME_RECORRENCIA + " BOOLEAN" +
                ")"
        );
        sqLiteDatabase.execSQL(
                "CREATE TABLE " +
                EstoqueEntry.TABLE_NAME +
                " (" +
                EstoqueEntry.COLUMN_NAME_ID + " VARCHAR(30) PRIMARY KEY," +
                EstoqueEntry.COLUMN_NAME_ID_TRABALHO + " VARCHAR(30)," +
                EstoqueEntry.COLUMN_NAME_ID_PERSONAGEM + " VARCHAR(30)," +
                EstoqueEntry.COLUMN_NAME_QUANTIDADE + " INTEGER" +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrabalhoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PersonagemEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrabalhoProducaoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EstoqueEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
