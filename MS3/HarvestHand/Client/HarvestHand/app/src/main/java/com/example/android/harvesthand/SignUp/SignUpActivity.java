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
import android.widget.Toast;

import com.example.android.harvesthand.MainActivity;
import com.example.android.harvesthand.R;

import static com.example.android.harvesthand.Contracts.BASE_URL;
import static com.example.android.harvesthand.Contracts.URL_IP;
import static com.example.android.harvesthand.Contracts.URL_IP_BASE;
import static com.example.android.harvesthand.Contracts.URL_PORT;
import static com.example.android.harvesthand.Contracts.URL_PROTOCOL;

public class SignUpActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private View dialogView;
    private SharedPreferences sPrefIp;
    private SharedPreferences sPrefUser;

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
            mViewPager = (ViewPager) findViewById(R.id.container);
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

            sPrefIp = getSharedPreferences("IP_Address", Context.MODE_PRIVATE);
            sPrefUser = getSharedPreferences("User_id Pref", MODE_PRIVATE);

            if(!(sPrefIp.getString("ip_address", null) == null)){
                URL_IP = sPrefIp.getString("ip_address", null);
                BASE_URL = URL_PROTOCOL + URL_IP + URL_PORT;
                Toast.makeText(SignUpActivity.this, sPrefIp.getString("ip_address", null),
                        Toast.LENGTH_LONG).show();
                if(!(sPrefUser.getString("user_id", null) == null)) {
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    finish();
                }
            }else {
                showIPdialog();
            }

        }else {
            Toast.makeText(this, getString(R.string.msg_no_internet_connection), Toast.LENGTH_LONG).show();
        }

    }

    private void showIPdialog (){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        dialogView = getLayoutInflater().inflate(R.layout.ipaddress_custom_dialog, null);
        final EditText mIp = (EditText) dialogView.findViewById(R.id.input_enter_ip);

        if(sPrefIp.getString("ip_address", null) == null){
            mIp.setText(URL_IP_BASE);
        }else {
            mIp.setText(sPrefIp.getString("ip_address", null));
        }

        mBuilder.setView(dialogView);
        final AlertDialog dialog = mBuilder.create();

        Button mSave = (Button) dialogView.findViewById(R.id.save_ip);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mIp.getText().toString().trim().isEmpty()){
                    savePreferences(mIp.getText().toString().trim());
                    URL_IP = sPrefIp.getString("ip_address", null);
                    BASE_URL = URL_PROTOCOL + URL_IP + URL_PORT;
                    Toast.makeText(SignUpActivity.this, getString(R.string.dialog_ip_saved), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    if(!(sPrefUser.getString("user_id", null) == null)) {
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    }
                }else {
                    mIp.setError(getString(R.string.errmsg_valid_input_required));
                    Toast.makeText(SignUpActivity.this, getString(R.string.dialog_please_enter_ip), Toast.LENGTH_LONG).show();
                }

            }
        });

        Button mDiscard = (Button) dialogView.findViewById(R.id.discard_ip);
        mDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(SignUpActivity.this, getString(R.string.dialog_ip_not_changed), Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    private void savePreferences(String ip) {
        SharedPreferences.Editor editor = sPrefIp.edit();
        editor.putString("ip_address", ip);
        editor.apply();
        Log.i("Save IP Address: ", ip);
    }
}
