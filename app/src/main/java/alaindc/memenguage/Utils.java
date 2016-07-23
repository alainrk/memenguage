package alaindc.memenguage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by narko on 23/07/16.
 */
public class Utils {
    public static void addAttempt(Context context, boolean correct) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        long attempts = sharedPref.getLong(Constants.PREF_STATS_NUMATTEMPTS, 0);
        editor.putLong(Constants.PREF_STATS_NUMATTEMPTS, attempts+1);
        if (correct) {
            long corrects = sharedPref.getLong(Constants.PREF_STATS_NUMCORRECT, 0);
            editor.putLong(Constants.PREF_STATS_NUMCORRECT, corrects+1);
        }
        editor.commit();
    }

    public static void clearStats(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit();
        editor.putLong(Constants.PREF_STATS_NUMATTEMPTS, 0);
        editor.putLong(Constants.PREF_STATS_NUMCORRECT, 0);
        editor.commit();
    }
}
