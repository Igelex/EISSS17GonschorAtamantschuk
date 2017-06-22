package com.example.android.harvesthand;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.os.Build.VERSION_CODES.M;
import static com.example.android.harvesthand.Contracts.*;

public class AddNewEntry extends AppCompatActivity {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ImageButton locationButton;
    private ProgressBar locationPb;
    private EditText locationEdit, nameEdit, areaEdit, heightEdit, airtempEdit, airmoistureEdit;
    private EditText soiltempEdit, soilmoistureEdit, phEdit, collabEdit;
    private Geocoder geocoder;
    private ScrollView container;
    private Contracts contracts;
    private TextInputLayout locationInputLayout, nameInputLayout, areaInputLayout, heightInputLayout;
    private TextInputLayout airtempInputLayout, airmoistureInputLayout, soiltempInputLayout, soilmoistureInputLayout;
    private TextInputLayout phInputLayout, collabInputLayout;
    private String countryISOCode, city, locationName;
    private SharedPreferences sPrefUser;
    private ArrayList<String> collabs;
    private int cropId, soilId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_entry);

        collabs = new ArrayList<>();

        sPrefUser = getSharedPreferences(USER_SHARED_PREFS, MODE_PRIVATE);

        contracts = new Contracts();

        geocoder = new Geocoder(this, Locale.getDefault());

        container = (ScrollView) findViewById(R.id.add_new_entry_container);

        locationPb = (ProgressBar) findViewById(R.id.add_new_entry_location_pb);

        locationInputLayout = (TextInputLayout) findViewById(R.id.add_inputlayout_entry_location);

        Spinner cropSpinner = (Spinner) findViewById(R.id.spinner_crop);
        Spinner soilSpinner = (Spinner) findViewById(R.id.spinner_soil);
        cropSpinner.setAdapter(setupSpinner(R.array.crop_spinner_array));
        setupCropSpinner(cropSpinner);
        soilSpinner.setAdapter(setupSpinner(R.array.soil_spinner_array));
        setupCropSpinner(soilSpinner);


        locationEdit = (EditText) findViewById(R.id.add_entry_location);
        nameEdit = (EditText) findViewById(R.id.add_entry_name);
        areaEdit = (EditText) findViewById(R.id.add_entry_area);
        heightEdit = (EditText) findViewById(R.id.add_entry_height);
        airtempEdit = (EditText) findViewById(R.id.add_entry_airtemp);
        airmoistureEdit = (EditText) findViewById(R.id.add_entry_airmoisture);
        soiltempEdit = (EditText) findViewById(R.id.add_entry_soiltemp);
        soilmoistureEdit = (EditText) findViewById(R.id.add_entry_soilmoisture);
        phEdit = (EditText) findViewById(R.id.add_entry_ph);
        collabEdit = (EditText) findViewById(R.id.add_entry_collab);

        locationButton = (ImageButton) findViewById(R.id.add_location_button);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
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
                sendRequest(BASE_URL + URL_BASE_ENTRIES);
                Log.i("Post entry with URL :", BASE_URL + URL_BASE_ENTRIES);

                break;
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
            locationObject.put("ccountryISOCodeou", countryISOCode);
            locationObject.put("city", city);

            entryObject.put("entry_name", nameEdit.getText().toString().trim());
            entryObject.put("area", Integer.valueOf(areaEdit.getText().toString().trim()));
            entryObject.put("air_temp", Integer.valueOf(airtempEdit.getText().toString().trim()));
            entryObject.put("air_moisture", Integer.valueOf(airmoistureEdit.getText().toString().trim()));
            entryObject.put("soil_moisture", Integer.valueOf(soilmoistureEdit.getText().toString().trim()));
            entryObject.put("soil_temp", Integer.valueOf(soiltempEdit.getText().toString().trim()));
            entryObject.put("ph_value", Integer.valueOf(phEdit.getText().toString().trim()));
            entryObject.put("height_meter", Integer.valueOf(heightEdit.getText().toString().trim()));
            entryObject.put("collaborators", collabs.add(collabEdit.getText().toString().trim()));
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
                        cropId = -1;
                        break;
                    case 1:
                        cropId = 0;
                        break;
                    case 2:
                        cropId = 1;
                        break;
                    case 3:
                        cropId = 2;
                        break;
                    default:
                        cropId = -1;
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
                        soilId = -1;
                        break;
                    case 1:
                        soilId = 0;
                        break;
                    /*case 2:
                        soilId = 1;
                        break;
                    case 3:
                        cropId = 2;
                        break;*/
                    default:
                        cropId = -1;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void stopRequestLocation() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}
