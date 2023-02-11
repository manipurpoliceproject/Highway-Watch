package com.manipur.locationtracker.LocaDatabase;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.manipur.locationtracker.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocationDataJson {

    private static final String TAG = "LocationDataJsonTag";
    static LocationDataHelper locationDataHelper = new LocationDataHelper(MyApplication.getAppContext());

    public static JSONObject getLocations() {
        SQLiteDatabase database = locationDataHelper.getReadableDatabase();

        JSONObject locations = new JSONObject();
        JSONArray list = new JSONArray();

        Cursor cursor = database.rawQuery("SELECT * FROM locationTable;", null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject loca = new JSONObject();

                @SuppressLint("Range") long time = cursor.getLong(cursor.getColumnIndex(LocationDataFeeder.FeedEntry.COLUMN_NAME_TIME));
                @SuppressLint("Range") double lat = cursor.getDouble(cursor.getColumnIndex(LocationDataFeeder.FeedEntry.COLUMN_NAME_LATITUDE));
                @SuppressLint("Range") double lng = cursor.getDouble(cursor.getColumnIndex(LocationDataFeeder.FeedEntry.COLUMN_NAME_LONGITUDE));

                try {
                    loca.put("time", time);
                    loca.put("lat", lat);
                    loca.put("lng", lng);

                    list.put(loca);
                } catch (JSONException e) {
                    Log.d(TAG, "getLocations: Data put err: " + e.getMessage());
                    MyApplication.handleUncaughtException(new Exception("Issue: Location Data Put err: ", e));
                    e.printStackTrace();
                }

            } while (cursor.moveToNext());
        }

        Log.d(TAG, "getLocations: List Size: " + list.length());

        cursor.close();
        database.close();

        try {
            locations.put("LocationList", list);
        } catch (JSONException e) {
            Log.d(TAG, "getLocations: List Put error: " + e.getMessage());
            MyApplication.handleUncaughtException(new Exception("Issue: Location List Put error: ", e));
            e.printStackTrace();
        }

        return locations;
    }
}
