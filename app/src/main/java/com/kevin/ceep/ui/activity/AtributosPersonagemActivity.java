package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.utilitario.Utilitario.geraIdAleatorio;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_REQUISICAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_ALTERA_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_TRABALHO;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kevin.ceep.R;
import com.kevin.ceep.databinding.ActivityAtributosPersonagemBinding;
import com.kevin.ceep.model.Personagem;

import java.util.Objects;

public class AtributosPersonagemActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference minhareferencia;
    private Personagem personagemRecebido;
    private TextInputLayout personagemNomeTxt, personagemEspacoProducaoTxt, personagemEmailTxt, personagemSenhaTxt;
    private EditText personagemNome, personagemEspacoProducao, personagemEmail, personagemSenha;
    private SwitchCompat personagemSwUso, personagemSwEstado;
    private String usuarioId;
    private ActivityAtributosPersonagemBinding binding;
    private int codigoRequisicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAtributosPersonagemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        inicializaComponentes();
        recebeDadosIntent();
    }
    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_PERSONAGEM)){
            codigoRequisicao = (int) dadosRecebidos.getSerializableExtra(CHAVE_REQUISICAO);
            if (codigoRequisicao == CODIGO_REQUISICAO_INSERE_TRABALHO){

            } else if (codigoRequisicao == CODIGO_REQUISICAO_ALTERA_TRABALHO) {
                personagemRecebido = (Personagem) dadosRecebidos.getSerializableExtra(CHAVE_PERSONAGEM);
                if (personagemRecebido != null){
                    preencheCampos();
                }
            }
        }
    }

    private void preencheCampos() {
        personagemNome.setText(personagemRecebido.getNome());
        personagemEspacoProducao.setText(String.valueOf(personagemRecebido.getEspacoProducao()));
        personagemEmail.setText(personagemRecebido.getEmail());
        personagemSenha.setText(personagemRecebido.getSenha());
        if (personagemRecebido.getUso()){
            personagemSwUso.setChecked(true);
        }
        if (personagemRecebido.getEstado()){
            personagemSwEstado.setChecked(true);
        }
    }

    private void inicializaComponentes() {
        database = FirebaseDatabase.getInstance();
        minhareferencia = database.getReference(CHAVE_USUARIOS);
        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        personagemNome = binding.edtNomePersonagem;
        personagemNomeTxt = binding.txtNomePersonagem;
        personagemEspacoProducao = binding.edtEspacoProducaoPersonagem;
        personagemEspacoProducaoTxt = binding.txtEspacoProducaoPersonagem;
        personagemSwUso = binding.swUsoPersonagem;
        personagemSwEstado = binding.swEstadoPersonagem;
        personagemEmail = binding.edtEmailPersonagem;
        personagemEmailTxt = binding.txtEmailPersonagem;
        personagemSenha = binding.edtSenhaPersonagem;
        personagemSenhaTxt = binding.txtSenhaPersonagem;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario_nota_salva, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.itemMenuSalvaTrabalho) {
            if (codigoRequisicao == CODIGO_REQUISICAO_ALTERA_TRABALHO) {
                if (verifcaPersonagemModificado()) {
                    Snackbar.make(binding.constraintLayoutAtributosPersonagem, "Personagem modificado", Snackbar.LENGTH_LONG).show();
                    MaterialAlertDialogBuilder dialogoDeAlerta = new MaterialAlertDialogBuilder(this);
                    dialogoDeAlerta.setMessage("Deseja confirmar alterações?");
                    dialogoDeAlerta.setNegativeButton("Não", ((dialogInterface, i) -> {
                        vaiParaFragmentoPersonagens();
                    }));
                    dialogoDeAlerta.setPositiveButton("Sim", (dialogInterface, i) -> {
                        modificaPersonagemServidor();
                        vaiParaFragmentoPersonagens();
                    });
                    dialogoDeAlerta.show();
                } else {
                    vaiParaFragmentoPersonagens();
                }
            } else if (codigoRequisicao == CODIGO_REQUISICAO_INSERE_TRABALHO) {
                if (verificaCampos()){
                    String novoId = geraIdAleatorio();
                    Personagem novoPersonagem = new Personagem(
                            novoId,
                            personagemNome.getText().toString(),
                            personagemEmail.getText().toString(),
                            personagemSenha.getText().toString(),
                            personagemSwEstado.isChecked(),
                            personagemSwUso.isChecked(),
                            Integer.parseInt(personagemEspacoProducao.getText().toString())
                    );
                    minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).child(novoId).setValue(novoPersonagem);
                    vaiParaFragmentoPersonagens();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean verificaCampos() {
        boolean confirmacao = true;
        if (personagemNome.getText().toString().isEmpty()){
            personagemNomeTxt.setHelperText("Campo requerido!");
            confirmacao = false;
        }else {
            personagemNomeTxt.setHelperTextEnabled(false);
        }
        if (personagemEspacoProducao.getText().toString().isEmpty()){
            personagemEspacoProducaoTxt.setHelperText("Campo requerido!");
            confirmacao = false;
        }else {
            personagemEspacoProducaoTxt.setHelperTextEnabled(false);
        }
        if (personagemEmail.getText().toString().isEmpty()){
            personagemEmailTxt.setHelperText("Campo requerido!");
            confirmacao = false;
        }else {
            personagemEmailTxt.setHelperTextEnabled(false);
        }
        if (personagemSenha.getText().toString().isEmpty()){
            personagemSenhaTxt.setHelperText("Campo requerido!");
            confirmacao = false;
        }else {
            personagemSenhaTxt.setHelperTextEnabled(false);
        }
        return confirmacao;
    }

    @Override
    public void onBackPressed() {
        if (codigoRequisicao == CODIGO_REQUISICAO_ALTERA_TRABALHO) {
            if (verifcaPersonagemModificado()) {
                Snackbar.make(binding.constraintLayoutAtributosPersonagem, "Personagem modificado", Snackbar.LENGTH_LONG).show();
                MaterialAlertDialogBuilder dialogoDeAlerta = new MaterialAlertDialogBuilder(this);
                dialogoDeAlerta.setMessage("Deseja descartar alterações?");
                dialogoDeAlerta.setNegativeButton("Não", ((dialogInterface, i) -> {
                    modificaPersonagemServidor();
                    vaiParaFragmentoPersonagens();
                    super.onBackPressed();
                }));
                dialogoDeAlerta.setPositiveButton("Sim", (dialogInterface, i) -> {
                    vaiParaFragmentoPersonagens();
                    super.onBackPressed();
                });
                dialogoDeAlerta.show();
            } else {
                super.onBackPressed();
            }
        } else if (codigoRequisicao == CODIGO_REQUISICAO_INSERE_TRABALHO) {
            super.onBackPressed();
        }
    }

    private void modificaPersonagemServidor() {
        if (!(personagemNome.getText().toString().equals(personagemRecebido.getNome()))){
            minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemRecebido.getId()).child("nome").setValue(personagemNome.getText().toString());
        }
        if (!(personagemEspacoProducao.getText().toString().equals(String.valueOf(personagemRecebido.getEspacoProducao())))){
            minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemRecebido.getId()).child("espacoProducao").setValue(Integer.valueOf(personagemEspacoProducao.getText().toString()));
        }
        if (!(personagemEmail.getText().toString().equals(personagemRecebido.getEmail()))){
            minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemRecebido.getId()).child("email").setValue(personagemEmail.getText().toString());
        }
        if (!(personagemSenha.getText().toString().equals(personagemRecebido.getSenha()))){
            minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemRecebido.getId()).child("senha").setValue(personagemSenha.getText().toString());
        }
        if (personagemSwUso.isChecked()!=personagemRecebido.getUso()){
            minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemRecebido.getId()).child("uso").setValue(personagemSwUso.isChecked());
        }
        if (personagemSwEstado.isChecked()!=personagemRecebido.getEstado()){
            minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemRecebido.getId()).child("estado").setValue(personagemSwEstado.isChecked());
        }

    }
    private void vaiParaFragmentoPersonagens() {
        Intent iniciaVaiParaFragmentoPersonagens = new Intent(getApplicationContext(), MenuNavegacaoLateral.class);
        setResult(1,iniciaVaiParaFragmentoPersonagens);
        AtributosPersonagemActivity.super.onBackPressed();
    }

    private boolean verifcaPersonagemModificado() {
        return !(personagemNome.getText().toString().equals(personagemRecebido.getNome()))||
                personagemSwUso.isChecked()!=personagemRecebido.getUso()||
                personagemSwEstado.isChecked()!=personagemRecebido.getEstado()||
                !(personagemEspacoProducao.getText().toString().equals(String.valueOf(personagemRecebido.getEspacoProducao())))||
                !(personagemEmail.getText().toString().equals(personagemRecebido.getEmail()))||
                !(personagemSenha.getText().toString().equals(personagemRecebido.getSenha()));
    }
}