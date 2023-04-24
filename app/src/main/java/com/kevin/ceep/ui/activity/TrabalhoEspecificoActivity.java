package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOTA;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_ALTERA_STATUS_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Trabalho;

public class TrabalhoEspecificoActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference minhareferencia;
    private String usuarioId;
    private Trabalho trabalhoRecebido;
    private ProgressDialog progressDialog;
    private EditText edtNomeTrabalho;
    private EditText edtNivelTrabalho;
    private AutoCompleteTextView autoCompleteEstado,autoCompleteLicenca;
    private String[] estadosTrabalho,licencasTrabalho;
    private String personagemId,trabalhoId;

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
            }
        }
    }

    private void configuraComponentes() {
        setTitle(CHAVE_TITULO_ALTERA_STATUS_TRABALHO);
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
}