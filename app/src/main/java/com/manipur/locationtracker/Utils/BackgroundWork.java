package com.manipur.locationtracker.Utils;

import android.app.Activity;

public class BackgroundWork {
    private Activity activity;

    public BackgroundWork(Activity activity) {
        this.activity = activity;
    }

    public void startBackground(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                doInBackground();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onPostExecute();
                        }
                    });
                }
            }
        }).start();
    }

    public void execute() {
        startBackground();
    }
    public void doInBackground() { }
    public void onPostExecute() { }
}
