package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kevin.ceep.R;
import com.kevin.ceep.databinding.ActivityAtributosPersonagemBinding;
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.model.Profissao;
import com.kevin.ceep.model.Raridade;
import com.kevin.ceep.model.TrabalhoProducao;

import java.util.Objects;

public class AtributosPersonagemActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference minhareferencia;
    private Personagem personagemRecebido;
    private EditText personagemNome, personagemEspacoProducao, personagemEmail, personagemSenha;
    private SwitchCompat personagemSwUso, personagemSwEstado;
    private String usuarioId;
    private ActivityAtributosPersonagemBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAtributosPersonagemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        inicializaComponentes();
        recebeDadosIntent();
        preencheCampos();
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

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_PERSONAGEM)){
            personagemRecebido = (Personagem) dadosRecebidos.getSerializableExtra(CHAVE_PERSONAGEM);
        }
    }

    private void inicializaComponentes() {
        database = FirebaseDatabase.getInstance();
        minhareferencia = database.getReference(CHAVE_USUARIOS);
        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        personagemNome = binding.edtNomePersonagem;
        personagemEspacoProducao = binding.edtEspacoProducaoPersonagem;
        personagemSwUso = binding.swUsoPersonagem;
        personagemSwEstado = binding.swEstadoPersonagem;
        personagemEmail = binding.edtEmailPersonagem;
        personagemSenha = binding.edtSenhaPersonagem;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario_nota_salva, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.itemMenuSalvaTrabalho) {
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
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
        }else {
            super.onBackPressed();
        }
    }

    private void modificaPersonagemServidor() {
        if (!(personagemNome.getText().toString().equals(personagemRecebido.getNome()))){
            minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemRecebido.getId()).setValue(personagemNome.getText().toString());
        }
        if (!(personagemEspacoProducao.getText().toString().equals(String.valueOf(personagemRecebido.getEspacoProducao())))){
            minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemRecebido.getId()).setValue(Integer.valueOf(personagemEspacoProducao.getText().toString()));
        }
        if (!(personagemEmail.getText().toString().equals(personagemRecebido.getEmail()))){
            minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemRecebido.getId()).setValue(Integer.valueOf(personagemEmail.getText().toString()));
        }
        if (!(personagemSenha.getText().toString().equals(personagemRecebido.getSenha()))){
            minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemRecebido.getId()).setValue(Integer.valueOf(personagemSenha.getText().toString()));
        }
        if (personagemSwUso.isChecked()!=personagemRecebido.getUso()){
            minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemRecebido.getId()).setValue(personagemSwUso.isChecked());
        }
        if (personagemSwEstado.isChecked()!=personagemRecebido.getUso()){
            minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemRecebido.getId()).setValue(personagemSwEstado.isChecked());
        }

    }
    private void vaiParaFragmentoPersonagens() {
        Intent vaiParaFragmentoPersonagens = new Intent(getApplicationContext(), MenuNavegacaoLateral.class);
        //vaiParaFragmentoPersonagens.putExtra();
        setResult(1,vaiParaFragmentoPersonagens);
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