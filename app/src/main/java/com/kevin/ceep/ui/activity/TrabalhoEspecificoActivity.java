package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.Utilitario.comparaString;
import static com.kevin.ceep.ui.Utilitario.geraIdAleatorio;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_DESEJO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_NOVO_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_ALTERA_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INVALIDA;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.databinding.ActivityTrabalhoEspecificoBinding;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoProducao;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class TrabalhoEspecificoActivity extends AppCompatActivity {
    private ActivityTrabalhoEspecificoBinding binding;
    private FirebaseDatabase meuBanco;
    private DatabaseReference minhareferencia;
    private TrabalhoProducao trabalhoRecebido;
    private LinearLayoutCompat linearLayoutTrabalhoNecessario, linearLayoutTrabalhoNecessario2;
    private TextInputEditText edtNomeTrabalho, edtExperienciaTrabalho, edtNivelTrabalho;
    private TextInputLayout txtInputNome, txtInputProfissao, txtInputExperiencia, txtInputNivel, txtInputRaridade, txtInputQuantidade, txtInputLicenca, txtInputEstado;
    private CheckBox checkBoxRecorrenciaTrabalho;
    private AutoCompleteTextView autoCompleteProfissao, autoCompleteRaridade, autoCompleteTrabalhoNecessario1, autoCompleteTrabalhoNecessario2, autoCompleteQuantidade, autoCompleteLicenca, autoCompleteEstado;
    private ShapeableImageView imagemTrabalhoNecessario1, imagemTrabalhoNecessario2;
    private String[] estadosTrabalho;
    private String[] licencasTrabalho;
    private ArrayList<Trabalho> todosTrabalhoComunsMelhorados;
    private final String[] mensagemErro={"Campo requerido!","Inválido!"};
    private String usuarioId, personagemId, trabalhoId, licencaModificada, nome, profissao, experiencia, nivel, raridade, trabalhoNecessario1, trabalhoNecessario2;
    private int codigoRequisicao = CODIGO_REQUISICAO_INVALIDA, posicaoEstado=0;
    private boolean recorrencia=true, acrescimo = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrabalhoEspecificoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inicializaComponentes();
        pegaTodosTrabalhosComunsMelhorados();
        recebeDadosIntent();
        configuraDropdownRaridades();
        configuraAcaoImagem();

        configuraDropdownEstados();
        modificaEstado();
    }

    private void configuraAcaoImagem() {
        imagemTrabalhoNecessario1.setOnClickListener(view -> linearLayoutTrabalhoNecessario2.setVisibility(View.VISIBLE));
        imagemTrabalhoNecessario2.setOnClickListener(view -> {
            autoCompleteTrabalhoNecessario2.setText("");
            linearLayoutTrabalhoNecessario2.setVisibility(View.GONE);
        });
    }

    private void pegaTodosTrabalhosComunsMelhorados() {
        todosTrabalhoComunsMelhorados = new ArrayList<>();
        DatabaseReference minhaReferenciaTrabalhos = meuBanco.getReference(CHAVE_LISTA_TRABALHO);
        minhaReferenciaTrabalhos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                todosTrabalhoComunsMelhorados.clear();
                for (DataSnapshot dn : dataSnapshot.getChildren()) {
                    Trabalho trabalho = dn.getValue(Trabalho.class);
                    String raridade;
                    if (trabalho != null) {
                        raridade = trabalho.getRaridade();
                        if (comparaString(raridade, "Comum") || comparaString(raridade, "Melhorado")) {
                            todosTrabalhoComunsMelhorados.add(trabalho);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(Objects.requireNonNull(getCurrentFocus()), "Erro ao carregar dados: "+databaseError, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void configuraDropdownRaridades() {
        autoCompleteRaridade.setOnItemClickListener((adapterView, view, i, l) -> {
            String raridadeClicada = adapterView.getAdapter().getItem(i).toString();
            if (comparaString(raridadeClicada, "Melhorado") || comparaString(raridadeClicada, "Raro")) {
                linearLayoutTrabalhoNecessario.setVisibility(View.VISIBLE);
                configuraDropdownTrabalhoNecessario(raridadeClicada);
            } else {
                linearLayoutTrabalhoNecessario.setVisibility(View.GONE);
            }
        });
    }

    private void configuraDropdownTrabalhoNecessario(String raridade) {
        profissao = autoCompleteProfissao.getText().toString();
        nivel = Objects.requireNonNull(edtNivelTrabalho.getText()).toString();
        ArrayList<String> trabalhosNecessarios = new ArrayList<>();
        ArrayAdapter<String> trabalhoNecessarioAdapter;
        for (Trabalho trabalho : todosTrabalhoComunsMelhorados) {
            if (comparaString(raridade, "Melhorado")) {
                if (comparaString(trabalho.getProfissao(), profissao) && comparaString(trabalho.getNivel().toString(), nivel) && comparaString(trabalho.getRaridade(), "Comum")){
                    trabalhosNecessarios.add(trabalho.getNome());
                }
            } else if (comparaString(raridade, "Raro")) {
                if (comparaString(trabalho.getProfissao(), profissao) && comparaString(trabalho.getNivel().toString(), nivel) && comparaString(trabalho.getRaridade(), "Melhorado")){
                    trabalhosNecessarios.add(trabalho.getNome());
                }
            }
        } if (trabalhosNecessarios.isEmpty()){
            Snackbar.make(Objects.requireNonNull(getCurrentFocus()), "Trabalho necessário não encontrado!", Snackbar.LENGTH_LONG).show();
            trabalhosNecessarios.clear();
            trabalhosNecessarios.add("Nada encontrado");
        }
        trabalhoNecessarioAdapter = new ArrayAdapter<>(getApplication(), R.layout.item_dropdrown, trabalhosNecessarios);
        trabalhoNecessarioAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        autoCompleteTrabalhoNecessario1.setAdapter(trabalhoNecessarioAdapter);
        autoCompleteTrabalhoNecessario2.setAdapter(trabalhoNecessarioAdapter);
    }

    private void modificaEstado() {
        autoCompleteEstado.setOnItemClickListener((adapterView, view, i, l) -> posicaoEstado=i);
        autoCompleteLicenca.setOnItemClickListener((adapterView, view, i, l) -> {
            licencaModificada = licencasTrabalho[i];
            if (licencaModificada.toLowerCase(Locale.ROOT).equals("licença de produção do principiante")){
                Log.d("LICENCA", licencaModificada);
                int novaExperiencia = (int) (1.50 * Integer.parseInt(Objects.requireNonNull(edtExperienciaTrabalho.getText()).toString()));
                Log.d("LICENCA", String.valueOf(novaExperiencia));
                edtExperienciaTrabalho.setText(String.valueOf(novaExperiencia));
                acrescimo = true;
            } else if (acrescimo) {
                int novaExperiencia = (int) ((Integer.parseInt(Objects.requireNonNull(edtExperienciaTrabalho.getText()).toString())) / 1.5);
                Log.d("LICENCA", String.valueOf(novaExperiencia));
                edtExperienciaTrabalho.setText(String.valueOf(novaExperiencia));
                acrescimo = false;
            }
        });
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
        meuBanco = FirebaseDatabase.getInstance();
        minhareferencia = meuBanco.getReference(CHAVE_USUARIOS);
        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        linearLayoutTrabalhoNecessario = binding.linearLayoutTrabalhoNecessario1;
        linearLayoutTrabalhoNecessario2 = binding.linearLayoutTrabalhoNecessario3;
        edtNomeTrabalho = binding.edtNomeTrabalho;
        edtNivelTrabalho = binding.edtNivelTrabalho;
        edtExperienciaTrabalho = binding.edtExperienciaTrabalho;

        txtInputEstado = binding.txtLayoutEstadoTrabalho;
        txtInputLicenca = binding.txtLayoutLicencaTrabalho;
        txtInputNome = binding.txtLayoutNomeTrabalho;
        txtInputProfissao = binding.txtLayoutProfissaoTrabalho;
        txtInputExperiencia = binding.txtLayoutExperienciaTrabalho;
        txtInputNivel = binding.txtLayoutNivelTrabalho;
        txtInputRaridade = binding.txtLayoutRaridadeTrabalho;
        txtInputQuantidade = binding.txtLayoutQuantidadeTrabalho;

        autoCompleteProfissao = binding.txtAutoCompleteProfissaoTrabalho;
        autoCompleteRaridade = binding.txtAutoCompleteRaridadeTrabalho;
        autoCompleteTrabalhoNecessario1 = binding.txtAutoCompleteTrabalhoNecessario;
        autoCompleteTrabalhoNecessario2 = binding.txtAutoCompleteTrabalhoNecessario2;
        autoCompleteQuantidade = binding.txtAutoCompleteQuantidadeTrabalho;
        autoCompleteLicenca = binding.txtAutoCompleteLicencaTrabalho;
        autoCompleteEstado = binding.txtAutoCompleteEstadoTrabalho;
        imagemTrabalhoNecessario1 = binding.imagemTrabalhoNecessario1;
        imagemTrabalhoNecessario2 = binding.imagemTrabalhoNecessario2;

        checkBoxRecorrenciaTrabalho = binding.checkBoxRecorrenciaTrabalho;

        String[] raridadesTrabalho = getResources().getStringArray(R.array.raridades);
        licencasTrabalho = getResources().getStringArray(R.array.licencas_completas);
        estadosTrabalho = getResources().getStringArray(R.array.estados);
        // vou modificar
        String[] profissoesTrabalho = getResources().getStringArray(R.array.profissoes);
        ArrayAdapter<String> profissoesAdapter = new ArrayAdapter<>(getApplication(), R.layout.item_dropdrown, profissoesTrabalho);
        ArrayAdapter<String> raridadeAdapter = new ArrayAdapter<>(getApplication(), R.layout.item_dropdrown, raridadesTrabalho);
        profissoesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        raridadeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        autoCompleteProfissao.setAdapter(profissoesAdapter);
        autoCompleteRaridade.setAdapter(raridadeAdapter);
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_TRABALHO)){
            codigoRequisicao = (int) dadosRecebidos
                    .getSerializableExtra(CHAVE_TRABALHO);
            personagemId = (String) dadosRecebidos.getSerializableExtra(CHAVE_PERSONAGEM);
            if (codigoRequisicao != CODIGO_REQUISICAO_INVALIDA){
                if (codigoRequisicao == CODIGO_REQUISICAO_ALTERA_TRABALHO){
                    trabalhoRecebido = (TrabalhoProducao) dadosRecebidos
                            .getSerializableExtra(CHAVE_NOME_TRABALHO);
                    if (trabalhoRecebido != null && trabalhoRecebido.getTipo_licenca().toLowerCase(Locale.ROOT).equals("licença de produção do principiante")) {
                        acrescimo = true;
                    }
                    configuraComponentesAlteraTrabalho();
                }else if (codigoRequisicao == CODIGO_REQUISICAO_INSERE_TRABALHO){
                    configuraLayoutNovoTrabalho();
                }
            }
        }
    }

    private void configuraLayoutNovoTrabalho() {
        setTitle(CHAVE_TITULO_NOVO_TRABALHO);
        linearLayoutTrabalhoNecessario.setVisibility(View.GONE);
        txtInputLicenca.setVisibility(View.GONE);
        txtInputEstado.setVisibility(View.GONE);
        txtInputQuantidade.setVisibility(View.GONE);
        checkBoxRecorrenciaTrabalho.setVisibility(View.GONE);
    }

    private void configuraComponentesAlteraTrabalho() {
        setTitle(trabalhoRecebido.getNome());
        edtNomeTrabalho.setEnabled(false);
        edtNivelTrabalho.setEnabled(false);
        edtExperienciaTrabalho.setEnabled(false);
        autoCompleteProfissao.setEnabled(false);
        autoCompleteProfissao.setText(trabalhoRecebido.getProfissao());
        autoCompleteRaridade.setEnabled(false);
        autoCompleteRaridade.setText(trabalhoRecebido.getRaridade());
        autoCompleteTrabalhoNecessario1.setEnabled(false);
        autoCompleteTrabalhoNecessario2.setEnabled(false);
        autoCompleteQuantidade.setEnabled(false);
        trabalhoId=trabalhoRecebido.getId();
        autoCompleteEstado.setText(estadosTrabalho[trabalhoRecebido.getEstado()]);
        edtNivelTrabalho.setText(String.valueOf(trabalhoRecebido.getNivel()));
        edtExperienciaTrabalho.setText(String.valueOf(trabalhoRecebido.getExperiencia()));
        autoCompleteLicenca.setText(trabalhoRecebido.getTipo_licenca());
        edtNomeTrabalho.setText(trabalhoRecebido.getNome());
        posicaoEstado=trabalhoRecebido.getEstado();
        licencaModificada=trabalhoRecebido.getTipo_licenca();
        if (trabalhoRecebido.getRecorrencia()){
            checkBoxRecorrenciaTrabalho.setChecked(true);
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
        if (item.getItemId() == R.id.itemMenuSalvaTrabalho) {
            if (codigoRequisicao == CODIGO_REQUISICAO_ALTERA_TRABALHO) {
                if (verificaTrabalhoModificado()) {

                }else{
                    finish();
                }
            } else if (codigoRequisicao == CODIGO_REQUISICAO_INSERE_TRABALHO) {
                nome = Objects.requireNonNull(edtNomeTrabalho.getText()).toString().trim();
                profissao = Objects.requireNonNull(autoCompleteProfissao).getText().toString().trim();
                experiencia = Objects.requireNonNull(edtExperienciaTrabalho.getText()).toString().trim();
                nivel = Objects.requireNonNull(edtNivelTrabalho.getText()).toString().trim();
                raridade = Objects.requireNonNull(autoCompleteRaridade).getText().toString().trim();
                trabalhoNecessario1 = Objects.requireNonNull(autoCompleteTrabalhoNecessario1).getText().toString().trim();
                trabalhoNecessario2 = Objects.requireNonNull(autoCompleteTrabalhoNecessario2).getText().toString().trim();
                if (verificaCamposNovoTrabalho()) {
                    cadastraNovoTrabalho();
                    edtNomeTrabalho.setText("");
                    edtNomeTrabalho.requestFocus();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean verificaCamposNovoTrabalho() {
        return verificaValorCampo(nome, txtInputNome, 0)
                & verificaValorCampo(profissao, txtInputProfissao, 1)
                & verificaValorCampo(experiencia,txtInputExperiencia,1)
                & verificaValorCampo(nivel, txtInputNivel, 1)
                & verificaValorCampo(raridade, txtInputRaridade, 1);
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
        recorrencia = checkBoxRecorrenciaTrabalho.isChecked();
    }

    private void modificaTrabalhoServidor(Trabalho trabalhoModificado) {
        minhareferencia.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM)
                .child(personagemId).child(CHAVE_LISTA_DESEJO)
                .child(trabalhoId).setValue(trabalhoModificado);
    }

    private Boolean verificaValorCampo(String stringCampo, TextInputLayout inputLayout, int posicaoErro) {
        if (stringCampo.isEmpty()){
            inputLayout.setHelperText(mensagemErro[posicaoErro]);
            inputLayout.setHelperTextColor(AppCompatResources.getColorStateList(this,R.color.cor_background_bordo));
            return false;

        }
        inputLayout.setHelperTextEnabled(false);
        return true;
    }

    private void cadastraNovoTrabalho() {
        String novoId = geraIdAleatorio();
        String trabalhoNecessario = "";
        if (comparaString(trabalhoNecessario1, "Trabalho necessário")){
            trabalhoNecessario1 = "";
        }
        if (comparaString(trabalhoNecessario2, "Trabalho necessário")){
            trabalhoNecessario2 = "";
        }
        if (!trabalhoNecessario1.isEmpty() && !trabalhoNecessario2.isEmpty()){
            trabalhoNecessario = trabalhoNecessario1+ ","+ trabalhoNecessario2;
        } else if (!trabalhoNecessario1.isEmpty()){
            trabalhoNecessario = trabalhoNecessario1;
        } else if (!trabalhoNecessario2.isEmpty()){
            trabalhoNecessario = trabalhoNecessario2;
        }
        Trabalho novoTrabalho = new Trabalho(
                novoId,
                nome,
                profissao,
                raridade,
                trabalhoNecessario,
                Integer.parseInt(nivel),
                Integer.parseInt(experiencia));
        salvaNovoTrabalhoNoServidor(novoTrabalho);
    }

    private void salvaNovoTrabalhoNoServidor(Trabalho novoTrabalho) {
        DatabaseReference minhaReferencia = meuBanco.getReference(CHAVE_LISTA_TRABALHO);
        minhaReferencia.child(novoTrabalho.getId()).setValue(novoTrabalho);
        Snackbar.make(Objects.requireNonNull(getCurrentFocus()), novoTrabalho.getNome()+" foi cadastrado com sucesso!", Snackbar.LENGTH_LONG).show();
    }
    @Override
    protected void onStop() {
        super.onStop();
        binding = null;
    }
}