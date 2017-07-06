package com.example.android.harvesthand;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.android.harvesthand.Contracts.BASE_URL;
import static com.example.android.harvesthand.Contracts.URL_BASE_ENTRIES;
import static com.example.android.harvesthand.Contracts.URL_BASE_TUTORIAL;

public class EntryDetails extends AppCompatActivity {
    private SendRequest request = new SendRequest();
    private Contracts contracts;
    private ScrollView container;
    private TextView name, location, area, airTemp, airHumidity, soilTemp, soilMoisture, ph, crop, soilType;
    private CircleImageView airTempImg, airHumidityImg, cropImg, phImg, soilTempImg, soilMoistureImg;
    private String celsius = "\u2103", entryId, tutorialId;
    private int cropId, airTepmDeviation, airMoistureDeviation, soilMoistureDeviation, soilTempDeviation, phDeviation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_details);

        contracts = new Contracts(null);

        container = (ScrollView) findViewById(R.id.details_container);

        name = (TextView) findViewById(R.id.details_name);
        area = (TextView) findViewById(R.id.details_area);
        location = (TextView) findViewById(R.id.details_location);
        crop = (TextView) findViewById(R.id.details_crop);
        soilType = (TextView) findViewById(R.id.details_soil);
        airTemp = (TextView) findViewById(R.id.details_air_temp);
        airHumidity = (TextView) findViewById(R.id.details_air_humidity);
        soilTemp = (TextView) findViewById(R.id.details_soil_temp);
        soilMoisture = (TextView) findViewById(R.id.details_soil_moisture);
        ph = (TextView) findViewById(R.id.details_ph);

        Intent intent = getIntent();
        if (intent != null) {
            name.setText(intent.getStringExtra("entry_name"));
            area.setText(String.valueOf(intent.getIntExtra("entry_area", 0))
                    + getString(R.string.add_entry_m_squared));
            entryId = intent.getStringExtra("entry_id");
            tutorialId = intent.getStringExtra("tutorial_id");
            cropId = intent.getIntExtra("crop_id", 0);
            location.setText(intent.getStringExtra("entry_location_city"));
        }else{
            contracts.showSnackbar(container, getString(R.string.msg_no_data), true, false);
        }

        airTempImg = (CircleImageView) findViewById(R.id.details_airtemp_circle);
        airHumidityImg = (CircleImageView) findViewById(R.id.details_airhumidity_circle);
        soilMoistureImg = (CircleImageView) findViewById(R.id.details_soilmoisture_circle);
        soilTempImg = (CircleImageView) findViewById(R.id.details_soiltemp_circle);
        cropImg = (CircleImageView) findViewById(R.id.details_crop_circle);
        phImg = (CircleImageView) findViewById(R.id.details_ph_circle);

        //TextButton, der zum Tutorial führt, wird nicht implementiert, da irrelevant
        TextView showTutorial = (TextView) findViewById(R.id.details_show_tutorial);
        showTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(EntryDetails.this, EntryTutorialActivity.class));
            }
        });

        requestEntryData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details_entry_menu, menu);
        return true;
    }

    private void requestEntryData() {
        //progBar.setVisibility(View.VISIBLE);
        //Request in der externen Klasse
        request.requestData(this, Request.Method.GET, null, container, BASE_URL + URL_BASE_ENTRIES + entryId + URL_BASE_TUTORIAL + tutorialId,
                null, new AddNewEntry.ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        if (response != null) {
                            try {
                                JSONObject airTempObject = response.getJSONObject("air_temp");
                                int airTempCurrentValue = airTempObject.getInt("currentValue");
                                airTepmDeviation = airTempObject.getInt("deviation");

                                JSONObject airHumidityObject = response.getJSONObject("air_humidity");
                                int airHumidityCurrentValue = airHumidityObject.getInt("currentValue");
                                airMoistureDeviation = airHumidityObject.getInt("deviation");

                                JSONObject soilMoistureObject = response.getJSONObject("soil_moisture");
                                int soilMoistureCurrentValue = soilMoistureObject.getInt("currentValue");
                                soilMoistureDeviation = soilMoistureObject.getInt("deviation");

                                JSONObject soilObject = response.getJSONObject("soil");
                                int soilCurrentValue = soilObject.getInt("currentValue");

                                JSONObject soilTempObject = response.getJSONObject("soil_temp");
                                int soilTempCurrentValue = soilTempObject.getInt("currentValue");
                                soilTempDeviation = soilTempObject.getInt("deviation");

                                JSONObject phObject = response.getJSONObject("ph_value");
                                int phCurrentValue = phObject.getInt("currentValue");
                                phDeviation = phObject.getInt("deviation");

                                //Felder mit daten füllen
                                airTemp.setText(String.valueOf(airTempCurrentValue) + celsius);
                                airHumidity.setText(String.valueOf(airHumidityCurrentValue) + "%");
                                ph.setText(String.valueOf(phCurrentValue));
                                soilTemp.setText(String.valueOf(soilTempCurrentValue) + celsius);
                                soilMoisture.setText(String.valueOf(soilMoistureCurrentValue) + "%");

                                setBackgroundColor();
                                setCropBackgroundImg(cropId);
                                setCropName(cropId);
                                setSoilName(soilCurrentValue);

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

    /**
     * Image der Pflanze wird abhängig von der cropID gesetzt
     *
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
     * Name der Pflanze wird abhängig von der cropID gesetzt
     *
     * @param cropID - id der bestimmten Pflanze im System
     */
    private void setCropName(int cropID) {
        switch (cropID) {
            case 1:
                crop.setText(getString(R.string.crop_coffe));
                break;

            case 2:
                crop.setText(getString(R.string.crop_cacao));
                break;
            case 3:
                crop.setText(getString(R.string.crop_banana));
                break;
            /*...*/
        }
    }

    /**
     * Name des Bodens wird abhängig von der soilID gesetzt
     *
     * @param soilID - id des bestimmten Bodentypes im System
     */
    private void setSoilName(int soilID) {
        switch (soilID) {
            case 1:
                soilType.setText(getString(R.string.soil_sand));
                break;

            case 2:
                soilType.setText(getString(R.string.soil_clay));
                break;
            case 3:
                soilType.setText(getString(R.string.soil_humus));
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
        airHumidityImg.setImageResource(setColor(airMoistureDeviation));
        soilTempImg.setImageResource(setColor(soilTempDeviation));
        soilMoistureImg.setImageResource(setColor(soilMoistureDeviation));
        phImg.setImageResource(setColor(phDeviation));
    }

    /**
     * Die Color wird abhängig von @deviation gesetz
     *
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

}
