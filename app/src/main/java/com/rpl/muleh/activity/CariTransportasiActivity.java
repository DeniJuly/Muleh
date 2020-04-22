package com.rpl.muleh.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.rpl.muleh.ApiConnect;
import com.rpl.muleh.R;
import com.rpl.muleh.RequestHandler;
import com.rpl.muleh.adapter.RVTransportasiAdapter;
import com.rpl.muleh.method.Transportasi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CariTransportasiActivity extends AppCompatActivity {

    private String BASE_URL = "https://muleh.iamdeni.com/asset/foto-transportasi/";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Transportasi> lstTransportasi;
    private Spinner spinner;
    private LinearLayout lLayoutError;
    private ShimmerFrameLayout shimmerFrameLayout;
    private ImageView ivErrorImage;
    private TextView tvErrorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_transportasi);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cari Transportasi");

        spinner         = findViewById(R.id.spinnerTujuan);
        lLayoutError    = findViewById(R.id.lLayoutError);
        recyclerView    = findViewById(R.id.rvTransportasi);
        shimmerFrameLayout = findViewById(R.id.shimmer);
        ivErrorImage    = findViewById(R.id.ivErrorImage);
        tvErrorText     = findViewById(R.id.tvErrorText);

        getDataTransportasi();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getDataTransportasi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(
                        getApplicationContext(),
                        "Tolong pilih tujuan anda",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

    }

    private void getDataTransportasi() {
        final String tujuan = spinner.getSelectedItem().toString().trim();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();

        StringRequest sr = new StringRequest(
                Request.Method.POST,
                ApiConnect.URL_GET_TRANSPORTASI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("error")){

                                recyclerView.setVisibility(View.VISIBLE);

                                lstTransportasi = new ArrayList<>();
                                JSONArray result = jsonObject.getJSONArray("data");
                                int total = result.length();
                                for (int i = 0; i < total;i++){

                                    JSONObject jo = result.optJSONObject(i);
                                    String idUser   = jo.optString("id_user");
                                    String id       = jo.optString("id_transportasi");
                                    String nama     = jo.optString("nama_transportasi");
                                    String nomor    = jo.optString("nomor_transportasi");
                                    String img      = jo.optString("foto_transportasi");
                                    String asal     = jo.optString("asal");
                                    String tujuan   = jo.optString("tujuan");
                                    Integer status  = jo.optInt("status_transportasi");
                                    String  real_status = "tidak tau";
                                    if (status == 1){
                                        real_status = "aktif";
                                    }else{
                                        real_status = "tidak aktif";
                                    }
                                    lstTransportasi.add(new Transportasi(
                                            idUser,
                                            id,
                                            nama,
                                            nomor,
                                            BASE_URL+img,
                                            real_status,
                                            asal,
                                            tujuan
                                    ));

                                }
                                setupRecyclerView(lstTransportasi);
                            }else {
                                recyclerView.setVisibility(View.GONE);
                                setupError("NOT_FOUND");
                                lLayoutError.setVisibility(View.VISIBLE);
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

                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);

                        if (error instanceof TimeoutError || error instanceof NetworkError){
                            setupError("TIMEOUT");
                            lLayoutError.setVisibility(View.VISIBLE);
                        }else if (error instanceof ServerError){
                            setupError("SERVER");
                            lLayoutError.setVisibility(View.VISIBLE);
                        }
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap();
                params.put("tujuan",tujuan);
                return params;

            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(sr);
    }

    private void setupError(String server) {
        if (server == "NETWORK"){
            ivErrorImage.setImageResource(R.drawable.icon_grey_error_outline);
            tvErrorText.setText("Tidak dapat terhubung ke server");
        }else if (server == "SERVER"){
            ivErrorImage.setImageResource(R.drawable.icon_grey_error_outline);
            tvErrorText.setText("Server error");
        }else if (server == "NOT_FOUND"){
            ivErrorImage.setImageResource(R.drawable.icon_grey_search);
            tvErrorText.setText("Transportasi tidak ditemukan");
        }
    }

    private void setupRecyclerView(List<Transportasi> lstTransportasi) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RVTransportasiAdapter(this,lstTransportasi);
        recyclerView.setAdapter(mAdapter);
    }

}
