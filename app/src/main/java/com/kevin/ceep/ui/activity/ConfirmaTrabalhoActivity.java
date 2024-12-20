package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_CONFIRMA;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TRABALHO;
import static com.kevin.ceep.utilitario.Utilitario.comparaString;
import static com.kevin.ceep.utilitario.Utilitario.geraIdAleatorio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.kevin.ceep.R;
import com.kevin.ceep.model.Trabalho;
import com.kevin.ceep.model.TrabalhoProducao;
import com.kevin.ceep.repository.TrabalhoProducaoRepository;
import com.kevin.ceep.ui.viewModel.TrabalhoProducaoViewModel;
import com.kevin.ceep.ui.viewModel.factory.TrabalhoProducaoViewModelFactory;

public class ConfirmaTrabalhoActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteLicenca,autoCompleteQuantidade;
    private String personagemId;
    private Trabalho trabalhoRecebido;
    private int contador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_trabalho);
        setTitle(CHAVE_TITULO_CONFIRMA);
        recebeDadosIntent();
        configuraBotaoCadastraTrabalho();
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_TRABALHO)) {
            trabalhoRecebido = (Trabalho) dadosRecebidos
                    .getSerializableExtra(CHAVE_TRABALHO);
            if (trabalhoRecebido != null) {
                setTitle(trabalhoRecebido.getNome());
            }
            personagemId = (String) dadosRecebidos.
                    getSerializableExtra(CHAVE_PERSONAGEM);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        configuraDropDrow();
    }
    private void configuraDropDrow() {
        autoCompleteLicenca = findViewById(R.id.txtAutoCompleteLicencaConfirmaTrabalho);
        autoCompleteQuantidade = findViewById(R.id.txtAutoCompleteQuantidadeConfirmaTrabalho);

        String[] licencas = getResources().getStringArray(R.array.licencas_completas);
        String[] quantidade = getResources().getStringArray(R.array.quantidade);

        ArrayAdapter<String> adapterLicenca = new ArrayAdapter<>(this,
                R.layout.item_dropdrown, licencas);
        ArrayAdapter<String> adapterQuantidade = new ArrayAdapter<>(this,
                R.layout.item_dropdrown, quantidade);

        adapterLicenca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterQuantidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteLicenca.setText(licencas[3]);
        autoCompleteLicenca.setAdapter(adapterLicenca);
        autoCompleteQuantidade.setAdapter(adapterQuantidade);
    }

    private void configuraBotaoCadastraTrabalho() {
        AppCompatButton botaoCadastraTrabalho = findViewById(R.id.botaoCadastraConfirmaTrabalho);
        botaoCadastraTrabalho.setOnClickListener(view -> {
            botaoCadastraTrabalho.setEnabled(false);
            cadastraNovoTrabalho();
        });
    }

    private void cadastraNovoTrabalho() {
        TrabalhoProducaoViewModelFactory trabalhoProducaoViewModelFactory = new TrabalhoProducaoViewModelFactory(new TrabalhoProducaoRepository(personagemId));
        TrabalhoProducaoViewModel trabalhoProducaoViewModel = new ViewModelProvider(this, trabalhoProducaoViewModelFactory).get(TrabalhoProducaoViewModel.class);
        int quantidadeSelecionada = Integer.parseInt(autoCompleteQuantidade.getText().toString());
        contador = 1;
        for (int x = 0; x < quantidadeSelecionada; x ++){
            TrabalhoProducao novoTrabalho = defineNovoModeloTrabalhoProducao();
            trabalhoProducaoViewModel.salvaNovoTrabalhoProducao(novoTrabalho).observe(this, resposta -> {
                if (resposta.getErro() == null) {
                    if (contador == quantidadeSelecionada) {
                        finish();
                    }
                    contador += 1;
                }
            });
        }
    }

    private TrabalhoProducao defineNovoModeloTrabalhoProducao() {
        String novoId = geraIdAleatorio();
        String licencaSelecionada = autoCompleteLicenca.getText().toString();
        CheckBox checkRecorrencia=findViewById(R.id.checkBoxProducaoRecorrenteConfirmaTrabalho);
        int experiencia = trabalhoRecebido.getExperiencia();
        if (comparaString(licencaSelecionada,"licença de produção do principiante")){
            experiencia = (int) (experiencia * 1.5);
        }
        return new TrabalhoProducao(
                novoId,
                trabalhoRecebido.getId(),
                trabalhoRecebido.getNome(),
                trabalhoRecebido.getNomeProducao(),
                trabalhoRecebido.getProfissao(),
                trabalhoRecebido.getRaridade(),
                trabalhoRecebido.getTrabalhoNecessario(),
                trabalhoRecebido.getNivel(),
                experiencia,
                licencaSelecionada,
                0,
                checkRecorrencia.isChecked());
    }
}