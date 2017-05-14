package com.example.android.harvesthand;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.os.Build.VERSION_CODES.N;
import static com.example.android.harvesthand.SendRequest.entryArrayList;

public class EntryTutorialActivity extends AppCompatActivity {

    ImageView phImg, waterImg, mineralsImg;
    String tutorialURL;
    int ph, water, minerals, tutorial_ph, tutorial_water, tutorial_minerals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_tutorial);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            Intent intent = getIntent();
            if (intent.hasExtra("URL")) {
                tutorialURL = intent.getStringExtra("URL");
                Toast.makeText(this, "URL : " + tutorialURL, Toast.LENGTH_LONG).show();
            }

            if (intent.hasExtra("ph")) {
                ph = intent.getIntExtra("ph", 0);
            }

            if (intent.hasExtra("URL")) {
                water = intent.getIntExtra("water", 0);
            }

            if (intent.hasExtra("URL")) {
                minerals = intent.getIntExtra("minerals", 0);
            }

            if (intent.hasExtra("name")) {

                setTitle(intent.getStringExtra("name"));
            }


            phImg = (ImageView) findViewById(R.id.ph_icon);
            waterImg = (ImageView) findViewById(R.id.water_icon);
            mineralsImg = (ImageView) findViewById(R.id.minerals_icon);
            getTutorial(tutorialURL);
        }

    }

    public void getTutorial(String url) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.length() > 0) {
                    try {
                        Toast.makeText(EntryTutorialActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                        tutorial_ph = response.getInt("ph");
                        tutorial_water = response.getInt("water");
                        tutorial_minerals = response.getInt("minerals");

                        setPhIcon(tutorial_ph);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(this.getApplicationContext()).add(jsonRequest);
    }

    public void setPhIcon(int ph){
        switch (ph){
            case 0:
                phImg.setImageResource(R.drawable.emoticon);
                break;
            case 1:
                phImg.setImageResource(R.drawable.emoticon_sad);
                break;
            case 2:
                phImg.setImageResource(R.drawable.emoticon_neutral);
                break;
            default:
                phImg.setImageResource(R.drawable.emoticon_neutral);
        }
    }

    public void setWaterIcon(int water){
        switch (ph){
            case 0:
                phImg.setImageResource(R.drawable.emoticon);
                break;
            case 1:
                phImg.setImageResource(R.drawable.emoticon_sad);
                break;
            case 2:
                phImg.setImageResource(R.drawable.emoticon_neutral);
                break;
            default:
                phImg.setImageResource(R.drawable.emoticon_neutral);
        }
    }

}
