package com.kevin.gestorproducao.utilitario;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Utilitario {
    public static String removeAcentos(String string) {
        if (string == null) {
            return "";
        }
        return Normalizer.normalize(string, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
    public static String limpaString(String string) {
        return removeAcentos(string).toLowerCase().replace(" ","");
    }
    public static boolean comparaString(String string1, String string2) {
        return limpaString(string1).equals(limpaString(string2));
    }
    public static boolean stringContemString(String string1, String string2) {
        return removeAcentos(string1).toLowerCase().replace(" ","").contains(removeAcentos(string2).toLowerCase().replace(" ",""));
    }

    public static String geraIdAleatorio() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String formatarTimestamp(Long timestamp) {
        if (timestamp == null) return "";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    public static Integer extrairNivel(String texto) {
        String numeros = texto.replaceAll("\\D+", "");

        if (numeros.isEmpty()) {
            return null;
        }

        return Integer.parseInt(numeros);
    }

    public static String extrairDescricao(String texto) {
        return texto.replaceAll("\\d+", "").trim();
    }
}

