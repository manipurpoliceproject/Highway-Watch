package com.manipur.locationtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.manipur.locationtracker.LoginRegister.LoginRegisterActivity;
import com.manipur.locationtracker.Utils.SharedPrefHelper;
import com.manipur.locationtracker.Utils.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTag";
    TextView carNum, latTv, lngTv, time, logout;

    private BroadcastReceiver locationReceiver;

    @Override
    protected void onStart() {
        super.onStart();

        SharedPrefHelper.addBoolean(MainActivity.this, "LoggedIn", true);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            FirebaseAuth.getInstance().signInAnonymously();
        }

        // Disable Battery optimization
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intentbattery = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intentbattery.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intentbattery.setData(Uri.parse("package:" + packageName));
                startActivity(intentbattery);
            }
        }

        // Asking Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 121);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 121);
            }

        }

        Log.d(TAG, "onStart: Fine: " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
        Log.d(TAG, "onStart: Coarse: " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION));
        Log.d(TAG, "onStart: Backg: " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialising views
        _init();

        // Setup location Receiver
        SetLocationReceiver();

        // Set logout Action
        LogoutUser();

        String gson = SharedPrefHelper.getString(this, "User");
        User user = new Gson().fromJson(gson, User.class);

        carNum.setText(user.getCarNumber().toUpperCase());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {

            Location location = new Location();
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            SetLocationUI(lat, lng, System.currentTimeMillis());
        }
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    private void LogoutUser() {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure?")
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                SharedPrefHelper.deleteData(MainActivity.this);

                                // Moving to registration page
                                Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(locationReceiver!=null){
            registerReceiver(locationReceiver, new IntentFilter("com.manipur.locationtracker.LocationReceiver"));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationReceiver!=null){
            unregisterReceiver(locationReceiver);
        }
    }

    private void SetLocationReceiver() {
        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent!=null && intent.hasExtra("lat") && intent.hasExtra("lng") && intent.hasExtra("time")){
                    double lat = intent.getDoubleExtra("lat", 0);
                    double lng = intent.getDoubleExtra("lng", 0);
                    long time = intent.getLongExtra("time", 0);

                    Log.d(TAG, "onReceive: " + lat + "/" + lng + "/" + time);

                    SetLocationUI(lat, lng, time);
                }
            }
        };
    }

    private void SetLocationUI(double lat, double lng, long tim) {
        if(lat==0 || lng==0 || tim==0){
            return;
        }

        String date = "";
        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Date res = new Date(tim);
        date = simple.format(res);


        latTv.setText(lat + "");
        lngTv.setText(lng + "");
        time.setText(date);

    }

    private void _init() {
        carNum = findViewById(R.id.main_car_number);
        latTv = findViewById(R.id.main_latitude);
        lngTv = findViewById(R.id.main_longitude);
        time = findViewById(R.id.main_time);
        logout = findViewById(R.id.main_logout);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            Location location = new Location();
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            SetLocationUI(lat, lng, System.currentTimeMillis());
        }
    }
}