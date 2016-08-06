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
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import alaindc.memenguage.Constants;
import alaindc.memenguage.DBManager;
import alaindc.memenguage.R;
import alaindc.memenguage.RandomIntentService;
import alaindc.memenguage.Utils;

public class GuessActivity extends AppCompatActivity {

    private DBManager dbmanager;
    private Cursor crs;
    private Random random;

    private TextView guesstext, translatext, difflabel;
    private Button yesbutton, nobutton;
    private ImageButton hintbutton;
    private RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        int randomCase, rating;

        guesstext = (TextView) findViewById(R.id.guessText);
        translatext = (TextView) findViewById(R.id.translateText);
        yesbutton = (Button) findViewById(R.id.yesbutton);
        nobutton = (Button) findViewById(R.id.nobutton);
        hintbutton = (ImageButton) findViewById(R.id.hintButton);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        difflabel = (TextView) findViewById(R.id.difflabel);

        difflabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast t = Toast.makeText(getApplicationContext(), "More difficult you rate your words, more chance are given to you to improve your memory about them.", Toast.LENGTH_LONG);
                t.setGravity(Gravity.BOTTOM, 0, 100);
                t.show();
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabplay);
//        if (fab != null)
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent playactivity = new Intent(GuessActivity.this, PlayActivity.class);
//                    GuessActivity.this.startActivity(playactivity);
//                }
//            });

        setTitle("Memento!");

        Intent i = getIntent();
        final long wordId = i.getLongExtra(Constants.EXTRA_GUESS_IDWORD, -1);

        dbmanager = new DBManager(getApplicationContext());

        String guess, transl, itaflag, engflag;

        if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1){
            itaflag = "\uDBB9\uDCE9 ";
            engflag = "\uDBB9\uDCEA ";
        } else {
            itaflag = "IT: ";
            engflag = "EN: ";
        }

        crs = dbmanager.getWordById(wordId);
        crs.moveToFirst();

        switch (Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("guess_mode", "0"))) {
            case Constants.PREF_GUESS_MIXED:
                this.random = new Random(System.currentTimeMillis());
                randomCase = random.nextInt(2);
                break;
            case Constants.PREF_GUESS_ITALIAN:
                randomCase = Constants.ITALIAN_GUESS;
                break;
            case Constants.PREF_GUESS_ENGLISH:
            default:
                randomCase = Constants.ENGLISH_GUESS;
        }

        rating = crs.getInt(crs.getColumnIndex(Constants.FIELD_RATING));
        ratingBar.setRating(rating);

        guess = crs.getString(crs.getColumnIndex( (randomCase == Constants.ENGLISH_GUESS) ? Constants.FIELD_ENG : Constants.FIELD_ITA) );
        transl = crs.getString(crs.getColumnIndex( (randomCase == Constants.ENGLISH_GUESS) ? Constants.FIELD_ITA : Constants.FIELD_ENG) );

        SpannableString guessstyle = new SpannableString((randomCase == Constants.ENGLISH_GUESS) ? engflag + guess : itaflag + guess);
        SpannableString translstyle = new SpannableString((randomCase == Constants.ENGLISH_GUESS) ? itaflag + transl : engflag + transl);

        guessstyle.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, 0);
        translstyle.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, 0);

        guesstext.setText(guessstyle);
        translatext.setText(translstyle);
        translatext.setVisibility(View.INVISIBLE);

        Intent randomStart = new Intent(GuessActivity.this, RandomIntentService.class);
        randomStart.setAction(Constants.ACTION_RANDOM_START);
        getApplicationContext().startService(randomStart);

        yesbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translatext.setVisibility(View.VISIBLE);
                yesbutton.setEnabled(false);
                nobutton.setEnabled(false);
                dbmanager.setWordUsed(wordId);
                Utils.addAttempt(getApplicationContext(), true);
            }
        });

        nobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translatext.setVisibility(View.VISIBLE);
                yesbutton.setEnabled(false);
                nobutton.setEnabled(false);
                Utils.addAttempt(getApplicationContext(), false);
            }
        });

        hintbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crs = dbmanager.getContextById(wordId);
                if (crs != null && crs.getCount() > 0) {
                    crs.moveToFirst();
                    String text = crs.getString(crs.getColumnIndex(Constants.FIELD_CONTEXT));
                    Toast t = Toast.makeText(getApplicationContext(), (text.equals("")) ? "No context sentence" : text, Toast.LENGTH_LONG);
                    t.setGravity(Gravity.TOP, 0, 250);
                    t.show();
                }
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    dbmanager = new DBManager(getApplicationContext());
                    dbmanager.setRating(wordId, (int) rating);
                }
            }
        });

    }
}
