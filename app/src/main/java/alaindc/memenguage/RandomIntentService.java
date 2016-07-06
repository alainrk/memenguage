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
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
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
//        int f = crs.getColumnIndex(Constants.FIELD_ID);
//        Long i = crs.getLong(f);
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

        builder.setSmallIcon(R.drawable.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(title)
                .setContentText(description)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(Constants.ID_NOTIFICATION_RANDOM_WORD, builder.build());
    }
}