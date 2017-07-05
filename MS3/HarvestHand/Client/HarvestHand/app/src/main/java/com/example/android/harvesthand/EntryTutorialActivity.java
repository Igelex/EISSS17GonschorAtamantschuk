package com.example.android.harvesthand;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private ImageButton imgSoilmoisture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_tutorial);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            InitTTS tts = new InitTTS(this);
            speaker = tts.initTTS();
            contracts = new Contracts(speaker);

            //Hol entry-Daten von der Parent-Activity
            Intent intent = getIntent();
            if (intent != null) {
                try {
                    entryId = intent.getStringExtra("entry_id");
                    tutorialID = intent.getStringExtra("tutorial_id");
                    Log.i("Tutorila ID:", tutorialID);
                    entryLocationCity = intent.getStringExtra("entry_location_city");
                    cropID = intent.getIntExtra("crop_id", 0);
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
            //Init CircleImages, die die einzelnen Werte darstellen
            airTempImg = (CircleImageView) findViewById(R.id.tutorial_airtemp_image);
            airMoistureImg = (CircleImageView) findViewById(R.id.tutorial_airmoisture_image);
            cropImg = (CircleImageView) findViewById(R.id.tutorial_crop_image);
            soilImg = (CircleImageView) findViewById(R.id.tutorial_soil_image);
            phImg = (CircleImageView) findViewById(R.id.tutorial_ph_image);
            soilTempImg = (CircleImageView) findViewById(R.id.tutorial_soiltemp_image);
            soilMoistureImg = (CircleImageView) findViewById(R.id.tutorial_soilmoisture_image);
            //Init TextViews
            location = (TextView) findViewById(R.id.tutorial_loc);
            height = (TextView) findViewById(R.id.tutorial_hight);
            mature = (TextView) findViewById(R.id.tutorial_mature);
            airTemp = (TextView) findViewById(R.id.tutorial_text_air_temp);
            airMoisture = (TextView) findViewById(R.id.tutorial_text_air_moisture);
            soilTemp = (TextView) findViewById(R.id.tutorial_text_soil_temp);
            soilMoisture = (TextView) findViewById(R.id.tutorial_text_soil_moisture);
            ph = (TextView) findViewById(R.id.tutorial_text_ph);
            //request Tutorial-Daten
            getTutorial(buildeUrl());

        } else {
            contracts.showSnackbar(container, getString(R.string.msg_no_internet_connection), true, false );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tutorial_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_speak:
                speaker.speak("I can speak to you", TextToSpeech.QUEUE_FLUSH, null);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Reuqeust Tutorial Daten und fülle die UI mit entsprechenden Daten
     * @param url - http://192.168.0.17:3001/entries/entri_id/tutorials/tutorial_id
     */
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

                        JSONObject airHumidityObject = response.getJSONObject("air_humidity");
                        airMoistureCurrentValue = airHumidityObject.getInt("currentValue");
                        airMoistureStatus = airHumidityObject.getInt("status");
                        airMoistureDeviation = airHumidityObject.getInt("deviation");
                        airMoistureNorm = airHumidityObject.getString("norm");

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

                        //Helpermethods werde aufgerufen
                        setTextView();
                        setBackgroundColor();
                        setCropBackgroundImg(cropID);
                        setSoilBackgroundImg(soilCurrentValue);
                        setSpeaker();
                        setStartTutorialClickListener();

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

    /**
     * Image des Bodenartes wird abhängig von der soilId gesetzt
     * @param soilId - id des bestimmten Bodenartes im System
     */
    private void setSoilBackgroundImg(int soilId) {
        switch (soilId) {
            case 1:
                soilImg.setImageResource(R.drawable.soil_0_sand_img);
                break;

            case 2:
                soilImg.setImageResource(R.drawable.soil_1_clay_img);
                break;
            case 3:
                soilImg.setImageResource(R.drawable.soil_3_humus_img);
                break;
            /*...*/
        }
    }

    /**
     * Image der Pflanze wird abhängig von der cropID gesetzt
     * @param cropID - id der bestimmten Pflanze im System
     */
    private void setCropBackgroundImg(int cropID) {
        switch (cropID) {
            case 1:
                cropImg.setImageResource(R.drawable.crop_0_caffe_img);
                break;

            case 2:
                cropImg.setImageResource(R.drawable.crop_1_cacao_img);
                break;
            case 3:
                cropImg.setImageResource(R.drawable.crop_2_banane_img);
                break;
            /*...*/
        }
    }

    /**
     * die BackgroundColor der CircleImages wird gesetzt, background ist ein Circle, definiert in
     * drawables (circle_10.xml ...)
     */
    private void setBackgroundColor() {
        //für jeweilige CircleImages wird setColor aufgerufen
        airTempImg.setImageResource(setColor(airTepmDeviation));
        airMoistureImg.setImageResource(setColor(airMoistureDeviation));
        soilTempImg.setImageResource(setColor(soilTempDeviation));
        soilMoistureImg.setImageResource(setColor(soilMoistureDeviation));
        phImg.setImageResource(setColor(phDeviation));
    }

    /**
     * Die Color wird abhängig von @deviation gesetz
     * @param deviation - prozentuelle Abweichung eines bestimmten Wertes von der Norm
     * @return - drawable-id
     */
    private int setColor(int deviation) {
        int mCircle;
        if (deviation <= 10) {
            mCircle = R.drawable.circle_10;
        } else if (deviation <= 20) {
            mCircle = R.drawable.circle_20;
        } else if (deviation <= 30) {
            mCircle = R.drawable.circle_30;
        } else if (deviation <= 40) {
            mCircle = R.drawable.circle_40;
        } else {
            mCircle = R.drawable.circle_50;
        }
        return mCircle;
    }

    /**
     * TextVies werden mit entsprechenden Inhalten gefüllt
     */
    private void setTextView() {
        location.setText(entryLocationCity);
        height.setText(String.valueOf(heightCurrentValue) + getString(R.string.add_entry_meter));
        mature.setText(String.valueOf(matureMonth) + getString(R.string.tutorial_month));
        airTemp.setText(airTempCurrentValue + celsius);
        airMoisture.setText(String.valueOf(airMoistureCurrentValue));
        ph.setText(String.valueOf(phCurrentValue));
        soilTemp.setText(soilTempCurrentValue + celsius);
        soilMoisture.setText(String.valueOf(soilMoistureCurrentValue));
    }

    /**
     * Url zur Request des Tutorials wird gebildet
     * @return - URL
     */
    private String buildeUrl() {
        Log.i("Tutorila URL :", BASE_URL + URL_BASE_ENTRIES + entryId + URL_BASE_TUTORIAL + tutorialID);
        return BASE_URL + URL_BASE_ENTRIES + entryId + URL_BASE_TUTORIAL + tutorialID;
    }

    /**
     * Die earButtons werden initialisiert, für die Buttons wird die Methode earButtonsClickListener(...)
     * aufgerufen
     */
    private void setSpeaker() {
        ImageButton earLocation = (ImageButton) findViewById(R.id.tutorial_location_ear);
        //der zu Text zu Vorlesen wird übergeben
        earButtonsClickListener(earLocation, getString(R.string.item_speaktext_location)
                + entryLocationCity);
        ImageButton earHeight = (ImageButton) findViewById(R.id.tutorial_height_ear);
        //der zu Text zu Vorlesen wird übergeben
        earButtonsClickListener(earHeight, getString(R.string.tutorial_speaktext_height)
                + heightCurrentValue);
        ImageButton earMature = (ImageButton) findViewById(R.id.tutorial_mature_ear);
        //der zu Text zu Vorlesen wird übergeben
        earButtonsClickListener(earMature, getString(R.string.tutorial_speaktext_mature)
                + matureMonth + getString(R.string.tutorial_month));
    }

    /**
     * Die OnClickListener für ear-Buttons werden definiert
     * @param ear - Button (Location-Button, Height-Button..,)
     * @param text - Text, der beim Anklicken des jeweiligen Buttons gesprochen wird
     */
    private void earButtonsClickListener(ImageButton ear, final String text) {
        ear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Der Text wird Vorgelesen
                speakText(text);
            }
        });
    }

    /**
     * Vorlesen des Textes
     * @param text - der Text zum Vorlesen
     */
    private void speakText(String text) {
        speaker.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * Bei Anklicken eines CircleImages wird zur jewiligen Eigenschaft die Anbauepmfehlung(Tutorial) angezeigt
     * Für alle Eigenschaften wird Tutorial gestartet, für die Prototypepräsentation werden nur
     * @airTemp und @soilMoisture implementiert
     */
    private void setStartTutorialClickListener(){
        //für Tutorial werden für jeweilige Eigenschaft relevante Daten übergeben
        //@airTemp
        startTutorial(soilMoistureImg, soilMoistureNorm, soilMoistureStatus, soilMoistureCurrentValue,
                PROPERTY_SOIL_MOISTURE, waterRequire, soilMoistureDeviation, 0);
        //@soilMoisture
        startTutorial(airTempImg, airTempNorm, airTempStatus, airTempCurrentValue, PROPERTY_AIR_TEMP,
                0, airTepmDeviation, 0);
        //Allgemeine Anbauempfehlung zur bestimmten Pflanze
        startTutorial(cropImg, null, 0, 0, PROPERTY_GENERAL, 0, 0, cropID);
        /*        .
        .
        .
        .
        * */
    }

    /**
     * Hier wird ShowTutorialActivity gestartet, die die Tutorials präsentiert
     * @param button- CircleImage, das angeklickt wurde
     * @param norm - Norm-Wert der jeweiligen Eigenschaft
     * @param status - status der jeweiligen Eigenschaft(LESS, GRAETER, NORM)
     * @param currentValue - aktueller Wert der jeweiligen Eingenschaft
     * @param propertyId - id der jeweiligen Eigenschaft (PROPERTY_SOIL_MOISTURE, ...)
     * @param waterReq - Wasserverbrauch, falls vorhanden
     * @param deviation - die Abweichung von Norm in prozent
     */
    private void startTutorial(ImageView button, final String norm, final int status,
                               final int currentValue, final int propertyId, final int waterReq,
                               final int deviation, final int cropId) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntryTutorialActivity.this, ShowTutorialActivity.class);
                intent.putExtra("norm", norm);
                intent.putExtra("status", status);
                intent.putExtra("currentValue", currentValue);
                intent.putExtra("property", propertyId);
                intent.putExtra("deviation", deviation);
                if (waterReq != 0){
                    intent.putExtra("water_require", waterReq);
                }
                if (cropId != 0){
                    intent.putExtra("crop_id", cropId);
                }
                startActivity(intent);
            }
        });

    }

    @Override
    public void onDestroy() {
        if (speaker != null) {
            speaker.stop();
            speaker.shutdown();
        }
        super.onDestroy();
    }

}
