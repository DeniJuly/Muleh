package com.rpl.muleh.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rpl.muleh.ApiConnect;
import com.rpl.muleh.R;
import com.rpl.muleh.RequestHandler;
import com.rpl.muleh.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DaftarActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etNama, etEmail, etPassword;
    private Button btnDaftar;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        if (SharedPrefManager.getInstance(this).sudahMasuk()){
            Intent i = new Intent(DaftarActivity.this, HomeActivity.class);
            startActivity(i);
        }

        etNama      = findViewById(R.id.etNama);
        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);

        etNama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etNama.getText().toString().length() <=0 ){
                    etNama.setBackgroundResource(R.drawable.bg_et_error);
                    etNama.setError("di isi dulu sayang");
                    cekForm();
                }else{
                    etNama.setBackgroundResource(R.drawable.bg_et);
                    etNama.setError(null);
                    cekForm();
                }
            }
        });
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etEmail.getText().toString().length() <=0 ){
                    etEmail.setBackgroundResource(R.drawable.bg_et_error);
                    etEmail.setError("di isi dulu sayang");
                    cekForm();
                }else{
                    etEmail.setBackgroundResource(R.drawable.bg_et);
                    etEmail.setError(null);
                    cekForm();
                }

            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = etPassword.getText().toString();
                if (password.length() <=0 ){
                    etPassword.setBackgroundResource(R.drawable.bg_et_error);
                    etPassword.setError("di isi dulu sayang");
                    cekForm();
                }else if (password.length() < 6) {
                    etPassword.setBackgroundResource(R.drawable.bg_et_error);
                    etPassword.setError("minimal panjang password 6 karakter");
                    cekForm();
                }else {
                    etPassword.setBackgroundResource(R.drawable.bg_et);
                    etPassword.setError(null);
                    cekForm();
                }
            }
        });

        btnDaftar   = findViewById(R.id.btnDaftar);
        btnDaftar.setOnClickListener(this);

        pd = new ProgressDialog(this);
        pd.setCancelable(false);

    }

    private void cekForm() {
        String nama     = etNama.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String pass     = etPassword.getText().toString().trim();
        if (nama.length() > 0 && email.length() >0 && pass.length() > 0 && pass.length() >= 6){
            btnDaftar.setEnabled(true);
        }else{
            btnDaftar.setEnabled(false);
        }
    }

    public void showMasukActivity(View view) {
        Intent i = new Intent(DaftarActivity.this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        if(v == btnDaftar){
            userDaftar();
        }
    }

    private void userDaftar() {
        final String nama     = etNama.getText().toString().trim();
        final String email    = etEmail.getText().toString().trim();
        final String pass     = etPassword.getText().toString().trim();

        pd.setMessage("Daftar...");
        pd.show();

        StringRequest sr = new StringRequest(
                Request.Method.POST,
                ApiConnect.URL_DAFTAR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(
                                    getApplicationContext(),
                                    jsonObject.getString("message"),
                                    Toast.LENGTH_LONG
                            ).show();
                        } catch (JSONException e) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError){
                            Toast.makeText(
                                    getApplicationContext(),
                                    "request time out",
                                    Toast.LENGTH_LONG
                            ).show();
                        }else if (error instanceof ServerError){
                            Toast.makeText(
                                    getApplicationContext(),
                                    "server error",
                                    Toast.LENGTH_LONG
                            ).show();
                        }else if (error instanceof NetworkError){
                            Toast.makeText(
                                    getApplicationContext(),
                                    "network error",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("nama",nama);
                params.put("email",email);
                params.put("password",pass);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(sr);
//        RequestQueue rq = Volley.newRequestQueue(this);
//        rq.add(sr);

    }
}
