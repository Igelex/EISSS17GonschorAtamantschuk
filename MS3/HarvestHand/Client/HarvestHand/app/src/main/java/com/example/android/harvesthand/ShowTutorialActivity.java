package com.example.android.harvesthand;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ShowTutorialActivity extends AppCompatActivity {
    private String norm;
    private int status, currentValue, waterReq, property;
    private static final int GREATER = 2;
    private Contracts contracts;
    private LinearLayout container;
    TextView currentValueText, normText;
    private ArrayList<Tutorial> tutorialList = new ArrayList<>();
    private RecyclerView recyclerView;
    TutorialRecyclerAdapter adapter;

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

                recyclerView = (RecyclerView) findViewById(R.id.show_list);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);


            } catch (Exception e) {
                e.printStackTrace();
                contracts.showSnackbar(container, getString(R.string.msg_no_data), true, false);
                return;
            }

            switchProperty();
        }

        currentValueText = (TextView) findViewById(R.id.show_current_value);
        currentValueText.setText(String.valueOf(currentValue));
        normText = (TextView) findViewById(R.id.show_norm_value);

        String[] strings = getResources().getStringArray(R.array.soil_spinner_array);
        for (String s : strings) {
            int i = s.indexOf(getString(R.string.soil_sand));
            if (i >= 0) {
                normText.setText(s);
            }
        }

    }

    private void switchProperty() {
        switch (property) {
            case 0:
                switchStatus("soilmoisture");
        }
    }

    private void switchStatus(String property) {
        if (status == GREATER) {
            try {
                Field drawables = R.drawable.class.getField("tutorial_" + property + "_greater");
                Log.i("Image Name: ", drawables.getName());

            } catch (NoSuchFieldException e) {

            }

        } else {
            try {
                Field[] drawables = R.drawable.class.getFields();

                for (Field f : drawables) {
                    if (f.getName().matches("tutorial_" + property + "_less(.*)")) {
                        int drawableId = getResources().getIdentifier(f.getName(), "drawable",
                                this.getPackageName());

                        tutorialList.add(new Tutorial(drawableId));
                        //ImageView img = (ImageView) findViewById(R.id.img);
                        //img.setImageResource(drawableId);
                        //img.setImageDrawable(getResources().getDrawable(drawableId));
                        Log.i("Image Name: ", f.getName());
                    }
                }
                adapter = new TutorialRecyclerAdapter(tutorialList);
                recyclerView.setAdapter(adapter);

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }
}
