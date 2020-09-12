package com.qiscus.meet.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {
    private static PreferencesHelper INSTANCE;
    private SharedPreferences sharedPreferences;

    private PreferencesHelper(Context context) {
        sharedPreferences = context
                .getApplicationContext()
                .getSharedPreferences("meet.qiscus.app", Context.MODE_PRIVATE);
    }

    public static PreferencesHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new PreferencesHelper(context);
        }
        return INSTANCE;
    }

    public SharedPreferences Pref() {
        return sharedPreferences;
    }


    public void setName(String isName) {
        sharedPreferences.edit().putString("isName", isName).apply();
    }

    public String getName() {
        return sharedPreferences.getString("isName", "");
    }

    public void setMuted(boolean isMuted) {
        sharedPreferences.edit().putBoolean("isMuted", isMuted).apply();
    }

    public boolean getMuted() {
        return sharedPreferences.getBoolean("isMuted", false);
    }

    public Type getConfigType() {
        return Type.valueOf(sharedPreferences.getString("type", Type.PRODUCTION.name()));
    }

    public void setConfigType(Type type) {
        sharedPreferences.edit().putString("type", type.name()).apply();
    }

    public enum Type {
        STAGING, PRODUCTION
    }

}
