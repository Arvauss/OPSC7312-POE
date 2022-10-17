package com.example.marksapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
//import com.google.maps.model.LatLng;
import com.example.marksapp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.model.PlacesSearchResult;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    public static boolean isGpsEnabled = false;
    public static double lat = 0, lng = 0;
    public static LatLng destLatLng;
    public static PlacesSearchResult[] nearbyPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            Bundle dest = getIntent().getExtras();
            if (!dest.isEmpty()) {
                destLatLng = new LatLng(dest.getDouble("DestLat"), dest.getDouble("DestLng"));
            } else {
                destLatLng = null;
            }
        } catch (Exception e){
            Toast.makeText(this, "No destination yet", Toast.LENGTH_SHORT).show();
        }






        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mMap = googleMap;
        GetLocation(mMap, lm);
        nearbyPlaces = new NearbySearch().searchResponse(lat, lng).results;
        for (PlacesSearchResult place: nearbyPlaces) {
            Log.d("123456", "onMapReady: " + place.formattedAddress);
            mMap.addMarker(new MarkerOptions().position(new LatLng(place.geometry.location.lat, place.geometry.location.lng)).title(place.formattedAddress));
        }

    }
    @SuppressLint("MissingPermission")
    public void GetLocation(GoogleMap mMap, LocationManager lm){
        //
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
                    mMap.setMinZoomPreference(14.0f);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(curLoc));

                    NearbyPlacesTask task = new NearbyPlacesTask();
                    task.execute(curLoc);

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

    class NearbyPlacesTask extends AsyncTask<LatLng, Void, Void>{

        @Override
        protected Void doInBackground(LatLng... latlngs) {
          //  LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            nearbyPlaces = new NearbySearch().searchResponse(latlngs[0].latitude, latlngs[0].longitude).results;
            return null;
        }

        @Override
        protected void onPostExecute(Void p) {
            super.onPostExecute(p);
            for (PlacesSearchResult place: nearbyPlaces) {
                Log.d("123456", "onMapReady: " + place.formattedAddress);
                mMap.addMarker(new MarkerOptions().position(new LatLng(place.geometry.location.lat, place.geometry.location.lng)).title(place.formattedAddress));
            }

        }
    }


}