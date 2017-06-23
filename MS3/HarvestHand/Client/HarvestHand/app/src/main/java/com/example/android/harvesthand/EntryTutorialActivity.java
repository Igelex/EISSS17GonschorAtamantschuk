package com.example.android.harvesthand;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.android.harvesthand.Contracts.*;

public class EntryTutorialActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private String entryId, tutorialID, entryLocationCity, entryName;
    private String airTempNorm, airMoistureNorm, soilMoistureNorm, soilTempNorm, phNorm, heightNorm;
    private int cropID, airTempStatus, airTepmDeviation, airTempCurrentValue;
    private int airMoistureStatus, airMoistureDeviation, airMoistureCurrentValue;
    private int soilTempStatus, soilTempDeviation, soilTempCurrentValue;
    private int phStatus, phDeviation, phCurrentValue;
    private int heightStatus, heightDeviation, heightCurrentValue;
    private int soilMoistureStatus, soilMoistureDeviation, soilMoistureCurrentValue, waterRequire;
    private int soilStatus, soilCurrentValue, soilNorm, matureMonth;
    private ConstraintLayout container;
    private TextToSpeech speaker;
    private Contracts contracts;
    private TextView location, height, mature, airMoisture, airTemp, ph, soilTemp, soilMoisture;
    private String celsius = "\u2103";
    private CircleImageView airTempImg, airMoistureImg, cropImg, soilImg, phImg, soilTempImg, soilMoistureImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_tutorial);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        contracts = new Contracts();

        if (networkInfo != null && networkInfo.isConnected()) {

            Intent intent = getIntent();
            if (intent != null) {
                try {
                    entryId = intent.getStringExtra("entry_id");
                    tutorialID = intent.getStringExtra("tutorial_id");
                    Log.i("Tutorila ID:", tutorialID);
                    entryLocationCity = intent.getStringExtra("entry_location_city");
                    cropID = intent.getIntExtra("cropID", 0);
                    entryName = intent.getStringExtra("entry_name");
                    setTitle(entryName);
                } catch (Exception e) {
                    contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
                    return;
                }
            }

            container = (ConstraintLayout) findViewById(R.id.entry_tutorial_root);

            progressBar = (ProgressBar) findViewById(R.id.tutorial_pg);
            progressBar.setVisibility(View.VISIBLE);

            airTempImg = (CircleImageView) findViewById(R.id.tutorial_airtemp_image);
            airMoistureImg = (CircleImageView) findViewById(R.id.tutorial_airmoisture_image);
            cropImg = (CircleImageView) findViewById(R.id.tutorial_crop_image);
            soilImg = (CircleImageView) findViewById(R.id.tutorial_soil_image);
            phImg = (CircleImageView) findViewById(R.id.tutorial_ph_image);
            soilTempImg = (CircleImageView) findViewById(R.id.tutorial_soiltemp_image);
            soilMoistureImg = (CircleImageView) findViewById(R.id.tutorial_soilmoisture_image);

            location = (TextView) findViewById(R.id.tutorial_loc);
            height = (TextView) findViewById(R.id.tutorial_hight);
            mature = (TextView) findViewById(R.id.tutorial_mature);
            airTemp = (TextView) findViewById(R.id.tutorial_text_air_temp);
            airMoisture = (TextView) findViewById(R.id.tutorial_text_air_moisture);
            soilTemp = (TextView) findViewById(R.id.tutorial_text_soil_temp);
            soilMoisture = (TextView) findViewById(R.id.tutorial_text_soil_moisture);
            ph = (TextView) findViewById(R.id.tutorial_text_ph);

            getTutorial(buildeUrl());
        } else {
            Toast.makeText(EntryTutorialActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }

        speaker = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    speaker.setLanguage(Locale.ENGLISH);
                }
            }
        });

    }

    //Request Tutorial
    public void getTutorial(String url) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                if (response.length() > 0) {
                    try {
                        JSONObject airTempObject = response.getJSONObject("air_temp");
                        airTempCurrentValue = airTempObject.getInt("currentValue");
                        airTempStatus = airTempObject.getInt("status");
                        airTepmDeviation = airTempObject.getInt("deviation");
                        airTempNorm = airTempObject.getString("norm");

                        JSONObject airMoistureObject = response.getJSONObject("air_moisture");
                        airMoistureCurrentValue = airMoistureObject.getInt("currentValue");
                        airMoistureStatus = airMoistureObject.getInt("status");
                        airMoistureDeviation = airMoistureObject.getInt("deviation");
                        airMoistureNorm = airMoistureObject.getString("norm");

                        JSONObject soilMoistureObject = response.getJSONObject("soil_moisture");
                        soilMoistureCurrentValue = soilMoistureObject.getInt("currentValue");
                        soilMoistureStatus = soilMoistureObject.getInt("status");
                        soilMoistureDeviation = soilMoistureObject.getInt("deviation");
                        soilMoistureNorm = soilMoistureObject.getString("norm");
                        waterRequire = soilMoistureObject.getInt("water_requirements");

                        JSONObject soilObject = response.getJSONObject("soil");
                        soilCurrentValue = soilObject.getInt("currentValue");
                        soilStatus = soilObject.getInt("status");
                        soilNorm = soilObject.getInt("norm");

                        JSONObject soilTempObject = response.getJSONObject("soil_temp");
                        soilTempCurrentValue = soilTempObject.getInt("currentValue");
                        soilTempStatus = soilTempObject.getInt("status");
                        soilTempDeviation = soilTempObject.getInt("deviation");
                        soilTempNorm = soilTempObject.getString("norm");

                        JSONObject phObject = response.getJSONObject("ph_value");
                        phCurrentValue = phObject.getInt("currentValue");
                        phStatus = phObject.getInt("status");
                        phDeviation = phObject.getInt("deviation");
                        phNorm = phObject.getString("norm");

                        JSONObject heightObject = response.getJSONObject("height_meter");
                        heightCurrentValue = heightObject.getInt("currentValue");
                        heightStatus = heightObject.getInt("status");
                        heightDeviation = heightObject.getInt("deviation");
                        heightNorm = heightObject.getString("norm");
                        matureMonth = response.getInt("mature_after_month");

                        setTextView();
                        setBackgroundColor();
                        setCropBackgroundImg(cropID);
                        setSoilBackgroundImg(soilCurrentValue);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
                    }
                } else {
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
                        default:
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

    private void setSoilBackgroundImg(int soilId){
        switch (soilId) {
            case 0:
                soilImg.setImageResource(R.drawable.soil_0_sand_img);
                break;

            case 1:
                soilImg.setImageResource(R.drawable.soil_1_clay_img);
                break;
            /*...*/
        }
    }

    private void setCropBackgroundImg(int cropID){
        switch (cropID) {
            case 0:
                cropImg.setImageResource(R.drawable.crop_0_caffe_img);
                break;

            case 1:
                cropImg.setImageResource(R.drawable.crop_1_tomato_img);
                break;
            /*...*/
        }
    }

    private void setBackgroundColor(){
        airTempImg.setImageResource(setColor(airTepmDeviation));
        airMoistureImg.setImageResource(setColor(airMoistureDeviation));
        soilTempImg.setImageResource(setColor(soilTempDeviation));
        soilMoistureImg.setImageResource(setColor(soilMoistureDeviation));
        phImg.setImageResource(setColor(phDeviation));}

    private int setColor(int deviation){
        int mColor;
        if (deviation <= 10){
            mColor = R.drawable.circle_10;
        } else if (deviation <= 20){
            mColor = R.drawable.circle_20;
        } else if (deviation <= 30){
            mColor = R.drawable.circle_30;
        } else if (deviation <= 40){
            mColor = R.drawable.circle_40;
        } else {
            mColor = R.drawable.circle_50;
        }
        return mColor;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tutorial_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_speak:
                speaker.speak("I can speak to you", TextToSpeech.QUEUE_FLUSH, null);
                Toast.makeText(this, "He speak to you", Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setTextView(){
        location.setText(entryLocationCity);
        height.setText(String.valueOf(heightCurrentValue));
        mature.setText(String.valueOf(matureMonth));
        airTemp.setText(airTempCurrentValue + celsius);
        airMoisture.setText(String.valueOf(airMoistureCurrentValue));
        ph.setText(String.valueOf(phCurrentValue));
        soilTemp.setText(soilTempCurrentValue + celsius);
        soilMoisture.setText(String.valueOf(soilMoistureCurrentValue));
    }

    private String buildeUrl(){
        Log.i("Tutorila URL :", BASE_URL + URL_BASE_ENTRIES + entryId + URL_BASE_TUTORIAL + tutorialID);
        return BASE_URL + URL_BASE_ENTRIES + entryId + URL_BASE_TUTORIAL + tutorialID;
    }

    @Override
    public void onPause() {
        if (speaker != null) {
            speaker.stop();
            speaker.shutdown();
        }
        super.onPause();
    }

}
