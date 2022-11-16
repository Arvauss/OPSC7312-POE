package com.example.marksapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.maps.model.PlaceType;
import com.google.maps.model.Unit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Users {
    private Unit MeasurementPref;

  /*  private List<LandmarksModel> SavedLandmarks = new ArrayList<>();
    private HashMap<String, LandmarksModel> savedLandmarks;
*/
    private PlaceType prefType;

    private long totalTravelDistance;

    private long levelGoal;

    private int level;

    public Users(){

    }

    public Users(Unit userMeasurement, long userTotalDistance, PlaceType pref){
        this.MeasurementPref = userMeasurement;
        this.totalTravelDistance = userTotalDistance;
        this.prefType = pref;
      //  this.SavedLandmarks.add(new LandmarksModel());
        this.level = 1;
        this.levelGoal = 2000;

    }


    public Unit getMeasurementPref() {
        return MeasurementPref;
    }

    public void setMeasurementPref(Unit measurementPref) {
        MeasurementPref = measurementPref;
    }

    public void updateTravelDistance(double distanceTraveled){
        totalTravelDistance += distanceTraveled;
    }

    public PlaceType getPrefType() {
        return prefType;
    }

    public void setPrefType(PlaceType prefType) {
        this.prefType = prefType;
    }

    public long getTotalTravelDistance(){
        return this.totalTravelDistance;
    }

    /*public HashMap<String, LandmarksModel> getSavedLandmarks() {
        return savedLandmarks;
    }*/

    /*public void setSavedLandmarks(HashMap<String, LandmarksModel> savedLandmarks) {
        this.savedLandmarks = savedLandmarks;
    }*/

/*
    public void setSavedLandmarks(List<LandmarksModel> savedLandmarks) {
        SavedLandmarks = savedLandmarks;
    }
*/

    public long getLevelGoal() {
        return levelGoal;
    }

    public void setLevelGoal(long levelGoal) {
        this.levelGoal = levelGoal;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
