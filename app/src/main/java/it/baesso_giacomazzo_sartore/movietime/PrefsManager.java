package it.baesso_giacomazzo_sartore.movietime;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    private static PrefsManager prefsManager;
    private SharedPreferences prefs;

    public static PrefsManager getInstance(Context c)
    {
        if(prefsManager == null)
            prefsManager = new PrefsManager(c);

        return prefsManager;
    }

    private PrefsManager(Context c)
    {
        prefs = c.getApplicationContext().getSharedPreferences(c.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public void setPreference(String key, String value)
    {
        prefs.edit().putString(key, value).apply();
    }

    public void setPreference(String key, boolean value)
    {
        prefs.edit().putBoolean(key, value).apply();
    }

    public String getPreference(String key, String defaultValue)
    {
        return prefs.getString(key, defaultValue);
    }

    public boolean getPreference(String key, boolean defaultValue)
    {
        return prefs.getBoolean(key, defaultValue);
    }
}