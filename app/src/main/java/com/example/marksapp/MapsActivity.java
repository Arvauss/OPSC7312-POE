package com.example.marksapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
//import com.google.maps.model.LatLng;
import com.example.marksapp.databinding.ActivityMapsBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.Distance;
import com.google.maps.model.Duration;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.Unit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    public static boolean isGpsEnabled = false;
    public static double lat = 0, lng = 0;
    public static LatLng destLatLng = null;
    public static PlacesSearchResult[] nearbyPlaces;
    public static String tDistance = "", tDuration = "";

    SwitchCompat MeasurementSwitch;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MapsInitializer.initialize(getApplicationContext(), MapsInitializer.Renderer.LATEST, this);

        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), getString(R.string._google_api_key));
        }

        mAuth  = FirebaseAuth.getInstance(); //need firebase authentication instance

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


        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG);
        AutocompleteSupportFragment destLocat = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.destLocation);
        destLocat.setPlaceFields(fields);

        destLocat.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                Log.d("123456", "Search onError:  " + status.getStatusMessage());
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mMap.clear();
                DisplayCurLocation();
                final Place destination = place;
                destLatLng = destination.getLatLng();
                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destLatLng, 14.0f));
                GetAndDisplayRoute();



            }
        });
        MeasurementSwitch = (SwitchCompat) findViewById(R.id.SwitchID);
        MeasurementSwitch.setText(MeasurementSwitch.getTextOff());
        MeasurementSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                if (MeasurementSwitch.isChecked()){
                    MeasurementSwitch.setText(MeasurementSwitch.getTextOn());
                    Unit pref = Unit.IMPERIAL;
                    reference.child(firebaseUser.getUid()).child("measurementPref").setValue(Unit.IMPERIAL).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Measurement preference is now " + MeasurementSwitch.getTextOn(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    MeasurementSwitch.setText(MeasurementSwitch.getTextOff());
                    Unit pref = Unit.METRIC;
                    reference.child(firebaseUser.getUid()).child("measurementPref").setValue(Unit.METRIC).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Measurement preference is now " + MeasurementSwitch.getTextOff(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }

    public void DisplayTripFrag(){
        //Initialises trip info fragment, sends duration & distance to be displayed
        FragmentManager fragman = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("Duration", tDuration);
        bundle.putString("Distance", tDistance);
        TripInfoFragment tif = new TripInfoFragment();
        tif.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragcontainer_id, tif, null). commitNow();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mMap = googleMap;

        mMap.clear();
        GetLocation(mMap);


        NearbyPlacesTask task = new NearbyPlacesTask();
        task.execute(new LatLng(lat, lng));



    }


    @SuppressLint("MissingPermission")
    public void GetLocation(GoogleMap mMap){
        //
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
                    LatLng curLoc = new LatLng(lat, lng);
                    NearbyPlacesTask task = new NearbyPlacesTask();
                    task.execute(curLoc);
                    DisplayCurLocation();

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
    public void DisplayCurLocation(){
        mMap.clear();
        LatLng curLoc = new LatLng(lat,lng);
        mMap.addMarker(new MarkerOptions().position(curLoc).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 14.0f));
    }
    public void GetAndDisplayRoute(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseUser.getUid()).child("measurementPref");

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Unit measurement = snapshot.child(firebaseUser.getUid()).child("measurementPref").getValue(Unit.class);
                mMap.addMarker(new MarkerOptions().position(destLatLng).title("Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                List<LatLng> path = new ArrayList<>();
                GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyALqIxRQNGQ11cUlmUEf4HY7dfQh6wp_9E").build();
                String startLatLng = lat + "," + lng;
                String destinationLatLng = destLatLng.latitude + "," + destLatLng.longitude;
                DirectionsApiRequest req = DirectionsApi.getDirections(context, startLatLng, destinationLatLng).units(measurement);
                try {
                    // Distance tripLength = null;
                    DirectionsResult res = req.await();
                    if (res.routes !=null && res.routes.length > 0){
                        DirectionsRoute route = res.routes[0];
                        if(route.legs !=null) {
                            for(int i=0 ; i<route.legs.length; i++){
                                DirectionsLeg leg = route.legs[i];
                                //Distance tripLength = leg.distance;
                                tDistance = leg.distance.humanReadable;
                                tDuration = leg.duration.humanReadable;
                                // Duration tripDuration = leg.duration;
                                // Toast.makeText(this, "Total Trip Distance:   " + tripLength.humanReadable + "   Trip Duration:   " + tripDuration.humanReadable , Toast.LENGTH_SHORT).show();
                                if(leg.steps !=null){
                                    for (int j=0; j<leg.steps.length; j++){
                                        DirectionsStep step = leg.steps[j];
                                        if (step.steps !=null && step.steps.length >0 ){
                                            for (int k=0; k<step.steps.length; k++){
                                                DirectionsStep step1 = step.steps[k];
                                                EncodedPolyline points1 = step1.polyline;
                                                if (points1 != null){
                                                    List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                                    for(com.google.maps.model.LatLng coord : coords1){
                                                        path.add(new LatLng(coord.lat, coord.lng));
                                                    }
                                                }
                                            }
                                        } else {
                                            EncodedPolyline points = step.polyline;
                                            if (points !=null){
                                                List<com.google.maps.model.LatLng> coords = points.decodePath();
                                                for (com.google.maps.model.LatLng coord: coords){
                                                    path.add(new LatLng(coord.lat, coord.lng));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }

                } catch (IOException | ApiException | InterruptedException e) {
                    e.printStackTrace();
                    Log.e("123456", "onPlaceSelected: ", e);
                }

                if (path.size() > 0 ){
                    LatLngBounds.Builder b = new LatLngBounds.Builder();
                    b.include(new LatLng(lat, lng));
                    b.include(destLatLng);
                    LatLngBounds bounds = b.build();
                    Log.d("123456", "onPlaceSelected: " + bounds.toString());

                    PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(10);
                    mMap.addPolyline(opts);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    CameraUpdate cU = CameraUpdateFactory.newLatLngBounds(bounds, 25);
                  //  mMap.moveCamera(cU);
                    mMap.animateCamera(cU);

                    DisplayTripFrag();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addValueEventListener(userListener);


    }



    //Async task to get list of nearby places, then display them on the map (Android Developers, 2022) https://developer.android.com/reference/android/os/AsyncTask
    class NearbyPlacesTask extends AsyncTask<LatLng, Void, Void>{

        @Override
        protected Void doInBackground(LatLng... latlngs) {
          //  LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            nearbyPlaces = new NearbySearch().searchResponse(lat, lng).results;
            Log.d("123456", "doInBackground: " + lat + lng);
            return null;
        }

        @Override
        protected void onPostExecute(Void p) {
            super.onPostExecute(p);
            Log.d("123456", "onPostExecute: null places");
            if (nearbyPlaces != null){
                for (PlacesSearchResult place: nearbyPlaces) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(place.geometry.location.lat, place.geometry.location.lng)).title(place.name));
                    Log.d("123456", "onPostExecute: " + place.name);
                }
            }
        }
    }


}