package com.kevin.ceep.repository;

import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_ESTOQUE;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_LISTA_PERSONAGEM;
import static com.kevin.ceep.ui.activity.NotaActivityConstantes.CHAVE_USUARIOS;
import static com.kevin.ceep.utilitario.Utilitario.comparaString;
import static com.kevin.ceep.utilitario.Utilitario.geraIdAleatorio;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevin.ceep.model.TrabalhoEstoque;
import com.kevin.ceep.model.TrabalhoProducao;

public class PersonagemRepository {
    private DatabaseReference minhaReferencia;
    private final String usuarioID, personagemID;

    public PersonagemRepository(String usuarioID, String personagemID) {
        this.usuarioID = usuarioID;
        this.personagemID = personagemID;
        minhaReferencia = FirebaseDatabase.getInstance().getReference(CHAVE_USUARIOS);
    }

    public void modificaQuantidadeTrabalhoNecessarioNoEstoque(TrabalhoProducao trabalhoProducao) {
        String[] listaTrabalhosNecessarios = trabalhoProducao.getTrabalhoNecessario().split(",");
        minhaReferencia.child(usuarioID).child(CHAVE_LISTA_PERSONAGEM)
                .child(personagemID).child(CHAVE_LISTA_ESTOQUE).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            TrabalhoEstoque trabalho = dn.getValue(TrabalhoEstoque.class);
                            if (trabalho != null) {
                                for (String nome: listaTrabalhosNecessarios) {
                                    if (comparaString(trabalho.getNome(), nome)) {
                                        int novaQuantidade = trabalho.getQuantidade() - 1;
                                        if (novaQuantidade < 0) {
                                            novaQuantidade = 0;
                                        }
                                        alteraQuantidadeTrabalhoNoEstoque(trabalho, novaQuantidade);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void alteraQuantidadeTrabalhoNoEstoque(TrabalhoEstoque trabalho, int novaQuantidade) {
        if (trabalho.getId() != null) {
            minhaReferencia.child(usuarioID).child(CHAVE_LISTA_PERSONAGEM)
                    .child(personagemID).child(CHAVE_LISTA_ESTOQUE)
                    .child(trabalho.getId()).child("quantidade").setValue(novaQuantidade);
        }
    }

    public void modificaTrabalhoNoEstoque(TrabalhoProducao trabalhoConcluido) {
        minhaReferencia.child(usuarioID).child(CHAVE_LISTA_PERSONAGEM).
                child(personagemID).child(CHAVE_LISTA_ESTOQUE).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        TrabalhoEstoque trabalhoEncontrado = null;
                        for (DataSnapshot dn:dataSnapshot.getChildren()){
                            TrabalhoEstoque trabalho = dn.getValue(TrabalhoEstoque.class);
                            if (trabalho != null && comparaString(trabalho.getNome(), trabalhoConcluido.getNomeProducao())) {
                                trabalhoEncontrado = trabalho;
                                break;
                            }
                        }
                        if (trabalhoEncontrado != null) {
                            int novaQuantidade = trabalhoEncontrado.getQuantidade()+1;
                            alteraQuantidadeTrabalhoNoEstoque(trabalhoEncontrado, novaQuantidade);
                        } else {
                            TrabalhoEstoque novoTrabalho = defineNovoTrabalho(trabalhoConcluido);
                            adicionaNovoTrabalhoAoEstoque(novoTrabalho);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @NonNull
    private static TrabalhoEstoque defineNovoTrabalho(TrabalhoProducao trabalhoConcluido) {
        String novoId = geraIdAleatorio();
        return new TrabalhoEstoque(
                novoId,
                trabalhoConcluido.getNomeProducao(),
                trabalhoConcluido.getNome(),
                trabalhoConcluido.getProfissao(),
                trabalhoConcluido.getRaridade(),
                trabalhoConcluido.getTrabalhoNecessario(),
                trabalhoConcluido.getNivel(),
                trabalhoConcluido.getExperiencia(),
                1
        );
    }

    private void adicionaNovoTrabalhoAoEstoque(TrabalhoEstoque novoTrabalho) {
        minhaReferencia.child(usuarioID).child(CHAVE_LISTA_PERSONAGEM).
                child(personagemID).child(CHAVE_LISTA_ESTOQUE).child(novoTrabalho.getId()).setValue(novoTrabalho);
    }
}
