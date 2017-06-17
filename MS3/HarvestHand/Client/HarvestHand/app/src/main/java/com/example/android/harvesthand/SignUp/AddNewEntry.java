package com.example.android.harvesthand.SignUp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.harvesthand.Contracts;
import com.example.android.harvesthand.MainActivity;
import com.example.android.harvesthand.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.android.harvesthand.Contracts.*;

import static com.example.android.harvesthand.R.id.container;

public class AddNewEntry extends AppCompatActivity {


    private ViewPager mViewPager;
    private View dialogView;
    private SharedPreferences sPrefIp;
    private SharedPreferences sPrefUser;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            // Create the adapter that will return a fragment for each of the two
            // primary sections of the activity.
            FragmentPagerAdapter fragmentAdapter = new FragmentPagerAdapter(this, getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(container);
            mViewPager.setAdapter(fragmentAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);

            FloatingActionButton flIP = (FloatingActionButton) findViewById(R.id.fl_ip_settings);
            flIP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showIPdialog();
                }
            });

            progressBar = (ProgressBar) findViewById(R.id.signup_progressbar);

            sPrefIp = getSharedPreferences(IP_ADDRESS_SHARED_PREFS, Context.MODE_PRIVATE);
            sPrefUser = getSharedPreferences(USER_SHARED_PREFS, Context.MODE_PRIVATE);

            if (!(sPrefIp.getString(IP_SP_IP, null) == null)) {
                URL_IP = sPrefIp.getString(IP_SP_IP, null);
                BASE_URL = URL_PROTOCOL + URL_IP + URL_PORT;
                if (!(sPrefUser.getString(USER_SP_ID, null) == null)) {
                    reqeustUserId(BASE_URL + URL_BASE_USERS + sPrefUser.getString(USER_SP_ID, null) );
                    }
            } else {
                showIPdialog();
            }

        } else {
            Toast.makeText(this, getString(R.string.msg_no_internet_connection), Toast.LENGTH_LONG).show();
        }

    }

    private void showIPdialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        dialogView = getLayoutInflater().inflate(R.layout.ipaddress_custom_dialog, null);
        final EditText mIp = (EditText) dialogView.findViewById(R.id.input_enter_ip);

        if (sPrefIp.getString(IP_SP_IP, null) == null) {
            mIp.setText(URL_IP_BASE);
        } else {
            mIp.setText(sPrefIp.getString(IP_SP_IP, null));
        }

        mBuilder.setView(dialogView);
        final AlertDialog dialog = mBuilder.create();

        Button mSave = (Button) dialogView.findViewById(R.id.save_ip);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIp.getText().toString().trim().isEmpty()) {
                    savePreferences(mIp.getText().toString().trim());
                    URL_IP = sPrefIp.getString(IP_SP_IP, null);
                    BASE_URL = URL_PROTOCOL + URL_IP + URL_PORT;
                    Toast.makeText(AddNewEntry.this, getString(R.string.dialog_ip_saved), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    if (!(sPrefUser.getString(USER_SP_ID, null) == null)) {
                        startActivity(new Intent(AddNewEntry.this, MainActivity.class));
                        finish();
                    }
                } else {
                    mIp.setError(getString(R.string.errmsg_valid_input_required));
                    Toast.makeText(AddNewEntry.this, getString(R.string.dialog_please_enter_ip), Toast.LENGTH_LONG).show();
                }

            }
        });

        Button mDiscard = (Button) dialogView.findViewById(R.id.discard_ip);
        mDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(AddNewEntry.this, getString(R.string.dialog_ip_not_changed), Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    private void savePreferences(String ip) {
        SharedPreferences.Editor editor = sPrefIp.edit();
        editor.putString(IP_SP_IP, ip);
        editor.apply();
        Log.i("Save IP Address: ", ip);
    }

    public void reqeustUserId(String URL) {
        final View view = findViewById(R.id.main_content);
        final Contracts contracts = new Contracts();

        Log.i("URL: ", URL);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            String currentUserId = response.getString("_id");
                            if (currentUserId != null) {
                                startActivity(new Intent(AddNewEntry.this, MainActivity.class));
                                Toast.makeText(AddNewEntry.this, getString(R.string.welcome_to_harvesthand), Toast.LENGTH_SHORT).show();
                            }else {
                                contracts.showSnackbar(view, getString(R.string.msg_please_login), true);
                            }
                            Log.i("User_id: ", currentUserId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            contracts.showSnackbar(view, getString(R.string.msg_error), true);
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
                                    contracts.showSnackbar(view, getString(R.string.msg_internal_error), true);
                                    break;
                                case 404:
                                    contracts.showSnackbar(view, getString(R.string.msg_404_error), true);
                                    break;
                            }
                        } else {
                            contracts.showSnackbar(view, getString(R.string.connection_err), true);
                        }
                    }
                });
        Volley.newRequestQueue(this).add(request);
    }
}
