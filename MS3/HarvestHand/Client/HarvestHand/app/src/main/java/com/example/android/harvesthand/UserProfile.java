package com.example.android.harvesthand;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.harvesthand.SignUp.SignUpActivity;

import static com.example.android.harvesthand.Contracts.*;

public class UserProfile extends AppCompatActivity {
    private Animation animation;
    private Boolean change = false;

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

        animation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        final TextView email = (TextView) findViewById(R.id.profile_email);
        final TextView number = (TextView) findViewById(R.id.profile_number);

        final Button saveButton = (Button) findViewById(R.id.profile_save_button);
        saveButton.setEnabled(false);
        saveButton.setAlpha(.5f);
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

        final ImageButton editEmail = (ImageButton) findViewById(R.id.profile_edit_email_button);
        final ImageButton editNumber = (ImageButton) findViewById(R.id.profile_edit_number_button);
        final ImageButton closeEmail = (ImageButton) findViewById(R.id.profile_close_email_button);
        final ImageButton closeNumber = (ImageButton) findViewById(R.id.profile_close_number_button);

        final EditText inputEmail = (EditText) findViewById(R.id.profile_input_email);
        inputEmail.setText(email.getText());
        inputEmail.setOnTouchListener(mTouchListener);
        final EditText inputNumber = (EditText) findViewById(R.id.profile_input_number);
        inputNumber.setText(number.getText());
        inputNumber.setOnTouchListener(mTouchListener);

        inputEmail.addTextChangedListener(new TextWatcher() {
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

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHide(editEmail, closeEmail, email, null, inputEmail, null);

            }
        });

        closeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHide(closeEmail, editEmail, null, email, null, inputEmail);
            }
        });

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
    }

    private void onClickHide(ImageButton buttonToHide, ImageButton buttonToShow, TextView textToHide, TextView textToShow, EditText inputToShow, EditText inputToHide) {
        buttonToHide.setVisibility(View.GONE);
        if (textToHide != null) {
            textToHide.setVisibility(View.GONE);
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
            inputToHide.setVisibility(View.GONE);
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
}
