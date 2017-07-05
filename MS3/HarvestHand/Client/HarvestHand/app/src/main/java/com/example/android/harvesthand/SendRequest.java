package com.example.android.harvesthand;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Helperclass, führt Requests aus
 */

public class SendRequest {
    public SendRequest() {
    }

    /**
     * @param context
     * @param progressBar
     * @param container - View, in der Snackbar angezeigt wird
     * @param url - request-url
     * @param callback - um die Daten an UI zu übergeben
     * @param method - Request-Methode
     */
    protected void requestData(final Context context, int method, final ProgressBar progressBar, final View container,
                               String url, JSONObject jsonObject,
                               final AddNewEntry.ServerCallback callback) {
        final Contracts contracts = new Contracts(null);
        Log.i("Current URL: ", url);
        JsonObjectRequest request = new JsonObjectRequest(method, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        //Gib Repsone zurück
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (error.networkResponse != null) {
                            switch (error.networkResponse.statusCode) {
                                case 500:
                                    contracts.showSnackbar(container, context.getString(R.string.msg_internal_error), true, false);
                                    break;
                                case 404:
                                    contracts.showSnackbar(container, context.getString(R.string.msg_404_error), true, false);
                                    break;
                                case 409:
                                    contracts.showSnackbar(container, context.getString(R.string.msg_number_exist), true, false);
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


