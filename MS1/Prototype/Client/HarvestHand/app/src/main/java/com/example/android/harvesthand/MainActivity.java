package com.example.android.harvesthand;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getName();
    ArrayList<Entry> entryArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    private static final String URL = "http://192.168.0.12:3000/entries";
    RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Check Internetconnection*/
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            getEntries(URL);
            //Toast.makeText(this, entryArrayList.toString(), Toast.LENGTH_LONG).show();


        }

    }

    public void getEntries(String url) {
        JsonArrayRequest jsonRequest = new JsonArrayRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            JSONArray collaboratorsArray = jsonObject.getJSONArray("collaborators");
                            //Toast.makeText(MainActivity.this, collaboratorsArray.toString(), Toast.LENGTH_LONG).show();
                            String name = jsonObject.getString("entry_name");
                            int ph = jsonObject.getInt("ph_value");
                            String id = jsonObject.getString("_id");
                            int water = jsonObject.getInt("water");
                            int minerals = jsonObject.getInt("minerals");
                            entryArrayList.add(new Entry(id, name, 2, ph, water, minerals, null));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    RecyclerAdapter adapter = new RecyclerAdapter(entryArrayList);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this,"No Data found", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MainActivity.this,"Error", Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(this.getApplicationContext()).add(jsonRequest);
    }
}

