package alaindc.memenguage.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import alaindc.memenguage.Constants;
import alaindc.memenguage.DBManager;
import alaindc.memenguage.R;

public class CreateEditActivity extends AppCompatActivity {

    private EditText itaedittext;
    private EditText engedittext;
    private Button saveButton;
    private Button deleteButton;
    private long wordId;

    private DBManager dbmanager;
    private Cursor crs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String action = intent.getAction();

        setContentView(R.layout.activity_create_edit);
        setTitle(action.startsWith(Constants.ACTION_ADD_WORD) ? "Add a word" : "Edit word");

        itaedittext = (EditText) findViewById(R.id.itaEditText);
        engedittext = (EditText) findViewById(R.id.engEditText);
        saveButton = (Button) findViewById(R.id.savebutton);
        deleteButton = (Button) findViewById(R.id.deletebutton);

        itaedittext.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(itaedittext, InputMethodManager.SHOW_IMPLICIT);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEditAct);
        setSupportActionBar(toolbar);

        dbmanager = new DBManager(getApplicationContext());

        if (action.startsWith(Constants.ACTION_ADD_WORD)) {
            deleteButton.setVisibility(View.INVISIBLE);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long res = dbmanager.insertWord(itaedittext.getText().toString(), engedittext.getText().toString());
                    Toast t = Toast.makeText(getApplicationContext(), (res != -1) ? "Word saved!" : "Error in saving!", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.TOP, 0, 20);
                    t.show();
                    if (res != -1) {
                        itaedittext.setText("");
                        engedittext.setText("");
                    }
                }
            });

        } else if (action.startsWith(Constants.ACTION_EDIT_WORD)) {
            deleteButton.setVisibility(View.VISIBLE);
            wordId = intent.getLongExtra(Constants.EXTRA_EDIT_ID, -1);
            if (wordId == -1) {
                // TODO Handle this
            }

            itaedittext.setText(intent.getStringExtra(Constants.EXTRA_EDIT_ITA));
            engedittext.setText(intent.getStringExtra(Constants.EXTRA_EDIT_ENG));

            itaedittext.setSelection(itaedittext.getText().length());

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int res = dbmanager.updateWord(wordId, itaedittext.getText().toString(), engedittext.getText().toString());
                    Toast t = Toast.makeText(getApplicationContext(), (res != -1) ? "Word saved!" : "Error in saving!", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.TOP, 0, 20);
                    t.show();
                    if (res != -1) {
                        finish();
                    }
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateEditActivity.this, R.style.MyDialogTheme);
                    builder.setTitle("DELETING WORD")
                            .setMessage("Are you sure?")
                            .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    int res = dbmanager.deleteWord(wordId);
                                    Toast t = Toast.makeText(getApplicationContext(), (res > 0) ? "Word deleted!" : "Error in deleting!", Toast.LENGTH_LONG);
                                    t.setGravity(Gravity.TOP, 0, 20);
                                    t.show();
                                    if (res > 0)
                                        finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            })
                            .create()
                            .show();
                }
            });
        }



    }

}
