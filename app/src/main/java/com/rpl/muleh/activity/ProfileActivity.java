package com.rpl.muleh.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rpl.muleh.ApiConnect;
import com.rpl.muleh.R;
import com.rpl.muleh.RequestHandler;
import com.rpl.muleh.SharedPrefManager;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvNama, tvNoTelp;
    private ImageView ivProfile;
    private Button btnEditProfile, btnKeluar, btnEditFotoProfile;
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout lLayoutProfile;
    private Bitmap bitmap;
    private ProgressDialog progressDialog;

    final int CODE_GALLERY_REQUEST = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (!SharedPrefManager.getInstance(this).sudahMasuk()){
            Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(i);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        //  mendefinisikan id pada variabel button
        btnEditProfile  = findViewById(R.id.btnEditProfile);
        btnKeluar       = findViewById(R.id.btnKeluar);
        btnEditFotoProfile = findViewById(R.id.btnEditFotoProfile);

        shimmerFrameLayout = findViewById(R.id.shimmerProfile);
        lLayoutProfile  = findViewById(R.id.lLayoutProfile);

        //  Set onclick listener pada variabel button
        btnEditProfile.setOnClickListener(this);
        btnKeluar.setOnClickListener(this);
        btnEditFotoProfile.setOnClickListener(this);

        //  mendefinisikan id pada veriabel text view
        tvNoTelp        = findViewById(R.id.tvNomor);
        tvNama          = findViewById(R.id.tvNama);

        //  mendefinisikan id pada variabel image view
        ivProfile       = findViewById(R.id.profile_image);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("ubah foto profile...");
        progressDialog.setCancelable(false);
        getDataUser();
    }

    public void getDataUser(){
        final String email = SharedPrefManager.getInstance(this).getEmail();
        final String id    = SharedPrefManager.getInstance(this).getIdUser();

        shimmerFrameLayout.setVisibility(View.VISIBLE);
        lLayoutProfile.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();

        StringRequest sr = new StringRequest(
                Request.Method.POST,
                ApiConnect.URL_GET_DATA_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        shimmerFrameLayout.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmer();
                        lLayoutProfile.setVisibility(View.VISIBLE);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")){

                                //  set isi text view
                                tvNoTelp.setText(jsonObject.getString("notelp"));
                                tvNama.setText(jsonObject.getString("nama"));

                                //  set resource image view
                                Picasso
                                        .with(getApplicationContext())
                                        .load(ApiConnect.URL_IMG_PROFILE+jsonObject.getString("imgProfile"))
                                        .networkPolicy(NetworkPolicy.NO_CACHE)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                        .into(ivProfile);

                            }else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_LONG
                                        ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        shimmerFrameLayout.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmer();
                        lLayoutProfile.setVisibility(View.VISIBLE);
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
        if(v == btnEditProfile){
            Intent i = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(i);
        }else if(v == btnKeluar){
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

            builder.setMessage("Apakah kamu yakin mau keluar?");
            builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SharedPrefManager.getInstance(ProfileActivity.this).keluar();
                    Intent i = new Intent(ProfileActivity.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
            });

            builder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else if (v == btnEditFotoProfile){
            ActivityCompat.requestPermissions(
                    ProfileActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    CODE_GALLERY_REQUEST
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODE_GALLERY_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(
                                intent,
                                "Pilih Foto"
                        ),
                        CODE_GALLERY_REQUEST
                );
            }else {
                Toast.makeText(
                        getApplicationContext(),
                        "Muleh Tidak Memiliki Akses Ke Gallery!",
                        Toast.LENGTH_LONG
                ).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CODE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null){
            Uri filePath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                uploadFotoProfile();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadFotoProfile() {
        progressDialog.show();
        final String id = SharedPrefManager.getInstance(this).getIdUser();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                ApiConnect.URL_UBAH_FOTO_PENUMPANG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){
                                getDataUser();
                            }else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(
                                getApplicationContext(),
                                error.toString(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                String imageData = imageToString(bitmap);

                params.put("image",imageData);
                params.put("id",id);

                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, outputStream);
        byte[] imageByte = outputStream.toByteArray();

        String encodeImage = Base64.encodeToString(imageByte, Base64.DEFAULT);
        return encodeImage;
    }
}
