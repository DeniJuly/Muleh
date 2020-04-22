package com.rpl.muleh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationResult;
import com.rpl.muleh.activity.HomeActivity;

public class MyLocationService extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATE="com.rpl.muleh.UPDATE_LOCATION";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent!=null){
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATE.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if (result!=null){
                    Location location = result.getLastLocation();
                    try {
                        HomeActivity.getInstance().saveLocationUpdate(location);
                    }catch (Exception e){

                    }
                }
            }
        }
    }
}
