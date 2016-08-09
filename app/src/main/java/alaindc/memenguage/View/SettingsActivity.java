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

package alaindc.memenguage.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.text.format.Time;
import android.view.MenuItem;

import alaindc.memenguage.Constants;
import alaindc.memenguage.DatePreference;
import alaindc.memenguage.R;
import alaindc.memenguage.RandomIntentService;
import alaindc.memenguage.Utils;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_notification);

        final Preference intervalpref = findPreference("interval_notifications");
        final DatePreference startdp = (DatePreference) findPreference("start_dateguess");
        final DatePreference enddp = (DatePreference) findPreference("end_dateguess");

        intervalpref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                Intent randomStart = new Intent(SettingsActivity.this, RandomIntentService.class);
                randomStart.setAction(Constants.ACTION_RANDOM_START);
                getApplicationContext().startService(randomStart);
                return true;
            }
        });

        startdp.setSummary(startdp.getText());
        startdp.setDefaultValue(startdp.getText());
        startdp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                startdp.setText((String) newValue);
                String starttime = startdp.getText();
                startdp.setSummary(starttime);
                getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit().putLong(Constants.PREF_STARTGUESSTIME, Utils.dateToTimestamp(starttime)).commit();
                return true;
            }
        });

        enddp.setSummary(enddp.getText());
        enddp.setDefaultValue(enddp.getText());
        enddp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                enddp.setText((String) newValue);
                String endtime = enddp.getText();
                enddp.setSummary(endtime);
                getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).edit().putLong(Constants.PREF_ENDGUESSTIME, Utils.dateToTimestamp(endtime)).commit();
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == android.R.id.home) {
//            finish();
//            return true;
//        }
        finish();
        return super.onOptionsItemSelected(item);
    }
}
