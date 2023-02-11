package com.manipur.locationtracker.LocaDatabase;

import android.provider.BaseColumns;

public class LocationDataFeeder {

    public LocationDataFeeder() {
    }

    public static class FeedEntry implements BaseColumns {
        public static final String _ID = "id";
        public static final String TABLE_NAME = "locationTable";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_LATITUDE = "lat";
        public static final String COLUMN_NAME_LONGITUDE = "lng";
    }
}
