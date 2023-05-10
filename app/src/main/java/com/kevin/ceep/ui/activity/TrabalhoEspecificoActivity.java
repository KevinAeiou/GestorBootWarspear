package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PROFISSAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_RARIDADE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOTA;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_NOVO_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.Trabalho;

public class TrabalhoEspecificoActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference minhareferencia;
    private Trabalho trabalhoRecebido;
    private Profissao raridadeRecebido,profissaoRecebido;
    private ProgressDialog progressDialog;
    private TextInputEditText edtNomeTrabalho,edtNivelTrabalho;
    private TextInputLayout txtInputEstado, txtInputLicenca,txtInputNome,txtInputNivel;
    private AutoCompleteTextView autoCompleteEstado,autoCompleteLicenca;
    private String[] estadosTrabalho,licencasTrabalho, mensagemErro={"Campo requerido!","Inválido!"};
    private String usuarioId,personagemId,trabalhoId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trabalho_especifico);

        inicializaComponentes();

        recebeDadosIntent();

        configuraDropdownEstados();
        modificaEstado();
    }

    private void modificaEstado() {
        autoCompleteEstado.setOnItemClickListener((adapterView, view, i, l) -> {
            mostraDialogodeProresso();
            minhareferencia.child(usuarioId).child(CHAVE_PERSONAGEM)
                    .child(personagemId).child(CHAVE_LISTA_DESEJO)
                    .child(trabalhoId).child("estado").setValue(i);
            progressDialog.dismiss();
        });
        autoCompleteLicenca.setOnItemClickListener((adapterView, view, i, l) -> {
            mostraDialogodeProresso();
            minhareferencia.child(usuarioId).child(CHAVE_PERSONAGEM)
                    .child(personagemId).child(CHAVE_LISTA_DESEJO)
                    .child(trabalhoId).child("tipo_licenca").setValue(licencasTrabalho[i]);
            progressDialog.dismiss();
        });
    }

    private void mostraDialogodeProresso(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Modificando estado...");
        progressDialog.show();
    }

    private void configuraDropdownEstados() {
        ArrayAdapter adapterLicenca=new ArrayAdapter(this,
                R.layout.item_dropdrown,licencasTrabalho);
        ArrayAdapter adapterEstado=new ArrayAdapter(this,
                R.layout.item_dropdrown,estadosTrabalho);
        adapterLicenca.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteLicenca.setAdapter(adapterLicenca);
        autoCompleteEstado.setAdapter(adapterEstado);
    }

    private void inicializaComponentes() {
        database = FirebaseDatabase.getInstance();
        minhareferencia = database.getReference(CHAVE_USUARIOS);
        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        edtNomeTrabalho=findViewById(R.id.edtNomeTrabalho);
        edtNivelTrabalho=findViewById(R.id.edtNivelTrabalho);
        txtInputEstado=findViewById(R.id.txtLayoutEstadoTrabalho);
        txtInputLicenca=findViewById(R.id.txtLayoutLicencaTrabalho);
        txtInputNome=findViewById(R.id.txtLayoutNomeTrabalho);
        txtInputNivel=findViewById(R.id.txtLayoutNivelTrabalho);
        autoCompleteEstado=findViewById(R.id.txtAutoCompleteEstadoTrabalho);
        autoCompleteLicenca=findViewById(R.id.txtAutoCompleteLicencaTrabalhoEspecifico);
        licencasTrabalho=getResources().getStringArray(R.array.licencas_completas);
        estadosTrabalho = getResources().getStringArray(R.array.estados);
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos=getIntent();

        if (dadosRecebidos.hasExtra(CHAVE_NOTA)){
            int codigoRequisicao = (int) dadosRecebidos
                    .getSerializableExtra(CHAVE_NOTA);
            personagemId= (String) dadosRecebidos.getSerializableExtra(CHAVE_NOME_PERSONAGEM);
            if (codigoRequisicao==2){
                trabalhoRecebido= (Trabalho) dadosRecebidos
                        .getSerializableExtra(CHAVE_NOME_TRABALHO);
                Log.d("trabalho", String.valueOf(trabalhoRecebido.getNivel()));
                configuraComponentes();
            }else if (codigoRequisicao==1){
                setTitle(CHAVE_TITULO_NOVO_TRABALHO);
                raridadeRecebido=(Profissao) dadosRecebidos
                        .getSerializableExtra(CHAVE_NOME_RARIDADE);
                profissaoRecebido=(Profissao) dadosRecebidos
                        .getSerializableExtra(CHAVE_NOME_PROFISSAO);
                txtInputLicenca.setVisibility(View.GONE);
                txtInputEstado.setVisibility(View.GONE);
            }
        }
    }

    private void configuraComponentes() {
        String trabalho=trabalhoRecebido.getNome();
        setTitle(trabalho);
        String estado = estadosTrabalho[trabalhoRecebido.getEstado()];
        String licenca=trabalhoRecebido.getTipo_licenca();
        trabalhoId=trabalhoRecebido.getId();
        autoCompleteEstado.setText(estado);
        autoCompleteLicenca.setText(licenca);
        edtNomeTrabalho.setEnabled(false);
        edtNivelTrabalho.setEnabled(false);
        edtNomeTrabalho.setText(trabalhoRecebido.getNome());
        edtNivelTrabalho.setText(String.valueOf(trabalhoRecebido.getNivel()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario_nota_salva,
                menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemMenuSalvaTrabalho:
                String nome=edtNomeTrabalho.getText().toString().trim();
                String nivel=edtNivelTrabalho.getText().toString().trim();

                if (verificaEdtTrabalho(nome,txtInputNome,0)
                        & verificaEdtTrabalho(nivel,txtInputNivel,1)){
                    cadastraNovoTrabalho(nome, nivel);
                    vaiParaListaTrabalhosEspecificosActivity();
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean verificaEdtTrabalho(String edtTexto,TextInputLayout inputLayout,int posicaoErro) {
        if (edtTexto.isEmpty()){
            inputLayout.setHelperText(mensagemErro[posicaoErro]);
            inputLayout.setHelperTextColor(AppCompatResources.getColorStateList(this,R.color.cor_background_bordo));
            return false;

        }
        inputLayout.setHelperTextEnabled(false);
        return true;
    }

    private void cadastraNovoTrabalho(String nome, String nivel) {
        DatabaseReference minhaReferencia = database.getReference(CHAVE_LISTA_TRABALHO);
        String novoId = geraIdAleatorio();
        Trabalho novoTrabalho=
                new Trabalho(novoId
                        , nome
                        ,profissaoRecebido.getNome()
                        ,""
                        ,raridadeRecebido.getNome()
                        ,0
                        ,Integer.parseInt(nivel));
        minhaReferencia.child(novoId).setValue(novoTrabalho);
    }

    private void vaiParaListaTrabalhosEspecificosActivity() {
        Intent vaiParaListaTrabalhos=
                new Intent(getApplicationContext()
                        ,ListaTrabalhosEspecificosActivity.class);
        vaiParaListaTrabalhos.putExtra(CHAVE_NOME_PERSONAGEM,personagemId);
        vaiParaListaTrabalhos.putExtra(CHAVE_NOME_PROFISSAO,profissaoRecebido);
        vaiParaListaTrabalhos.putExtra(CHAVE_NOME_RARIDADE,raridadeRecebido);
        startActivity(vaiParaListaTrabalhos
                ,ActivityOptions.makeSceneTransitionAnimation(TrabalhoEspecificoActivity.this)
                        .toBundle());
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