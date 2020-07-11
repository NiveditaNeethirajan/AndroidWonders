package com.example.medapptest.common;

import android.content.Context;
import android.content.SharedPreferences;

public class MedAppUserPreference {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static final String KEY_USERNAME = "UserName";

    public static void initialize(Context context) {
        sharedPreferences = context.getSharedPreferences("MedAppTest", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public static void storeUserName(String name) {
        editor.putString(KEY_USERNAME, name);
        editor.commit();
    }

    public static String getUserName() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }
}
