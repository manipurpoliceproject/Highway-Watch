package com.manipur.locationtracker;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.manipur.locationtracker.LocaDatabase.LocationDataHelper;
import com.manipur.locationtracker.Utils.SharedPrefHelper;
import com.manipur.locationtracker.Utils.TimeStamp;

public class SendLocation {
    private final String TAG = "SendLocationTag";

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;

    private final long UPDATE_INTERVAL_IN_MILLISECONDS = 1 * 60 * 1000;
    private final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1 * 60 * 1000;


    @SuppressLint("MissingPermission")
    public SendLocation(LocationListener locationListener) {
        this.locationRequest = new LocationRequest();
        this.locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        this.locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(this.locationRequest);
        this.locationSettingsRequest = builder.build();

        SharedPrefHelper.addLong(MyApplication.getAppContext(), "LastLocation", System.currentTimeMillis());

        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult); // why? this. is. retarded. Android.
                Location currentLocation = locationResult.getLastLocation();
                locationListener.getLocation(currentLocation.getLatitude(), currentLocation.getLongitude());

                Log.d(TAG, "onLocationResult: Loc: "+ currentLocation.getLatitude() + "/" + currentLocation.getLongitude());

                TimeStamp.getTimeStamp(new TimeStamp.TimeListener() {
                    @Override
                    public void getTime(long time) {
                        SharedPrefHelper.addLong(MyApplication.getAppContext(), "LastLocation", time);
                        new LocationDataHelper(MyApplication.getAppContext()).addLocation(time, currentLocation.getLatitude(), currentLocation.getLongitude());
                    }
                });
            }
        };

        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MyApplication.getAppContext());
        this.mFusedLocationClient.requestLocationUpdates(this.locationRequest,
                this.locationCallback, Looper.myLooper());
    }


    public interface LocationListener{
        void getLocation(double lat, double lng);
    }

}