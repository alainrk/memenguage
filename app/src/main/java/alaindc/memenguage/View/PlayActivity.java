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

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import alaindc.memenguage.Constants;
import alaindc.memenguage.DBManager;
import alaindc.memenguage.R;

public class PlayActivity extends AppCompatActivity {

    private DBManager dbmanager;
    private Cursor crs;
    private Random random;

    private TextView guesstext;
    private TextView translatext;
    private Button yesbutton;
    private Button nobutton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        this.random = new Random(System.currentTimeMillis());

        guesstext = (TextView) findViewById(R.id.guessTextPlay);
        translatext = (TextView) findViewById(R.id.translateTextPlay);
        yesbutton = (Button) findViewById(R.id.yesbuttonPlay);
        nobutton = (Button) findViewById(R.id.nobuttonPlay);

        setTitle("Guess these words!");

        dbmanager = new DBManager(getApplicationContext());

        String guess, transl, itaflag, engflag;

        if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN){
            itaflag = "\uDBB9\uDCE9 ";
            engflag = "\uDBB9\uDCEA ";
        } else {
            itaflag = "IT: ";
            engflag = "EN: ";
        }


        crs = dbmanager.getRandomWordNotUsed();
        crs.moveToFirst();
        final Long wordId = crs.getLong(crs.getColumnIndex(Constants.FIELD_ID));

        int randomCase = random.nextInt(2);

        guess = crs.getString(crs.getColumnIndex( (randomCase == Constants.ENGLISH_GUESS) ? Constants.FIELD_ENG : Constants.FIELD_ITA) );
        transl = crs.getString(crs.getColumnIndex( (randomCase == Constants.ENGLISH_GUESS) ? Constants.FIELD_ITA : Constants.FIELD_ENG) );

        SpannableString guessstyle = new SpannableString((randomCase == Constants.ENGLISH_GUESS) ? engflag + guess : itaflag + guess);
        SpannableString translstyle = new SpannableString((randomCase == Constants.ENGLISH_GUESS) ? itaflag + transl : engflag + transl);

        guessstyle.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, 0);
        translstyle.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, 0);

        guesstext.setText(guessstyle);
        translatext.setText(translstyle);
        translatext.setVisibility(View.INVISIBLE);

        //Toast.makeText(getApplicationContext(), crs.getString(crs.getColumnIndex(Constants.FIELD_ENG))+ " " + crs.getString(crs.getColumnIndex(Constants.FIELD_ITA)), Toast.LENGTH_LONG).show();

        yesbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translatext.setVisibility(View.VISIBLE);
                yesbutton.setEnabled(false);
                nobutton.setEnabled(false);
                onCreate(savedInstanceState);
            }
        });

        nobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translatext.setVisibility(View.VISIBLE);
                yesbutton.setEnabled(false);
                nobutton.setEnabled(false);
                dbmanager.setWordNotUsed(wordId);
                onCreate(savedInstanceState);
            }
        });
    }
}
