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


public class CheckCollaborator {

    static String user = null;

    public CheckCollaborator() {
    }

    protected String getUser(final Context context, ProgressBar pg, View c, String url ) {
        final ProgressBar progressBar = pg;
        final View container = c;
        final Contracts contracts = new Contracts();

        Log.i("URL in CHECK: ", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (response != null && response.length() > 0) {
                            try {
                                Log.i("CHecke USER:", response.getString("_id"));
                                String userId = response.getString("_id");
                                if (userId != null && userId.length() > 0) {
                                    user = userId;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                contracts.showSnackbar(container, context.getString(R.string.msg_error), true, false);
                                user = null;
                            }
                        }else{
                            contracts.showSnackbar(container,
                                    context.getString(R.string.msg_user_not_found), true, false);
                            user = null;
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
                                    user = null;
                                    break;
                                case 404:
                                    contracts.showSnackbar(container, context.getString(R.string.msg_404_error), true, false);
                                    user = null;
                                    break;
                                case 204:
                                    contracts.showSnackbar(container, "hui popimi", true, false);
                                    user = null;
                                    break;
                            }
                        } else {
                            contracts.showSnackbar(container, context.getString(R.string.connection_err), true, false);
                            user = null;
                        }
                    }
                });
        Volley.newRequestQueue(context).add(request);
        return user;
    }

}
