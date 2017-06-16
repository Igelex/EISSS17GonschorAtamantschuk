package com.example.android.harvesthand;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        final TextView email = (TextView) findViewById(R.id.profile_email);
        final TextView number = (TextView) findViewById(R.id.profile_number);

        final ImageButton editEmail = (ImageButton) findViewById(R.id.profile_edit_email_button);
        ImageButton editNumber = (ImageButton) findViewById(R.id.profile_edit_number_button);
        final ImageButton closeEmail = (ImageButton) findViewById(R.id.profile_close_email_button);
        ImageButton closeNumber = (ImageButton) findViewById(R.id.profile_close_number_button);

        final EditText inputEmail = (EditText) findViewById(R.id.profile_input_email);
        EditText inputNumber = (EditText) findViewById(R.id.profile_input_number);

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEmail.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
                inputEmail.setText(email.getText());
                inputEmail.setVisibility(View.VISIBLE);
                inputEmail.startAnimation(animation);
                inputEmail.requestFocus();
                closeEmail.setVisibility(View.VISIBLE);
                closeEmail.startAnimation(animation);
            }
        });
    }
}
