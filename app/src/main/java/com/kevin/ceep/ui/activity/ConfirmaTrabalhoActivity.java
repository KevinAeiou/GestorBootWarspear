package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.Utilitario.geraIdAleatorio;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_CONFIRMA;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoProducao;

import java.util.Locale;
import java.util.Objects;

public class ConfirmaTrabalhoActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteLicenca,autoCompleteQuantidade;
    private String personagemId;
    private Trabalho trabalhoRecebido;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_trabalho);
        setTitle(CHAVE_TITULO_CONFIRMA);
        recebeDadosIntent();
        configuraBotaoCadastraTrabalho();
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_TRABALHO)) {
            trabalhoRecebido = (Trabalho) dadosRecebidos
                    .getSerializableExtra(CHAVE_TRABALHO);
            if (trabalhoRecebido != null) {
                setTitle(trabalhoRecebido.getNome());
            }
            personagemId = (String) dadosRecebidos.
                    getSerializableExtra(CHAVE_PERSONAGEM);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        configuraDropDrow();
        Log.i(TAG_ACTIVITY,"onResumeConfirma");
    }
    private void configuraDropDrow() {
        autoCompleteLicenca = findViewById(R.id.txtAutoCompleteLicencaConfirmaTrabalho);
        autoCompleteQuantidade = findViewById(R.id.txtAutoCompleteQuantidadeConfirmaTrabalho);

        String[] licencas = getResources().getStringArray(R.array.licencas_completas);
        String[] quantidade = getResources().getStringArray(R.array.quantidade);

        ArrayAdapter<String> adapterLicenca = new ArrayAdapter<>(this,
                R.layout.item_dropdrown, licencas);
        ArrayAdapter<String> adapterQuantidade = new ArrayAdapter<>(this,
                R.layout.item_dropdrown, quantidade);

        adapterLicenca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterQuantidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteLicenca.setText(licencas[3]);
        autoCompleteLicenca.setTextColor(Color.BLACK);
        autoCompleteLicenca.setAdapter(adapterLicenca);
        autoCompleteQuantidade.setTextColor(Color.BLACK);
        autoCompleteQuantidade.setAdapter(adapterQuantidade);
    }

    private void configuraBotaoCadastraTrabalho() {
        AppCompatButton botaoCadastraTrabalho = findViewById(R.id.botaoCadastraConfirmaTrabalho);
        botaoCadastraTrabalho.setOnClickListener(view -> {
            cadastraNovoTrabalho();
            finish();
        });
    }

    private void cadastraNovoTrabalho() {
        int quantidadeSelecionada = Integer.parseInt(autoCompleteQuantidade.getText().toString());
        for (int x=0;x<quantidadeSelecionada;x++){
            adicionaNovoTrabalho();
        }
    }

    private void adicionaNovoTrabalho() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhareferencia = database.getReference(CHAVE_USUARIOS);
        String usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String novoId = geraIdAleatorio();
        String liencaSelecionada = autoCompleteLicenca.getText().toString();
        CheckBox checkRecorrencia = findViewById(R.id.checkBoxProducaoRecorrenteConfirmaTrabalho);
        TrabalhoProducao novoTrabalho = new TrabalhoProducao(
                novoId,
                trabalhoRecebido.getId(),
                liencaSelecionada,
                0,
                checkRecorrencia.isChecked());
        minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                .child(personagemId).child(CHAVE_LISTA_DESEJO)
                .child(novoId)
                .setValue(novoTrabalho);
    }
}