package com.manipur.locationtracker.Worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.manipur.locationtracker.LocaDatabase.LocationDataJson;
import com.manipur.locationtracker.MyApplication;
import com.manipur.locationtracker.Utils.SharedPrefHelper;
import com.manipur.locationtracker.Utils.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UploadLocationWorker extends Worker {
    private static final String TAG = "UploadLocationWorkerTag";

    public UploadLocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.d(TAG, "UploadLocationWorker: Hurray!!");
    }

    @NonNull
    @Override
    public Result doWork() {

        JSONObject locations = LocationDataJson.getLocations();

        User user = SharedPrefHelper.getUser(MyApplication.getAppContext());

        try {
            FirebaseStorage.getInstance().getReference(user.getCarNumber() + "/location.json")
                    .putBytes(locations.toString().getBytes("utf-8"))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "onSuccess: Success Uploaded locations");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Error: " + e.getMessage());
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "doWork: Err: " + e.getMessage());
            MyApplication.handleUncaughtException(new Exception("Issue: UploadLocation doWork: ", e));
            e.printStackTrace();
        }

        if (locations.has("LocationList")) {
            try {
                JSONArray jsonArray = locations.getJSONArray("LocationList");
                if (jsonArray.length() > 0) {
                    JSONObject object = (JSONObject) jsonArray.get(jsonArray.length() - 1);
                    long time = object.getLong("time");
                    double lat = object.getDouble("lat");
                    double lng = object.getDouble("lng");

                    if (lat != 0 && lng != 0) {
                        Log.d(TAG, "doWork: Location turned On!!");

                        Map<String, Object> map = new HashMap<>();
                        map.put("lat", lat);
                        map.put("lng", lng);
                        map.put("time", time);
                        map.put("locationStatus", true);

                        String date = "";
                        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                        Date res = new Date(time);
                        date = simple.format(res);

                        Log.d(TAG, "doWork: Last Location upload date: " + date);

                        FirebaseDatabase.getInstance().getReference("User")
                                .child(user.getCarNumber().toLowerCase().trim())
                                .updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: Updated new locations -> " + map.toString());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Update Fail: " + e.getMessage());
                                    }
                                });
                    } else {
                        Log.d(TAG, "doWork: Location turned off!");

                        Map<String, Object> map = new HashMap<>();
                        map.put("locationStatus", false);

                        FirebaseDatabase.getInstance().getReference("User")
                                .child(user.getCarNumber().toLowerCase().trim())
                                .updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: Updated locationStatus false");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Update locationStatus Fail: " + e.getMessage());
                                    }
                                });
                    }

                }
            } catch (JSONException e) {
                MyApplication.handleUncaughtException(new Exception("Issue: UploadLocation JsonErr: ", e));
                e.printStackTrace();
            }
        }

        return Result.success();
    }
}
