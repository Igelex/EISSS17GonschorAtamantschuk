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
    private Contracts contracts;
    private RelativeLayout emptyView;
    InitTTS tts = new InitTTS(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Check Internetconnection*/
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            //Initialisiere TextToSpeech
            TextToSpeech speaker = tts.initSpeaker();
            contracts = new Contracts(speaker);

            container = findViewById(R.id.entries_relativelayout);

            entryList = (ListView) findViewById(R.id.entry_list);
            progressBar = (ProgressBar) findViewById(R.id.progressBar_main);
            progressBar.setVisibility(View.VISIBLE);

            /*Prefs zur IP*/
            sPrefIp = getSharedPreferences(IP_ADDRESS_SHARED_PREFS, Context.MODE_PRIVATE);
            /*Check, ob ip bereits vorhanden*/
            if (!(sPrefIp.getString(IP_SP_IP, null) == null)) {
                /*Wenn IP bereits in SharedPreferences gespeichert, füge die in URL ein, ansonsten
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
                if (sPrefUser.getString(USER_SHARED_PREFS_ID, null) != null) {
                    USER_ID = sPrefUser.getString(USER_SHARED_PREFS_ID, null);
                    USER_NUMBER = sPrefUser.getString(USER_SHARED_PREFS_NUMBER, null);
                    //Flas user_id vorhanden, prüfe, ob der user in DB exestiert
                    reqeustUserId(BASE_URL + URL_BASE_USERS + sPrefUser.getString(USER_SHARED_PREFS_ID, null));
                } else {
                    //Falls nicht --> Login
                    startActivity(new Intent(this, SignUpActivity.class));
                    Toast.makeText(this, getString(R.string.msg_please_login), Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                //Fals user_type = Alphabet, PlusButton anzeigen
                if (sPrefUser.getInt(USER_SHARED_PREFS_TYPE, -1) == USER_TYPE_LITERATE) {
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
                //User_id nicht in SharedPreferences --> Login
                startActivity(new Intent(this, SignUpActivity.class));
                Toast.makeText(this, getString(R.string.msg_please_login), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            //Emptystate, Falls keine Entries gefunden
            emptyView = (RelativeLayout) findViewById(R.id.empty_state_container);
            entryList.setEmptyView(emptyView);

            View footer = getLayoutInflater().inflate(R.layout.list_footer, null);
            entryList.addFooterView(footer, null, false);
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
                //Aktualisieren der Liste
                progressBar.setVisibility(View.VISIBLE);
                if (adapter != null) {
                    adapter.clear();
                }
                requestEntries(CURRENT_ENTRIES_URL);
                break;
            case R.id.action_person:
                //Zum User-Profile
                startActivity(new Intent(this, UserProfile.class));
                break;
            case R.id.action_settings:
                //IP eingeben
                showIPdialog();
                break;
        }
        return true;
    }

    /**
     * Falls User_id in SharedPreferences vorhanden, checke ob der User noch in der DB existiert
     *
     * @param URL - http://ip:3001/users/id
     */
    public void reqeustUserId(String URL) {
        Log.i("URL request user: ", URL);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        //User nicht vorhanden --> Login
                        if (response == null || response.length() == 0) {
                            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                            Toast.makeText(MainActivity.this, getString(R.string.msg_please_login),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                        try {
                            //User existiert --> Request seine Entries
                            Log.i("Checke USER:", response.getString("user_id"));
                            USER_ID = response.getString("user_id");
                            requestEntries(buildURL());
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

    //Request all Entries for user
    public void requestEntries(String url) {
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

    /**
     * Hier wird die Aktion für das Anklicken eines Listeneintrags definiert
     */
    private void defineOnItemClickListener() {
        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entry currentEq = adapter.getItem(position);
                //Fals User Analphabet ist, wird er direkt zur EntryTutorialActivity navigiert
                if (sPrefUser.getInt(USER_SHARED_PREFS_TYPE, -1) == USER_TYPE_ILLITERATE) {
                    //Bestimmte daten werden übergeben
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
                    Intent intent = new Intent(MainActivity.this, EntryDetails.class);
                    intent.putExtra("entry_id", currentEq.getEntryId());
                    intent.putExtra("entry_location_city", currentEq.getLocation());
                    intent.putExtra("entry_name", currentEq.getEntryName());
                    Log.i("Intent name", currentEq.getEntryName());
                    intent.putExtra("entry_area", currentEq.getArea());
                    intent.putExtra("tutorial_id", currentEq.getTutorialId());
                    Log.i("Intent tutorial_id", currentEq.getTutorialId());
                    intent.putExtra("crop_id", currentEq.getCropId());
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });

    }

    /**
     * Dialogfenster zum eintragen der IP(Nur für Prototype relevant)
     */
    private void showIPdialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.ipaddress_custom_dialog, null);
        final EditText mIp = dialogView.findViewById(R.id.input_enter_ip);

        if (sPrefIp.getString(IP_SP_IP, null) == null) {
            //wenn IP bereits NICHT in SP, IP-Vorlage (192.168..) als Text im Inputfeld setzen
            mIp.setText(URL_IP_BASE);
        } else {
            //Ansonsten IP aus SP als Text im Inputfeld setzen
            mIp.setText(sPrefIp.getString(IP_SP_IP, null));
        }
        mBuilder.setView(dialogView);
        final AlertDialog dialog = mBuilder.create();

        Button mSave = dialogView.findViewById(R.id.save_ip);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Falls IP eingegeben, speichern und BASE_URL aktualiseiren
                if (!mIp.getText().toString().trim().isEmpty()) {
                    savePreferences(mIp.getText().toString().trim());
                    URL_IP = sPrefIp.getString(IP_SP_IP, null);
                    BASE_URL = URL_PROTOCOL + URL_IP + URL_PORT;
                    Toast.makeText(MainActivity.this, getString(R.string.dialog_ip_saved), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    if ((sPrefUser.getString(USER_SHARED_PREFS_ID, null) == null)) {
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

    /**
     * Ip wird gespeichert
     * @param ip - IP aus dem Dialogfenster
     */
    private void savePreferences(String ip) {
        SharedPreferences.Editor editor = sPrefIp.edit();
        editor.putString(IP_SP_IP, ip);
        editor.apply();
        Log.i("Save IP Address: ", ip);
    }

    /**
     * Bilde URL für Entries - Request, Es werden die Entries requested, die ein User erstellt hat (owner_id)
     * und in denen er als Collaborator(collab_id) eingetragen ist
     * Beispiel für URL - http://ip:3001/entries/?owner_id=59562da73bb306153c8d9603&collab_id=59562da73bb306153c8d9603
     */
    private String buildURL() {
        Uri baseUri = Uri.parse(CURRENT_ENTRIES_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(URL_PARAMS_OWNER_ID, USER_ID);
        uriBuilder.appendQueryParameter(URL_PARAMS_COLLAB_ID, USER_ID);
        CURRENT_ENTRIES_URL = uriBuilder.toString();
        Log.i("URI: ", CURRENT_ENTRIES_URL);
        return CURRENT_ENTRIES_URL;
    }

    @Override
    protected void onPause() {
        tts.stopSpeaker();
        super.onPause();
    }
}

