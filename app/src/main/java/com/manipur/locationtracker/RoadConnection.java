package com.manipur.locationtracker;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class RoadConnection {
    @SerializedName("Highway")
    private String Highway;
    @SerializedName("Sector")
    private String Sector;
    @SerializedName("Zone")
    private String Zone;

    public RoadConnection() {
    }

    public RoadConnection(String highway, String sector, String zone) {
        Highway = highway;
        Sector = sector;
        Zone = zone;
    }

    public String getHighway() {
        return Highway;
    }

    public void setHighway(String highway) {
        Highway = highway;
    }

    public String getSector() {
        return Sector;
    }

    public void setSector(String sector) {
        Sector = sector;
    }

    public String getZone() {
        return Zone;
    }

    public void setZone(String zone) {
        Zone = zone;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "Highway='" + Highway + '\'' +
                ", Sector='" + Sector + '\'' +
                ", Zone='" + Zone + '\'' +
                '}';
    }
}
