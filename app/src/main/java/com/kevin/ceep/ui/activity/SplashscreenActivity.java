package com.kevin.ceep.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kevin.ceep.R;

@SuppressLint("CustomSplashScreen")
public class SplashscreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);

        if (preferences.contains("ja_abriu_app")) {
            vaiParaEntraUsuarioActivity();
        } else {
            adicionarPreferenceJaAbriu(preferences);
            mostrarSplash();
        }
    }

    private void mostrarSplash() {
        Handler handler = new Handler();
        handler.postDelayed(this::vaiParaEntraUsuarioActivity, 3000);
    }

    private void adicionarPreferenceJaAbriu(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("ja_abriu_app", true);
        editor.apply();
    }

    private void vaiParaEntraUsuarioActivity() {
        Intent vaiParaEntraUsuarioActivity = new Intent(getApplicationContext(),EntrarUsuarioActivity.class);
        startActivity(vaiParaEntraUsuarioActivity);
        finish();
    }
}
