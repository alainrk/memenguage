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

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;

import alaindc.memenguage.Constants;
import alaindc.memenguage.R;
import alaindc.memenguage.RandomIntentService;

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
