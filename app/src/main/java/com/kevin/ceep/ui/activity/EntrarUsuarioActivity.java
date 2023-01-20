package com.kevin.ceep.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kevin.ceep.R;

public class EntrarUsuarioActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText edtEmail, edtSenha;
    private ProgressDialog progressDialog;
    String [] menssagens = {"Preencha todos os campos!", "Login efetuado com sucesso!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrar_usuario);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        AppCompatButton botao_entrar = findViewById(R.id.botaoEntrar);
        TextView txtCadastrar = findViewById(R.id.txtLinkCadastro);
        TextView txtRecuperarSenha = findViewById(R.id.txtEsqueceuSenha);

        botao_entrar.setOnClickListener(this);
        txtCadastrar.setOnClickListener(this);
        txtRecuperarSenha.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.botaoEntrar:
                entrarUsuario(view);
                break;
            case R.id.txtLinkCadastro:
                startActivity(new Intent(this, CadastrarUsuarioActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            case R.id.txtEsqueceuSenha:
                startActivity(new Intent(this, RecuperarSenhaActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
        }
    }

    private void entrarUsuario(View view) {
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);

        String email = edtEmail.getText().toString();
        String senha = edtSenha.getText().toString();

        if (email.isEmpty() || senha.isEmpty()){
            Snackbar snackbar = Snackbar.make(view, menssagens[0], Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
        }else {
            autenticarUsuario(view);
        }

    }

    private void mostraDialogoProgresso() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Carregando dados...");
        progressDialog.show();
    }

    private void autenticarUsuario(View view) {
        String email = edtEmail.getText().toString();
        String senha = edtSenha.getText().toString();

        mostraDialogoProgresso();
        entrarContaUsuario(view,email,senha);
    }

    private void entrarContaUsuario(View view, String email, String senha) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        new Handler().postDelayed(() -> vaiParaListaTrabalhos(), 3000);
                    }else {
                        String erro;
                        try{
                            throw task.getException();
                        }catch (Exception e){
                            erro = "Erro ao entrar!";
                        }
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar.make(view
                        ,erro,Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();
                    }
                });
    }

    private void vaiParaListaTrabalhos() {
        Intent vaiParaListaTrabalho =  new Intent(getApplicationContext(), ListaPersonagemActivity.class);
        startActivity(vaiParaListaTrabalho,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioAtual != null){
            vaiParaListaTrabalhos();
        }
    }
}