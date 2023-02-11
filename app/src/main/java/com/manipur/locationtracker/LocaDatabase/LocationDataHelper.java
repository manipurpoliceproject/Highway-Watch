package com.manipur.locationtracker.LocaDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class LocationDataHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LocationDataFeeder.FeedEntry.TABLE_NAME + " (" +
                    LocationDataFeeder.FeedEntry._ID + " TEXT PRIMARY KEY," +
                    LocationDataFeeder.FeedEntry.COLUMN_NAME_TIME + " TEXT ," +
                    LocationDataFeeder.FeedEntry.COLUMN_NAME_LATITUDE + " TEXT," +
                    LocationDataFeeder.FeedEntry.COLUMN_NAME_LONGITUDE + " TEXT )";

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "LocationData.db";
    private static final String TAG = "LocationDataHelperTag";


    public LocationDataHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        onCreate(db);
    }

    public void addLocation(long time, double lat, double lng){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LocationDataFeeder.FeedEntry._ID, ""+time+lat+lng);
        values.put(LocationDataFeeder.FeedEntry.COLUMN_NAME_TIME, time);
        values.put(LocationDataFeeder.FeedEntry.COLUMN_NAME_LATITUDE, lat);
        values.put(LocationDataFeeder.FeedEntry.COLUMN_NAME_LONGITUDE, lng);

        db.insert(LocationDataFeeder.FeedEntry.TABLE_NAME, null, values);
        db.close();

        Log.d(TAG, "addLocation: Added: " + values.toString());
    }
}
