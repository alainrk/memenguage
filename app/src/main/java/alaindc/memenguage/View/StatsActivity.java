package alaindc.memenguage.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;

import alaindc.memenguage.Constants;
import alaindc.memenguage.DBManager;
import alaindc.memenguage.R;
import alaindc.memenguage.Utils;

public class StatsActivity extends AppCompatActivity {

    private Button clearStatButton;
    private ListView listView;
    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        clearStatButton = (Button) findViewById(R.id.clearstatsbut);
        listView = (ListView) findViewById(R.id.listatsview);

        items = new ArrayList<>();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(itemsAdapter);

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
        DBManager dbmanager = new DBManager(getApplicationContext());

        long allwords = dbmanager.countWords();
        long complwords = dbmanager.countGuessedWords();
        int percent = (int)(100*(complwords*1.0)/allwords);

        long guess = sharedPref.getLong(Constants.PREF_STATS_NUMCORRECT, 0);
        long games = sharedPref.getLong(Constants.PREF_STATS_NUMATTEMPTS, 0);
        int success = (int)(100*(guess*1.0)/games);

        if (Double.isNaN(success))
            success = 0;

        String numGames = "Words played: " + games;
        String numGuess = "Words guessed: " + guess;
        String successperc = "Success: " + success + "%";
        String numwords = "Number of words: " + allwords;
        String cyclecompl = "Cycle Completion: " + percent + "%";

        items.clear();
        items.add(numGames);
        items.add(numGuess);
        items.add(successperc);
        items.add(numwords);
        items.add(cyclecompl);

        itemsAdapter.notifyDataSetChanged();

        // TODO Fill listview
    }

}
