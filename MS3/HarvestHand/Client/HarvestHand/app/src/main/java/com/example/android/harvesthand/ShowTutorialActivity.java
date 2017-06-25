package com.example.android.harvesthand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.example.android.harvesthand.R.id.container;

public class ShowTutorialActivity extends AppCompatActivity {
    private String norm;
    private int status, currentValue, waterReq, property;
    private Contracts contracts;
    private LinearLayout container;
    TextView currentValueText, normText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tutorial);

        contracts = new Contracts();

        container = (LinearLayout) findViewById(R.id.show_container);

        Intent intent = getIntent();
        if (intent != null) {
            try {
                norm = intent.getStringExtra("norm");
                status = intent.getIntExtra("status", -1);
                currentValue = intent.getIntExtra("currentValue", -1);
                property = intent.getIntExtra("property", -1);
                if (intent.hasExtra("water_require")) {
                    waterReq = intent.getIntExtra("water_require", 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                contracts.showSnackbar(container, getString(R.string.msg_no_data), true, false);
                return;
            }
        }

        currentValueText = (TextView) findViewById(R.id.show_current_value);
        normText = (TextView) findViewById(R.id.show_norm_value);
        currentValueText.setText(String.valueOf(currentValue));
        normText.setText(norm);

    }
}
