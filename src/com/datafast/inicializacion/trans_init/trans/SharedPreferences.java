package com.datafast.inicializacion.trans_init.trans;

import android.content.Context;

import com.android.newpos.pay.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Julian on 20/06/2018.
 */

public class SharedPreferences {
    private static String PREFS_KEY;

    public static String KEY_STAN = "STAN";




    public static void saveValueStrPreference(Context context, String key, String value) {
        PREFS_KEY = context.getString(R.string.pref_key);
        android.content.SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void saveValueIntPreference(Context context, String key, int value) {
        PREFS_KEY = context.getString(R.string.pref_key);
        android.content.SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static String getValueStrPreference(Context context, String key) {
        PREFS_KEY = context.getString(R.string.pref_key);
        android.content.SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static int getValueIntPreference(Context context, String key) {
        PREFS_KEY = context.getString(R.string.pref_key);
        android.content.SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }

    public static void saveValueBooleanPreference(Context context, String key, Boolean value) {
        PREFS_KEY = context.getString(R.string.pref_key);
        android.content.SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static Boolean getValueBooleanPreference(Context context, String key) {
        PREFS_KEY = context.getString(R.string.pref_key);
        android.content.SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

}
