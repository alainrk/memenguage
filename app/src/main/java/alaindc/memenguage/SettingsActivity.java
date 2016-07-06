package alaindc.memenguage;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_notification);

        final Preference intervalpref = findPreference("interval_notifications");

        intervalpref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                Intent randomStart = new Intent(SettingsActivity.this, RandomIntentService.class);
                randomStart.setAction(Constants.ACTION_RANDOM_START);
                getApplicationContext().startService(randomStart);
                return true;
            }
        });
    }
}
