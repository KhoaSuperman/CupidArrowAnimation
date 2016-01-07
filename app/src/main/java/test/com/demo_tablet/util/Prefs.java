package test.com.demo_tablet.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by HoangAnhKhoa on 1/7/16.
 */
public class Prefs {

    public static final String PREF = "pirvot";

    public static void setFloat(Context context, String key, float value) {
        getInstance(context).edit().putFloat(key, value).apply();
    }

    public static float getFloat(Context context, String key) {
        return getInstance(context).getFloat(key, 0);
    }

    public static SharedPreferences getInstance(Context context){
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }
}
