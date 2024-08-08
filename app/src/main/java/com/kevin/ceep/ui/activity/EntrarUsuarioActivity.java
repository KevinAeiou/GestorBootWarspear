package com.kevin.ceep.ui.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kevin.ceep.R;
import com.kevin.ceep.databinding.ActivityEntrarUsuarioBinding;

import java.util.Objects;

public class EntrarUsuarioActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityEntrarUsuarioBinding binding;
    private TextInputEditText edtEmail, edtSenha;
    private TextInputLayout txtEmail, txtSenha;
    private TextView txtCadastrar, txtRecuperarSenha;
    private AppCompatButton botao_entrar;
    String [] menssagens = {"Campo requerido!", "Login efetuado com sucesso!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEntrarUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        inicializaComponentes();

        botao_entrar.setOnClickListener(this);
        txtCadastrar.setOnClickListener(this);
        txtRecuperarSenha.setOnClickListener(this);
    }

    private void inicializaComponentes() {
        botao_entrar = binding.botaoEntrar;
        txtCadastrar = binding.txtLinkCadastro;
        txtRecuperarSenha = binding.txtEsqueceuSenha;
        txtEmail = binding.txtEmail;
        txtSenha = binding.txtSenha;
        edtEmail = binding.edtEmail;
        edtSenha = binding.edtSenha;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.botaoEntrar:
                botao_entrar.setEnabled(false);
                entrarUsuario();
                break;
            case R.id.txtLinkCadastro:
                vaiParaCadastroUsuarioActivity();
                break;
            case R.id.txtEsqueceuSenha:
                vaiParaRecuperarSenhaActivity();
                break;
        }
    }

    private void vaiParaRecuperarSenhaActivity() {
        Intent iniciaVaiParaRecuperarSenhaActivity = new Intent(this, RecuperarSenhaActivity.class);
        startActivity(iniciaVaiParaRecuperarSenhaActivity,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private void vaiParaCadastroUsuarioActivity() {
        Intent iniciaVaiParaCadastroUsuarioActivity = new Intent(this, CadastrarUsuarioActivity.class);
        startActivity(iniciaVaiParaCadastroUsuarioActivity,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private void entrarUsuario() {
        String email = Objects.requireNonNull(edtEmail.getText()).toString();
        String senha = Objects.requireNonNull(edtSenha.getText()).toString();

        if (camposVazios(email, senha)){
            configuraErrosCampos(email, senha);
        }else {
            txtEmail.setHelperTextEnabled(false);
            txtSenha.setHelperTextEnabled(false);
            autenticarUsuario(email, senha);
        }

    }

    private void configuraErrosCampos(String email, String senha) {
        if (email.isEmpty()) {
            txtEmail.setHelperText(menssagens[0]);
        } else {
            txtEmail.setHelperTextEnabled(false);
        }
        if (senha.isEmpty()) {
            txtSenha.setHelperText(menssagens[0]);
        } else {
            txtSenha.setHelperTextEnabled(false);
        }
        botao_entrar.setEnabled(true);
    }

    private static boolean camposVazios(String email, String senha) {
        return email.isEmpty() || senha.isEmpty();
    }

    private void autenticarUsuario(String email, String senha) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        vaiParaMenuNavegacao();
                    } else {
                        configuraErroExecoesCampos(Objects.requireNonNull(task.getException()));
                    }
                });
    }

    private void configuraErroExecoesCampos(Exception exception) {
        botao_entrar.setEnabled(true);
        switch (Objects.requireNonNull(exception.getMessage())){
            case "The email address is badly formatted.":
            case "There is no user record corresponding to this identifier. The user may have been deleted.":
                txtEmail.setHelperText("Email inválido!");
                txtSenha.setHelperTextEnabled(false);
                break;
            case "The password is invalid or the user does not have a password.":
                txtSenha.setHelperText("Senha inválida!");
                txtEmail.setHelperTextEnabled(false);
                break;
            case "A network error (such as timeout, interrupted connection or unreachable host) has occurred.":
                Snackbar.make(binding.getRoot(), "Sem conexão com a internet!", Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private void vaiParaMenuNavegacao() {
        Intent vaiParaMenuNavegacao =  new Intent(getApplicationContext(), MainActivity.class);
        startActivity(vaiParaMenuNavegacao,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioAtual != null){
            vaiParaMenuNavegacao();
        }
    }
}