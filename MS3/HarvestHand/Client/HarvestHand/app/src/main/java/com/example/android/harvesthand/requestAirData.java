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
 * Helperclass, request Wetterdaten, wird in AddNewEntry aufgerufen
 */

public class requestAirData {
    public requestAirData() {
    }

    /**
     * @param context
     * @param progressBar
     * @param container - View, in der Snackbar angezeigt wird
     * @param url - request-url
     * @param callback - um die Daten an UI zu übergeben
     */
    protected void requestData(final Context context, int method, final ProgressBar progressBar, final View container,
                               String url,
                               final AddNewEntry.ServerCallback callback) {
        final Contracts contracts = new Contracts(null);
        Log.i("URL in CHECK: ", url);

        JsonObjectRequest request = new JsonObjectRequest(method, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        callback.onSuccess(response);
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


