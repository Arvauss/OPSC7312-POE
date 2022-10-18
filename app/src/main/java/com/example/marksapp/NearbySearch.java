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
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;

import java.io.IOException;

public class NearbySearch {

   public static boolean isGpsEnabled = false;
    public static double lat = 0, lng = 0;
    public static PlaceType prefType;

    //Code to obtain list of nearby landmarks (evan, 2020) https://stackoverflow.com/questions/59922561/how-to-find-nearby-places-using-new-places-sdk-for-android
    public PlacesSearchResponse searchResponse(double lat, double lng){

        //TODO: Get pref from database, placeholder restaurant
        prefType = PlaceType.RESTAURANT;

        GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyALqIxRQNGQ11cUlmUEf4HY7dfQh6wp_9E").build();
        PlacesSearchResponse request = new PlacesSearchResponse();

       // GetLocation(lm);

        LatLng location = new LatLng(lat, lng);
        try {
            request = PlacesApi.nearbySearchQuery(context, location)
                    .radius(5000)
                    .rankby(RankBy.DISTANCE)
                    .language("en")
                    .type(prefType)
                    .await();
            }
        catch (ApiException | IOException | InterruptedException e){
            Log.d("123456", "searchResponse: " + e.getMessage());
        }
        finally {
            return request;
        }
    }
/*    @SuppressLint("MissingPermission")
    public void GetLocation(LocationManager lm){
     //   LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 30, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    // Toast.makeText(MainActivity.this, "Lat: " + lat + "\tLng: " + lng, Toast.LENGTH_SHORT).show();
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
