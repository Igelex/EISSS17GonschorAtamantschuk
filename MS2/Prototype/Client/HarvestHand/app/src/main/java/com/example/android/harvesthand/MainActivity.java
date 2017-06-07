package com.example.android.harvesthand;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LabeledIntent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.R.attr.entries;
import static android.R.attr.port;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getName();
    private ArrayList<Entry> entryArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private static final int PORT = 3001;
    private static final String IP_ADRESS = "";
    private static final String URL = "http://192.168.2.102:" + PORT + "/entries"; // muss am jeweiligen rechner angepasst werden
    private SharedPreferences sPref;
    private ProgressBar progressBar;
    private ListView entryList;
    private ListAdapter adapter;

    private final String URL_BASE = "http://192.168.2.102:3001/entries/"; // muss am jeweiligen rechner angepasst werden
    private final String URL_TUTORIAL = "/tutorials/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Check Internetconnection*/
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            //Liste mit Card zum Anzeigen der Daten
            /*recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);*/

            FloatingActionButton fb = (FloatingActionButton) findViewById(R.id.fb_add_new_entry);
            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, AddNewEntry.class));
                }
            });
            entryList = (ListView) findViewById(R.id.entry_list);
            progressBar = (ProgressBar) findViewById(R.id.progressBar_main);
            /*
            *Holl user_id aus SharedPreferences
            */
            sPref = getSharedPreferences("User_id Pref",MODE_PRIVATE);
            /*
            * Bilde Uri für Request
            */
            Uri baseUri = Uri.parse(URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("owner_id", sPref.getString("user_id", ""));
            Log.i("URI: ", uriBuilder.toString());

            //Helper methode
            getEntries(uriBuilder.toString());
        }else {
            Toast.makeText(MainActivity.this, getString(R.string.msg_no_internet_connection), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
                            String name = jsonObject.getString("entry_name");
                            int ph = jsonObject.getInt("ph_value");
                            String id = jsonObject.getString("_id");
                            int art_id = jsonObject.getInt("art_id");
                            String tutorial_id = jsonObject.getString("tutorial_id");
                            int water = jsonObject.getInt("water");
                            int minerals = jsonObject.getInt("minerals");

                            //Die Daten werden der ArrayList hinzugefügt
                            entryArrayList.add(new Entry(id, tutorial_id, name, art_id, ph, water, minerals, null));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    //Liste wird mit den Daten gefüllt
                    adapter = new ListAdapter(getApplicationContext(), entryArrayList);
                    //recyclerView.setAdapter(adapter);
                    //adapter.addAll(entryArrayList);
                    entryList.setAdapter(adapter);
                    defineOnItemClickListener();
                } else {
                    Toast.makeText(MainActivity.this,"No Data found", Toast.LENGTH_LONG).show();
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
                            Snackbar snackbarIE = Snackbar.make(recyclerView, getString(R.string.msg_internal_error), Snackbar.LENGTH_LONG);
                            View sbie = snackbarIE.getView();
                            TextView snackBarText = (TextView) sbie.findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.rgb(253, 86, 86));
                            snackbarIE.show();
                            break;
                        case 404:
                            Snackbar snackbar404 = Snackbar.make(recyclerView, getString(R.string.msg_404_error), Snackbar.LENGTH_LONG);
                            View snackbarView404 = snackbar404.getView();
                            TextView snackBarText404 = (TextView) snackbarView404.findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText404.setTextColor(Color.rgb(253, 86, 86));
                            snackbar404.show();
                            break;
                        case 204:
                            Snackbar snackbar = Snackbar.make(recyclerView, getString(R.string.msg_internal_error), Snackbar.LENGTH_LONG);
                            View text = snackbar.getView();
                            TextView snackBarText2 = (TextView) text.findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText2.setTextColor(Color.rgb(253, 86, 86));
                            snackbar.show();
                            break;
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(recyclerView, getString(R.string.connection_err), Snackbar.LENGTH_LONG);
                    View text = snackbar.getView();
                    TextView snackBarText = (TextView) text.findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.rgb(253, 86, 86));
                    snackbar.show();
                }

                error.printStackTrace();
                Toast.makeText(MainActivity.this,"Error", Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(this.getApplicationContext()).add(jsonRequest);
    }

    private void defineOnItemClickListener (){

        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entry currentEq = adapter.getItem(position);

                Intent intent = new Intent(MainActivity.this, EntryDetails.class);

                /*Intent intent = new Intent(MainActivity.this, EntryTutorialActivity.class);
                intent.putExtra("URL", URL_BASE + currentEq.getEntryId() + URL_TUTORIAL
                        + currentEq.getTutorialId());
                intent.putExtra("ph", currentEq.getEntryPhValue());
                intent.putExtra("name", currentEq.getEntryName());
                intent.putExtra("water", currentEq.getEntryWater());
                //intent.putExtra("minerals", arrayList.get(position).getEntryMinerals());*/
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }
}

