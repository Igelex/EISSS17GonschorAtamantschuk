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
    private Button saveButton;

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

        saveButton = (Button) findViewById(R.id.profile_save_button);
        saveButton.setAlpha((float) .5);
        saveButton.setEnabled(false);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputIsValid()) {
                    updateUserData();
                }
            }
        });
        //User wird ausgeloggt
        final Button logoutButton = (Button) findViewById(R.id.profile_logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sPref = getSharedPreferences(USER_SHARED_PREFS, Context.MODE_PRIVATE);
                if (sPref != null){
                    sPref.edit().remove(USER_SHARED_PREFS_ID).apply();
                    USER_ID = null;
                    USER_NUMBER = null;
                    startActivity(new Intent(UserProfile.this, SignUpActivity.class));
                }
            }
        });

        final ImageButton editNumber = (ImageButton) findViewById(R.id.profile_edit_number_button);
        final ImageButton closeEditNumber = (ImageButton) findViewById(R.id.profile_close_number_button);

        inputNumber = (EditText) findViewById(R.id.profile_input_number);
        inputNumber.setOnTouchListener(mTouchListener);
        //Klick auf Edit-Button blendet Input-Feld ein
        editNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHide(editNumber, closeEditNumber, number, null, inputNumber, null);
            }
        });
        //Das umgekehrte
        closeEditNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHide(closeEditNumber, editNumber, null, number, null, inputNumber);
            }
        });


    }

    /**
     * Animattion für Einblenden/Ausblenden des Inputfeldes, unwichtig
     * @param buttonToHide
     * @param buttonToShow
     * @param textToHide
     * @param textToShow
     * @param inputToShow
     * @param inputToHide
     */
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
                //Bei nicht gespeicherten Änderungen Dialog anzeigen
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

    /**
     * Userdaten(Telefonnummer) wird requested
     */
    private void requestUserData(){
        //In der externen Klasse
        request.requestData(this, Request.Method.GET, pb, container, BASE_URL + URL_BASE_USERS
                + USER_ID, null, new AddNewEntry.ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    number.setText(result.getString("phone_number"));
                    inputNumber.setText(number.getText());
                    onTextChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
                }
            }
        });
    }

    /**
     * PUT-Request, User Telefnonummer wird aktualisiert
     */
    private void updateUserData(){
        pb.setVisibility(View.VISIBLE);
        JSONObject newUserNumber = new JSONObject();
        try {
            newUserNumber.put("phone_number", inputNumber.getText());
        } catch (JSONException e) {
            e.printStackTrace();
            contracts.showSnackbar(container, getString(R.string.msg_error), true, false);
        }
        //Request in er externen Klasse
        request.requestData(this, Request.Method.PUT, pb, container, BASE_URL + URL_BASE_USERS
                + sPrefUser.getString(USER_SHARED_PREFS_ID, null), newUserNumber, new AddNewEntry.ServerCallback() {
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
        if (inputNumber.getText().toString().trim().isEmpty()){
            inputNumber.setError(getString(R.string.errmsg_valid_input_required));
            inputNumber.requestFocus();
            return false;
        }
        return true;
    }
    /**
     * Aktualisierte Daten werden In SharedPreferences gespeichert
     * @param number - neue Telefonnummer
     */
    private void savePreferences(String number) {
        SharedPreferences sPref = this.getSharedPreferences(USER_SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(USER_SHARED_PREFS_NUMBER, number);
        USER_NUMBER = number;
        editor.apply();
        Log.i("Save User_number: ", String.valueOf(number));
    }
    /**
     * Fals Änderungen gemacht wurden, wird der Save-Button aktiviert
     */
    private void onTextChanged(){
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
}
