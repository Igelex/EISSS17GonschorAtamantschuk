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
import com.example.android.harvesthand.R;

import static com.example.android.harvesthand.Contracts.*;

import static com.example.android.harvesthand.R.id.container;

public class SignUpActivity extends AppCompatActivity {


    private ViewPager mViewPager;
    private View dialogView;
    private SharedPreferences sPrefIp;
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
            if (!(sPrefIp.getString(IP_SP_IP, null) == null)) {
                /*Wenn IP bereits in SharedPreferences, gespeichert, füg die in URL ein, ansonsten
                dialogfenster anzeigen*/
                URL_IP = sPrefIp.getString(IP_SP_IP, null);
                BASE_URL = URL_PROTOCOL + URL_IP + URL_PORT;
            } else {
                showIPdialog();
            }

        } else {
            Toast.makeText(this, getString(R.string.msg_no_internet_connection), Toast.LENGTH_LONG).show();
        }

    }
    /**
    * Dialogfenster zur eingabe der IP-Adresse
    * */
    private void showIPdialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        dialogView = getLayoutInflater().inflate(R.layout.ipaddress_custom_dialog, null);
        final EditText mIp = dialogView.findViewById(R.id.input_enter_ip);

        if (sPrefIp.getString(IP_SP_IP, null) == null) {
            mIp.setText(URL_IP_BASE);
        } else {
            mIp.setText(sPrefIp.getString(IP_SP_IP, null));
        }

        mBuilder.setView(dialogView);
        final AlertDialog dialog = mBuilder.create();

        Button mSave = dialogView.findViewById(R.id.save_ip);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIp.getText().toString().trim().isEmpty()) {
                    savePreferences(mIp.getText().toString().trim());
                    URL_IP = sPrefIp.getString(IP_SP_IP, null);
                    BASE_URL = URL_PROTOCOL + URL_IP + URL_PORT;
                    Toast.makeText(SignUpActivity.this, getString(R.string.dialog_ip_saved), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else {
                    mIp.setError(getString(R.string.errmsg_valid_input_required));
                    Toast.makeText(SignUpActivity.this, getString(R.string.dialog_please_enter_ip), Toast.LENGTH_LONG).show();
                }

            }
        });

        Button mDiscard = dialogView.findViewById(R.id.discard_ip);
        mDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(SignUpActivity.this, getString(R.string.dialog_ip_not_changed), Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    /**
     * die IP wird permanent in SharedPreferences gespeichert
     * @param ip - ip vom Dialogfenster
     */
    private void savePreferences(String ip) {
        SharedPreferences.Editor editor = sPrefIp.edit();
        editor.putString(IP_SP_IP, ip);
        editor.apply();
        Log.i("Save IP Address: ", ip);
    }
}
