package org.eu.spacc.spaccdotweb.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;

public class SharedPrefHelper {
    private final SharedPreferences sharedPref;

    public SharedPrefHelper(Context context) {
        this.sharedPref = context.getSharedPreferences("SpaccWebView", Context.MODE_PRIVATE);
    }

    public SharedPrefHelper(Context context, String name) {
        this.sharedPref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public Integer getInt(String name) {
        return getInt(name, -1);
    }

    public Integer getInt(String name, int fallback) {
        Integer value = (Integer)sharedPref.getInt(name, fallback);
        return (value != -1 ? value : null);
    }

    public void setInt(String name, int value) {
        SharedPreferences.Editor editor = sharedPref.edit().putInt(name, value);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public ArrayList<String> getStringList(String name) {
        try {
            String json = sharedPref.getString(name, null);
            if (json != null) {
                JSONArray parsed = new JSONArray(json);
                ArrayList<String> restored = new ArrayList<>(parsed.length());
                for (int i = 0; i < parsed.length(); i++) {
                    restored.add(parsed.getString(i));
                }
                return restored;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void setStringList(String name, ArrayList<String> list) {
        SharedPreferences.Editor editor = sharedPref.edit().putString(name, new JSONArray(list).toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
}
