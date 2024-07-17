package com.kevin.ceep.model;
import java.io.Serializable;
import java.util.ArrayList;

public class Profissao implements Serializable {
    private String nome;
    private Integer experiencia;
    private boolean prioridade;
    private ArrayList<Integer> xpNiveis;

    public Profissao(){}

    public Profissao(String nome, Integer experiencia, boolean prioridade) {
        this.nome = nome;
        this.experiencia = experiencia;
        this.prioridade = prioridade;
        this.xpNiveis = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public Integer getExperiencia() {
        return experiencia;
    }

    public boolean isPrioridade() {
        return prioridade;
    }

    public Integer getNivel() {
        xpNiveis.add(20);
        xpNiveis.add(200);
        xpNiveis.add(540);
        xpNiveis.add(1250);
        xpNiveis.add(2550);
        xpNiveis.add(4700);
        xpNiveis.add(7990);
        xpNiveis.add(12770);
        xpNiveis.add(19440);
        xpNiveis.add(28440);
        xpNiveis.add(40270);
        xpNiveis.add(55450);
        xpNiveis.add(74570);
        xpNiveis.add(98250);
        xpNiveis.add(127180);
        xpNiveis.add(156110);
        xpNiveis.add(185040);
        xpNiveis.add(215000);
        xpNiveis.add(245000);
        xpNiveis.add(300000);
        xpNiveis.add(375000);
        xpNiveis.add(470000);
        xpNiveis.add(585000);
        xpNiveis.add(705000);
        xpNiveis.add(830000);
        int i;
        for (i = 0; i < xpNiveis.size()-1; i ++){
            if (experiencia >= xpNiveis.get(i) && experiencia < xpNiveis.get(i + 1)) {
                return i + 2;
            }
        }
        return i + 2;
    }

    public int getXpRestante(int xpNecessario) {
        for (int i=0; i<xpNiveis.size();i++){
            if (i==0 && experiencia<xpNiveis.get(i)){
                return experiencia;
            }else if (i>=1 && experiencia>=xpNiveis.get(i-1) && experiencia<xpNiveis.get(i)){
                return xpNecessario-(experiencia-xpNiveis.get(i-1));
            }
        }
        return 0;
    }

    public int getXpNecessario() {

        return 0;
    }
}
