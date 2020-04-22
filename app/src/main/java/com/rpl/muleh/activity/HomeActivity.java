package com.rpl.muleh.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TextView;
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

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rpl.muleh.ApiConnect;
import com.rpl.muleh.MyLocationService;
import com.rpl.muleh.R;
import com.rpl.muleh.RequestHandler;
import com.rpl.muleh.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements OnClickListener {

    private TextView tvSelamat,tvNama;
    private Button btnProfile, btnCariTransportasi, btnKeluar;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    static HomeActivity instance;

    public static HomeActivity getInstance(){return instance;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        instance = this;

        if (!SharedPrefManager.getInstance(this).sudahMasuk()){
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(i);
        }
        updateLocation();

        tvNama = findViewById(R.id.tvNama);
        tvSelamat = findViewById(R.id.tvSelamat);
        btnProfile = findViewById(R.id.btnProfile);
        btnCariTransportasi = findViewById(R.id.btnCariTransportasi);

        getUserData();
        setSelamat();

        btnCariTransportasi.setOnClickListener(this);

        btnProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        btnKeluar = findViewById(R.id.btnKeluar);
        btnKeluar.setOnClickListener(this);

    }

    private void updateLocation() {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE
            );
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent i = new Intent(HomeActivity.this, MyLocationService.class);
        i.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            updateLocation();
        }
    }

    private void setSelamat() {
        Calendar c = Calendar.getInstance();
        int timeOfDay;
        timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if(timeOfDay >= 0 && timeOfDay < 11){
            tvSelamat.setText("Selamat Pagi,");
        }else if(timeOfDay >= 11 && timeOfDay < 14){
            tvSelamat.setText("Selamat Siang,");
        }else if(timeOfDay >= 14 && timeOfDay < 18){
            tvSelamat.setText("Selamat Sore,");
        }else if(timeOfDay >= 18 && timeOfDay < 24){
            tvSelamat.setText("Selamat Malam,");
        }
    }

    public void getUserData(){

        final String email = SharedPrefManager.getInstance(this).getEmail();
        final String id    = SharedPrefManager.getInstance(this).getIdUser();

        StringRequest sr = new StringRequest(
                Request.Method.POST,
                ApiConnect.URL_GET_DATA_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")){
                                //  set edit teks value
                                tvNama.setText(jsonObject.getString("nama"));
                            }else{
                                Toast.makeText(
                                        getApplicationContext(),
                                        "gagal mendapatkan data user",
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
                        }
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
        if(v == btnKeluar){
            SharedPrefManager.getInstance(this).keluar();
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }else if(v == btnCariTransportasi){
            Intent i = new Intent(HomeActivity.this, CariTransportasiActivity.class);
            startActivity(i);
        }
    }

    public void saveLocationUpdate(final Location location) {
        HomeActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Double latitude = location.getLatitude();
                Double longitude= location.getLongitude();

                String userId = SharedPrefManager.getIdUser();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("passengerLocaiton");
                GeoFire geoFire = new GeoFire(databaseReference);
                geoFire.setLocation(userId,new GeoLocation(latitude,longitude), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

