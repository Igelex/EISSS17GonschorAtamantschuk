package com.example.android.harvesthand;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.harvesthand.SignUp.SignUpActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.android.harvesthand.Contracts.*;
public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getName();
    private ArrayList<Entry> entryArrayList = new ArrayList<>();
    private View container;
    private static String CURRENT_ENTRIES_URL;
    private ProgressBar progressBar;
    private ListView entryList;
    private ListAdapter adapter;
    private SharedPreferences sPrefUser, sPrefIp;
    private String userId;
    private View dialogView;
    private Contracts contracts;
    private RelativeLayout emptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Check Internetconnection*/
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            contracts = new Contracts(null);

            container = findViewById(R.id.entries_relativelayout);

            entryList = (ListView) findViewById(R.id.entry_list);
            progressBar = (ProgressBar) findViewById(R.id.progressBar_main);
            progressBar.setVisibility(View.VISIBLE);

            /*Prefs zur IP*/
            sPrefIp = getSharedPreferences(IP_ADDRESS_SHARED_PREFS, Context.MODE_PRIVATE);
            /*Check, ob ip bereits vorhanden*/
            if (!(sPrefIp.getString(IP_SP_IP, null) == null)) {
                /*Wenn IP bereits in SharedPreferences, gespeichert, füg die in URL ein, ansonsten
                dialogfenster anzeigen*/
                URL_IP = sPrefIp.getString(IP_SP_IP, null);
                BASE_URL = URL_PROTOCOL + URL_IP + URL_PORT;
                CURRENT_ENTRIES_URL = BASE_URL + URL_BASE_ENTRIES;
            } else {
                showIPdialog();
            }
            /*
            *Holl user_id aus SharedPreferences
            */
            sPrefUser = getSharedPreferences(USER_SHARED_PREFS, MODE_PRIVATE);
            if (sPrefUser != null) {
                if (sPrefUser.getString(USER_SP_ID, null) != null) {
                    reqeustUserId(BASE_URL + URL_BASE_USERS + sPrefUser.getString(USER_SP_ID, null));
                } else {
                    startActivity(new Intent(this, SignUpActivity.class));
                    Toast.makeText(this, getString(R.string.msg_please_login), Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                if (sPrefUser.getInt(USER_SP_TYPE, -1) == 0) {
                    FloatingActionButton fb = (FloatingActionButton) findViewById(R.id.fb_add_new_entry);
                    fb.setVisibility(View.VISIBLE);
                    fb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(MainActivity.this, AddNewEntry.class));
                        }
                    });
                }

            } else {
                startActivity(new Intent(this, SignUpActivity.class));
                Toast.makeText(this, getString(R.string.msg_please_login), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            emptyView = (RelativeLayout) findViewById(R.id.empty_state_container);
            entryList.setEmptyView(emptyView);

            /**
            * Bilde Uri für Entries - Request
            */
            Uri baseUri = Uri.parse(CURRENT_ENTRIES_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter(URL_PARAMS_OWNER_ID, sPrefUser.getString(USER_SP_ID, null));
            uriBuilder.appendQueryParameter(URL_PARAMS_PHONE_NUMBER, sPrefUser.getString(USER_SP_NUMBER, null));
            CURRENT_ENTRIES_URL = uriBuilder.toString();
            Log.i("URI: ", CURRENT_ENTRIES_URL);
            //Helper methode
            sendRequest(CURRENT_ENTRIES_URL);

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
                sendRequest(CURRENT_ENTRIES_URL);
                break;
            case R.id.action_person:
                startActivity(new Intent(this, UserProfile.class));
                break;
            case R.id.action_settings:
                showIPdialog();
                break;
        }
        return true;
    }

    //Request all Entries for user
    public void sendRequest(String url) {
        JsonArrayRequest jsonRequest = new JsonArrayRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);
                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject entryObject = response.getJSONObject(i);
                            JSONObject locationObject = entryObject.getJSONObject("location");

                            String entryName = entryObject.getString("entry_name");
                            String entryLocationCity = locationObject.getString("city");
                            String entryID = entryObject.getString("_id");
                            int area = entryObject.getInt("area");
                            int cropID = entryObject.getInt("crop_id");
                            String tutorialID = entryObject.getString("tutorial_id");

                            //Die Daten werden der ArrayList hinzugefügt
                            entryArrayList.add(new Entry(entryID, entryName, cropID, entryLocationCity, area, tutorialID));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
                        }

                    }
                    //Liste wird mit den Daten gefüllt
                    adapter = new ListAdapter(MainActivity.this, entryArrayList);
                    entryList.setAdapter(adapter);
                    defineOnItemClickListener();
                } else {
                    Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fadein);
                    emptyView.startAnimation(animation);
                    contracts.showSnackbar(container, getString(R.string.msg_no_data), true, false);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                if (error.networkResponse != null) {
                    switch (error.networkResponse.statusCode) {
                        case 500:
                            contracts.showSnackbar(container, getString(R.string.msg_internal_error), true, false);
                            error.printStackTrace();
                            break;
                        case 404:
                            contracts.showSnackbar(container, getString(R.string.msg_404_error), true, false);
                            error.printStackTrace();
                            break;
                    }
                } else {
                    contracts.showSnackbar(container, getString(R.string.connection_err), true, false);
                    error.printStackTrace();
                }
            }
        });

        Volley.newRequestQueue(this.getApplicationContext()).add(jsonRequest);
    }

    private void defineOnItemClickListener() {

        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entry currentEq = adapter.getItem(position);
                if (sPrefUser.getInt(USER_SP_TYPE, -1) == 1) {
                    Intent intent = new Intent(MainActivity.this, EntryTutorialActivity.class);
                    intent.putExtra("entry_id", currentEq.getEntryId());
                    intent.putExtra("tutorial_id", currentEq.getTutorialId());
                    intent.putExtra("entry_location_city", currentEq.getLocation());
                    intent.putExtra("crop_id", currentEq.getCropId());
                    intent.putExtra("entry_name", currentEq.getEntryName());
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } else {
                    startActivity(new Intent(MainActivity.this, EntryDetails.class));
                }
            }
        });

    }

    public void reqeustUserId(String URL) {
        Log.i("URL request user: ", URL);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (response == null || response.length() == 0){
                            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                            Toast.makeText(MainActivity.this, getString(R.string.msg_please_login),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                        try {
                            Log.i("CHecke USER:", response.getString("user_id"));
                            userId = response.getString("user_id");
                            if (userId == null || !userId.equals(sPrefUser.getString(USER_SP_ID, null))) {
                                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                                Toast.makeText(MainActivity.this, getString(R.string.msg_please_login),
                                        Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (error.networkResponse != null) {
                            switch (error.networkResponse.statusCode) {
                                case 500:
                                    contracts.showSnackbar(container, getString(R.string.msg_internal_error), true, false);
                                    error.printStackTrace();
                                    break;
                                case 404:
                                    contracts.showSnackbar(container, getString(R.string.msg_404_error), true, false);
                                    error.printStackTrace();
                                    break;
                            }
                        } else {
                            contracts.showSnackbar(container, getString(R.string.connection_err), true, false);
                            error.printStackTrace();
                        }
                    }
                });
        Volley.newRequestQueue(this).add(request);
    }

    private void showIPdialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        dialogView = getLayoutInflater().inflate(R.layout.ipaddress_custom_dialog, null);
        final EditText mIp = dialogView.findViewById(R.id.input_enter_ip);

        if (sPrefIp.getString(IP_SP_IP, null) == null) {
            mIp.setText(URL_IP_BASE);
        } else {
            mIp.setText(sPrefIp.getString(IP_SP_IP, null));
        }

        mBuilder.setView(dialogView);
        final AlertDialog dialog = mBuilder.create();

        Button mSave = dialogView.findViewById(R.id.save_ip);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIp.getText().toString().trim().isEmpty()) {
                    savePreferences(mIp.getText().toString().trim());
                    URL_IP = sPrefIp.getString(IP_SP_IP, null);
                    BASE_URL = URL_PROTOCOL + URL_IP + URL_PORT;
                    Toast.makeText(MainActivity.this, getString(R.string.dialog_ip_saved), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    if ((sPrefUser.getString(USER_SP_ID, null) == null)) {
                        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                        finish();
                    }
                } else {
                    mIp.setError(getString(R.string.errmsg_valid_input_required));
                    Toast.makeText(MainActivity.this, getString(R.string.dialog_please_enter_ip), Toast.LENGTH_LONG).show();
                }

            }
        });

        Button mDiscard = dialogView.findViewById(R.id.discard_ip);
        mDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, getString(R.string.dialog_ip_not_changed), Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    private void savePreferences(String ip) {
        SharedPreferences.Editor editor = sPrefIp.edit();
        editor.putString(IP_SP_IP, ip);
        editor.apply();
        Log.i("Save IP Address: ", ip);
    }
}

