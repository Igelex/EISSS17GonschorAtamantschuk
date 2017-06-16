package com.example.android.harvesthand;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.android.harvesthand.Contracts.*;
import static com.example.android.harvesthand.Contracts.BASE_URL;
import static com.example.android.harvesthand.Contracts.URL_BASE_ENTRIES;
import static com.example.android.harvesthand.Contracts.URL_PARAMS_OWNER_ID;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getName();
    private ArrayList<Entry> entryArrayList = new ArrayList<>();
    private View container;
    private static final String URL = BASE_URL + URL_BASE_ENTRIES;
    private static String CURRENT_URL;
    private ProgressBar progressBar;
    private ListView entryList;
    private ListAdapter adapter;
    SharedPreferences sPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Check Internetconnection*/
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            container = (View) findViewById(R.id.entries_relativelayout);

            FloatingActionButton fb = (FloatingActionButton) findViewById(R.id.fb_add_new_entry);
            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, AddNewEntry.class));
                }
            });

            entryList = (ListView) findViewById(R.id.entry_list);
            progressBar = (ProgressBar) findViewById(R.id.progressBar_main);
            progressBar.setVisibility(View.VISIBLE);
            /*
            *Holl user_id aus SharedPreferences
            */
            sPref = getSharedPreferences(USER_SHARED_PREFS, MODE_PRIVATE);
            /*
            * Bilde Uri für Entries - Request
            */
            Uri baseUri = Uri.parse(URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter(URL_PARAMS_OWNER_ID, sPref.getString(USER_SP_ID, null));
            uriBuilder.appendQueryParameter(URL_PARAMS_COLLAB_ID, sPref.getString(USER_SP_ID, null));
            CURRENT_URL = uriBuilder.toString();
            Log.i("URI: ", CURRENT_URL);
            //Helper methode
            getEntries(CURRENT_URL);

        } else {
            Toast.makeText(this, getString(R.string.msg_no_internet_connection), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                progressBar.setVisibility(View.VISIBLE);
                if (adapter != null) {
                    adapter.clear();
                }
                getEntries(CURRENT_URL);
                break;
            case R.id.action_person:
                startActivity(new Intent(this, UserProfile.class));
                break;
        }
        return true;
    }

    //Request all Entries for user

    public void getEntries(String url) {
        JsonArrayRequest jsonRequest = new JsonArrayRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            //JSONArray collaboratorsArray = jsonObject.getJSONArray("collaborators");
                            String entryName = jsonObject.getString("entry_name");
                            String location = jsonObject.getString("location");
                            String entryID = jsonObject.getString("_id");
                            int area = jsonObject.getInt("area");
                            int artId = jsonObject.getInt("art_id");

                            //Die Daten werden der ArrayList hinzugefügt
                            entryArrayList.add(new Entry(entryID, entryName, artId, location, area));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    //Liste wird mit den Daten gefüllt
                    adapter = new ListAdapter(MainActivity.this, entryArrayList);
                    entryList.setAdapter(adapter);
                    defineOnItemClickListener();
                } else {
                    Snackbar snackbar = Snackbar.make(container, getString(R.string.msg_no_data), Snackbar.LENGTH_LONG);
                    View text = snackbar.getView();
                    TextView snackBarText2 = (TextView) text.findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText2.setTextColor(Color.rgb(253, 86, 86));
                    snackbar.show();
                    return;
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                if (error.networkResponse != null) {
                    switch (error.networkResponse.statusCode) {
                        case 500:
                            Snackbar snackbarIE = Snackbar.make(container, getString(R.string.msg_internal_error), Snackbar.LENGTH_LONG);
                            View sbie = snackbarIE.getView();
                            TextView snackBarText = (TextView) sbie.findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.rgb(253, 86, 86));
                            snackbarIE.show();
                            break;
                        case 404:
                            Snackbar snackbar404 = Snackbar.make(container, getString(R.string.msg_404_error), Snackbar.LENGTH_LONG);
                            View snackbarView404 = snackbar404.getView();
                            TextView snackBarText404 = (TextView) snackbarView404.findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText404.setTextColor(Color.rgb(253, 86, 86));
                            snackbar404.show();
                            break;
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(container, getString(R.string.connection_err), Snackbar.LENGTH_LONG);
                    View text = snackbar.getView();
                    TextView snackBarText = (TextView) text.findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.rgb(253, 86, 86));
                    snackbar.show();
                }
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this.getApplicationContext()).add(jsonRequest);
    }

    private void defineOnItemClickListener() {

        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entry currentEq = adapter.getItem(position);
                if (sPref.getInt(USER_SP_TYPE, -1) == 1) {
                    startActivity(new Intent(MainActivity.this, EntryTutorialActivity.class));
                } else {
                    Intent intent = new Intent(MainActivity.this, EntryDetails.class);
                    intent.putExtra("URL", URL + currentEq.getEntryId() + URL_BASE_TUTORIAL
                            + currentEq.getTutorialId());
                    Log.i("TUTORIAL_ID: ", URL + currentEq.getEntryId() + URL_BASE_TUTORIAL
                            + currentEq.getTutorialId());
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });

    }
}

