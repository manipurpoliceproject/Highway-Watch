package com.manipur.locationtracker.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class SharedPrefHelper {


    private static SharedPreferences getSharedPref(Context context){
        return context.getSharedPreferences("LocationTrackerPref", Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor sharedEditor(Context context){
        return getSharedPref(context).edit();
    }

    public static void addString(Context context, String key, String val){
        SharedPreferences.Editor editor = sharedEditor(context);
        editor.putString(key, val);
        editor.apply();
    }

    public static String getString(Context context, String key){
        return getSharedPref(context).getString(key, null);
    }

    public static void addLong(Context context, String key, long value){
        SharedPreferences.Editor editor = sharedEditor(context);
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(Context context, String key){
        return getSharedPref(context).getLong(key,0);
    }

    public static void addBoolean(Context context, String key, boolean value){
        SharedPreferences.Editor editor = sharedEditor(context);
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key){
        return getSharedPref(context).getBoolean(key, false);
    }


    public static User getUser(Context context){
        String gson = getString(context, "User");
        if(gson==null || gson.trim().isEmpty()){
            return null;
        }
        return new Gson().fromJson(gson, User.class);
    }

    public static void deleteData(Context context){
        SharedPreferences.Editor editor =  sharedEditor(context);
        editor.clear();
        editor.apply();
    }
}
