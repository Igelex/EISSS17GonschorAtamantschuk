package com.example.android.harvesthand;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.os.Build.VERSION_CODES.M;
import static com.example.android.harvesthand.Contracts.*;

public class AddNewEntry extends AppCompatActivity {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ImageButton locationButton, airTempAutofillButton, airHumidityAutofillButton;
    private ProgressBar locationPb, collabPb, airTempPb, airHumidityPb, progBar;
    private EditText locationEdit, nameEdit, areaEdit, heightEdit, airtempEdit, airhumidityEdit;
    private EditText soiltempEdit, soilmoistureEdit, phEdit, addCollab;
    private Geocoder geocoder;
    private ScrollView container;
    private Contracts contracts;
    private SendRequest request = new SendRequest();
    private TextInputLayout locationInputLayout;
    private String countryISOCode, city, locationName, entryId, ownerId = null, tutorialId = null;
    private SharedPreferences sPrefUser;
    private ArrayList collabsArray;
    private int cropId, soilId;
    private int requestMethod = Request.Method.POST;
    private Spinner cropSpinner, soilSpinner;
    private Boolean change = false;
    private TextToSpeech speaker;
    double lat, lon;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            change = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_entry);

        final CheckCollaborator checkCollaborator = new CheckCollaborator();

        collabsArray = new ArrayList<>();

        sPrefUser = getSharedPreferences(USER_SHARED_PREFS, MODE_PRIVATE);

        /*InitTTS tts = new InitTTS(this);
        speaker = tts.initTTS();*/
        contracts = new Contracts(null);

        geocoder = new Geocoder(this, Locale.getDefault());

        container = (ScrollView) findViewById(R.id.add_new_entry_container);

        progBar = (ProgressBar) findViewById(R.id.add_new_entry_pb);
        locationPb = (ProgressBar) findViewById(R.id.add_new_entry_location_pb);
        collabPb = (ProgressBar) findViewById(R.id.add_new_entry_collab_pb);
        airTempPb = (ProgressBar) findViewById(R.id.add_new_entry_airtemp_pb);
        airHumidityPb = (ProgressBar) findViewById(R.id.add_new_entry_airhumidity_pb);

        locationInputLayout = (TextInputLayout) findViewById(R.id.add_inputlayout_entry_location);

        airTempAutofillButton = (ImageButton) findViewById(R.id.add_entry_imb_airtemp_autofill);
        airHumidityAutofillButton = (ImageButton) findViewById(R.id.add_entry_imb_airhumidity_autofill);
        airDataAutoFill(airTempAutofillButton, airTempPb);
        airDataAutoFill(airHumidityAutofillButton, airHumidityPb);

        //Spinners zur Auswahl der Pflanzenart und des Bodentypes
        cropSpinner = (Spinner) findViewById(R.id.spinner_crop);
        soilSpinner = (Spinner) findViewById(R.id.spinner_soil);
        cropSpinner.setAdapter(setupSpinner(R.array.crop_spinner_array));
        setupCropSpinner(cropSpinner);
        soilSpinner.setAdapter(setupSpinner(R.array.soil_spinner_array));
        setupSoilSpinner(soilSpinner);

        //Init Inputfelder
        locationEdit = (EditText) findViewById(R.id.add_entry_location);
        nameEdit = (EditText) findViewById(R.id.add_entry_name);
        areaEdit = (EditText) findViewById(R.id.add_entry_area);
        heightEdit = (EditText) findViewById(R.id.add_entry_height);
        airtempEdit = (EditText) findViewById(R.id.add_entry_airtemp);
        airhumidityEdit = (EditText) findViewById(R.id.add_entry_airmoisture);
        soiltempEdit = (EditText) findViewById(R.id.add_entry_soiltemp);
        soilmoistureEdit = (EditText) findViewById(R.id.add_entry_soilmoisture);
        phEdit = (EditText) findViewById(R.id.add_entry_ph);
        setOnTouchListener();

        addCollab = (EditText) findViewById(R.id.add_entry_collab);
        //Liste mit als Collaborator eingefügten Usern
        ListView collabList = (ListView) findViewById(R.id.add_entry_collab_list);
        final CollabListAdapter adapter = new CollabListAdapter(this, collabsArray);
        collabList.setAdapter(adapter);
        //User wird nach der Telefnonummer in der Datenbank gesucht, und falls gefunden der Liste
        //hinzugefügt
        addCollab.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                    if (i == KeyEvent.KEYCODE_ENTER) {
                        //Request in der CheckCollaborator Klasse wird gesendet
                        if (checkCollaborator.getUser(AddNewEntry.this, collabPb, container, buildURL())) {
                            collabsArray.add(0, addCollab.getText().toString().trim());
                            adapter.notifyDataSetChanged();
                            addCollab.setText(null);
                            return true;
                        }
                    }
                return false;
            }
        });

        /*
         * ermitteln der Koordinaten
         */
        locationButton = (ImageButton) findViewById(R.id.add_location_button);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                contracts.showSnackbar(container, getString(R.string.msg_receive_gps_data), false, true);
                if (!Geocoder.isPresent()) {
                    contracts.showSnackbar(container, getString(R.string.msg_can_not_get_location), true, false);
                    stopRequestLocation();
                    return;
                }
                lat = location.getLatitude();
                lon = location.getLongitude();
                findGeocoder(location.getLatitude(), location.getLongitude());
                stopRequestLocation();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

        };
        setButton();

        Intent intent = getIntent();
        if (intent.hasExtra("entry_id")) {
            entryId = intent.getStringExtra("entry_id");
            requestMethod = intent.getIntExtra("method", Request.Method.POST);
            requestEntryToUpdate();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setButton();
                }
                break;
            default:
                break;
        }
    }

    private void setButton() {
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check permissions, erlaubnis zur Ermittlung der GPS-Daten wird abgefragt
                if (ActivityCompat.checkSelfPermission(AddNewEntry.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(AddNewEntry.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= M) {
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        android.Manifest.permission.INTERNET}

                                , 10);
                    }
                    return;
                }
                //Ermitteln der GPS-Daten
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    //Visual feddback, Progressbar wird angezeigt
                    locationPb.setVisibility(View.VISIBLE);
                    locationInputLayout.setHint(getString(R.string.msg_request_location));
                    locationManager.requestLocationUpdates("gps", 1000, 10, locationListener);
                } else {
                    Toast.makeText(AddNewEntry.this, getString(R.string.msg_can_not_get_location),
                            Toast.LENGTH_SHORT).show();
                    //Fals GPS aus, wird man zur den Einstellungen navigiert
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_entry_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                if (validateUserInput()) {
                    progBar.setVisibility(View.VISIBLE);
                    if (entryId == null) {
                        sendRequest(BASE_URL + URL_BASE_ENTRIES, requestMethod);
                    }
                    Log.i("PUT URL", BASE_URL + URL_BASE_ENTRIES + entryId);
                    Log.i("Method", String.valueOf(requestMethod));
                    sendRequest(BASE_URL + URL_BASE_ENTRIES + entryId, requestMethod);
                }
                break;
            case android.R.id.home:
                //Fals keine Änderungen, zurück in MainActivity
                if (!change) {
                    NavUtils.navigateUpFromSameTask(AddNewEntry.this);
                    return true;
                }
                //Fals Änderungen vorgenommen wurden, wird Dialogfenster angezeigt
                //Hier wird Aktion für DiscardButton festgelegt(verlassen der Activity)
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(AddNewEntry.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helpermethod zur Initialisierung der Spinners
     *
     * @param strings - array mit Strings
     * @return - spinner adapter
     */
    public ArrayAdapter setupSpinner(int strings) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                strings, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    /**
     * Sende POST Request mit eingegebenen Daten
     *
     * @param URL - die POST-URL, http://ip:3001/entries
     */
    public void sendRequest(String URL, int method) {
        JSONObject object = createJsonObject();
        JsonObjectRequest request = new JsonObjectRequest(method, URL, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progBar.setVisibility(View.GONE);
                        try {
                            //Response vom Server; ({msg: saved/notsaved, res: true/false})
                            String msgResponse = response.getString("msg");
                            Boolean statusResponse = response.getBoolean("res");
                            if (statusResponse) {
                                Toast.makeText(AddNewEntry.this, msgResponse, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                contracts.showSnackbar(container, msgResponse, true, false);
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
                        progBar.setVisibility(View.GONE);
                        if (error.networkResponse != null) {
                            switch (error.networkResponse.statusCode) {
                                case 500:
                                    contracts.showSnackbar(container, getString(R.string.msg_cant_save), true, false);
                                    break;
                                case 404:
                                    contracts.showSnackbar(container, getString(R.string.msg_404_error), true, false);
                                    break;
                            }
                        } else {
                            contracts.showSnackbar(container, getString(R.string.connection_err), true, false);
                        }
                    }
                });
        Volley.newRequestQueue(this).add(request);
    }

    /**
     * Ermitteln der Stadt, des Landes, und des ISO-Codes nach Koordinaten
     *
     * @param lat - Koordinaten
     * @param lon - Koordinaten
     */
    private void findGeocoder(Double lat, Double lon) {
        final int maxResults = 1;
        List<Address> addresses;
        locationPb.setVisibility(View.GONE);
        locationInputLayout.setHint(getString(R.string.new_entry_hint_location));
        //Ermitteln der Adresse
        try {
            addresses = geocoder.getFromLocation(lat, lon, maxResults);
            if (addresses != null || addresses.size() != 0) {
                Address currentAddress = addresses.get(0);
                locationEdit.setText(currentAddress.getCountryName() + getString(R.string.item_speaktext_coma)
                        + currentAddress.getLocality());
                countryISOCode = currentAddress.getCountryCode();
                city = currentAddress.getLocality();
                locationName = locationEdit.getText().toString();
            } else {
                contracts.showSnackbar(container, getString(R.string.msg_can_not_get_location), true, false);
            }

        } catch (IOException e) {
            e.printStackTrace();
            contracts.showSnackbar(container, getString(R.string.msg_can_not_get_location), true, false);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            contracts.showSnackbar(container, getString(R.string.msg_can_not_get_location), true, false);
        }

    }

    /**
     * Die Usereingaben werden dem JSONOBject hinzugefügt
     */
    private JSONObject createJsonObject() {

        JSONObject entryObject = new JSONObject();
        JSONObject locationObject = new JSONObject();
        try {
            //Location als Json-Object
            locationObject.put("name", locationName);
            locationObject.put("countryISOCode", countryISOCode);
            locationObject.put("city", city);

            //User-Eingaben im Json-Object gespeichert
            entryObject.put("entry_name", nameEdit.getText().toString().trim());
            entryObject.put("area", Integer.valueOf(areaEdit.getText().toString().trim()));
            entryObject.put("air_temp", Integer.valueOf(airtempEdit.getText().toString().trim()));
            entryObject.put("air_humidity", Integer.valueOf(airhumidityEdit.getText().toString().trim()));
            entryObject.put("soil_moisture", Integer.valueOf(soilmoistureEdit.getText().toString().trim()));
            entryObject.put("soil_temp", Integer.valueOf(soiltempEdit.getText().toString().trim()));
            entryObject.put("ph_value", Integer.valueOf(phEdit.getText().toString().trim()));
            entryObject.put("height_meter", Integer.valueOf(heightEdit.getText().toString().trim()));
            entryObject.put("collaborators", new JSONArray(collabsArray));

            if (ownerId != null) {
                //wenn Update modus
                entryObject.put("owner_id", ownerId);
            } else {
                entryObject.put("owner_id", sPrefUser.getString(USER_SP_ID, null));
            }
            if (tutorialId != null) {
                //wenn Update modus
                entryObject.put("tutorial_id", tutorialId);
            } else {
                entryObject.put("tutorial_id", "");
            }
            entryObject.put("crop_id", cropId);
            entryObject.put("soil", soilId);
            entryObject.put("location", locationObject);

        } catch (JSONException e) {
            e.printStackTrace();
            contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
        } catch (Exception e) {
            e.printStackTrace();
            contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
        }
        return entryObject;
    }

    /**
     * CropSpinner Eingabe
     *
     * @param cropSpinner - Pflanzenart-Spinner
     */
    private void setupCropSpinner(Spinner cropSpinner) {
        cropSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        cropId = DEFAULT_SELECTION;
                        break;
                    case 1:
                        cropId = CROP_ID_CAFFE;
                        break;
                    case 2:
                        cropId = CROP_ID_CACAO;
                        break;
                    case 3:
                        cropId = CROP_ID_BANANA;
                        break;
                    default:
                        cropId = DEFAULT_SELECTION;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Auswahl der Bodenart
     *
     * @param soilSpinner - Bodenart-Spinner
     */
    private void setupSoilSpinner(Spinner soilSpinner) {
        soilSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        soilId = DEFAULT_SELECTION;
                        break;
                    case 1:
                        soilId = SOIL_ID_SAND;
                        break;
                    case 2:
                        soilId = SOIL_ID_CLAY;
                        break;
                    default:
                        soilId = DEFAULT_SELECTION;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * URL zur Request der Collaborators(Users) wird gebildet.
     *
     * @return - URL, http://ip:3001/users/?phone_number=...
     */
    private String buildURL() {
        Uri baseUri = Uri.parse(BASE_URL + URL_BASE_USERS);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        //Parameter werden an die URL angehängt, hier die Telefonnummer
        uriBuilder.appendQueryParameter(URL_PARAMS_PHONE_NUMBER, addCollab.getText().toString().trim());
        return uriBuilder.toString();
    }

    /**
     * Helpermethode, Validation aller Benutzereingaben     *
     *
     * @return - true, falls alle Eingaben valide
     */
    private boolean validateUserInput() {
        if (validationEditText(nameEdit) && validationEditText(locationEdit) && validateSpinners(cropSpinner)
                && validateSpinners(soilSpinner) && validationEditText(areaEdit) && validationEditText(heightEdit)
                && validationEditText(airtempEdit) && validationEditText(airhumidityEdit) && validationEditText(soiltempEdit)
                && validationEditText(soilmoistureEdit) && validationEditText(phEdit)) {
            return true;
        }
        return false;
    }

    /**
     * Validiert Spnner
     *
     * @param spinner - Spinner zur Überprüfung
     * @return - true, falls Eingabe valide
     */
    private boolean validateSpinners(Spinner spinner) {
        if (spinner.getSelectedItemPosition() != DEFAULT_SELECTION) {
            return true;
        }
        View selectedView = spinner.getSelectedView();
        //Visual Feedback
        if (selectedView != null && selectedView instanceof TextView) {
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError(getString(R.string.errmsg_valid_input_required));
        }
        contracts.showSnackbar(container, getString(R.string.msg_select_spinners), true, false);
        return false;
    }

    /**
     * Helpermethode, Validiere User Eingaben, alle InputFelder werden hier validiert, wenn @false,
     * wird auf den Fehler hingewiesen
     *
     * @param input - Inputfeld
     * @return true, falls alle Eingaben valide
     */
    private boolean validationEditText(EditText input) {
        if (!input.getText().toString().trim().isEmpty()) {
            return true;
        }
        input.setError(getString(R.string.errmsg_valid_input_required));
        input.requestFocus();
        return false;
    }

    /**
     * Helpermethode, OnTouchListener registriert die Berührung der Inputfelder
     */
    private void setOnTouchListener() {
        locationEdit.setOnTouchListener(mTouchListener);
        nameEdit.setOnTouchListener(mTouchListener);
        airtempEdit.setOnTouchListener(mTouchListener);
        airhumidityEdit.setOnTouchListener(mTouchListener);
        soiltempEdit.setOnTouchListener(mTouchListener);
        soilmoistureEdit.setOnTouchListener(mTouchListener);
        areaEdit.setOnTouchListener(mTouchListener);
        heightEdit.setOnTouchListener(mTouchListener);
        phEdit.setOnTouchListener(mTouchListener);
    }

    /**
     * Falls Änderungen in Eingaben gemacht wurden und die Activity verlassen wird, wird erst
     * Dialogfenster angezeigt
     *
     * @param discardButtonClickListener
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_unsaved_changes_dialog);
        builder.setPositiveButton(R.string.dialog_ip_discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Die Luftdaten werden vom System automatisch ermittelt, hier wird request an den Server geschickt,
     * aktuelle Lokation wird als queryparamter übergeben, der server Fragt Wetter(Lufttemperatur und
     * Luftfeuchtigkeit ab) ab gibt Daten zurück an Client. Die entsprechenden Inputfelder werden mit
     * den jeweiligen daten gefüllt.
     *
     * @param autoFillButton - der angeklickte Button
     * @param pb             - Progressbar des jeweiligen Inputfeldes
     */
    private void airDataAutoFill(ImageButton autoFillButton, final ProgressBar pb) {
        autoFillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Prüfe ob location verfügbar
                if (countryISOCode != null && city != null) {
                    //Visual feedback
                    pb.setVisibility(View.VISIBLE);
                    //Hänge parameter an die URL an
                    Uri baseUri = Uri.parse(BASE_URL + URL_BASE_WEATHER);
                    Uri.Builder uriBuilder = baseUri.buildUpon();
                    uriBuilder.appendQueryParameter("countryISOCode", countryISOCode);
                    uriBuilder.appendQueryParameter("city", city);
                    //Request wird in der externen Klasse
                    request.requestData(AddNewEntry.this, Request.Method.GET, pb, container,
                            uriBuilder.toString(),
                            new ServerCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    Log.i("HOP HEY", "REQUeSt Weather");
                                    if (response != null) {
                                        try {
                                            int airTemp = response.getInt("temp");
                                            int airHumidity = response.getInt("humidity");
                                            airtempEdit.setText(String.valueOf(airTemp));
                                            airhumidityEdit.setText(String.valueOf(airHumidity));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                            contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
                                        }
                                    } else {
                                        contracts.showSnackbar(container, getString(R.string.msg_no_data_available), true, false);
                                    }
                                }
                            });
                } else {
                    contracts.showSnackbar(container, getString(R.string.msg_add_location), true, false);
                }

            }
        });
    }

    private void requestEntryToUpdate() {
        progBar.setVisibility(View.VISIBLE);
        request.requestData(this, Request.Method.GET, progBar, container, BASE_URL + URL_BASE_ENTRIES + entryId,
                new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        if (response != null) {
                            try {
                                String name = response.getString("entry_name");
                                tutorialId = response.getString("tutorial_id");
                                ownerId = response.getString("owner_id");
                                int cropId = response.getInt("crop_id");
                                int soilType = response.getInt("soil");
                                int airTemp = response.getInt("air_temp");
                                int airHumidity = response.getInt("air_humidity");
                                int soilTemp = response.getInt("soil_temp");
                                int soilMoisture = response.getInt("soil_moisture");
                                int ph = response.getInt("ph_value");
                                int height = response.getInt("height_meter");
                                int area = response.getInt("area");
                                JSONArray array = response.getJSONArray("collaborators");
                                for (int i = 0; i < array.length(); i++) {
                                    collabsArray.add(array.getString(i));
                                }


                                JSONObject locationObject = response.getJSONObject("location");
                                String locName = locationObject.getString("name");
                                String locationISOCode = locationObject.getString("countryISOCode");
                                String locationCity = locationObject.getString("city");
                                countryISOCode = locationISOCode;
                                city = locationCity;
                                locationName = locName;

                                nameEdit.setText(name);
                                locationEdit.setText(locName);
                                airtempEdit.setText(String.valueOf(airTemp));
                                airhumidityEdit.setText(String.valueOf(airHumidity));
                                phEdit.setText(String.valueOf(ph));
                                soiltempEdit.setText(String.valueOf(soilTemp));
                                soilmoistureEdit.setText(String.valueOf(soilMoisture));
                                heightEdit.setText(String.valueOf(height));
                                areaEdit.setText(String.valueOf(area));
                                cropSpinner.setSelection(cropId);
                                soilSpinner.setSelection(soilType);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
                            }
                        } else {
                            contracts.showSnackbar(container, getString(R.string.msg_no_data_available), true, false);
                        }
                    }
                });
    }

    public interface ServerCallback {
        void onSuccess(JSONObject result);
    }

    private void stopRequestLocation() {
        //Locationupdates werden angehalten
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRequestLocation();
    }

    @Override
    protected void onDestroy() {
        //TextToSpeech wird angehalten
        if (speaker != null) {
            speaker.stop();
            speaker.shutdown();
        }
        super.onDestroy();
    }
}
