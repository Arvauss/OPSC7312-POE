package com.example.marksapp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;
import com.google.maps.model.Unit;

import java.io.IOException;

public class NearbySearch {

   public static boolean isGpsEnabled = false;
    public static PlaceType prefType;
    FirebaseAuth mAuth;

    //Code to obtain list of nearby landmarks (evan, 2020) https://stackoverflow.com/questions/59922561/how-to-find-nearby-places-using-new-places-sdk-for-android
    public PlacesSearchResponse searchResponse(double lat, double lng){

        mAuth  = FirebaseAuth.getInstance(); //need firebase authentication instance
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        ValueEventListener val = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                prefType = snapshot.child(firebaseUser.getUid()).child("prefType").getValue(PlaceType.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addValueEventListener(val);

        GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyALqIxRQNGQ11cUlmUEf4HY7dfQh6wp_9E").build();
        PlacesSearchResponse request = new PlacesSearchResponse();

       // GetLocation(lm);

        LatLng location = new LatLng(lat, lng);
        Log.d("123456", "searchResponse: " + location.toString());
        try {
            request = PlacesApi.nearbySearchQuery(context, location)
                    .radius(5000)
                    .rankby(RankBy.PROMINENCE)
                    .language("en")
                    .type(prefType)
                    .await();
            return request;
            }
        catch (Exception e){
            Log.d("123456", "searchResponse: " + e.getMessage());
        }
        finally {
            return request;
        }
    }

}
