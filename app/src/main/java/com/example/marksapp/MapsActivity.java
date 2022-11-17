package com.example.marksapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
//import com.google.maps.model.LatLng;
import com.example.marksapp.databinding.ActivityMapsBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;

    public static GoogleMap mMap;
    private ActivityMapsBinding binding;
    public View popup;

    public static Unit measurement;

    public static boolean isGpsEnabled = false;
    public static double lat = 0, lng = 0;
    public static LatLng destLatLng = null;
    public static Unit mUnit = null;
    public static AutocompleteSupportFragment destLocat;
    /*  mMode Codes:
    0 = Empty map
    1 = Home
    2 = Work
    3 = Destination via popup
    4 = Destination via search
    Important to prevent unwanted GetAndDisplayRoute display
    */
    public static int mMode = 0;
    public static PlacesSearchResult[] nearbyPlaces;
    public static String tDistance = "", tDuration = "";
    public static long tMeters = 0;

    private static Users curU;

    ConstraintLayout cl;

    SwitchCompat MeasurementSwitch;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // drawer layout instance to toggle the menu icon to open (The IIE, 2022)
        //drawer and back button to close drawer (geeksforgeeks.org, 2022).
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //Instantiating burgerNavigationView and binding it to view (Pulak, 2017).
        navigationView = findViewById(R.id.nav_view);
        //Setting navigation item listener (Pulak, 2017).
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        // pass the Open and Close toggle for the drawer layout listener (The IIE, 2022)
        // to toggle the button (geeksforgeeks.org, 2022).
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // to make the Navigation drawer icon always appear on the action bar (geeksforgeeks.org, 2022).
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), getString(R.string._google_api_key));
        }


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mAuth  = FirebaseAuth.getInstance(); //need firebase authentication instance
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                curU = snapshot.getValue(Users.class);
                MeasurementSwitch.setText(curU.getMeasurementPref().toString());
                Log.d("1234567", "onDataChange: " + curU.getMeasurementPref());
                Log.d("1234567", "onDataChange: " + curU.getTotalTravelDistance());
                if (curU.getMeasurementPref().equals(Unit.METRIC)){
                    MeasurementSwitch.setChecked(false);
                } else {
                    MeasurementSwitch.setChecked(true);
                }

                if (destLatLng != null && mMode != 0){
                    Log.d("123456", "onDataChange: MapReady Work/Home route displayed");
                    GetAndDisplayRoute();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        cl = findViewById(R.id.popup_menu);
        popup = findViewById(R.id.popid);

        try {
            Bundle dest = getIntent().getExtras();
            if (!dest.isEmpty()) {
                destLatLng = new LatLng(dest.getDouble("DestLat"), dest.getDouble("DestLng"));
                mMode = dest.getInt("Mode");
                if (dest.getInt("MUnit") == 0) mUnit = Unit.METRIC;
                else mUnit = Unit.IMPERIAL;
                Log.d("1234567", "onCreate: " + mMode + "  " + destLatLng.toString());
            } else {
                destLatLng = null;
            }
        } catch (Exception e){
            Toast.makeText(this, "No destination yet", Toast.LENGTH_SHORT).show();
        }

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG);
        destLocat = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.destLocation);
        destLocat.setPlaceFields(fields);
        destLocat.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                Log.d("123456", "Search onError:  " + status.getStatusMessage());
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mMap.clear();
                Log.d("12345678", "onPlaceSelected: map cleared");
                DisplayCurLocation();
                final Place destination = place;
                destLatLng = destination.getLatLng();
                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destLatLng, 14.0f));
                LandmarksModel lm = new LandmarksModel(destination.getName(), destination.getAddress(), destination.getLatLng());
                if (mMode == 1){
                    lm.setHome(true);
                    dbRef.child("savedLandmarks").child("Home").setValue(lm);
                } else if (mMode == 2){
                    lm.setWork(true);
                    dbRef.child("savedLandmarks").child("Work").setValue(lm);
                }

                mMode = 4;
                GetAndDisplayRoute();



            }
        });
        MeasurementSwitch = (SwitchCompat) findViewById(R.id.SwitchID);
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
        bundle.putLong("Meters", tMeters);
        bundle.putString("Duration", tDuration);
        bundle.putString("Distance", tDistance);
        bundle.putLong("LGoal", curU.getLevelGoal());
        bundle.putInt("Level", curU.getLevel());
        bundle.putLong("TotalDistance", curU.getTotalTravelDistance());
        TripInfoFragment tif = new TripInfoFragment();
        tif.setArguments(bundle);
        if (!fragman.isDestroyed()){
            fragman.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragcontainer_id, tif, null).commit();
        } else Log.d("1234567", "DisplayTripFrag: fragman destroyed");


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                Log.d("123456", "onMarkerClick: in marker");

                LayoutInflater factory = LayoutInflater.from(MapsActivity.this);
                final View popup = factory.inflate(R.layout.popup_menu, null);
                final AlertDialog dialog = new AlertDialog.Builder(MapsActivity.this).create();

                TextView name = popup.findViewById(R.id.popup_locationName);
                TextView address = popup.findViewById(R.id.popup_locationAddress);
                TextView type = popup.findViewById(R.id.popup_locationType);
                name.setText(marker.getTitle());
                address.setText(marker.getPosition().toString());
                destLatLng = marker.getPosition();
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    String add = geocoder.getFromLocation(destLatLng.latitude, destLatLng.longitude, 1).get(0).getAddressLine(0);
                    address.setText(add);
                    type.setText(geocoder.getFromLocation(destLatLng.latitude, destLatLng.longitude, 1).get(0).getFeatureName());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                popup.setVisibility(View.VISIBLE);
                dialog.setView(popup);

                Button close = popup.findViewById(R.id.btnPClose);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                popup.findViewById(R.id.btnPDirections).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mMode = 3;
                        GetAndDisplayRoute();
                        dialog.dismiss();
                    }
                });
                popup.findViewById(R.id.btnPAddFav).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView add = findViewById(R.id.popup_locationAddress);
                        String ad = add.getText().toString();
                        LandmarksModel landmarksModel = new LandmarksModel(marker.getTitle(), ad, destLatLng);
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("savedLandmarks").child(landmarksModel.getLmName());
                        dbRef.setValue(landmarksModel);
                        dialog.dismiss();
                    }
                });
                dialog.show();


                return true;
            }
        });
        mMap.clear();
        Log.d("12345678", "onMapReady: map cleared");
        GetLocation(mMap);
        /*if (mMode ==1 || mMode == 2){
            DisplayCurLocation();
        } else*/
        if (destLatLng != null){
            Log.d("12345678", "onDataChange: MapReady Work/Home route displayed");
            GetAndDisplayRoute();
        }

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
                    if (mMode != 1 && mMode != 2){
                        NearbyPlacesTask task = new NearbyPlacesTask();
                        task.execute(curLoc);

                    }
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
        if (mMode != 1 || mMode != 2){
            mMap.clear();
        Log.d("12345678", "DisplayCurLocation: map cleared");}
        LatLng curLoc = new LatLng(lat,lng);
        mMap.addMarker(new MarkerOptions().position(curLoc).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 14.0f));


    }
    public void GetAndDisplayRoute(){
        Log.d("12345678", "GetAndDisplayRoute: called " + destLatLng.toString());
        DisplayCurLocation();

        /*if (mMode == 1){
            mMap.addMarker(new MarkerOptions().position(destLatLng).title("Home").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        } else if (mMode == 2){
            mMap.addMarker(new MarkerOptions().position(destLatLng).title("Work").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        } else*/
        mMap.addMarker(new MarkerOptions().position(destLatLng).title("Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        //Routing algorithm and directions (xomena, 2017) https://stackoverflow.com/questions/47492459/how-do-i-draw-a-route-along-an-existing-road-between-two-points
        List<LatLng> path = new ArrayList<>();
        GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyALqIxRQNGQ11cUlmUEf4HY7dfQh6wp_9E").build();
        String startLatLng = lat + "," + lng;
        String destinationLatLng = destLatLng.latitude + "," + destLatLng.longitude;
        DirectionsApiRequest req;
        if (curU != null)
            req = DirectionsApi.getDirections(context, startLatLng, destinationLatLng).units(curU.getMeasurementPref());
        else
            req = DirectionsApi.getDirections(context, startLatLng, destinationLatLng).units(mUnit);
        try {
            // Distance tripLength = null;
            DirectionsResult res = req.await();
            if (res.routes !=null && res.routes.length > 0){
                DirectionsRoute route = res.routes[0];
                if(route.legs !=null) {
                    for(int i=0 ; i<route.legs.length; i++){
                        DirectionsLeg leg = route.legs[i];
                        //Distance tripLength = leg.distance;
                        //getting trip distance & duration
                        tDistance = leg.distance.humanReadable;
                        tDuration = leg.duration.humanReadable;
                        tMeters = leg.distance.inMeters;
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

            if (mMode != 0) {
                PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(10);
                mMap.addPolyline(opts);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                CameraUpdate cU = CameraUpdateFactory.newLatLngBounds(bounds, 40);
                //  mMap.moveCamera(cU);
                mMap.animateCamera(cU);

                DisplayTripFrag();
            }

        }


    }
    //Async task to get list of nearby places, then display them on the map (Android Developers, 2022) https://developer.android.com/reference/android/os/AsyncTask
    class NearbyPlacesTask extends AsyncTask<LatLng, Void, Void> {
        //Code to obtain list of nearby landmarks (evan, 2020) https://stackoverflow.com/questions/59922561/how-to-find-nearby-places-using-new-places-sdk-for-android
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
            if (nearbyPlaces != null) {
                for (PlacesSearchResult place : nearbyPlaces) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(place.geometry.location.lat, place.geometry.location.lng)).title(place.name));
                    Log.d("123456", "onPostExecute: " + place.name);
                }
            }
            for (PlacesSearchResult place : nearbyPlaces) {
                Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(place.geometry.location.lat, place.geometry.location.lng)).title(place.name));


            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
    private boolean onNavigationItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if (id == R.id.nav_MainMenu){
            Intent goMainM = new Intent(this, MainActivity.class);
            startActivity(goMainM);
        } else
        if (id == R.id.nav_Map){
            Intent goMap = new Intent(this, MapsActivity.class);
            startActivity(goMap);
        } else
        if (id == R.id.nav_favLmrks){
            Intent goFavs = new Intent(this, MainActivity.class);
            startActivity(goFavs);
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}