package com.example.marksapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TripInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripInfoFragment extends Fragment {


    TextView dur, dist;
    Button btnCancel, btnComplete;

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
            dist.setText(bundle.getString("Distance"));
            dur.setText(bundle.getString("Duration"));
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
                LatLng curLoc = new LatLng(MapsActivity.lat,MapsActivity.lng);
                MapsActivity.mMap.addMarker(new MarkerOptions().position(curLoc).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Current Location"));
                MapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 14.0f));
                dist.setText("--");
                dur.setText("--:--");
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GoToMain = new Intent(getContext(), MainActivity.class);
                startActivity(GoToMain);
            }
        });


    }
}