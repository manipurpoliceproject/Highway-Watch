package com.manipur.locationtracker.Utils;

public class User {
    private String carNumber;
    private String policeInChargeName;
    private String phoneNumber;
    private String highwayNumber;
    private String zoneNumber;
    private String sectorNumber;
    private long time;
    private float lat = 0;
    private float lng = 0;

    public User() {
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getPoliceInChargeName() {
        return policeInChargeName;
    }

    public void setPoliceInChargeName(String policeInChargeName) {
        this.policeInChargeName = policeInChargeName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHighwayNumber() {
        return highwayNumber;
    }

    public void setHighwayNumber(String highwayNumber) {
        this.highwayNumber = highwayNumber;
    }

    public String getZoneNumber() {
        return zoneNumber;
    }

    public void setZoneNumber(String zoneNumber) {

        this.zoneNumber = zoneNumber;
    }

    public String getSectorNumber() {
        return sectorNumber;
    }

    public void setSectorNumber(String sectorNumber) {
        this.sectorNumber = sectorNumber;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }
}
