package com.rpl.muleh.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
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

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etNama, etNomor;
    private Button btnSimpan;
    private ProgressBar loadingEditProfile;
    private LinearLayout linearLayout;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (!SharedPrefManager.getInstance(this).sudahMasuk()){
            Intent i = new Intent(EditProfileActivity.this, LoginActivity.class);
            startActivity(i);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit Profile");

        //  mendefinisikan Edit teks dengan id
        etNama      = findViewById(R.id.etNama);
        etNomor     = findViewById(R.id.etNoTelp);
        loadingEditProfile = findViewById(R.id.loadingEditProfile);
        linearLayout= findViewById(R.id.lLayoutEditProfile);

        //  mendefinisikan Button dengan id
        btnSimpan = findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(this);

        pd = new ProgressDialog(this);

        getDataUser();
    }
    private void getDataUser(){
        final String email = SharedPrefManager.getInstance(this).getEmail();
        final String id    = SharedPrefManager.getInstance(this).getIdUser();

        loadingEditProfile.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);

        StringRequest sr = new StringRequest(
                Request.Method.POST,
                ApiConnect.URL_GET_DATA_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingEditProfile.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")){
                                //  set edit teks value
                                etNama.setText(jsonObject.getString("nama"));
                                etNomor.setText(jsonObject.getString("notelp"));
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
                        loadingEditProfile.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("id_user",id);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(sr);
    }

    @Override
    public void onClick(View v) {
        if(v == btnSimpan){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Serius nih mau diubah?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ubahDataUser();
                        }
                    })
                    .setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create();
            builder.show();
        }
    }

    //  method mengubah data user
    private void ubahDataUser() {
        final String nama    = etNama.getText().toString().trim();
        final String nomor   = etNomor.getText().toString().trim();
        final String id_user = SharedPrefManager.getInstance(this).getIdUser();

        pd.setMessage("ubah data user...");
        pd.show();
        StringRequest sr = new StringRequest(
                Request.Method.POST,
                ApiConnect.URL_UBAH_DATA_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")){
                                Intent i = new Intent(EditProfileActivity.this, ProfileActivity.class);
                                startActivity(i);
                            }else {
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
                        Toast.makeText(
                                getApplicationContext(),
                                error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id_user",id_user);
                params.put("nama",nama);
                params.put("nomor",nomor);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(sr);
    }
}
