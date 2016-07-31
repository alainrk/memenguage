package alaindc.memenguage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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


    public static String getDate(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd/MM/yyyy HH:mm", cal).toString();
        return date;
    }

    public static String getDatePickerDate(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("yyyy-MM-dd", cal).toString();
        return date;
    }

    public static long dateToTimestamp (String datestring) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            java.util.Date date = formatter.parse(datestring);
            return date.getTime();
        } catch (Exception e) {
            Log.e("Utils",e.toString());
            return System.currentTimeMillis();
        }
    }


}
