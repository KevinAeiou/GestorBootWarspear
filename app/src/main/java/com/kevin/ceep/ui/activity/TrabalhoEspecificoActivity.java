package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PROFISSAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_RARIDADE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_NOVO_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_ALTERA_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_TRABALHO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.Objects;

public class TrabalhoEspecificoActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference minhareferencia;
    private Trabalho trabalhoRecebido;
    private Profissao raridadeRecebido,profissaoRecebido;
    private TextInputEditText edtNomeTrabalho,edtNivelTrabalho;
    private TextInputLayout txtInputEstado, txtInputLicenca,txtInputNome,txtInputNivel;
    private AutoCompleteTextView autoCompleteEstado,autoCompleteLicenca;
    private String[] estadosTrabalho,licencasTrabalho;
    private final String[] mensagemErro={"Campo requerido!","Inválido!"};
    private String usuarioId,personagemId,trabalhoId,licencaModificada;
    private int codigoRequisicao,posicaoEstado=0;

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
        autoCompleteEstado.setOnItemClickListener((adapterView, view, i, l) -> posicaoEstado=i);
        autoCompleteLicenca.setOnItemClickListener((adapterView, view, i, l) -> licencaModificada=licencasTrabalho[i]);
    }

    private void mostraDialogodeProresso(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Modificando estado...");
        progressDialog.show();
    }

    private void configuraDropdownEstados() {
        ArrayAdapter<String> adapterLicenca= new ArrayAdapter<>(this,
                R.layout.item_dropdrown, licencasTrabalho);
        ArrayAdapter<String> adapterEstado= new ArrayAdapter<>(this,
                R.layout.item_dropdrown, estadosTrabalho);
        adapterLicenca.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteLicenca.setAdapter(adapterLicenca);
        autoCompleteEstado.setAdapter(adapterEstado);
    }

    private void inicializaComponentes() {
        database=FirebaseDatabase.getInstance();
        minhareferencia=database.getReference(CHAVE_USUARIOS);
        usuarioId= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        edtNomeTrabalho=findViewById(R.id.edtNomeTrabalho);
        edtNivelTrabalho=findViewById(R.id.edtNivelTrabalho);
        txtInputEstado=findViewById(R.id.txtLayoutEstadoTrabalho);
        txtInputLicenca=findViewById(R.id.txtLayoutLicencaTrabalho);
        txtInputNome=findViewById(R.id.txtLayoutNomeTrabalho);
        txtInputNivel=findViewById(R.id.txtLayoutNivelTrabalho);
        autoCompleteEstado=findViewById(R.id.txtAutoCompleteEstadoTrabalho);
        autoCompleteLicenca=findViewById(R.id.txtAutoCompleteLicencaTrabalhoEspecifico);
        licencasTrabalho=getResources().getStringArray(R.array.licencas_completas);
        estadosTrabalho=getResources().getStringArray(R.array.estados);
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos=getIntent();

        if (dadosRecebidos.hasExtra(CHAVE_TRABALHO)){
            codigoRequisicao= (int) dadosRecebidos
                    .getSerializableExtra(CHAVE_TRABALHO);
            personagemId= (String) dadosRecebidos.getSerializableExtra(CHAVE_NOME_PERSONAGEM);
            if (codigoRequisicao== CODIGO_REQUISICAO_ALTERA_TRABALHO){
                trabalhoRecebido= (Trabalho) dadosRecebidos
                        .getSerializableExtra(CHAVE_NOME_TRABALHO);
                configuraComponentesAlteraTrabalho();
            }else if (codigoRequisicao== CODIGO_REQUISICAO_INSERE_TRABALHO){
                raridadeRecebido=(Profissao) dadosRecebidos
                        .getSerializableExtra(CHAVE_NOME_RARIDADE);
                profissaoRecebido=(Profissao) dadosRecebidos
                        .getSerializableExtra(CHAVE_NOME_PROFISSAO);
                configuraLayoutNovoTrabalho();
            }
        }
    }

    private void configuraLayoutNovoTrabalho() {
        setTitle(CHAVE_TITULO_NOVO_TRABALHO);
        txtInputLicenca.setVisibility(View.GONE);
        txtInputEstado.setVisibility(View.GONE);
    }

    private void configuraComponentesAlteraTrabalho() {
        setTitle(trabalhoRecebido.getNome());
        edtNivelTrabalho.setEnabled(false);
        edtNomeTrabalho.setEnabled(false);
        trabalhoId=trabalhoRecebido.getId();
        autoCompleteEstado.setText(estadosTrabalho[trabalhoRecebido.getEstado()]);
        edtNivelTrabalho.setText(String.valueOf(trabalhoRecebido.getNivel()));
        autoCompleteLicenca.setText(trabalhoRecebido.getTipo_licenca());
        edtNomeTrabalho.setText(trabalhoRecebido.getNome());
        posicaoEstado=trabalhoRecebido.getEstado();
        licencaModificada=trabalhoRecebido.getTipo_licenca();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario_nota_salva,
                menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemMenuSalvaTrabalho) {
            if (codigoRequisicao == CODIGO_REQUISICAO_ALTERA_TRABALHO) {
                if (verificaTrabalhoModificado()) {
                    Trabalho trabalhoModificado=configuraTrabalho(trabalhoId,
                            trabalhoRecebido.getNome(),
                            trabalhoRecebido.getProfissao(),
                            licencaModificada,
                            trabalhoRecebido.getRaridade(),
                            posicaoEstado,
                            trabalhoRecebido.getNivel());
                    modificaTrabalhoServidor(trabalhoModificado);
                }
                vaiParaListaTrabalhosActivity();
            } else if (codigoRequisicao == CODIGO_REQUISICAO_INSERE_TRABALHO) {
                String nome = Objects.requireNonNull(edtNomeTrabalho.getText()).toString().trim();
                String nivel = Objects.requireNonNull(edtNivelTrabalho.getText()).toString().trim();

                if (verificaCamposNovoTrabalho(nome, nivel)) {
                    cadastraNovoTrabalho(nome, nivel);
                    vaiParaListaTrabalhosEspecificosActivity();
                }
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean verificaCamposNovoTrabalho(String nome, String nivel) {
        return verificaEdtTrabalho(nome, txtInputNome, 0)
                & verificaEdtTrabalho(nivel, txtInputNivel, 1);
    }

    private boolean verificaTrabalhoModificado() {
        return !(licencaModificada.equals(trabalhoRecebido.getTipo_licenca())) ||
                posicaoEstado!=trabalhoRecebido.getEstado();
    }

    @NonNull
    private Trabalho configuraTrabalho(String id, String nome,String profissao, String licenca,String raridade, int estado, int nivel) {
        return new Trabalho(id,nome,profissao,licenca,raridade,estado,nivel);
    }

    private void modificaTrabalhoServidor(Trabalho trabalhoModificado) {
        minhareferencia.child(usuarioId).child(CHAVE_PERSONAGEM)
                .child(personagemId).child(CHAVE_LISTA_DESEJO)
                .child(trabalhoId).setValue(trabalhoModificado);
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
        Trabalho novoTrabalho=configuraTrabalho(novoId,nome,profissaoRecebido.getNome(),"",raridadeRecebido.getNome(),0,Integer.parseInt(nivel));
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

    private void vaiParaListaTrabalhosActivity() {
        Intent vaiParaListaTrabalhos=
                new Intent(getApplicationContext()
                        ,ListaTrabalhosActivity.class);
        vaiParaListaTrabalhos.putExtra(CHAVE_NOME_PERSONAGEM,personagemId);
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