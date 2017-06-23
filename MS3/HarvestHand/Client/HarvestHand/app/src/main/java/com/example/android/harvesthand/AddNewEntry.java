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
    private ImageButton locationButton;
    private ProgressBar locationPb, collabPb;
    private EditText locationEdit, nameEdit, areaEdit, heightEdit, airtempEdit, airmoistureEdit;
    private EditText soiltempEdit, soilmoistureEdit, phEdit, addCollab ;
    private Geocoder geocoder;
    private ScrollView container;
    private Contracts contracts;
    private TextInputLayout locationInputLayout;
    private String countryISOCode, city, locationName, URL;
    private SharedPreferences sPrefUser;
    private ArrayList collabsArray;
    private int cropId, soilId;
    private Spinner cropSpinner, soilSpinner;
    private Boolean change = false;

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

        contracts = new Contracts();

        geocoder = new Geocoder(this, Locale.getDefault());

        container = (ScrollView) findViewById(R.id.add_new_entry_container);

        locationPb = (ProgressBar) findViewById(R.id.add_new_entry_location_pb);
        collabPb = (ProgressBar) findViewById(R.id.add_new_entry_collab_pb);

        locationInputLayout = (TextInputLayout) findViewById(R.id.add_inputlayout_entry_location);

        cropSpinner = (Spinner) findViewById(R.id.spinner_crop);
        soilSpinner = (Spinner) findViewById(R.id.spinner_soil);
        cropSpinner.setAdapter(setupSpinner(R.array.crop_spinner_array));
        setupCropSpinner(cropSpinner);
        soilSpinner.setAdapter(setupSpinner(R.array.soil_spinner_array));
        setupSoilSpinner(soilSpinner);


        locationEdit = (EditText) findViewById(R.id.add_entry_location);
        nameEdit = (EditText) findViewById(R.id.add_entry_name);
        areaEdit = (EditText) findViewById(R.id.add_entry_area);
        heightEdit = (EditText) findViewById(R.id.add_entry_height);
        airtempEdit = (EditText) findViewById(R.id.add_entry_airtemp);
        airmoistureEdit = (EditText) findViewById(R.id.add_entry_airmoisture);
        soiltempEdit = (EditText) findViewById(R.id.add_entry_soiltemp);
        soilmoistureEdit = (EditText) findViewById(R.id.add_entry_soilmoisture);
        phEdit = (EditText) findViewById(R.id.add_entry_ph);
        setOnTouchListener();

        addCollab = (EditText) findViewById(R.id.add_entry_collab);
        ListView collabList = (ListView) findViewById(R.id.add_entry_collab_list);
        final CollabListAdapter adapter = new CollabListAdapter(this, collabsArray);
        collabList.setAdapter(adapter);
        addCollab.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                    if (i == KeyEvent.KEYCODE_ENTER) {
                        if (checkCollaborator.getUser(AddNewEntry.this, collabPb, container, buildURL())){
                            collabsArray.add(0, addCollab.getText().toString().trim());
                            adapter.notifyDataSetChanged();
                            addCollab.setText("");
                            return true;
                        }
                    }
                return false;
            }
        });

        locationButton = (ImageButton) findViewById(R.id.add_location_button);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("LONG LAT", "" + location.getLatitude() + location.getLatitude());
                contracts.showSnackbar(container, getString(R.string.msg_receive_gps_data), false, true);
                if (!Geocoder.isPresent()) {
                    contracts.showSnackbar(container, getString(R.string.msg_can_not_get_location), true, false);
                    stopRequestLocation();
                    return;
                }
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
                Log.i("Push button", "");
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    locationPb.setVisibility(View.VISIBLE);
                    locationInputLayout.setHint(getString(R.string.msg_request_location));
                    locationManager.requestLocationUpdates("gps", 1000, 10, locationListener);
                } else {
                    Toast.makeText(AddNewEntry.this, getString(R.string.msg_can_not_get_location),
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    return;
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

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_save:
                if (validateUserInput()){
                    sendRequest(BASE_URL + URL_BASE_ENTRIES);
                    Log.i("Post entry with URL :", BASE_URL + URL_BASE_ENTRIES);
                }
                break;
            case android.R.id.home:
                if (!change) {
                    NavUtils.navigateUpFromSameTask(AddNewEntry.this);
                    return true;
                }
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

    public ArrayAdapter setupSpinner(int strings) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                strings, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public void sendRequest(String URL) {

        Log.i("URL: ", URL);

        JSONObject object = createJsonObject();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //progressBar.setVisibility(View.INVISIBLE);
                        try {
                            String msgResponse = response.getString("msg");
                            Boolean statusResponse = response.getBoolean("res");
                            if (statusResponse) {
                                Toast.makeText(AddNewEntry.this, msgResponse, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            Log.i("Status: ", statusResponse.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressBar.setVisibility(View.INVISIBLE);
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

    @Override
    protected void onPause() {
        super.onPause();
        stopRequestLocation();
    }

    private void findGeocoder(Double lat, Double lon) {
        Log.i("REQUEST ADRESS", "");
        final int maxResults = 1;
        List<Address> addresses;
        locationPb.setVisibility(View.GONE);
        locationInputLayout.setHint(getString(R.string.new_entry_hint_location));
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
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            contracts.showSnackbar(container, getString(R.string.msg_can_not_get_location), true, false);
        }

    }

    /*
    * Die Usereingaben werden dem JSONOBject hinzugefügt
    * */
    private JSONObject createJsonObject() {

        JSONObject entryObject = new JSONObject();
        JSONObject locationObject = new JSONObject();
        try {
            locationObject.put("name", locationName);
            locationObject.put("countryISOCode", countryISOCode);
            locationObject.put("city", city);

            entryObject.put("entry_name", nameEdit.getText().toString().trim());
            entryObject.put("area", Integer.valueOf(areaEdit.getText().toString().trim()));
            entryObject.put("air_temp", Integer.valueOf(airtempEdit.getText().toString().trim()));
            entryObject.put("air_moisture", Integer.valueOf(airmoistureEdit.getText().toString().trim()));
            entryObject.put("soil_moisture", Integer.valueOf(soilmoistureEdit.getText().toString().trim()));
            entryObject.put("soil_temp", Integer.valueOf(soiltempEdit.getText().toString().trim()));
            entryObject.put("ph_value", Integer.valueOf(phEdit.getText().toString().trim()));
            entryObject.put("height_meter", Integer.valueOf(heightEdit.getText().toString().trim()));
            entryObject.put("collaborators", new JSONArray(collabsArray));
            entryObject.put("owner_id", sPrefUser.getString(USER_SP_ID, null));
            entryObject.put("tutorial_id", "");
            entryObject.put("crop_id", cropId);
            entryObject.put("soil", soilId);
            entryObject.put("location", locationObject);

            Log.i("Entry Object: ", entryObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
        } catch (Exception e) {
            e.printStackTrace();
            contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
        }
        return entryObject;
    }

    private void setupCropSpinner(Spinner cropSpinner) {
        cropSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        cropId = NOT_SELECTED;
                        break;
                    case 1:
                        cropId = CROP_ID_CAFFE;
                        break;
                    case 2:
                        cropId = CROP_ID_TOMATO;
                        break;
                    case 3:
                        cropId = CROP_ID_RICE;
                        break;
                    default:
                        cropId = NOT_SELECTED;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupSoilSpinner(Spinner soilSpinner) {
        soilSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        soilId = NOT_SELECTED;
                        break;
                    case 1:
                        soilId = SOIL_ID_SAND;
                        break;
                    case 2:
                        soilId = SOIL_ID_CLAY;
                        break;
                    default:
                        soilId = NOT_SELECTED;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private String buildURL(){
        Uri baseUri = Uri.parse(BASE_URL + URL_BASE_USERS);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(URL_PARAMS_PHONE_NUMBER, addCollab.getText().toString().trim());
        Log.i("COLLAB URL:", uriBuilder.toString());
        URL = uriBuilder.toString();
        return URL;
    }
    private boolean validateUserInput(){
        if (validation(nameEdit) && validation(locationEdit) && validateSpinners(cropSpinner)
                && validateSpinners(soilSpinner) && validation(areaEdit) && validation(heightEdit)
                && validation(airtempEdit) && validation(airmoistureEdit) && validation(soiltempEdit)
                && validation(soilmoistureEdit) && validation(phEdit)){
            return true;
        }
        return false;
    }

    private boolean validateSpinners(Spinner spinner){
        if (spinner.getSelectedItemPosition() != DEFAULT_SELECTION){
            Log.i("Spinner validation: ", String.valueOf(spinner.getSelectedItemPosition()));
            return true;
        }
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError(getString(R.string.errmsg_valid_input_required));
        }
        contracts.showSnackbar(container, getString(R.string.msg_select_spinners), true, false);
        return false;
    }

    private boolean validation(EditText input) {
        if (!input.getText().toString().trim().isEmpty()){
            return true;
        }
        input.setError(getString(R.string.errmsg_valid_input_required));
        return false;
    }

    private void setOnTouchListener(){
        locationEdit.setOnTouchListener(mTouchListener);
        nameEdit.setOnTouchListener(mTouchListener);
        airtempEdit.setOnTouchListener(mTouchListener);
        airmoistureEdit.setOnTouchListener(mTouchListener);
        soiltempEdit.setOnTouchListener(mTouchListener);
        soilmoistureEdit.setOnTouchListener(mTouchListener);
        areaEdit.setOnTouchListener(mTouchListener);
        heightEdit.setOnTouchListener(mTouchListener);
        phEdit.setOnTouchListener(mTouchListener);
    }

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

    private void stopRequestLocation() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
