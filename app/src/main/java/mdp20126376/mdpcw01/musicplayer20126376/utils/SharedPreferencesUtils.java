package mdp20126376.mdpcw01.musicplayer20126376.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Ensure the preference values remain in a consistent state
 * and control when they are committed to storage
 */
public class SharedPreferencesUtils {

    public static String SHARE_NAME = "colorBackground";

    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
    }

    public static int getColorID(Context context, String key, int defValue) {
        return getSharedPreference(context).getInt(key, defValue);
    }

    public static void setColorID(Context context, String key, int value) {
        getSharedPreference(context).edit().putInt(key, value).apply();
    }

}
