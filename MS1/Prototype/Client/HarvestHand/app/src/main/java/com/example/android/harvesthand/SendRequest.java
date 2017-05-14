/*
package com.example.android.harvesthand;

import android.content.Context;
import android.util.Log;
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

*/
/**
 * Created by Pastuh on 13.05.2017.
 *//*


public class SendRequest {
    static ArrayList<Entry> entryArrayList = new ArrayList<>();

    public static ArrayList<Entry> getEntries(String url, final Context mContext) {
        JsonArrayRequest jsonRequest = new JsonArrayRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        JSONArray collaboratorsArray = jsonObject.getJSONArray("collaborators");
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
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(mContext.getApplicationContext()).add(jsonRequest);
        return entryArrayList;
    }
}
*/
