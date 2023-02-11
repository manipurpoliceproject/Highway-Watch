package com.manipur.locationtracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import com.manipur.locationtracker.Utils.BackgroundWork;

public class MyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new BackgroundWork(null) {
            @Override
            public void doInBackground() {
                super.doInBackground();
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(MyService.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MyService.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 6000, 60000, pendingIntent);
            }
        }.execute();
    }

    @Override
    public int onStartCommand(Intent intent1, int flags, int startId) {
        new BackgroundWork(null) {
            @Override
            public void doInBackground() {
                super.doInBackground();
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(MyService.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MyService.this, 0, intent,  PendingIntent.FLAG_IMMUTABLE);
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + 6000, 60000, pendingIntent);
            }
        }.execute();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                new Intent(getApplicationContext(), MyService.class),
                PendingIntent.FLAG_ONE_SHOT |  PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }

    private Thread.UncaughtExceptionHandler defaultUEH;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            ex.printStackTrace();

            //Same as done in onTaskRemoved()
            PendingIntent service = PendingIntent.getService(
                    getApplicationContext(),
                    1001,
                    new Intent(getApplicationContext(), MyService.class),
                    PendingIntent.FLAG_ONE_SHOT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
            System.exit(2);
        }
    };
}
