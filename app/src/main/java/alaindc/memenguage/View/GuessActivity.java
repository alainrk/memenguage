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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import alaindc.memenguage.Constants;
import alaindc.memenguage.DBManager;
import alaindc.memenguage.R;
import alaindc.memenguage.RandomIntentService;

public class GuessActivity extends AppCompatActivity {

    private DBManager dbmanager;
    private Cursor crs;
    private Random random;

    private TextView guesstext;
    private TextView translatext;
    private Button showbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);
        this.random = new Random(System.currentTimeMillis());

        guesstext = (TextView) findViewById(R.id.guessText);
        translatext = (TextView) findViewById(R.id.translateText);
        showbutton = (Button) findViewById(R.id.showbutton);

        setTitle("Memento!");

        Intent i = getIntent();
        long wordId = i.getLongExtra(Constants.EXTRA_GUESS_IDWORD, -1);

        dbmanager = new DBManager(getApplicationContext());
        crs = dbmanager.getWordByIdAndSetUsed(wordId);
        crs.moveToFirst();

        int randomCase = random.nextInt(2);

        String guess = crs.getString(crs.getColumnIndex( (randomCase == Constants.ENGLISH_GUESS) ? Constants.FIELD_ENG : Constants.FIELD_ITA) );
        String transl = crs.getString(crs.getColumnIndex( (randomCase == Constants.ENGLISH_GUESS) ? Constants.FIELD_ITA : Constants.FIELD_ENG) );

        String itaflag = "\uDBB9\uDCE9 ";
        String engflag = "\uDBB9\uDCEA ";

        SpannableString guessstyle = new SpannableString((randomCase == Constants.ENGLISH_GUESS) ? engflag + guess : itaflag + guess);
        SpannableString translstyle = new SpannableString((randomCase == Constants.ENGLISH_GUESS) ? itaflag + transl : engflag + transl);

        guesstext.setText(guessstyle);
        translatext.setText(translstyle);
        translatext.setVisibility(View.INVISIBLE);

        //Toast.makeText(getApplicationContext(), crs.getString(crs.getColumnIndex(Constants.FIELD_ENG))+ " " + crs.getString(crs.getColumnIndex(Constants.FIELD_ITA)), Toast.LENGTH_LONG).show();

        showbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translatext.setVisibility(View.VISIBLE);
                showbutton.setEnabled(false);
                Intent randomStart = new Intent(GuessActivity.this, RandomIntentService.class);
                randomStart.setAction(Constants.ACTION_RANDOM_START);
                getApplicationContext().startService(randomStart);
            }
        });
    }
}
