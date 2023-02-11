package com.manipur.locationtracker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.manipur.locationtracker.LocaDatabase.LocationDataHelper;
import com.manipur.locationtracker.Utils.SharedPrefHelper;
import com.manipur.locationtracker.Utils.TimeStamp;
import com.manipur.locationtracker.Worker.UploadLocationWorker;

import org.checkerframework.checker.units.qual.C;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiverTag";
    private static int count = 0;
    private static int limit = 2;


    @Override
    public void onReceive(Context context, Intent intent) {
        final IntentFilter intentScreenfilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentScreenfilter.addAction(Intent.ACTION_SCREEN_OFF);

        Log.d(TAG, "onReceive: ");

        if(!SharedPrefHelper.getBoolean(context, "LoggedIn")){
            return;
        }

        count %= limit;

        Log.d(TAG, "onReceive: Not location time... Count: " + count);

        if (count == 0) {
            Log.d(TAG, "onReceive: Location time :P");
            TimeStamp.getTimeStamp(new TimeStamp.TimeListener() {
                @Override
                public void getTime(long time) {
                    Location location = new Location();

                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    new LocationDataHelper(MyApplication.getAppContext()).addLocation(time, lat, lng);

                    Intent broadCastLocation = new Intent("com.manipur.locationtracker.LocationReceiver");
                    broadCastLocation.putExtra("lat", lat);
                    broadCastLocation.putExtra("lng", lng);
                    broadCastLocation.putExtra("time", time);

                    context.sendBroadcast(broadCastLocation);
                }
            });
        }

        count++;

        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
            boolean wifiConnect;
            boolean mobileConnect;

            if (activeInfo != null && activeInfo.isConnected()) {
                wifiConnect = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
                mobileConnect = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            } else {
                wifiConnect = false;
                mobileConnect = false;
            }

            Log.d(TAG, "onReceive: Connection : " + wifiConnect + "/" + mobileConnect);

            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                FirebaseAuth.getInstance().signInAnonymously();
            }

            if ((wifiConnect || mobileConnect) && FirebaseAuth.getInstance().getCurrentUser() != null) {
                Log.d(TAG, "onReceive: Starting worker...");
                Constraints workerConstraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                WorkManager workManager = WorkManager.getInstance();
                OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(UploadLocationWorker.class).setConstraints(workerConstraints).build();
                workManager.enqueue(oneTimeWorkRequest);
            }
        } catch (Exception e) {
            MyApplication.handleUncaughtException(new Exception("Issue: OnReceive: ", e));
            Log.d(TAG, "onReceive: Error: " + e.getMessage());
        }
    }

}
