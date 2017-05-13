package com.example.android.harvesthand;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Pastuh on 13.05.2017.
 */

public class SendRequest {
    Context mContext;
    ArrayList<Entry> jsonResponse;

    public SendRequest(Context mContext, ArrayList<Entry> jsonResponse) {
        this.mContext = mContext;
        this.jsonResponse = jsonResponse;
    }

    public void getEntries(URL url){
        JsonArrayRequest jsonRequest = new JsonArrayRequest(
                Request.Method.GET, url.toString(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0; i < response.length(); i++ ){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
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

        Volley.newRequestQueue(mContext.getApplicationContext()).add(jsonRequest);
    }
}
