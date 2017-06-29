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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.android.harvesthand.CheckCollaborator.userExist;

/**
 * Created by Pastuh on 28.06.2017.
 */

public class SendRequest {
    private JSONObject responseObject = null;
    private JSONArray responseArray = null;

    public SendRequest() {
    }

    protected void requestJsonObject(final Context context, final int method, final ProgressBar progressBar, final View container,
                                           String url,
                                           final AddNewEntry.ServerCallback callback) {
        final Contracts contracts = new Contracts(null);
        Log.i("URL in CHECK: ", url);

        JsonObjectRequest request = new JsonObjectRequest(method, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("HEAY HOP", "RESPONSE Weather");
                        progressBar.setVisibility(View.INVISIBLE);
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HEAY HOP", "RESPONSE Weather ERROR!!!!", error);
                        progressBar.setVisibility(View.INVISIBLE);
                        if (error.networkResponse != null) {
                            switch (error.networkResponse.statusCode) {
                                case 500:
                                    contracts.showSnackbar(container, context.getString(R.string.msg_internal_error), true, false);
                                    userExist = false;
                                    break;
                                case 404:
                                    contracts.showSnackbar(container, context.getString(R.string.msg_404_error), true, false);
                                    userExist = false;
                                    break;
                            }
                        } else {
                            contracts.showSnackbar(container, context.getString(R.string.connection_err), true, false);
                            userExist = false;
                        }
                    }
                });
        Volley.newRequestQueue(context).add(request);
    }
}


