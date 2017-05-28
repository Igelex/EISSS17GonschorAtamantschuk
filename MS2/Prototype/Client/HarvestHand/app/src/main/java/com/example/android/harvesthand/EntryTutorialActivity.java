package com.example.android.harvesthand;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class EntryTutorialActivity extends AppCompatActivity {

    private ImageView phImg, waterImg, mineralsImg;
    private ProgressBar progressBar;
    private String tutorialURL;
    private int ph, water, minerals, tutorial_ph, tutorial_water, tutorial_minerals;
    private RelativeLayout view;

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

            //werden erstmal nicht benutzt
            if (intent.hasExtra("ph")) {
                ph = intent.getIntExtra("ph", 0);
            }

            if (intent.hasExtra("URL")) {
                water = intent.getIntExtra("water", 0);
            }

            if (intent.hasExtra("URL")) {
                minerals = intent.getIntExtra("minerals", 0);
            }
            //werden erstmal nicht benutzt

            if (intent.hasExtra("name")) {

                setTitle(intent.getStringExtra("name"));
            }
            view = (RelativeLayout) findViewById(R.id.entry_tutorial_root);
            phImg = (ImageView) findViewById(R.id.ph_emotion);
            waterImg = (ImageView) findViewById(R.id.water_emotion);
            mineralsImg = (ImageView) findViewById(R.id.minerals_emotion);
            progressBar = (ProgressBar) findViewById(R.id.pg);
            getTutorial(tutorialURL);
        }else {
            Toast.makeText(EntryTutorialActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }

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
                        tutorial_ph = response.getInt("ph");
                        tutorial_water = response.getInt("water");
                        tutorial_minerals = response.getInt("minerals");

                        //Anhand der Tutorial-Daten Visual Feedback geben
                        setPhIcon(tutorial_ph);
                        setWaterIcon(tutorial_water);
                        setMineralsIcon(tutorial_minerals);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else{
                    Toast.makeText(EntryTutorialActivity.this, "No data found", Toast.LENGTH_SHORT).show();
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
                            Snackbar snackbarIE = Snackbar.make(view, getString(R.string.msg_internal_error), Snackbar.LENGTH_LONG);
                            View sbie = snackbarIE.getView();
                            TextView snackBarText = (TextView) sbie.findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.rgb(253, 86, 86));
                            snackbarIE.show();
                            break;
                        case 404:
                            Snackbar snackbar404 = Snackbar.make(view, getString(R.string.msg_404_error), Snackbar.LENGTH_LONG);
                            View snackbarView404 = snackbar404.getView();
                            TextView snackBarText404 = (TextView) snackbarView404.findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText404.setTextColor(Color.rgb(253, 86, 86));
                            snackbar404.show();
                            break;
                        default:
                            break;
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(view, getString(R.string.connection_err), Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    TextView snackBarText = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.rgb(253, 86, 86));
                    snackbar.show();
                    error.printStackTrace();
                }
            }
        });

        Volley.newRequestQueue(this.getApplicationContext()).add(jsonRequest);
    }

    //Icon setzen
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

    //Icon setzen
    public void setWaterIcon(int water){
        switch (water){
            case 0:
                waterImg.setImageResource(R.drawable.emoticon);
                break;
            case 1:
                waterImg.setImageResource(R.drawable.emoticon_sad);
                break;
            case 2:
                waterImg.setImageResource(R.drawable.emoticon_neutral);
                break;
            default:
                waterImg.setImageResource(R.drawable.emoticon_neutral);
        }
    }

    //Icon setzen
    public void setMineralsIcon(int minerals){
        switch (minerals){
            case 0:
                mineralsImg.setImageResource(R.drawable.emoticon);
                break;
            case 1:
                mineralsImg.setImageResource(R.drawable.emoticon_sad);
                break;
            case 2:
                mineralsImg.setImageResource(R.drawable.emoticon_neutral);
                break;
            default:
                mineralsImg.setImageResource(R.drawable.emoticon_neutral);
        }
    }

}
