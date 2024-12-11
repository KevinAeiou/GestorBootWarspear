package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_REQUISICAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_ALTERA_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_TRABALHO;
import static com.kevin.ceep.utilitario.Utilitario.geraIdAleatorio;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.kevin.ceep.R;
import com.kevin.ceep.databinding.ActivityAtributosPersonagemBinding;
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.repository.PersonagemRepository;
import com.kevin.ceep.ui.viewModel.PersonagemViewModel;
import com.kevin.ceep.ui.viewModel.factory.PersonagemViewModelFactory;

import java.util.Objects;

public class AtributosPersonagemActivity extends AppCompatActivity {

    private Personagem personagemRecebido;
    private TextInputLayout personagemNomeTxt, personagemEspacoProducaoTxt, personagemEmailTxt, personagemSenhaTxt;
    private EditText personagemNome, personagemEspacoProducao, personagemEmail, personagemSenha;
    private SwitchCompat personagemSwUso, personagemSwEstado;
    private ActivityAtributosPersonagemBinding binding;
    private int codigoRequisicao;
    private PersonagemViewModel personagemViewModel;

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
            if (codigoRequisicao == CODIGO_REQUISICAO_ALTERA_TRABALHO) {
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
        PersonagemViewModelFactory personagemViewModelFactory = new PersonagemViewModelFactory(new PersonagemRepository(getApplicationContext()));
        personagemViewModel = new ViewModelProvider(this, personagemViewModelFactory).get(PersonagemViewModel.class);
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
                    MaterialAlertDialogBuilder dialogoDeAlerta = new MaterialAlertDialogBuilder(this);
                    dialogoDeAlerta.setMessage("Deseja confirmar alterações?");
                    dialogoDeAlerta.setNegativeButton("Não", ((dialogInterface, i) -> finish()));
                    dialogoDeAlerta.setPositiveButton("Sim", (dialogInterface, i) -> {
                        Personagem personagemModificado = definePersonagemModificado();
                        modificaPersonagemServidor(personagemModificado);
                    });
                    dialogoDeAlerta.show();
                } else {
                    finish();
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
                    personagemViewModel.adicionaPersonagem(novoPersonagem).observe(this, resultadoInserePersonagem -> {
                        if (resultadoInserePersonagem.getErro() == null) {
                            finish();
                        } else {
                            Snackbar.make(getApplicationContext(), Objects.requireNonNull(getCurrentFocus()), "Erro: "+resultadoInserePersonagem.getErro(), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private Personagem definePersonagemModificado() {
        return new Personagem(personagemRecebido.getId(), personagemNome.getText().toString(), personagemEmail.getText().toString(), personagemSenha.getText().toString(), personagemSwEstado.isChecked(), personagemSwUso.isChecked(), Integer.parseInt(personagemEspacoProducao.getText().toString()));
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

    private void modificaPersonagemServidor(Personagem personagemModificado) {
        personagemViewModel.modificaPersonagem(personagemModificado).observe(this, resultadoModificaPersonagem -> {
            if (resultadoModificaPersonagem.getErro() != null) {
                Snackbar.make(getApplicationContext(), Objects.requireNonNull(getCurrentFocus()), "Erro: "+resultadoModificaPersonagem.getErro(), Snackbar.LENGTH_LONG).show();
            } else {
                finish();
            }
        });
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