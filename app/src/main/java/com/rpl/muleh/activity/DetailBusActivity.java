package com.rpl.muleh.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.rpl.muleh.adapter.RVGaleriTransportasiAdapter;
import com.rpl.muleh.method.GaleriTransportasi;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailBusActivity extends AppCompatActivity {

    public String BASE_URL = "https://muleh.iamdeni.com/asset/foto-galeri-transportasi/";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView tvNamaBus,tvNomorBus,tvPreviewBus;
    private ImageView ivProfile;
    private String idUser, id, nama, nomor, img;
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout lLayoutDetailBus;
    private List<GaleriTransportasi> galeriTransportasi;
    private Button btnDriverLocation, btnCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_bus);

        Intent intent = getIntent();
        idUser = intent.getExtras().getString("id_user");
        id   = intent.getExtras().getString("id");
        nama = intent.getExtras().getString("nama");
        nomor = intent.getExtras().getString("nomor");
        img   = intent.getExtras().getString("foto");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(nama);

        tvNamaBus = findViewById(R.id.tvNamaBus);
        tvNomorBus= findViewById(R.id.tvNomorBus);
        ivProfile = findViewById(R.id.ivProfileBus);
        btnCall   = findViewById(R.id.btnCallBus);
        tvPreviewBus       = findViewById(R.id.tvPreviewBus);
        shimmerFrameLayout = findViewById(R.id.shimmerDetailBus);
        recyclerView     = findViewById(R.id.rvDetailTransportasi);
        lLayoutDetailBus = findViewById(R.id.lLayoutDetailBus);
        btnDriverLocation= findViewById(R.id.btnDriverLocation);

        tvNamaBus.setText(nama);
        tvNomorBus.setText(nomor);
        Picasso.with(this).load(img).into(ivProfile);

        getGaleriTransportasi();
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+nomor));
                startActivity(intent);
            }
        });
        btnDriverLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailBusActivity.this,DriverLocationActivity.class);
                i.putExtra("id",idUser);
                startActivity(i);
            }
        });
    }

    private void getGaleriTransportasi() {
        lLayoutDetailBus.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        StringRequest sr = new StringRequest(
                Request.Method.POST,
                ApiConnect.URL_GET_GALERI_TRANSPORTASI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        lLayoutDetailBus.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmer();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){
                                galeriTransportasi = new ArrayList<>();
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                int total = jsonArray.length();
                                for (int i = 0; i < total; i++){
                                    JSONObject jo = jsonArray.optJSONObject(i);
                                    String img = jo.optString("foto_transportasi");
                                    galeriTransportasi.add( new GaleriTransportasi(BASE_URL+img) );
                                }
                                setupRecyclerView(galeriTransportasi);
                            }else{
                                recyclerView.setVisibility(View.GONE);
                                tvPreviewBus.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        lLayoutDetailBus.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.setVisibility(View.GONE);
                        shimmerFrameLayout.stopShimmer();
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
                params.put("id",id);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(sr);
    }

    private void setupRecyclerView(List<GaleriTransportasi> galeriTransportasi) {
        layoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RVGaleriTransportasiAdapter(this,galeriTransportasi);
        recyclerView.setAdapter(adapter);
    }
}
