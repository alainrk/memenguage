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

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import alaindc.memenguage.Constants;
import alaindc.memenguage.DBManager;
import alaindc.memenguage.R;
import alaindc.memenguage.RandomIntentService;
import alaindc.memenguage.ServerRequests;
import alaindc.memenguage.Utils;
import alaindc.memenguage.WordsAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView wordsListview;

    private String personName;
    private String personEmail;
    private String personId;
    private String personPhoto;

    private DBManager dbmanager;
    private Cursor crs;
    private WordsAdapter adapter;

    private Boolean exit = false;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLoggedIn();
        setContentView(R.layout.activity_main);

        dbmanager = new DBManager(getApplicationContext());
        crs = dbmanager.getAllWords();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null)
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent createWordIntentActivity = new Intent(MainActivity.this, CreateEditActivity.class);
                    createWordIntentActivity.setAction(Constants.ACTION_ADD_WORD);
                    MainActivity.this.startActivity(createWordIntentActivity);
                }
             });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) navigationView.setNavigationItemSelectedListener(this);

        ImageView navlogo = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navLogoImageView);
        TextView navtitle = (TextView)  navigationView.getHeaderView(0).findViewById(R.id.navTitle);
        TextView navsubtitle = (TextView)  navigationView.getHeaderView(0).findViewById(R.id.navSubTitle);
        progressDialog = new ProgressDialog(this);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.INTENT_VIEW_UPDATE)) {
                    adapter.getFilter().filter("");
                    adapter.notifyDataSetChanged();
                }
                else if (intent.getAction().equals(Constants.INTENT_COMMSERV_UPDATE)) {
                    boolean success = intent.getBooleanExtra(Constants.EXTRA_COMMSERV_SUCCESS, false);
                    String message = (intent.getIntExtra(Constants.EXTRA_COMMSERV_TYPE, 0) == Constants.UPLOAD) ? "Databased uploaded " : "Databased downloaded ";
                    message = (success) ? message + "successfully!" : "An error occurred, try again.";
                    progressDialog.setMessage(message);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 1500);

                } else {
                    Log.d("","");
                }
            }
        };

        IntentFilter updateviewIntFilt = new IntentFilter(Constants.INTENT_VIEW_UPDATE);
        IntentFilter serverCommIntFilt = new IntentFilter(Constants.INTENT_COMMSERV_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, updateviewIntFilt);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, serverCommIntFilt);


        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
        personName = sharedPref.getString(Constants.PREF_GOOGLEACCOUNT_NAME, "");
        personEmail = sharedPref.getString(Constants.PREF_GOOGLEACCOUNT_EMAIL, "");
        personId = sharedPref.getString(Constants.PREF_GOOGLEACCOUNT_ID, "");
        personPhoto = sharedPref.getString(Constants.PREF_GOOGLEACCOUNT_PHOTOURI, "");

//        if (!personPhoto.equals(""))
//            new AsyncImageTask(navlogo).execute(personPhoto);
        navtitle.setText(personName);
        navsubtitle.setText(personEmail);

        // TODO Just one time and in bcast receiver start phone
        Intent randomStart = new Intent(MainActivity.this, RandomIntentService.class);
        randomStart.setAction(Constants.ACTION_RANDOM_START);
        getApplicationContext().startService(randomStart);

        updateWordsList();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLoggedIn();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        crs = dbmanager.getAllWords();
        adapter.swapCursor(crs);
    }

    private void checkLoggedIn() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
        if (!sharedPref.getBoolean(Constants.PREF_GOOGLEACCOUNT_ISLOGGED, false)) {
            Intent signinintent = new Intent(this, SignInActivity.class);
            signinintent.setAction(Constants.SIGNIN_LOGOUT);
            startActivity(signinintent);
        }
    }

    private void updateWordsList() {
        wordsListview = (ListView) findViewById(R.id.wordslistview);

        adapter = new WordsAdapter(this, crs, 0);

        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return dbmanager.getMatchingWords(String.valueOf(constraint));
            }
        });

        wordsListview.setAdapter(adapter);
        wordsListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Intent createWordIntentActivity = new Intent(MainActivity.this, CreateEditActivity.class);
                createWordIntentActivity.setAction(Constants.ACTION_EDIT_WORD);

                Cursor crs = (Cursor) arg0.getItemAtPosition(pos);
                createWordIntentActivity.putExtra(Constants.EXTRA_EDIT_ITA, crs.getString(crs.getColumnIndex(Constants.FIELD_ITA)));
                createWordIntentActivity.putExtra(Constants.EXTRA_EDIT_ENG, crs.getString(crs.getColumnIndex(Constants.FIELD_ENG)));
                createWordIntentActivity.putExtra(Constants.EXTRA_EDIT_ID, id);

                MainActivity.this.startActivity(createWordIntentActivity);
                return true;
            }
        });

        wordsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                crs = (Cursor) arg0.getItemAtPosition(pos);
                String text = "Memory level: "+crs.getInt(crs.getColumnIndex(Constants.FIELD_RATING)) + "/5";
                text = text + "\nLast edit: " + Utils.getDate(crs.getLong(crs.getColumnIndex(Constants.FIELD_TIMESTAMP)));

                crs = dbmanager.getContextById(id);
                if (crs != null && crs.getCount() > 0) {
                    crs.moveToFirst();
                    String cont = crs.getString(crs.getColumnIndex(Constants.FIELD_CONTEXT));
                    text = text + "\n\n" + ((cont.equals("")) ? "Add a context sentence" : "Context:\n"+cont);
                }

                Toast t = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                t.setGravity(Gravity.TOP, 0, 250);
                t.show();
            }
        });

        //Toast.makeText(getApplicationContext(), adapter.getCount()+" words in Memenguage", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (exit) {
                finish();
            } else {
                Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);

            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("MainActivity Search", query.toString());

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("MainActivity Search", newText.toString());
                adapter.getFilter().filter(newText);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
            return true;
        } else if (id == R.id.action_search) {
            Log.d("MainActivity Search", item.toString());
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_settings) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
        } else if (id == R.id.nav_send) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
            builder.setTitle("Upload Database")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            progressDialog.setTitle("Upload");
                            progressDialog.setMessage("Uploading to server... please wait");
                            progressDialog.show();
                            ServerRequests.uploadFile(personId, getApplicationContext().getDatabasePath(Constants.DBNAME), getApplicationContext());
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

        } else if (id == R.id.nav_get) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
            builder.setTitle("Download Database")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            progressDialog.setTitle("Download");
                            progressDialog.setMessage("Downloading from server... please wait");
                            progressDialog.show();
                            ServerRequests.downloadFile(personId, getApplicationContext().getDatabasePath(Constants.DBNAME), getApplicationContext());
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

        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Memenguage, the best app on the world! Maybe.";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Memento mori!");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } else if (id == R.id.nav_signin) {
            Intent signinintent = new Intent(this, SignInActivity.class);
            signinintent.setAction(Constants.SIGNIN_LOGOUT);
            startActivity(signinintent);
        } else if (id == R.id.nav_play) {
            Intent playActivity = new Intent(this, PlayActivity.class);
            startActivity(playActivity);
        } else if (id == R.id.nav_stats) {
            Intent statsActivity = new Intent(this, StatsActivity.class);
            startActivity(statsActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
