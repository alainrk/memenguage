/*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*
* This file is part of Memenguage Android app.
* Copyright (C) 2016 Alain Di Chiappari
*/

package alaindc.memenguage;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import alaindc.memenguage.View.GuessActivity;
import alaindc.memenguage.View.MainActivity;

public class RandomIntentService extends IntentService {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private DBManager dbmanager;
    private Cursor crs;

    public RandomIntentService() {
        super("RandomIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.startsWith(Constants.ACTION_RANDOM_START)) {
                handleActionRandomStart();
            } else if (action.startsWith(Constants.ACTION_RANDOM_WORD)) {
                handleActionRandomWord();
            }
        }
    }

    private void handleActionRandomWord() {
        Boolean notificationsActive = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notifications_enabled", false);
        if (!notificationsActive) {
            setTimeout();
            return;
        }

        dbmanager = new DBManager(getApplicationContext());
        crs = dbmanager.getRandomWordNotUsed();

        try {
            crs.moveToFirst();
            sendNotification(crs.getLong(crs.getColumnIndex(Constants.FIELD_ID)));
        } catch (Exception e) {
            Log.d("RandomIntentService", e.toString());
            setTimeout();
        }
    }

    private void handleActionRandomStart() {
        setTimeout();
    }

    private void setTimeout() {
        // Set the alarms for next sensing of amplitude
        alarmMgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent intentAlarm = new Intent(getApplicationContext(), RandomIntentService.class);
        intentAlarm.setAction(Constants.ACTION_RANDOM_WORD);
        alarmIntent = PendingIntent.getService(getApplicationContext(), 0, intentAlarm, 0);

        try {
            // Remove the oldest one if exists
            alarmMgr.cancel(alarmIntent);
        } catch (Exception e) {
            Log.d("Randomintentservice", "Cancel pending intent error");
        }

        long millisec = Long.parseLong(PreferenceManager.getDefaultSharedPreferences(this).getString("interval_notifications", "120")) * 60 * 1000;
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + millisec, alarmIntent);
    }

    private void sendNotification(long id_word) {
        String title = "Memenguage";
        String description = "Do you remember?";

        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), GuessActivity.class);
        notificationIntent.setAction(Constants.ACTION_GUESS_ACTIVITY);
        notificationIntent.putExtra(Constants.EXTRA_GUESS_IDWORD, id_word);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntentWithParentStack(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.

        builder.setSmallIcon(R.drawable.icon)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logo))
                .setColor(Color.RED)
                .setContentTitle(title)
                .setContentText(description)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(notificationPendingIntent);

        try {
            String urisound = PreferenceManager.getDefaultSharedPreferences(this).getString("sound_notifications", "");
            builder.setSound(Uri.parse(urisound));
        } catch (Exception e) {
            Log.d("RandomIntentService", "Uri sound notification empty or wrong");
        }

        try {
            Boolean vibrate = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("vibrate_notifications", false);
            if (vibrate)
                builder.setVibrate(new long[] { 1000, 1000});
        } catch (Exception e) {
            Log.d("RandomIntentService", "Vibrate notification wrong");
        }

        builder.setDefaults(0);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(Constants.ID_NOTIFICATION_RANDOM_WORD, builder.build());
    }
}