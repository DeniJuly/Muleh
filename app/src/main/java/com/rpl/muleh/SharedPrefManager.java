package com.rpl.muleh;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SharedPrefManager {
    private static SharedPrefManager instance;
    private static Context ctx;

    private static final String SHARED_PREF_NAME = "shared";
    public static final String KEY_EMAIL    = "email";
    public static final String KEY_ID       = "no";

    private SharedPrefManager(Context context) {
        ctx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public boolean userMasuk(String id, String email){
        SharedPreferences sp = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(KEY_ID,id);
        editor.putString(KEY_EMAIL,email);

        editor.apply();
        return true;
    }

    public boolean sudahMasuk(){
        SharedPreferences sp = ctx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        if(sp.getString(KEY_EMAIL,null)!=null){
            return true;
        }
        return false;
    }

    public boolean keluar(){
        SharedPreferences sp = ctx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public String getEmail() {
        SharedPreferences sp = ctx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return  sp.getString(KEY_EMAIL,null);
    }

    public static String getIdUser() {
        SharedPreferences sp = ctx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return  sp.getString(KEY_ID,null);
    }


}
