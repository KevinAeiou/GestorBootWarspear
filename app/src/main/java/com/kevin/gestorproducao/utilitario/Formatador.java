package com.kevin.gestorproducao.utilitario;

import java.text.NumberFormat;
import java.util.Locale;

public class Formatador {

    private static final Locale LOCALE_BR =
            new Locale("pt", "BR");

    public static String formatarMilhar(int valor) {
        return NumberFormat
                .getInstance(LOCALE_BR)
                .format(valor);
    }

    public static String formatarMilhar(long valor) {
        return NumberFormat
                .getInstance(LOCALE_BR)
                .format(valor);
    }
}
