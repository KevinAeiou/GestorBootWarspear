package com.kevin.ceep.ui.activity;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TITULO_PRODUTO_VENDIDO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_TRABALHO;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.R;
import com.kevin.ceep.databinding.ActivityAtributosProdutoVendidoBinding;
import com.kevin.ceep.model.Personagem;
import com.kevin.ceep.model.ProdutoVendido;

import java.util.ArrayList;
import java.util.Objects;

public class AtributosProdutoVendidoActivity extends AppCompatActivity {
    private ActivityAtributosProdutoVendidoBinding binding;
    private MaterialTextView txtNomeProdutoVendido, txtDataProdutoVendido, txtValorProdutoVendido, txtQuantidadeProdutoVendido;
    private AutoCompleteTextView autoCompleteIdPersonagemProdutoVendido;
    private ArrayList<Personagem> todosPersonagens;
    private ArrayList<String> todosNomesPersonagens;
    private ProdutoVendido produtoRecebido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAtributosProdutoVendidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(CHAVE_TITULO_PRODUTO_VENDIDO);
        inicializaComponentes();
        recebeDadosIntent();
        pegaTodosPersoangens();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario_nota_salva, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemMenuSalvaTrabalho) {
            ListAdapter meuAdapter = autoCompleteIdPersonagemProdutoVendido.getAdapter();
        }
        return super.onOptionsItemSelected(item);
    }

    private void pegaTodosPersoangens() {
        todosNomesPersonagens = new ArrayList<>();
        todosPersonagens = new ArrayList<>();
        String usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(CHAVE_USUARIOS);
        databaseReference.child(usuarioId).child(CHAVE_LISTA_PERSONAGEM).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        todosPersonagens.clear();
                        todosNomesPersonagens.clear();
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            Personagem personagem = dn.getValue(Personagem.class);
                            todosPersonagens.add(personagem);
                            assert personagem != null;
                            todosNomesPersonagens.add(personagem.getNome());
                            if (personagem.getId().equals(produtoRecebido.getNomePersonagem())){
                                autoCompleteIdPersonagemProdutoVendido.setText(personagem.getNome());
                            }
                            configuraAutoCompleteIdPersonagem();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if ( dadosRecebidos != null && dadosRecebidos.hasExtra(CHAVE_TRABALHO) && dadosRecebidos.hasExtra(CHAVE_PERSONAGEM)){
            defineValoresCampos(dadosRecebidos);
        }
    }

    private void defineValoresCampos(Intent dadosRecebidos) {
        produtoRecebido = (ProdutoVendido) dadosRecebidos.getSerializableExtra(CHAVE_TRABALHO);
        assert produtoRecebido != null;
        txtNomeProdutoVendido.setText(produtoRecebido.getNomeProduto());
        txtDataProdutoVendido.setText(produtoRecebido.getDataVenda());
        txtValorProdutoVendido.setText(getString(R.string.stringOuroValor, produtoRecebido.getValorProduto()));
        txtQuantidadeProdutoVendido.setText(getString(R.string.stringQuantidadeValor, produtoRecebido.getQuantidadeProduto()));
    }

    private void inicializaComponentes() {
        txtNomeProdutoVendido = binding.txtNomeProdutoVendido;
        txtDataProdutoVendido = binding.txtDataProdutoVendido;
        txtValorProdutoVendido = binding.txtValorProdutoVendido;
        txtQuantidadeProdutoVendido = binding.txtQuantidadeProdutoVendido;
        autoCompleteIdPersonagemProdutoVendido = binding.autoCompleteIdPersonagemProdutoVendido;
    }

    private void configuraAutoCompleteIdPersonagem() {
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(this, R.layout.item_dropdrown, todosNomesPersonagens);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteIdPersonagemProdutoVendido.setAdapter(adapterEstado);
    }

    @Override
    protected void onResume() {
        super.onResume();
        configuraAutoCompleteIdPersonagem();
    }
}