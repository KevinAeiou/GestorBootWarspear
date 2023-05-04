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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Trabalho;

public class ConfirmaTrabalhoActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteLicenca,autoCompleteQuantidade;
    private String personagemId, licencaSelecionada;
    private Trabalho trabalho;
    private int quantidadeSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_trabalho);
        setTitle(CHAVE_TITULO_CONFIRMA);

        recebeDadosIntent();
        configuraBotaoCadastraTrabalho();

        Log.i(TAG_ACTIVITY,"onCreateConfirma");
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_NOME_TRABALHO)) {
            trabalho = (Trabalho) dadosRecebidos
                    .getSerializableExtra(CHAVE_NOME_TRABALHO);
            setTitle(trabalho.getNome());
            personagemId = (String) dadosRecebidos.
                    getSerializableExtra(CHAVE_NOME_PERSONAGEM);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        configuraDropDrow();
        configuraLicencaSelecionada();
        configuraQuantidadeSelecionada();
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

        String[] licencas = getResources().getStringArray(R.array.licencas);
        String[] quantidade = getResources().getStringArray(R.array.quantidade);

        ArrayAdapter adapterLicenca = new ArrayAdapter<>(this,
                R.layout.item_dropdrown, licencas);
        ArrayAdapter adapterQuantidade = new ArrayAdapter<>(this,
                R.layout.item_dropdrown, quantidade);

        adapterLicenca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterQuantidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteLicenca.setText(licencas[3]);
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
        for (int x=0;x<quantidadeSelecionada;x++){
            adicionaNovoTrabalho(trabalho);
        }
    }

    private void adicionaNovoTrabalho(Trabalho trabalho) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhareferencia = database.getReference(CHAVE_USUARIOS);
        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String novoId = geraIdAleatorio();
        minhareferencia.child(usuarioId).child(CHAVE_PERSONAGEM)
                .child(personagemId).child(CHAVE_LISTA_DESEJO)
                .child(novoId).setValue(trabalho);
        minhareferencia.child(usuarioId).child(CHAVE_PERSONAGEM)
                .child(personagemId).child(CHAVE_LISTA_DESEJO)
                .child(novoId).child("id").setValue(novoId);
        minhareferencia.child(usuarioId).child(CHAVE_PERSONAGEM)
                .child(personagemId).child(CHAVE_LISTA_DESEJO)
                .child(novoId).child("tipo_licenca").setValue(licencaSelecionada);
    }

    private void configuraQuantidadeSelecionada() {
        String[] quantidade = getResources().getStringArray(R.array.quantidade);
        quantidadeSelecionada = 1;
        autoCompleteQuantidade.setOnItemClickListener((adapterView, view, i, l) -> {
            quantidadeSelecionada = Integer.parseInt(quantidade[i]);
            Log.d("QUANTIDADE1", String.valueOf(i+1));
        });
        Log.d("QUANTIDADE2", String.valueOf(quantidadeSelecionada));
    }

    private void configuraLicencaSelecionada() {
        String[] licencas_completas = getResources().getStringArray(R.array.licencas_completas);
        licencaSelecionada = licencas_completas[3];
        autoCompleteLicenca.setOnItemClickListener((adapterView, view, i, l) -> {
            String selecao = (String) adapterView.getItemAtPosition(i);
            Log.d("LICENCA1", selecao.substring(11));
            for (int x = 0; x < licencas_completas.length; x++) {
                if (licencas_completas[x].contains(selecao.substring(11))) {
                    Log.d("LICENCA2", String.valueOf(x));
                    licencaSelecionada = licencas_completas[x];
                    break;
                }
            }
            Log.d("LICENCA3",licencaSelecionada);
        });
    }

    private void vaiParaListaTrabalhosActivity() {
        Intent iniciaListaTrabalho =
                new Intent(ConfirmaTrabalhoActivity.this,
                        ListaPersonagemActivity.class);
        iniciaListaTrabalho.putExtra(CHAVE_CONFIRMA_CADASTRO,true);
        startActivity(iniciaListaTrabalho,
                ActivityOptions.makeSceneTransitionAnimation(ConfirmaTrabalhoActivity.this).toBundle());
    }

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