package com.kevin.ceep.db.contracts;

import android.provider.BaseColumns;

public class ProfissaoDbContract {

    private ProfissaoDbContract() {}

    public static class ProfissaoEntry implements BaseColumns {
        public static final String TABLE_PROFISSOES = "profissoes";

        public static final String COLUMN_NAME_ID = "ID";

        public static final String COLUMN_NAME_NOME = "nome";
    }
}
