package com.kevin.ceep.ui;
import java.text.Normalizer;

public class Utilitario {
    public static String removeAcentos(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
    public static boolean comparaString(String string1, String string2) {
        return removeAcentos(string1).toLowerCase().replace(" ","").contains(removeAcentos(string2).toLowerCase().replace(" ",""));
    }
}
