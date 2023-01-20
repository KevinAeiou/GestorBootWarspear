package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_RECUPERA_SENHA;

import androidx.annotation.NonNull;
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
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.kevin.ceep.R;

public class RecuperarSenhaActivity extends AppCompatActivity {

    private TextInputLayout txtRecuperaSenha;
    private TextInputEditText edtRecuperaSenha;
    private AppCompatButton botaoRecuperarSenha;
    private FirebaseAuth autenticacao;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);
        setTitle(CHAVE_TITULO_RECUPERA_SENHA);

        autenticacao = FirebaseAuth.getInstance();
        botaoRecuperarSenha = findViewById(R.id.botaoRecuperarSenha);
        txtRecuperaSenha = findViewById(R.id.txtRecuperarSenha);
        edtRecuperaSenha = findViewById(R.id.edtRecuperarSenha);
        edtRecuperaSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email = edtRecuperaSenha.getText().toString().trim();
                Log.d("EMAIL",email);
                verificaEmailValido(email);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        botaoRecuperarSenha.setOnClickListener(view -> recuperaSenha());

    }

    @SuppressLint("NewApi")
    private void verificaEmailValido(String email) {
        if(configuraEditEmail(!email.isEmpty()) & configuraEditEmail(Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            habilitaBotaoRecuperaSenha();
        }else {
            configuraMenssagemAjuda(email);
        }
    }

    private void configuraMenssagemAjuda(String email) {

        if (!configuraEditEmail(Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            txtRecuperaSenha.setHelperText("Por favor, informe um email válido!");
        }
        if (!configuraEditEmail(!email.isEmpty()) & email.length()<1){
            txtRecuperaSenha.setHelperText("Campo requerido!");
        }
    }

    private void habilitaBotaoRecuperaSenha() {
        txtRecuperaSenha.setHintTextColor(ColorStateList.valueOf(Color.parseColor("#007FFF")));
        txtRecuperaSenha.setBoxStrokeColor(Color.parseColor("#007FFF"));
        txtRecuperaSenha.setHelperTextEnabled(false);
        botaoRecuperarSenha.setEnabled(true);
    }

    @SuppressLint("NewApi")
    private boolean configuraEditEmail(boolean email) {
        if (!email){
            txtRecuperaSenha.setHintTextColor(ColorStateList.valueOf(getColor(R.color.cor_background_bordo)));
            txtRecuperaSenha.setBoxStrokeColor(Color.parseColor("#A71500"));
            txtRecuperaSenha.setHelperTextColor(ColorStateList.valueOf(getColor(R.color.cor_background_bordo)));
            txtRecuperaSenha.setHelperTextEnabled(true);
            botaoRecuperarSenha.setEnabled(false);
            return false;
        }
        return true;
    }

    private void recuperaSenha() {
        autenticacao.sendPasswordResetEmail(email).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),
                                "Confira seu email.",
                                Toast.LENGTH_LONG).show();
                        Intent vaiParaEntrarUsuarioActivity = new Intent(getApplicationContext(),EntrarUsuarioActivity.class);
                        startActivity(vaiParaEntrarUsuarioActivity,
                                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(),
                                "Confira se seu email está correto e tente novamente.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}