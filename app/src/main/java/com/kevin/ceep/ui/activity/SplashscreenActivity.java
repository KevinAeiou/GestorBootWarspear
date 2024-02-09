package com.kevin.ceep.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kevin.ceep.R;

public class SplashscreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        Handler handler = new Handler();
        handler.postDelayed(() -> vaiParaEntraUsuarioActivity(), 3000);
    }

    private void vaiParaEntraUsuarioActivity() {
        Intent vaiParaEntraUsuarioActivity = new Intent(getApplicationContext(),EntrarUsuarioActivity.class);
        startActivity(vaiParaEntraUsuarioActivity);
        finish();
    }
}
