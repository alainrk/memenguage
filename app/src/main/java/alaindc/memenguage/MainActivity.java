package alaindc.memenguage;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView wordsListview;

    private DBManager dbmanager;
    private Cursor crs;
    private CursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // TODO Just one time and in bcast receiver start phone
        Intent randomStart = new Intent(MainActivity.this, RandomIntentService.class);
        randomStart.setAction(Constants.ACTION_RANDOM_START);
        getApplicationContext().startService(randomStart);

        updateWordsList();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        crs = dbmanager.getAllWords();
        adapter.swapCursor(crs);
    }

    private void updateWordsList() {
        wordsListview = (ListView) findViewById(R.id.wordslistview);

        // specify an adapter (see also next example
        adapter = new CursorAdapter(this, crs, 0) {
            @Override
            public View newView(Context ctx, Cursor arg1, ViewGroup arg2)
            {
                View v = getLayoutInflater().inflate(R.layout.wordstextview, null);
                return v;
            }
            @Override
            public void bindView(View v, Context arg1, Cursor crs)
            {
//                int color = (crs.getPosition() % 2 == 0) ? Color.argb(50,178,245,242) : Color.WHITE;
                int color = (crs.getPosition() % 2 == 0) ? Color.argb(50,255,202,40) : Color.WHITE;
                v.setBackgroundColor(color);
                String ita = crs.getString(crs.getColumnIndex(Constants.FIELD_ITA));
                String eng = crs.getString(crs.getColumnIndex(Constants.FIELD_ENG));

                TextView itatxt = (TextView) v.findViewById(R.id.itatxt);
                TextView engtxt = (TextView) v.findViewById(R.id.engtxt);
                itatxt.setText(ita);
                itatxt.setTag("itatxt");
                engtxt.setText(eng);
                itatxt.setTag("engtxt");

            }
            @Override
            public long getItemId(int position)
            {
                Cursor crs = adapter.getCursor();
                crs.moveToPosition(position);
                return crs.getLong(crs.getColumnIndex(Constants.FIELD_ID));
            }
        };
        wordsListview.setAdapter(adapter);
        wordsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), Long.toString(id), Toast.LENGTH_SHORT).show();
            }
        });
        wordsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Intent createWordIntentActivity = new Intent(MainActivity.this, CreateEditActivity.class);
                createWordIntentActivity.setAction(Constants.ACTION_EDIT_WORD);

                Cursor crs = (Cursor) arg0.getItemAtPosition(pos);
                createWordIntentActivity.putExtra(Constants.EXTRA_EDIT_ITA, crs.getString(crs.getColumnIndex(Constants.FIELD_ITA)));
                createWordIntentActivity.putExtra(Constants.EXTRA_EDIT_ENG, crs.getString(crs.getColumnIndex(Constants.FIELD_ENG)));
                createWordIntentActivity.putExtra(Constants.EXTRA_EDIT_ID, id);

                MainActivity.this.startActivity(createWordIntentActivity);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
