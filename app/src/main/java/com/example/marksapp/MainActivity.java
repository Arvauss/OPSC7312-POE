package com.example.marksapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

public class MainActivity extends AppCompatActivity {

    public static boolean isGpsEnabled = false;
    public static double lat = 0, lng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // TODO: GETS USER'S CURRENT LOCATION, REQUIRES PERMISSION CHECK, REMOVE COMMENTS AFTER
        /*if (!isGpsEnabled)
        {
            Intent enable_gps_intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(enable_gps_intent);
        } else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 30, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }
                @Override
                public void onProviderEnabled(@NonNull String provider) {

                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
            });

        }*/
    }
}