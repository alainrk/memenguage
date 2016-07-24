package alaindc.memenguage.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import alaindc.memenguage.Constants;
import alaindc.memenguage.R;
import alaindc.memenguage.Utils;

public class StatsActivity extends AppCompatActivity {

    private TextView numGamesTextView, numGuessTextView, successTextView;
    private Button clearStatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        numGamesTextView = (TextView) findViewById(R.id.numwordslab);
        numGuessTextView = (TextView) findViewById(R.id.numguesslab);
        successTextView = (TextView) findViewById(R.id.successlab);
        clearStatButton = (Button) findViewById(R.id.clearStatBut);

        clearStatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.clearStats(getApplicationContext());
                fillStats();
            }
        });

        fillStats();

    }

    private void fillStats() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);

        long guess = sharedPref.getLong(Constants.PREF_STATS_NUMCORRECT, 0);
        long games = sharedPref.getLong(Constants.PREF_STATS_NUMATTEMPTS, 0);
        double success = 100*(guess*1.0)/games;

        if (Double.isNaN(success))
            success = 0;

        numGamesTextView.setText("Words played: " + games);
        numGuessTextView.setText("Words guessed: " + guess);
        successTextView.setText("Success: " + String.format("%.2f", success) + "%");
    }

}
