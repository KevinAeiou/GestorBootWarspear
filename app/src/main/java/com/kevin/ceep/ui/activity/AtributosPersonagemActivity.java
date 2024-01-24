package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;

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
    private String usuarioId,personagemId;
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
}