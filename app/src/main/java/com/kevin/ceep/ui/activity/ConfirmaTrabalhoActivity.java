package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_CONFIRMA_CADASTRO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_CONFIRMA;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Trabalho;

public class ConfirmaTrabalhoActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteLicenca,autoCompleteQuantidade;
    private String[] licencas,quantidade;
    private String personagemId;
    int pos,quantidadeSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_trabalho);
        setTitle(CHAVE_TITULO_CONFIRMA);

        configuraBotaoCadastraTrabalho();

        Log.i(TAG_ACTIVITY,"onCreateConfirma");
    }

    @Override
    protected void onResume() {
        super.onResume();
        configuraDropDrow();
        Log.i(TAG_ACTIVITY,"onResumeConfirma");
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
        Log.i(TAG_ACTIVITY,"onStopConfirma");
    }

    private void configuraDropDrow() {
        autoCompleteLicenca = findViewById(R.id.txtAutoCompleteLicencaTrabalho);
        autoCompleteQuantidade = findViewById(R.id.txtAutoCompleteQuantidadeTrabalho);

        licencas = getResources().getStringArray(R.array.licencas);
        quantidade = getResources().getStringArray(R.array.quantidade);

        ArrayAdapter adapterLicenca = new ArrayAdapter<>(this,
                R.layout.item_dropdrown, licencas);
        ArrayAdapter adapterQuantidade = new ArrayAdapter<>(this,
                R.layout.item_dropdrown, quantidade);

        adapterLicenca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterQuantidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        autoCompleteLicenca.setAdapter(adapterLicenca);
        autoCompleteQuantidade.setAdapter(adapterQuantidade);
    }

    private void configuraBotaoCadastraTrabalho() {
        AppCompatButton botaoCadastraTrabalho = findViewById(R.id.botao_cadastrar);
        botaoCadastraTrabalho.setOnClickListener(view -> {
            cadastraNovoTrabalho();
            vaiParaListaTrabalhosActivity();
        });
    }

    private void cadastraNovoTrabalho() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_NOME_TRABALHO)) {
            Trabalho trabalho = (Trabalho) dadosRecebidos
                    .getSerializableExtra(CHAVE_NOME_TRABALHO);
            personagemId = (String) dadosRecebidos.
                    getSerializableExtra(CHAVE_NOME_PERSONAGEM);
            configuraImagemTrabalho(trabalho);
            int quantidadeTrabalho = retornaQuantidadeSelecionada();
            for (int x=0;x<quantidadeTrabalho;x++){
                adicionaNovoTrabalho(trabalho);
            }
        }
    }


    private void configuraImagemTrabalho(Trabalho trabalho) {
        ImageView imageView = findViewById(R.id.imageView);
    }

    private void adicionaNovoTrabalho(Trabalho trabalho) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhareferencia = database.getReference(CHAVE_USUARIOS);
        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String novoId = geraIdAleatorio();
        String licenca = retornaLicencaSelecionada();
        minhareferencia.child(usuarioId).child(CHAVE_PERSONAGEM).child(personagemId).child(CHAVE_LISTA_DESEJO).child(novoId).setValue(trabalho);
        minhareferencia.child(usuarioId).child(CHAVE_PERSONAGEM).child(personagemId).child(CHAVE_LISTA_DESEJO).child(novoId).child("id").setValue(novoId);
        minhareferencia.child(usuarioId).child(CHAVE_PERSONAGEM).child(personagemId).child(CHAVE_LISTA_DESEJO).child(novoId).child("tipo_licenca").setValue(licenca);
    }

    private int retornaQuantidadeSelecionada() {
        quantidadeSelecionada = 1;
        autoCompleteQuantidade.setOnItemClickListener((adapterView, view, i, l) -> {
            String selecao = (String) adapterView.getItemAtPosition(i);
            for (int x=0;x<quantidade.length;x++){
                if (quantidade[x].equals(selecao)){
                    Log.d("QUANTIDADE",selecao);
                    Log.d("QUANTIDADE",String.valueOf(x+1));
                    quantidadeSelecionada = x+1;
                }
            }
        });
        Log.d("QUANTIDADE", String.valueOf(quantidadeSelecionada));
        return quantidadeSelecionada;
    }

    private String retornaLicencaSelecionada() {
        String[] licencas_completas = getResources().getStringArray(R.array.licencas_completas);

        autoCompleteLicenca.setOnItemClickListener((adapterView, view, i, l) -> {
            pos = -1;
            String selecao = (String) adapterView.getItemAtPosition(i);
            for (int x = 0; x < licencas_completas.length; x++) {
                if (licencas_completas[x].equals(selecao)) {
                    pos = x;
                    break;
                }
            }
            Log.d("Licenca3",licencas_completas[pos]);
        });

        return licencas_completas[pos];
    }

    private void vaiParaListaTrabalhosActivity() {
        Intent iniciaListaTrabalho =
                new Intent(ConfirmaTrabalhoActivity.this,
                        ListaPersonagemActivity.class);
        iniciaListaTrabalho.putExtra(CHAVE_CONFIRMA_CADASTRO,true);
        startActivity(iniciaListaTrabalho,
                ActivityOptions.makeSceneTransitionAnimation(ConfirmaTrabalhoActivity.this).toBundle());
    }

    // function to generate a random string of length n
    static String geraIdAleatorio() {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(28);

        for (int i = 0; i < 28; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}