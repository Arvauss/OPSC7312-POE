package com.example.marksapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.maps.model.PlaceType;
import com.google.maps.model.Unit;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Users {
    private Unit MeasurementPref;

    private List<LandmarksModel> SavedLandmarks = new List<LandmarksModel>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(@Nullable Object o) {
            return false;
        }

        @NonNull
        @Override
        public Iterator<LandmarksModel> iterator() {
            return null;
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] ts) {
            return null;
        }

        @Override
        public boolean add(LandmarksModel landmarksModel) {
            return false;
        }

        @Override
        public boolean remove(@Nullable Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> collection) {
            return false;
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends LandmarksModel> collection) {
            return false;
        }

        @Override
        public boolean addAll(int i, @NonNull Collection<? extends LandmarksModel> collection) {
            return false;
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> collection) {
            return false;
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> collection) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public LandmarksModel get(int i) {
            return null;
        }

        @Override
        public LandmarksModel set(int i, LandmarksModel landmarksModel) {
            return null;
        }

        @Override
        public void add(int i, LandmarksModel landmarksModel) {

        }

        @Override
        public LandmarksModel remove(int i) {
            return null;
        }

        @Override
        public int indexOf(@Nullable Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(@Nullable Object o) {
            return 0;
        }

        @NonNull
        @Override
        public ListIterator<LandmarksModel> listIterator() {
            return null;
        }

        @NonNull
        @Override
        public ListIterator<LandmarksModel> listIterator(int i) {
            return null;
        }

        @NonNull
        @Override
        public List<LandmarksModel> subList(int i, int i1) {
            return null;
        }
    };

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
}
