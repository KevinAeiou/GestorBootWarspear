package com.kevin.ceep.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kevin.ceep.R;
import com.kevin.ceep.model.Usuario;

public class CadastrarUsuarioActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatButton botaoCadastrarUsuario;
    private TextInputLayout txtSenha;
    private TextInputEditText edtNome, edtEmail, edtSenha;
    String[] menssagens = {"Preencha todos os campos", "Usuário cadastrado com sucesso!"};
    String usuarioId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        txtSenha = findViewById(R.id.txtSenha);
        edtSenha = findViewById(R.id.edtSenha);

        configuraEdtSenhaRobusta();

        botaoCadastrarUsuario = findViewById(R.id.botaoCadastrarUsuario);
        TextView txtEntrar = findViewById(R.id.txtLinkEntrar);

        botaoCadastrarUsuario.setOnClickListener(this);
        txtEntrar.setOnClickListener(this);
    }

    private void configuraEdtSenhaRobusta() {


        edtSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String senha = edtSenha.getText().toString();
                verificaSenhaRobusta(senha);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void verificaSenhaRobusta(String senha) {
        int tamanhoSenha = senha.length();

        String upperCaseChars = getString(R.string.stringCasoChaMa);
        String lowerCaseChars = getString(R.string.stringCasoCharMi);
        String numbers = getString(R.string.stringCasoCharNum);
        String especial = getString(R.string.stringCasoCharS);

        Log.d("SENHA", String.valueOf(tamanhoSenha));

        if (configuraEditSenha(tamanhoSenha>=8)
                & configuraEditSenha(senha.matches(especial))
                & configuraEditSenha(senha.matches(numbers))
                & configuraEditSenha(senha.matches(lowerCaseChars))
                & configuraEditSenha(senha.matches(upperCaseChars))){

            habilitaBotaoCadastro();
        }else {
            configuraMenssagemAjuda(senha, tamanhoSenha, upperCaseChars, lowerCaseChars, numbers, especial);
        }
    }

    private void habilitaBotaoCadastro() {
        txtSenha.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#007FFF")));
        txtSenha.setBoxStrokeColor(Color.parseColor("#007FFF"));
        txtSenha.setHelperTextEnabled(false);
        botaoCadastrarUsuario.setEnabled(true);
    }

    private void configuraMenssagemAjuda(String senha, int tamanhoSenha, String upperCaseChars, String lowerCaseChars, String numbers, String especial) {
        if (!configuraEditSenha(tamanhoSenha >= 8)) {
            txtSenha.setHelperText(getString(R.string.string_senha_curta));
        }
        if (!configuraEditSenha(senha.matches(numbers))) {
            txtSenha.setHelperText(getString(R.string.string_senha_numerica));
        }
        if (!configuraEditSenha(senha.matches(lowerCaseChars))) {
            txtSenha.setHelperText(getString(R.string.string_senha_minuscula));
        }
        if (!configuraEditSenha(senha.matches(upperCaseChars))) {
            txtSenha.setHelperText(getString(R.string.string_senha_maiuscula));
        }
        if (!configuraEditSenha(senha.matches(especial))) {
            txtSenha.setHelperText(getString(R.string.string_senha_especial));
        }
    }

    @SuppressLint("NewApi")
    private boolean configuraEditSenha(boolean senha) {

        if (!senha){
            txtSenha.setHintTextColor(ColorStateList.valueOf(getColor(R.color.cor_background_bordo)));
            txtSenha.setBoxStrokeColor(Color.parseColor("#A71500"));
            txtSenha.setHelperTextColor(ColorStateList.valueOf(getColor(R.color.cor_background_bordo)));
            txtSenha.setHelperTextEnabled(true);
            botaoCadastrarUsuario.setEnabled(false);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtLinkEntrar:
                startActivity(new Intent(this,EntrarUsuarioActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            case R.id.botaoCadastrarUsuario:
                cadastrarUsuario(view);
        }
    }

    private void cadastrarUsuario(View view) {

        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);

        String nome = edtNome.getText().toString();
        String email = edtEmail.getText().toString();
        String senha = edtSenha.getText().toString();

        if (!verificaCampos(nome,email,senha)){
            Snackbar snackbar = Snackbar.make(view, menssagens[0], Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
        }else{
            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email,senha)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            salvarDadosUsuario();
                            startActivity(new Intent(getApplicationContext(), EntrarUsuarioActivity.class),
                                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                            finish();
                        }else{
                            String erro;
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                erro = "A senha deve conter no mínimo 8 caracteres!";
                            } catch (FirebaseAuthUserCollisionException e){
                                erro = "Conta já cadastrada!";
                            }catch (FirebaseAuthInvalidCredentialsException e) {
                                erro = "Email inválido!";
                            }catch (Exception e) {
                                erro = "Erro ao cadastrar usuário";
                            }

                            Snackbar snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.WHITE);
                            snackbar.setTextColor(Color.BLACK);
                            snackbar.show();
                        }
                    });
        }
    }

    private void salvarDadosUsuario() {
        String nome  = edtNome.getText().toString();
        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Usuario usuario = new Usuario(usuarioId,nome);
        Log.d("CADASTRO",usuario.getId());
        Log.d("CADASTRO",usuario.getNome());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhareferencia = database.getReference("Usuarios");

        minhareferencia.child(usuarioId).setValue(usuario);

    }

    private boolean verificaCampos(String nome, String email, String senha) {
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()){
            return false;
        }
        return true;
    }
}