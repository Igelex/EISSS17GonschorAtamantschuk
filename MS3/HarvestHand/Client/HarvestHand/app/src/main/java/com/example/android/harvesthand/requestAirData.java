package com.example.android.harvesthand;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Pastuh on 28.06.2017.
 */

public class requestAirData {
    public requestAirData() {
    }
    protected void requestData(final Context context, final ProgressBar progressBar, final View container,
                               String url,
                               final AddNewEntry.ServerCallback callback) {
        final Contracts contracts = new Contracts(null);
        Log.i("URL in CHECK: ", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (response!= null && response.length() > 0) {
                            try {
                                int airTemp = response.getInt("temp");
                                int airHumidity = response.getInt("humidity");
                                callback.onSuccess(airTemp, airHumidity);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                contracts.showSnackbar(container, context.getString(R.string.msg_error), true, false);
                            }
                        } else {
                            contracts.showSnackbar(container, context.getString(R.string.msg_no_data_available), true, false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (error.networkResponse != null) {
                            switch (error.networkResponse.statusCode) {
                                case 500:
                                    contracts.showSnackbar(container, context.getString(R.string.msg_internal_error), true, false);
                                    break;
                                case 404:
                                    contracts.showSnackbar(container, context.getString(R.string.msg_404_error), true, false);
                                    break;
                            }
                        } else {
                            contracts.showSnackbar(container, context.getString(R.string.connection_err), true, false);
                        }
                    }
                });
        Volley.newRequestQueue(context).add(request);
    }
}


