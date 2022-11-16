package com.example.marksapp;

import static com.example.marksapp.MapsActivity.lat;
import static com.example.marksapp.MapsActivity.lng;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.model.PlacesSearchResult;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TripInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripInfoFragment extends Fragment {

    boolean hasValues = false;
    TextView dur, dist;
    Button btnCancel, btnComplete;
    long distance = 0, totalTraveled = 0, levelGoal = 2000;
    int level = 1;
    public static PlacesSearchResult[] nearbyPlaces;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TripInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TripInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TripInfoFragment newInstance(String param1, String param2) {
        TripInfoFragment fragment = new TripInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trip_info, container, false);
        dist = (TextView) view.findViewById(R.id.lblDistance);
        dur = (TextView) view.findViewById(R.id.lblDuration);
        btnCancel = (Button) view.findViewById(R.id.btnTripCancel);
        btnComplete = (Button) view.findViewById(R.id.btnTripComplete);

        btnComplete.setText("Complete");
        btnCancel.setText("Cancel");

        Bundle bundle = this.getArguments();

        if (this.getArguments() != null){
            distance = bundle.getLong("Meters");
            dist.setText(bundle.getString("Distance"));
            dur.setText(bundle.getString("Duration"));
            totalTraveled = bundle.getLong("TotalDistance");
            levelGoal = bundle.getLong("LGoal");
            level = bundle.getInt("Level");
            hasValues = true;
        }



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsActivity.mMap.clear();
                GetNearbyPlaces();
                LatLng curLoc = new LatLng(lat, lng);
                MapsActivity.mMap.addMarker(new MarkerOptions().position(curLoc).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Current Location"));
                MapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 14.0f));
                dist.setText("--");
                dur.setText("--:--");

                FirebaseAuth mAuth  = FirebaseAuth.getInstance();
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");

                if (hasValues){
                    totalTraveled = totalTraveled + distance;
                    dbRef.child(mAuth.getUid()).child("totalTravelDistance").setValue(totalTraveled);
                    if (totalTraveled > levelGoal){
                        while (totalTraveled > levelGoal){
                            levelGoal *=  1.618034;
                            level++;
                        }
                        dbRef.child(mAuth.getUid()).child("levelGoal").setValue(levelGoal);
                        dbRef.child(mAuth.getUid()).child("level").setValue(level);
                    }
                }
                MapsActivity.mMode = 0;

                removeSelf();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsActivity.mMap.clear();
                GetNearbyPlaces();
                LatLng curLoc = new LatLng(lat, lng);
                MapsActivity.mMap.addMarker(new MarkerOptions().position(curLoc).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Current Location"));

                MapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 14.0f));
                dist.setText("--");
                dur.setText("--:--");

                removeSelf();

            }
        });


    }

    public void removeSelf(){
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    public void GetNearbyPlaces(){
        NearbyPlacesTask task = new NearbyPlacesTask();
        task.execute(new LatLng(lat, lng));
    }

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
                    MapsActivity.mMap.addMarker(new MarkerOptions().position(new LatLng(place.geometry.location.lat, place.geometry.location.lng)).title(place.name));
                    Log.d("123456", "onPostExecute: " + place.name);
                }
            }
            for (PlacesSearchResult place : nearbyPlaces) {
                Marker m = MapsActivity.mMap.addMarker(new MarkerOptions().position(new LatLng(place.geometry.location.lat, place.geometry.location.lng)).title(place.name));


            }
        }
    }

}
