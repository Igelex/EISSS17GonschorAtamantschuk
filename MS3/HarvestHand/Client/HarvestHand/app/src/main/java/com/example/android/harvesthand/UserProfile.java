package com.example.android.harvesthand;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.android.harvesthand.SignUp.SignUpActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.type;
import static com.example.android.harvesthand.Contracts.*;

public class UserProfile extends AppCompatActivity {
    private Animation animation;
    private Boolean change = false;
    private SendRequest request;
    private ProgressBar pb;
    private ConstraintLayout container;
    private SharedPreferences sPrefUser;
    private TextView number;
    private EditText inputNumber;
    private Contracts contracts;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            change = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        contracts = new Contracts(null);

        request = new SendRequest();

        pb = (ProgressBar) findViewById(R.id.profile_pb);
        pb.setVisibility(View.VISIBLE);

        sPrefUser = getSharedPreferences(USER_SHARED_PREFS, MODE_PRIVATE);

        container= (ConstraintLayout) findViewById(R.id.profile_container);

        animation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        number = (TextView) findViewById(R.id.profile_number);

        requestUserData();

        final Button saveButton = (Button) findViewById(R.id.profile_save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputIsValid()) {
                    updateUserData();
                }
            }
        });

        final Button logoutButton = (Button) findViewById(R.id.profile_logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sPref = getSharedPreferences(USER_SHARED_PREFS, Context.MODE_PRIVATE);
                if (sPref != null){
                    sPref.edit().remove(USER_SP_ID).apply();
                    startActivity(new Intent(UserProfile.this, SignUpActivity.class));
                }
            }
        });

        final ImageButton editNumber = (ImageButton) findViewById(R.id.profile_edit_number_button);
        final ImageButton closeNumber = (ImageButton) findViewById(R.id.profile_close_number_button);

        inputNumber = (EditText) findViewById(R.id.profile_input_number);
        inputNumber.setOnTouchListener(mTouchListener);

        editNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHide(editNumber, closeNumber, number, null, inputNumber, null);

            }
        });

        closeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHide(closeNumber, editNumber, null, number, null, inputNumber);
            }
        });

        inputNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                saveButton.setAlpha(1);
                saveButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void onClickHide(ImageButton buttonToHide, ImageButton buttonToShow, TextView textToHide,
                             TextView textToShow, EditText inputToShow, EditText inputToHide) {
        buttonToHide.setVisibility(View.INVISIBLE);
        if (textToHide != null) {
            textToHide.setVisibility(View.INVISIBLE);
        }
        if (textToShow != null) {
            textToShow.setVisibility(View.VISIBLE);
        }

        if (inputToShow != null) {
            inputToShow.setVisibility(View.VISIBLE);
            inputToShow.startAnimation(animation);
            inputToShow.requestFocus();
        }

        if (inputToHide != null) {
            inputToHide.setVisibility(View.INVISIBLE);
        }
        buttonToShow.setVisibility(View.VISIBLE);
        buttonToShow.startAnimation(animation);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!change) {
                    NavUtils.navigateUpFromSameTask(UserProfile.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(UserProfile.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_unsaved_changes_dialog);
        builder.setPositiveButton(R.string.dialog_ip_discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void requestUserData(){
        request.requestData(this, Request.Method.GET, pb, container, BASE_URL + URL_BASE_USERS
                + USER_ID, null, new AddNewEntry.ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    number.setText(result.getString("phone_number"));
                    inputNumber.setText(number.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                    contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
                }
            }
        });
    }

    private void updateUserData(){
        pb.setVisibility(View.VISIBLE);
        JSONObject newUserNumber = new JSONObject();
        try {
            newUserNumber.put("phone_number", inputNumber.getText());
        } catch (JSONException e) {
            e.printStackTrace();
            contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
        }
        request.requestData(this, Request.Method.PUT, pb, container, BASE_URL + URL_BASE_USERS
                + sPrefUser.getString(USER_SP_ID, null), newUserNumber, new AddNewEntry.ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    if (result.length() > 0 && result.getBoolean("res")){
                        savePreferences(inputNumber.getText().toString().trim());
                        Toast.makeText(UserProfile.this, R.string.msg_data_saved, Toast.LENGTH_LONG).show();
                        finish();
                    }else {
                        contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
                }
            }
        });
    }

    private boolean inputIsValid(){
        if (!change){
            contracts.showSnackbar(container, getString(R.string.msg_no_changes), true, false);
            number.setError(getString(R.string.msg_no_changes));
            return false;
        }
        if (inputNumber.getText().toString().trim().isEmpty()){
            inputNumber.setError(getString(R.string.errmsg_valid_input_required));
            inputNumber.requestFocus();
            return false;
        }
        return true;
    }

    private void savePreferences(String number) {
        SharedPreferences sPref = this.getSharedPreferences(USER_SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(USER_SP_NUMBER, number);
        USER_NUMBER = number;
        editor.apply();
        Log.i("Save User_number: ", String.valueOf(number));
    }
}
