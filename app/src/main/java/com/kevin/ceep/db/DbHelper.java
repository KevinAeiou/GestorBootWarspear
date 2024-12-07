package com.kevin.ceep.db;

import static com.kevin.ceep.db.contracts.TrabalhoProducaoContract.TrabalhoProducaoEntry.COLUMN_NAME_ESTADO;
import static com.kevin.ceep.db.contracts.TrabalhoProducaoContract.TrabalhoProducaoEntry.COLUMN_NAME_ID;
import static com.kevin.ceep.db.contracts.TrabalhoProducaoContract.TrabalhoProducaoEntry.COLUMN_NAME_ID_PERSONAGEM;
import static com.kevin.ceep.db.contracts.TrabalhoProducaoContract.TrabalhoProducaoEntry.COLUMN_NAME_ID_TRABALHO;
import static com.kevin.ceep.db.contracts.TrabalhoProducaoContract.TrabalhoProducaoEntry.COLUMN_NAME_LICENCA;
import static com.kevin.ceep.db.contracts.TrabalhoProducaoContract.TrabalhoProducaoEntry.COLUMN_NAME_RECORRENCIA;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kevin.ceep.db.contracts.PersoagemDbContract;
import com.kevin.ceep.db.contracts.TrabalhoDbContract;
import com.kevin.ceep.db.contracts.TrabalhoProducaoContract;

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "autoProducao.db";

    public DbHelper(Context context) {
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
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + PersoagemDbContract.PersonagemEntry.TABLE_NAME + " (" +
                        PersoagemDbContract.PersonagemEntry.COLUMN_NAME_ID + " VARCHAR(30) PRIMARY KEY," +
                        PersoagemDbContract.PersonagemEntry.COLUMN_NAME_ID_USUARIO + " VARCHAR(30)," +
                        PersoagemDbContract.PersonagemEntry.COLUMN_NAME_NOME + " TEXT," +
                        PersoagemDbContract.PersonagemEntry.COLUMN_NAME_EMAIL + " TEXT," +
                        PersoagemDbContract.PersonagemEntry.COLUMN_NAME_SENHA + " TEXT," +
                        PersoagemDbContract.PersonagemEntry.COLUMN_NAME_ESTADO + " BOOLEAN," +
                        PersoagemDbContract.PersonagemEntry.COLUMN_NAME_USO + " BOOLEAN," +
                        PersoagemDbContract.PersonagemEntry.COLUMN_NAME_ESPACO_PRODUCAO + " INTEGER" +
                        ")"
        );
        sqLiteDatabase.execSQL(
                "CREATE TABLE " +
                        TrabalhoProducaoContract.TrabalhoProducaoEntry.TABLE_NAME +
                        " (" +
                        COLUMN_NAME_ID + " VARCHAR(30) PRIMARY KEY," +
                        COLUMN_NAME_ID_TRABALHO + " VARCHAR(30)," +
                        COLUMN_NAME_ID_PERSONAGEM + " VARCHAR(30)," +
                        COLUMN_NAME_LICENCA + " TEXT," +
                        COLUMN_NAME_ESTADO + " INTEGER," +
                        COLUMN_NAME_RECORRENCIA + " BOOLEAN" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrabalhoDbContract.TrabalhoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PersoagemDbContract.PersonagemEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
