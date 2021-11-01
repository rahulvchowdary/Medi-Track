package com.example.meditrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.core.graphics.drawable.IconCompat;

import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferenceManager {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public void saveLoginDetails(Context context ,String username, String name, String password) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putString("Username", username);
        editor.putString("Name", name);
        editor.putString("Password", password);
        editor.putBoolean("LoggedIn", true);
        editor.commit();
    }
    public String getUsername(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("Username", "");
    }

    public String getName(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("Name", "");
    }

    public void logoutUser(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.remove("Username");
        editor.remove("Name");
        editor.remove("Password");
        editor.commit();
    }
}
