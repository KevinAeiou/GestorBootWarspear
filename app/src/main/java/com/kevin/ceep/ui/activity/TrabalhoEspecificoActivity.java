package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.ListaPersonagemActivity.geraIdAleatorio;
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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.Raridade;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoProducao;

import java.util.Objects;

public class TrabalhoEspecificoActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference minhareferencia;
    private TrabalhoProducao trabalhoRecebido;
    private Profissao profissaoRecebido;
    private Raridade raridadeRecebido;
    private TextInputEditText edtNomeTrabalho,edtNivelTrabalho,edtExperienciaTrabalho;
    private TextInputLayout txtInputEstado, txtInputLicenca,txtInputNome,txtInputNivel,txtInputExperiencia;
    private CheckBox checkBoxTrabalhoEspecifico;
    private AutoCompleteTextView autoCompleteEstado,autoCompleteLicenca;
    private String[] estadosTrabalho,licencasTrabalho;
    private final String[] mensagemErro={"Campo requerido!","Inválido!"};
    private String usuarioId,personagemId,trabalhoId,licencaModificada,nome,nivel,experiencia;
    private int codigoRequisicao,posicaoEstado=0;
    private boolean recorrencia=true;
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

    private void mostraDialogoDeProresso(int codigoRequisicao){
        ProgressDialog progressDialog = new ProgressDialog(this);
        configuraMensagemProgressDialog(codigoRequisicao, progressDialog);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancelar",(dialogInterface, i) ->
                progressDialog.dismiss());
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Confirmar",(dialogInterface, i) ->
                configuraAcaoTrabalho(codigoRequisicao, progressDialog));
        progressDialog.show();
    }

    private void configuraAcaoTrabalho(int codigoRequisicao, ProgressDialog progressDialog) {
        progressDialog.dismiss();
        if(codigoRequisicao==CODIGO_REQUISICAO_INSERE_TRABALHO){
            cadastraNovoTrabalho();
            vaiParaListaTrabalhosEspecificosActivity();
        }else if(codigoRequisicao==CODIGO_REQUISICAO_ALTERA_TRABALHO){
            TrabalhoProducao trabalhoModificado=new TrabalhoProducao(
                    trabalhoRecebido.getId(),
                    trabalhoRecebido.getNome(),
                    trabalhoRecebido.getProfissao(),
                    trabalhoRecebido.getRaridade(),
                    trabalhoRecebido.getNivel(),
                    trabalhoRecebido.getExperiencia(),
                    licencaModificada,
                    posicaoEstado,
                    recorrencia);
            modificaTrabalhoServidor(trabalhoModificado);
            vaiParaListaTrabalhosActivity();
        }
        finish();
    }

    private void configuraMensagemProgressDialog(int codigoRequisicao, ProgressDialog progressDialog) {
        progressDialog.setCancelable(false);
        if (codigoRequisicao==CODIGO_REQUISICAO_INSERE_TRABALHO){
            progressDialog.setMessage("Salvando novo trabalho...");
        }else if (codigoRequisicao==CODIGO_REQUISICAO_ALTERA_TRABALHO){
            progressDialog.setMessage("Modificando trabalho...");
        }
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
        edtExperienciaTrabalho=findViewById(R.id.edtExperienciaTrabalho);
        txtInputEstado=findViewById(R.id.txtLayoutEstadoTrabalho);
        txtInputLicenca=findViewById(R.id.txtLayoutLicencaTrabalho);
        txtInputNome=findViewById(R.id.txtLayoutNomeTrabalho);
        txtInputNivel=findViewById(R.id.txtLayoutNivelTrabalho);
        txtInputExperiencia=findViewById(R.id.txtLayoutExperienciaTrabalho);
        autoCompleteEstado=findViewById(R.id.txtAutoCompleteEstadoTrabalho);
        autoCompleteLicenca=findViewById(R.id.txtAutoCompleteLicencaTrabalhoEspecifico);
        licencasTrabalho=getResources().getStringArray(R.array.licencas_completas);
        estadosTrabalho=getResources().getStringArray(R.array.estados);
        checkBoxTrabalhoEspecifico=findViewById(R.id.checkBoxTrabalhoEspecifico);
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos=getIntent();

        if (dadosRecebidos.hasExtra(CHAVE_TRABALHO)){
            codigoRequisicao= (int) dadosRecebidos
                    .getSerializableExtra(CHAVE_TRABALHO);
            personagemId= (String) dadosRecebidos.getSerializableExtra(CHAVE_NOME_PERSONAGEM);
            if (codigoRequisicao== CODIGO_REQUISICAO_ALTERA_TRABALHO){
                trabalhoRecebido= (TrabalhoProducao) dadosRecebidos
                        .getSerializableExtra(CHAVE_NOME_TRABALHO);
                configuraComponentesAlteraTrabalho();
            }else if (codigoRequisicao== CODIGO_REQUISICAO_INSERE_TRABALHO){
                raridadeRecebido=(Raridade) dadosRecebidos
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
        checkBoxTrabalhoEspecifico.setVisibility(View.GONE);
    }

    private void configuraComponentesAlteraTrabalho() {
        setTitle(trabalhoRecebido.getNome());
        edtNomeTrabalho.setEnabled(false);
        edtNivelTrabalho.setEnabled(false);
        edtExperienciaTrabalho.setEnabled(false);
        trabalhoId=trabalhoRecebido.getId();
        autoCompleteEstado.setText(estadosTrabalho[trabalhoRecebido.getEstado()]);
        edtNivelTrabalho.setText(String.valueOf(trabalhoRecebido.getNivel()));
        edtExperienciaTrabalho.setText(String.valueOf(trabalhoRecebido.getExperiencia()));
        autoCompleteLicenca.setText(trabalhoRecebido.getTipo_licenca());
        edtNomeTrabalho.setText(trabalhoRecebido.getNome());
        posicaoEstado=trabalhoRecebido.getEstado();
        licencaModificada=trabalhoRecebido.getTipo_licenca();
        if (trabalhoRecebido.getRecorrencia()){
            checkBoxTrabalhoEspecifico.setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario_nota_salva,
                menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.itemMenuSalvaTrabalho) {
            if (codigoRequisicao==CODIGO_REQUISICAO_ALTERA_TRABALHO) {
                if (verificaTrabalhoModificado()) {
                    mostraDialogoDeProresso(CODIGO_REQUISICAO_ALTERA_TRABALHO);
                }else{
                    vaiParaListaTrabalhosActivity();
                    finish();
                }
            } else if (codigoRequisicao==CODIGO_REQUISICAO_INSERE_TRABALHO) {
                nome=Objects.requireNonNull(edtNomeTrabalho.getText()).toString().trim();
                nivel=Objects.requireNonNull(edtNivelTrabalho.getText()).toString().trim();
                experiencia=Objects.requireNonNull(edtExperienciaTrabalho.getText()).toString().trim();

                if (verificaCamposNovoTrabalho()) {
                    mostraDialogoDeProresso(CODIGO_REQUISICAO_INSERE_TRABALHO);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean verificaCamposNovoTrabalho() {
        return verificaEdtTrabalho(nome, txtInputNome, 0)
                & verificaEdtTrabalho(nivel, txtInputNivel, 1)
                & verificaEdtTrabalho(experiencia,txtInputExperiencia,1);
    }

    private boolean verificaTrabalhoModificado() {
        return !(licencaModificada.equals(trabalhoRecebido.getTipo_licenca())) ||
                posicaoEstado!=trabalhoRecebido.getEstado()||
                verificaCheckModificado();
    }

    private boolean verificaCheckModificado() {
        configuraCheckBoxRecorrencia();
        return recorrencia != trabalhoRecebido.getRecorrencia();
    }

    private void configuraCheckBoxRecorrencia() {
        recorrencia = checkBoxTrabalhoEspecifico.isChecked();
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

    private void cadastraNovoTrabalho() {
        DatabaseReference minhaReferencia = database.getReference(CHAVE_LISTA_TRABALHO);
        String novoId = geraIdAleatorio();
        Trabalho novoTrabalho = new Trabalho(
                novoId,
                nome,
                profissaoRecebido.getNome(),
                raridadeRecebido.getNome(),
                Integer.parseInt(nivel),
                Integer.parseInt(experiencia));
        minhaReferencia.child(novoId).setValue(novoTrabalho);
    }

    private void vaiParaListaTrabalhosEspecificosActivity() {
        Intent vaiParaListaTrabalhos=
                new Intent(getApplicationContext()
                        ,ListaTrabalhosEspecificosActivity.class);
        vaiParaListaTrabalhos.putExtra(CHAVE_NOME_PERSONAGEM,personagemId);
        vaiParaListaTrabalhos.putExtra(CHAVE_NOME_PROFISSAO,profissaoRecebido);
        vaiParaListaTrabalhos.putExtra(CHAVE_NOME_RARIDADE,raridadeRecebido);
        setResult(1,vaiParaListaTrabalhos);
        TrabalhoEspecificoActivity.super.onBackPressed();
    }

    private void vaiParaListaTrabalhosActivity() {
        Intent vaiParaListaTrabalhos=
                new Intent(getApplicationContext()
                        ,ListaTrabalhosActivity.class);
        vaiParaListaTrabalhos.putExtra(CHAVE_NOME_PERSONAGEM,personagemId);
        setResult(1,vaiParaListaTrabalhos);
        TrabalhoEspecificoActivity.super.onBackPressed();
    }
}