package com.example.marksapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.marksapp.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    public static boolean isGpsEnabled = false;
    public static double lat = 0, lng = 0;
    public static LatLng destLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle dest = getIntent().getExtras();
        destLatLng = new LatLng(dest.getDouble("DestLat"), dest.getDouble("DestLng"));
        Toast.makeText(this, destLatLng.toString(), Toast.LENGTH_SHORT).show();





        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GetLocation(mMap);
        // Add a marker in Sydney and move the camera

    }
    @SuppressLint("MissingPermission")
    public void GetLocation(GoogleMap mMap){
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGpsEnabled)
        {
            Intent enable_gps_intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(enable_gps_intent);
        } else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 30, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    // Toast.makeText(MainActivity.this, "Lat: " + lat + "\tLng: " + lng, Toast.LENGTH_SHORT).show();
                    LatLng curLoc = new LatLng(lat,lng);
                    mMap.addMarker(new MarkerOptions().position(curLoc).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(curLoc));
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

        }
    }
}