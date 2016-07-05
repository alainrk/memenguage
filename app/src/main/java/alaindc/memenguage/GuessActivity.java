package alaindc.memenguage;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

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

        guesstext.setText(guess);
        translatext.setText(transl);
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
