package com.rpl.muleh.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.rpl.muleh.SharedPrefManager;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SharedPrefManager.getInstance(this).sudahMasuk()){
            Intent i = new Intent(SplashScreen.this, HomeActivity.class);
            startActivity(i);
            finish();
        }else{
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
