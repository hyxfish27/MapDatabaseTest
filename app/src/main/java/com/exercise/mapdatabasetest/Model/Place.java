package com.exercise.mapdatabasetest.Model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Place {

    @Exclude
    private String key;
    private double platitude;
    private double plongitude;

    public Place() {}

    public Place(double platitude, double plongitude) {
        this.platitude = platitude;
        this.plongitude = plongitude;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getPlatitude() {
        return platitude;
    }

    public void setPlatitude(double platitude) {
        this.platitude = platitude;
    }

    public double getPlongitude() {
        return plongitude;
    }

    public void setPlongitude(double plongitude) {
        this.plongitude = plongitude;
    }
}
