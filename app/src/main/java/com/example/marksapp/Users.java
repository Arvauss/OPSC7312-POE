package com.example.marksapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.maps.model.PlaceType;
import com.google.maps.model.Unit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Users {
    private Unit MeasurementPref;

    private List<LandmarksModel> SavedLandmarks = new ArrayList<>();

    private PlaceType prefType;

    private double TotalTravelDistance;

    public Users(){

    }

    public Users(Unit userMeasurement, double userTotalDistance, PlaceType pref){
        this.MeasurementPref = userMeasurement;
        this.TotalTravelDistance = userTotalDistance;
        this.prefType = pref;
        this.SavedLandmarks.add(new LandmarksModel());

    }


    public Unit getMeasurementPref() {
        return MeasurementPref;
    }

    public void setMeasurementPref(Unit measurementPref) {
        MeasurementPref = measurementPref;
    }

    public void updateTravelDistance(double distanceTraveled){
        TotalTravelDistance += distanceTraveled;
    }

    public PlaceType getPrefType() {
        return prefType;
    }

    public void setPrefType(PlaceType prefType) {
        this.prefType = prefType;
    }

    public double getTotalTravelDistance(){
        return this.TotalTravelDistance;
    }

    public List<LandmarksModel> getSavedLandmarks() {
        return SavedLandmarks;
    }

    public void setSavedLandmarks(List<LandmarksModel> savedLandmarks) {
        SavedLandmarks = savedLandmarks;
    }
}
