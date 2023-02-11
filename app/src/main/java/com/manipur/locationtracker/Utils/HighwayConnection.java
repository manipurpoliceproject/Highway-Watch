package com.manipur.locationtracker.Utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.manipur.locationtracker.RoadConnection;

import java.util.ArrayList;

public class HighwayConnection {

    private static final String TAG = "HighwayConnectionTag";

    public static void getConnection(ConnectionList connectionList){
        // Fetch Highways Data
        FirebaseDatabase.getInstance().getReference("LocationsList")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if(dataSnapshot==null || !dataSnapshot.exists()){
                            connectionList.getError(new Exception("Data doesn't exist"));
                            return;
                        }
                        ArrayList<RoadConnection> roadConnections = new ArrayList<>();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            RoadConnection roadConnection = snapshot.getValue(RoadConnection.class);
                            roadConnections.add(roadConnection);
                        }
                        Log.d(TAG, "onSuccess: Size: " + roadConnections.size());

                        connectionList.getList(roadConnections);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        connectionList.getError(e);
                        Log.d(TAG, "onFailure: Error: " + e.getMessage());
                    }
                });
    }

    public interface ConnectionList{
        public void getList(ArrayList<RoadConnection> roadConnections);
        public void getError(Exception e);
    }
}
