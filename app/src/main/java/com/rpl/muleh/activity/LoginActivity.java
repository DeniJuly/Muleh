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
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.rpl.muleh.ApiConnect;
import com.rpl.muleh.R;
import com.rpl.muleh.RequestHandler;
import com.rpl.muleh.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etEmail, etPassword;
    private Button btnMasuk;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (SharedPrefManager.getInstance(this).sudahMasuk()){
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
        }

        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = etEmail.getText().toString();
                if (email.length() <= 0){
                    etEmail.setBackgroundResource(R.drawable.bg_et_error);
                    etEmail.setError("input masih kosong");
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
                if(password.length() <= 0){
                    etPassword.setBackgroundResource(R.drawable.bg_et_error);
                    etPassword.setError("input masih kosong");
                    cekForm();
                }else{
                    etPassword.setBackgroundResource(R.drawable.bg_et);
                    etPassword.setError(null);
                    cekForm();
                }
            }
        });

        btnMasuk    = findViewById(R.id.btnMasuk);
        btnMasuk.setOnClickListener(this);

        pd          = new ProgressDialog(this);
        pd.setMessage("Masuk...");
        pd.setCancelable(false);
    }

    private void cekForm() {
        String email = etEmail.getText().toString();
        String pass  = etPassword.getText().toString();
        if (email.length() > 0 && pass.length() > 0){
            btnMasuk.setEnabled(true);
        }else{
            btnMasuk.setEnabled(false);
        }
    }

    public void showDaftarActivity(View view) {
        Intent i = new Intent(LoginActivity.this, DaftarActivity.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        if(v == btnMasuk){
            userMasuk();
        }
    }

    private void userMasuk() {
        final String email = etEmail.getText().toString().trim();
        final String pass  = etPassword.getText().toString().trim();

        pd.show();

        StringRequest sr = new StringRequest(
                Request.Method.POST,
                ApiConnect.URL_MASUK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")){
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .userMasuk(
                                                jsonObject.getString("id"),
                                                jsonObject.getString("email")
                                        );
                                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(
                                        getApplicationContext(),
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
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
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "request time out",
                                    Toast.LENGTH_LONG
                            ).show();
                        } else if (error instanceof ServerError) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "server error",
                                    Toast.LENGTH_LONG
                            ).show();
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "network error",
                                    Toast.LENGTH_LONG
                            ).show();
                        } else if (error instanceof ParseError) {
                            // Indicates that the server response could not be parsed
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("password",pass);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(sr);
    }
}
