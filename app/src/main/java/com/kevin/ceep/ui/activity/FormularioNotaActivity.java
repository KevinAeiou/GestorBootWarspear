/*package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOTA;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_POSICAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.POSICAO_INVALIDA;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kevin.ceep.R;
import com.kevin.ceep.model.Trabalho;

public class FormularioNotaActivity extends AppCompatActivity {

    private int posicaoRecebida = POSICAO_INVALIDA;
    private TextView nome_trabalho;
    private TextView tipo_licenca;
    private TextView nivel_trabalho;
    private TextView profissao_trabalho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_trabalhos);

        inicializaCampos();

        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_NOTA)){
            Trabalho notaRecebida = (Trabalho) dadosRecebidos
                    .getSerializableExtra(CHAVE_NOTA);
            posicaoRecebida = dadosRecebidos.getIntExtra(CHAVE_POSICAO, POSICAO_INVALIDA);
            preencheCampos(notaRecebida);
        }
    }

    private void preencheCampos(Trabalho notaRecebida) {
        nome_trabalho.setText(notaRecebida.getNome());
        tipo_licenca.setText(notaRecebida.getProfissao());
    }

    private void inicializaCampos() {
        nome_trabalho = findViewById(R.id.formulario_nota_titulo);
        tipo_licenca = findViewById(R.id.formulario_nota_descricao);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario_nota_salva, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (ehMenuSalvaNota(item)){
            //Trabalho notaCriada = criaNota();
            //retornaNota(notaCriada);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void retornaNota(Trabalho nota) {
        Intent resultadoInsercao = new Intent();
        resultadoInsercao.putExtra(CHAVE_NOTA, nota);
        resultadoInsercao.putExtra(CHAVE_POSICAO, posicaoRecebida);
        setResult(Activity.RESULT_OK, resultadoInsercao);
    }

    /*@NonNull
    private Trabalho criaNota() {
        return new Trabalho(
                nome_trabalho.getText().toString(),
                tipo_licenca.getText().toString(),
                profissao_trabalho.getText().toString(),
                nivel_trabalho.getText().toString());
    }

    private boolean ehMenuSalvaNota(@NonNull MenuItem item) {
        return item.getItemId() == R.id.menuFormularioNotaIcSalva;
    }
}*/