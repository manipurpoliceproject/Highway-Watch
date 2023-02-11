package com.manipur.locationtracker;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.manipur.locationtracker.Utils.SharedPrefHelper;
import com.manipur.locationtracker.Utils.User;
import com.manipur.locationtracker.Worker.UploadErrorWorker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyApplication extends Application {

    private static final String TAG = "MyApplicationTag";
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();

        // Disabling NightView
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Setup handler for uncaught exceptions.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                 handleUncaughtException(e);
            }
        });
    }

    public static void handleUncaughtException(Throwable e) {
        Constraints workerConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        DateFormat simple = new SimpleDateFormat("HH:mm:ss dd MMM yyyy");
        Date res = new Date(System.currentTimeMillis());
        String date = simple.format(res);

        StringBuilder err = new StringBuilder();
        err.append(date).append("\n");
        err.append(e.getMessage()).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            err.append(element.toString()).append('\n');
        }

        // Adding Data
        Data.Builder data = new Data.Builder();
        data.putString("Error", err.toString());


        WorkManager workManager = WorkManager.getInstance();
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest
                .Builder(UploadErrorWorker.class)
                .setInputData(data.build())
                .setConstraints(workerConstraints).build();
        workManager.enqueue(oneTimeWorkRequest);

    }

    public static Context getAppContext() {
        return MyApplication.context;
    }


}