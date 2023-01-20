package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_CONFIRMA_CADASTRO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_CONFIRMA;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.TAG_ACTIVITY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityOptionsCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Trabalho;

public class ConfirmaTrabalhoActivity extends AppCompatActivity {

    private AutoCompleteTextView autoComplete;
    private String[] licencas;
    private String personagemId;
    int pos;

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
        retornaLicencaSelecionada();
        Log.i(TAG_ACTIVITY,"onResumeConfirma");
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
        Log.i(TAG_ACTIVITY,"onStopConfirma");
    }

    private void configuraDropDrow() {
        autoComplete = findViewById(R.id.autoCompleteTextView);
        licencas = getResources().getStringArray(R.array.licencas);
        ArrayAdapter adapter = new ArrayAdapter<>(this,
                R.layout.item_dropdrown, licencas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoComplete.setAdapter(adapter);
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
            adicionaNovoTrabalho(trabalho);
        }
    }

    private void configuraImagemTrabalho(Trabalho trabalho) {
        ImageView imageView = findViewById(R.id.imageView);
    }

    private void adicionaNovoTrabalho(Trabalho trabalho) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhareferencia = database.getReference("Usuarios");
        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String novoId = geraIdAleatorio();
        String licenca = retornaLicencaSelecionada();
        minhareferencia.child(usuarioId).child("Lista_personagem").child(personagemId).child("Lista_desejo").child(novoId).setValue(trabalho);
        minhareferencia.child(usuarioId).child("Lista_personagem").child(personagemId).child("Lista_desejo").child(novoId).child("id").setValue(novoId);
        minhareferencia.child(usuarioId).child("Lista_personagem").child(personagemId).child("Lista_desejo").child(novoId).child("tipo_licenca").setValue(licenca);
    }

    private String retornaLicencaSelecionada() {

        autoComplete.setOnItemClickListener((adapterView, view, i, l) -> {
            pos = -1;
            String selecao = (String) adapterView.getItemAtPosition(i);
            for (int x = 0; x < licencas.length; x++) {
                if (licencas[x].equals(selecao)) {
                    pos = x;
                    break;
                }
            }
            Log.d("Licenca3",licencas[pos]);
        });

        return licencas[pos];
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