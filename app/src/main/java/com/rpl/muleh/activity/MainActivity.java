package com.rpl.muleh.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rpl.muleh.R;
import com.rpl.muleh.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    Button btnMasuk,btnDaftar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (SharedPrefManager.getInstance(this).sudahMasuk()){
            Intent i = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(i);
        }

        btnMasuk = findViewById(R.id.btnMasuk);
        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inten1 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(inten1);
                finish();
            }
        });

        btnDaftar = findViewById(R.id.btnDaftar);
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inten1 = new Intent(MainActivity.this, DaftarActivity.class);
                startActivity(inten1);
                finish();
            }
        });
    }
}
