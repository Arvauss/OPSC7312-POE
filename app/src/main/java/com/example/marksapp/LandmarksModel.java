package com.example.marksapp;

import com.google.android.gms.maps.model.LatLng;

public class LandmarksModel {
    private String lmName;

    private String lmAddress;

    private double lmLat;

    private double lmLng;

    private boolean isHome;

    private boolean isWork;

    public LandmarksModel(){}

    public LandmarksModel(String name, String address, LatLng latlng){
        this.lmName = name;
        this.lmAddress = address;
        this.lmLat = latlng.latitude;
        this.lmLng = latlng.longitude;
    }

    public String getLmName() {
        return lmName;
    }

    public void setLmName(String lmName) {
        this.lmName = lmName;
    }

    public String getLmAddress() {
        return lmAddress;
    }

    public void setLmAddress(String lmAddress) {
        this.lmAddress = lmAddress;
    }

    public double getLmLat() {
        return lmLat;
    }

    public void setLmLat(double lmLat) {
        this.lmLat = lmLat;
    }

    public double getLmLng() {
        return lmLng;
    }

    public void setLmLng(double lmLng) {
        this.lmLng = lmLng;
    }

    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean home) {
        isHome = home;
    }

    public boolean isWork() {
        return isWork;
    }

    public void setWork(boolean work) {
        isWork = work;
    }
}
