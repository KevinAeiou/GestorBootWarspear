package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOME_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_NOVO_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_ALTERA_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_ALTERA_TRABALHO_PRODUCAO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INSERE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CODIGO_REQUISICAO_INVALIDA;
import static com.kevin.ceep.utilitario.Utilitario.comparaString;
import static com.kevin.ceep.utilitario.Utilitario.geraIdAleatorio;
import static com.kevin.ceep.utilitario.Utilitario.stringContemString;

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
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
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
import com.kevin.ceep.dao.TrabalhoDAO;
import com.kevin.ceep.dao.TrabalhoEstoqueDAO;
import com.kevin.ceep.dao.TrabalhoProducaoDAO;
import com.kevin.ceep.databinding.ActivityTrabalhoEspecificoBinding;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoProducao;

import java.util.ArrayList;
import java.util.Objects;

public class TrabalhoEspecificoActivity extends AppCompatActivity {
    private ActivityTrabalhoEspecificoBinding binding;
    private DatabaseReference minhaReferenciaTrabalhos;
    private TrabalhoProducao trabalhoProducaoRecebido;
    private Trabalho trabalhoRecebido;
    private LinearLayoutCompat linearLayoutTrabalhoNecessario2, linearLayoutTrabalhoNecessario3;
    private TextInputEditText edtNomeTrabalho, edtNomeProducaoTrabalho, edtExperienciaTrabalho, edtNivelTrabalho;
    private TextInputLayout txtInputNome, txtInputProfissao, txtInputExperiencia, txtInputNivel, txtInputRaridade, txtInputLicenca, txtInputEstado;
    private CheckBox checkBoxRecorrenciaTrabalho;
    private AutoCompleteTextView autoCompleteProfissao, autoCompleteRaridade, autoCompleteTrabalhoNecessario1, autoCompleteTrabalhoNecessario2, autoCompleteLicenca, autoCompleteEstado;
    private ShapeableImageView imagemTrabalhoNecessario1, imagemTrabalhoNecessario2;
    private LinearProgressIndicator indicadorProgresso;
    private MaterialButton btnExcluir;
    private String[] estadosTrabalho;
    private ArrayList<Trabalho> todosTrabalhoComunsMelhorados;
    private ArrayAdapter<String> adapterEstado;
    private final String[] mensagemErro={"Campo requerido!","Inválido!"};
    private String usuarioId, personagemId, licencaModificada, nome, nomeProducao, profissao, experiencia, nivel, raridade, trabalhoNecessario1, trabalhoNecessario2;
    private int codigoRequisicao = CODIGO_REQUISICAO_INVALIDA, posicaoEstadoModificado;
    private boolean acrescimo = false, recorrenciaModificada;
    private TrabalhoEstoqueDAO trabalhoEstoqueDAO;
    private TrabalhoDAO trabalhoDAO;
    private TrabalhoProducaoDAO trabalhoProducaoDAO;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrabalhoEspecificoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inicializaComponentes();
        pegaTodosTrabalhosComunsMelhorados();
        recebeDadosIntent();
        configuraAcaoImagem();
    }
    private void inicializaComponentes() {
        FirebaseDatabase meuBanco = FirebaseDatabase.getInstance();
        minhaReferenciaTrabalhos = meuBanco.getReference(CHAVE_LISTA_TRABALHO);
        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        linearLayoutTrabalhoNecessario2 = binding.linearLayoutTrabalhoNecessario2;
        linearLayoutTrabalhoNecessario3 = binding.linearLayoutTrabalhoNecessario3;
        edtNomeTrabalho = binding.edtNomeTrabalho;
        edtNomeProducaoTrabalho = binding.edtNomeProducaoTrabalho;
        edtNivelTrabalho = binding.edtNivelTrabalho;
        edtExperienciaTrabalho = binding.edtExperienciaTrabalho;

        txtInputEstado = binding.txtLayoutEstadoTrabalho;
        txtInputLicenca = binding.txtLayoutLicencaTrabalho;
        txtInputNome = binding.txtLayoutNomeTrabalho;
        txtInputProfissao = binding.txtLayoutProfissaoTrabalho;
        txtInputExperiencia = binding.txtLayoutExperienciaTrabalho;
        txtInputNivel = binding.txtLayoutNivelTrabalho;
        txtInputRaridade = binding.txtLayoutRaridadeTrabalho;

        autoCompleteProfissao = binding.txtAutoCompleteProfissaoTrabalho;
        autoCompleteRaridade = binding.txtAutoCompleteRaridadeTrabalho;
        autoCompleteTrabalhoNecessario1 = binding.txtAutoCompleteTrabalhoNecessario;
        autoCompleteTrabalhoNecessario2 = binding.txtAutoCompleteTrabalhoNecessario2;
        autoCompleteLicenca = binding.txtAutoCompleteLicencaTrabalho;
        autoCompleteEstado = binding.txtAutoCompleteEstadoTrabalho;
        imagemTrabalhoNecessario1 = binding.imagemTrabalhoNecessario1;
        imagemTrabalhoNecessario2 = binding.imagemTrabalhoNecessario2;

        checkBoxRecorrenciaTrabalho = binding.checkBoxRecorrenciaTrabalho;
        indicadorProgresso = binding.indicadorProgressoTrabalhoEspecifico;
        btnExcluir = binding.btnExcluiTrabalhoEspecifico;

        estadosTrabalho = getResources().getStringArray(R.array.estados);
        trabalhoDAO = new TrabalhoDAO();
    }
    private void pegaTodosTrabalhosComunsMelhorados() {
        todosTrabalhoComunsMelhorados = new ArrayList<>();
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
                configuraDropdownTrabalhoNecessario();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(Objects.requireNonNull(getCurrentFocus()), "Erro ao carregar dados: "+databaseError, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos != null && dadosRecebidos.hasExtra(CHAVE_TRABALHO)){
            codigoRequisicao = (int) dadosRecebidos.getSerializableExtra(CHAVE_TRABALHO);
            personagemId = (String) dadosRecebidos.getSerializableExtra(CHAVE_PERSONAGEM);
            if (codigoRequisicao != CODIGO_REQUISICAO_INVALIDA){
                if (codigoRequisicao == CODIGO_REQUISICAO_ALTERA_TRABALHO){
                    trabalhoRecebido = (Trabalho) dadosRecebidos
                            .getSerializableExtra(CHAVE_NOME_TRABALHO);
                    if (trabalhoRecebido != null){
                        configuraComponentesAlteraTrabalho();
                        configuraBotaoExcluiTrabalhoEspecifico();
                    }
                } else if (codigoRequisicao == CODIGO_REQUISICAO_ALTERA_TRABALHO_PRODUCAO){
                    trabalhoEstoqueDAO = new TrabalhoEstoqueDAO(personagemId);
                    trabalhoProducaoRecebido = (TrabalhoProducao) dadosRecebidos
                            .getSerializableExtra(CHAVE_NOME_TRABALHO);
                    if (trabalhoProducaoRecebido != null){
                        configuraComponentesAlteraTrabalhoProducao();
                    }
                } else if (codigoRequisicao == CODIGO_REQUISICAO_INSERE_TRABALHO){
                    configuraLayoutNovoTrabalho();
                }
            }
        }
    }
    private void configuraAcaoImagem() {
        imagemTrabalhoNecessario1.setOnClickListener(view -> linearLayoutTrabalhoNecessario3.setVisibility(View.VISIBLE));
        imagemTrabalhoNecessario2.setOnClickListener(view -> {
            autoCompleteTrabalhoNecessario2.setText("");
            linearLayoutTrabalhoNecessario3.setVisibility(View.GONE);
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemMenuSalvaTrabalho) {
            indicadorProgresso.setVisibility(View.VISIBLE);
            recuperaValoresCampos();
            Log.d("trabalhoModificado", "Trabalho necessario 1: "+trabalhoNecessario1);
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
            if (codigoRequisicao == CODIGO_REQUISICAO_ALTERA_TRABALHO_PRODUCAO) {
                if (verificaTrabalhoProducaoModificado()) {
                    TrabalhoProducao trabalhoModificado = defineTrabalhoProducaoModificado();
                    trabalhoProducaoDAO = new TrabalhoProducaoDAO(personagemId);

                    MutableLiveData<Boolean> retorno = trabalhoProducaoDAO.modificaTrabalhoProducaoServidor(trabalhoModificado);
                    Log.d("segundoPlano", "Retorno: "+retorno.getValue());
                    if (verificaEstadoModificado()) {
                        Integer estado = trabalhoModificado.getEstado();
                        if (estado == 1 || estado == 2) {
                            if (!trabalhoModificado.ehProducaoDeRecursos()) {
                                switch (estado) {
                                    case 1:
                                        if (trabalhoModificado.possueTrabalhoNecessarioValido()) {
                                            trabalhoEstoqueDAO.modificaQuantidadeTrabalhoNecessarioNoEstoque(trabalhoModificado);
                                        }
                                        break;
                                    case 2:
                                        trabalhoEstoqueDAO.modificaQuantidadeTrabalhoNoEstoque(trabalhoModificado);
                                        break;
                                }
                            }
                        }
                    }
                }
                finish();
            } else if (codigoRequisicao == CODIGO_REQUISICAO_ALTERA_TRABALHO) {
                if (verificaTrabalhoModificado()) {
                    Trabalho trabalhoModificado = defineTrabalhoModificado(trabalhoNecessario);
                    trabalhoDAO.modificaTrabalho(trabalhoModificado);
                }
                finish();
            } else if (codigoRequisicao == CODIGO_REQUISICAO_INSERE_TRABALHO) {
                if (verificaCamposNovoTrabalho()) {
                    Trabalho novoTrabalho = defineNovoTrabalho(trabalhoNecessario);
                    trabalhoDAO.salvaNovoTrabalho(novoTrabalho);
                    edtNomeTrabalho.setText("");
                    edtNomeProducaoTrabalho.setText("");
                    edtNomeTrabalho.requestFocus();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void runThread(TrabalhoProducao trabalhoModificado) {
        Log.d("segundoPlano", "runThread: inicializou segundo plano");
        new Thread() {
            public void run() {
                int i =0;
                while (i++ < 1000) {
                    try {
                        runOnUiThread(() -> {
                        });
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        Log.d("segundoPlano", "runThread: finalizou segundo plano");
    }
    private void recuperaValoresCampos() {
        nome = Objects.requireNonNull(edtNomeTrabalho.getText()).toString().trim();
        nomeProducao = Objects.requireNonNull(edtNomeProducaoTrabalho.getText()).toString().trim();
        profissao = Objects.requireNonNull(autoCompleteProfissao).getText().toString().trim();
        experiencia = Objects.requireNonNull(edtExperienciaTrabalho.getText()).toString().trim();
        nivel = Objects.requireNonNull(edtNivelTrabalho.getText()).toString().trim();
        raridade = Objects.requireNonNull(autoCompleteRaridade).getText().toString().trim();
        trabalhoNecessario1 = Objects.requireNonNull(autoCompleteTrabalhoNecessario1).getText().toString().trim();
        trabalhoNecessario2 = Objects.requireNonNull(autoCompleteTrabalhoNecessario2).getText().toString().trim();
    }

    private void configuraBotaoExcluiTrabalhoEspecifico() {
        btnExcluir.setOnClickListener(v -> {
            indicadorProgresso.setVisibility(View.VISIBLE);
            trabalhoDAO.excluiTrabalhoEspecificoServidor(trabalhoRecebido);
        });
    }
    private void configuraDropdownProfissoes() {
        String[] profissoesTrabalho = getResources().getStringArray(R.array.profissoes);
        ArrayAdapter<String> profissoesAdapter = new ArrayAdapter<>(getApplication(), R.layout.item_dropdrown, profissoesTrabalho);
        profissoesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        autoCompleteProfissao.setAdapter(profissoesAdapter);
    }
    private void configuraDropdownRaridades() {
        String[] raridadesTrabalho = getResources().getStringArray(R.array.raridades);
        ArrayAdapter<String> raridadeAdapter = new ArrayAdapter<>(getApplication(), R.layout.item_dropdrown, raridadesTrabalho);
        raridadeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        autoCompleteRaridade.setAdapter(raridadeAdapter);
        autoCompleteRaridade.setOnItemClickListener((adapterView, view, i, l) -> {
            String raridadeClicada = adapterView.getAdapter().getItem(i).toString();
            if (comparaString(raridadeClicada, "Melhorado") || comparaString(raridadeClicada, "Raro")) {
                linearLayoutTrabalhoNecessario2.setVisibility(View.VISIBLE);
                configuraDropdownTrabalhoNecessario();
            } else {
                linearLayoutTrabalhoNecessario2.setVisibility(View.GONE);
            }
        });
    }

    private void configuraDropdownTrabalhoNecessario() {
        profissao = autoCompleteProfissao.getText().toString();
        nivel = Objects.requireNonNull(edtNivelTrabalho.getText()).toString();
        raridade = Objects.requireNonNull(autoCompleteRaridade).getText().toString().trim();

        ArrayList<String> trabalhosNecessarios = new ArrayList<>();
        ArrayAdapter<String> trabalhoNecessarioAdapter;
        for (Trabalho trabalho : todosTrabalhoComunsMelhorados) {
            if (comparaString(raridade, "Melhorado")) {
                if (comparaString(trabalho.getProfissao(), profissao)
                    && comparaString(trabalho.getNivel().toString(), nivel)
                    && comparaString(trabalho.getRaridade(), "Comum")){
                    trabalhosNecessarios.add(trabalho.getNome());
                }
            } else if (comparaString(raridade, "Raro")) {
                if (comparaString(trabalho.getProfissao(), profissao)
                && comparaString(trabalho.getNivel().toString(), nivel)
                    && comparaString(trabalho.getRaridade(), "Melhorado")){
                    trabalhosNecessarios.add(trabalho.getNome());
                }
            }
        } if (trabalhosNecessarios.isEmpty()){
            Snackbar.make(binding.getRoot(), "Trabalho necessário não encontrado!", Snackbar.LENGTH_LONG).show();
            trabalhosNecessarios.clear();
            trabalhosNecessarios.add("Nada encontrado");
        }
        trabalhoNecessarioAdapter = new ArrayAdapter<>(getApplication(), R.layout.item_dropdrown, trabalhosNecessarios);
        trabalhoNecessarioAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        autoCompleteTrabalhoNecessario1.setAdapter(trabalhoNecessarioAdapter);
        autoCompleteTrabalhoNecessario2.setAdapter(trabalhoNecessarioAdapter);
        Log.d("dropDownTrabalhoNeces","Fim configura dropdownTrabalhoNecessario.");
    }

    private void configuraDropdownLicencas() {
        String[] licencasTrabalho = getResources().getStringArray(R.array.licencas_completas);
        ArrayAdapter<String> adapterLicenca= new ArrayAdapter<>(this,
                R.layout.item_dropdrown, licencasTrabalho);
        adapterLicenca.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        autoCompleteLicenca.setAdapter(adapterLicenca);

        autoCompleteLicenca.setOnItemClickListener((adapterView, view, i, l) -> {
            if (comparaString(autoCompleteLicenca.getText().toString(), "licença de produção do principiante")) {
                if (!acrescimo) {
                    int novaExperiencia = (int) (1.50 * Integer.parseInt(Objects.requireNonNull(edtExperienciaTrabalho.getText()).toString()));
                    edtExperienciaTrabalho.setText(String.valueOf(novaExperiencia));
                    acrescimo = true;
                }
            } else if (acrescimo){
                int novaExperiencia = (int) ((Integer.parseInt(Objects.requireNonNull(edtExperienciaTrabalho.getText()).toString())) / 1.5);
                edtExperienciaTrabalho.setText(String.valueOf(novaExperiencia));
                acrescimo = false;
            }
        });
    }
    private void configuraDropdownEstados() {
        adapterEstado= new ArrayAdapter<>(this,
                R.layout.item_dropdrown, estadosTrabalho);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteEstado.setAdapter(adapterEstado);
    }
    private void configuraComponentesAlteraTrabalho() {
        checkBoxRecorrenciaTrabalho.setVisibility(View.GONE);
        txtInputLicenca.setVisibility(View.GONE);
        txtInputEstado.setVisibility(View.GONE);
        btnExcluir.setVisibility(View.VISIBLE);
        setTitle(trabalhoRecebido.getNome());

        edtNomeTrabalho.setText(trabalhoRecebido.getNome());
        edtNomeProducaoTrabalho.setText(trabalhoRecebido.getNomeProducao());
        autoCompleteProfissao.setText(trabalhoRecebido.getProfissao());
        edtExperienciaTrabalho.setText(String.valueOf(trabalhoRecebido.getExperiencia()));
        edtNivelTrabalho.setText(String.valueOf(trabalhoRecebido.getNivel()));
        autoCompleteRaridade.setText(trabalhoRecebido.getRaridade());
        if (trabalhoRecebido.getRaridade().equals("Raro") || trabalhoRecebido.getRaridade().equals("Melhorado")) {
            linearLayoutTrabalhoNecessario2.setVisibility(View.VISIBLE);
            configuraDropdownTrabalhoNecessario();
            String[] trabalhosNecessarios = null;
            if (trabalhoRecebido.getTrabalhoNecessario() != null) {
                trabalhosNecessarios = trabalhoRecebido.getTrabalhoNecessario().split(",");
            }
            if (trabalhosNecessarios != null) {
                if (trabalhosNecessarios.length > 1) {
                    autoCompleteTrabalhoNecessario1.setText(trabalhosNecessarios[0]);
                    autoCompleteTrabalhoNecessario2.setText(trabalhosNecessarios[1]);
                } else {
                    autoCompleteTrabalhoNecessario1.setText(trabalhosNecessarios[0]);
                }
            }
        }
    }

    private void configuraLayoutNovoTrabalho() {
        setTitle(CHAVE_TITULO_NOVO_TRABALHO);
        linearLayoutTrabalhoNecessario2.setVisibility(View.GONE);
        txtInputLicenca.setVisibility(View.GONE);
        txtInputEstado.setVisibility(View.GONE);
        checkBoxRecorrenciaTrabalho.setVisibility(View.GONE);
    }

    private void configuraComponentesAlteraTrabalhoProducao() {
        setTitle(trabalhoProducaoRecebido.getNome());

        desativaCamposTrabalhoProducao();

        recorrenciaModificada = trabalhoProducaoRecebido.getRecorrencia();
        licencaModificada = trabalhoProducaoRecebido.getTipo_licenca();
        posicaoEstadoModificado = trabalhoProducaoRecebido.getEstado();

        if (comparaString(licencaModificada, "licença de produção do principiante")) {
            acrescimo = true;
        }

        defineValoresCamposTrabalhoProducao();
    }

    private void defineValoresCamposTrabalhoProducao() {
        edtNomeTrabalho.setText(trabalhoProducaoRecebido.getNome());
        edtNomeProducaoTrabalho.setText(trabalhoProducaoRecebido.getNomeProducao());
        autoCompleteProfissao.setText(trabalhoProducaoRecebido.getProfissao());
        edtExperienciaTrabalho.setText(String.valueOf(trabalhoProducaoRecebido.getExperiencia()));
        edtNivelTrabalho.setText(String.valueOf(trabalhoProducaoRecebido.getNivel()));
        autoCompleteRaridade.setText(trabalhoProducaoRecebido.getRaridade());
        checkBoxRecorrenciaTrabalho.setChecked(recorrenciaModificada);
        autoCompleteLicenca.setText(licencaModificada);
        autoCompleteEstado.setText(estadosTrabalho[posicaoEstadoModificado]);
        defineValoresCamposTrabalhosNecessarios();
    }

    private void defineValoresCamposTrabalhosNecessarios() {
        if (valorTrabalhoNecessarioExiste()) {
            linearLayoutTrabalhoNecessario2.setVisibility(View.VISIBLE);
            String[] trabalhosNecessarios = trabalhoProducaoRecebido.getTrabalhoNecessario().split(",");
            if (trabalhosNecessarios.length > 1) {
                autoCompleteTrabalhoNecessario1.setText(trabalhosNecessarios[0]);
                autoCompleteTrabalhoNecessario2.setText(trabalhosNecessarios[1]);
            } else {
                autoCompleteTrabalhoNecessario1.setText(trabalhosNecessarios[0]);
            }
        }
    }

    private boolean valorTrabalhoNecessarioExiste() {
        return trabalhoProducaoRecebido.getTrabalhoNecessario() != null;
    }

    private void desativaCamposTrabalhoProducao() {
        edtNomeTrabalho.setEnabled(false);
        edtNomeProducaoTrabalho.setEnabled(false);
        autoCompleteProfissao.setEnabled(false);
        edtExperienciaTrabalho.setEnabled(false);
        edtNivelTrabalho.setEnabled(false);
        autoCompleteRaridade.setEnabled(false);
        autoCompleteTrabalhoNecessario1.setEnabled(false);
        imagemTrabalhoNecessario1.setEnabled(false);
        autoCompleteTrabalhoNecessario2.setEnabled(false);
        imagemTrabalhoNecessario2.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario_nota_salva,
                menu);
        return super.onCreateOptionsMenu(menu);
    }
    @NonNull
    private TrabalhoProducao defineTrabalhoProducaoModificado() {
        TrabalhoProducao trabalhoModificado = trabalhoProducaoRecebido;
        trabalhoModificado.setRecorrencia(checkBoxRecorrenciaTrabalho.isChecked());
        trabalhoModificado.setTipo_licenca(autoCompleteLicenca.getText().toString());
        trabalhoModificado.setEstado(adapterEstado.getPosition(autoCompleteEstado.getText().toString()));
        return trabalhoModificado;
    }

    @NonNull
    private Trabalho defineTrabalhoModificado(String trabalhoNecessario) {
        return new Trabalho(
                trabalhoRecebido.getId(),
                nome,
                nomeProducao,
                profissao,
                raridade,
                trabalhoNecessario,
                Integer.parseInt(nivel),
                Integer.parseInt(experiencia));
    }
    private boolean verificaTrabalhoModificado() {
        return verificaCampoModificado(nome, trabalhoRecebido.getNome()) ||
                verificaCampoModificado(nomeProducao, trabalhoRecebido.getNomeProducao()) ||
                verificaCampoModificado(profissao, trabalhoRecebido.getProfissao()) ||
                verificaCampoModificado(experiencia, trabalhoRecebido.getExperiencia().toString()) ||
                verificaCampoTrabalhoNecessario(trabalhoNecessario1, trabalhoRecebido.getTrabalhoNecessario()) ||
                verificaCampoTrabalhoNecessario(trabalhoNecessario2, trabalhoRecebido.getTrabalhoNecessario()) ||
                verificaCampoModificado(nivel, trabalhoRecebido.getNivel().toString()) ||
                verificaCampoModificado(raridade, trabalhoRecebido.getRaridade());
    }

    private boolean verificaCampoTrabalhoNecessario(String trabalhoNecessario1, String trabalhoNecessarioRecebido) {
        if (trabalhoNecessario1.isEmpty() && trabalhoNecessarioRecebido == null) {
            return false;
        } else if (trabalhoNecessarioRecebido == null) {
            return true;
        }
        return stringContemString(trabalhoNecessario1, trabalhoNecessarioRecebido);
    }

    private boolean verificaCampoModificado(String campo, String valorRecebido) {
        Snackbar.make(binding.getRoot(), "Campo modificado? "+!comparaString(campo, valorRecebido), Snackbar.LENGTH_LONG).show();
        return !comparaString(campo, valorRecebido);
    }

    private boolean verificaCamposNovoTrabalho() {
        return verificaValorCampo(nome, txtInputNome, 0)
                & verificaValorCampo(profissao, txtInputProfissao, 1)
                & verificaValorCampo(experiencia,txtInputExperiencia,1)
                & verificaValorCampo(nivel, txtInputNivel, 1)
                & verificaValorCampo(raridade, txtInputRaridade, 1);
    }

    private boolean verificaTrabalhoProducaoModificado() {
        return verificaLicencaModificada() ||
                verificaEstadoModificado() ||
                verificaCheckModificado();
    }

    private boolean verificaLicencaModificada() {
        return !comparaString(licencaModificada, autoCompleteLicenca.getText().toString());
    }

    private boolean verificaEstadoModificado() {
        return adapterEstado.getPosition(autoCompleteEstado.getText().toString()) != posicaoEstadoModificado;
    }

    private boolean verificaCheckModificado() {
        return checkBoxRecorrenciaTrabalho.isChecked() != recorrenciaModificada;
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

    private Trabalho defineNovoTrabalho(String trabalhoNecessario) {
        String novoId = geraIdAleatorio();
        return new Trabalho(
                novoId,
                nome,
                nomeProducao,
                profissao,
                raridade,
                trabalhoNecessario,
                Integer.parseInt(nivel),
                Integer.parseInt(experiencia));
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (codigoRequisicao != CODIGO_REQUISICAO_INVALIDA) {
            if (codigoRequisicao == CODIGO_REQUISICAO_INSERE_TRABALHO || codigoRequisicao == CODIGO_REQUISICAO_ALTERA_TRABALHO) {
                configuraDropdownProfissoes();
                configuraDropdownRaridades();
            } else if (codigoRequisicao == CODIGO_REQUISICAO_ALTERA_TRABALHO_PRODUCAO) {
                configuraDropdownLicencas();
                configuraDropdownEstados();
            }
        }
    }
}