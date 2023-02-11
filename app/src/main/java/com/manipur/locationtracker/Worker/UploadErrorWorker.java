package com.manipur.locationtracker.Worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.FirebaseDatabase;
import com.manipur.locationtracker.MyApplication;
import com.manipur.locationtracker.Utils.SharedPrefHelper;
import com.manipur.locationtracker.Utils.User;

public class UploadErrorWorker extends Worker {

    private static final String TAG = "UploadErrorWorkerTag";

    public UploadErrorWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.d(TAG, "UploadErrorWorker: ");
    }

    @NonNull
    @Override
    public Result doWork() {
        String error = getInputData().getString("Error");

        User user = SharedPrefHelper.getUser(MyApplication.getAppContext());

        if(user==null){
            return Result.failure();
        }

        FirebaseDatabase.getInstance().getReference("ErrorLog")
                .child(user.getCarNumber())
                .push()
                .setValue(error);

        return Result.success();
    }
}
