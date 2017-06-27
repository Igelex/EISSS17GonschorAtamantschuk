package com.example.android.harvesthand;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowTutorialActivity extends AppCompatActivity {
    private String norm;
    private int status, waterReq, property, deviation, currentValue;
    private static final int GREATER = 2;
    private Contracts contracts;
    private LinearLayout container;
    TextView currentValueText;
    private ArrayList<Tutorial> tutorialList = new ArrayList<>();
    private RecyclerView recyclerView;
    TutorialRecyclerAdapter adapter;
    TextToSpeech speaker;
    ImageButton currentValueEar;

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
                deviation = intent.getIntExtra("deviation", -1);
                if (intent.hasExtra("water_require")) {
                    waterReq = intent.getIntExtra("water_require", 0);
                }

                recyclerView = (RecyclerView) findViewById(R.id.show_list);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);

                speaker = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            speaker.setLanguage(Locale.getDefault());
                        }
                    }
                });

                currentValueEar = (ImageButton) findViewById(R.id.show_current_value_ear);

                currentValueText = (TextView) findViewById(R.id.show_current_value);
                currentValueText.setText(String.valueOf(currentValue));

                ImageButton closeButton = (ImageButton) findViewById(R.id.show_close_imb);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

                setColor(deviation);
                switchProperty();


            } catch (Exception e) {
                e.printStackTrace();
                contracts.showSnackbar(container, getString(R.string.msg_no_data), true, false);
                return;
            }
        }
    }

    private void switchProperty() {
        switch (property) {
            case 0:
                switchStatus("soilmoisture");
                speak(currentValueEar, String.valueOf(currentValue), getString(R.string.property_soilmoisture));

        }
    }

    private void switchStatus(String property) {
        if (status == GREATER) {
            try {
                Field[] drawables = R.drawable.class.getFields();
                int i = 1;
                for (Field f : drawables) {
                    if (f.getName().matches("tutorial_" + property + "_greater(.*)")) {
                        tutorialList.add(new Tutorial(f.getInt(null), norm,
                                getDescriptionStrings("desc_array_" + property + "_greater_" + i)));
                        i++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            try {
                Field[] drawables = R.drawable.class.getFields();
                int i = 1;
                for (Field f : drawables) {
                    if (f.getName().matches("tutorial_" + property + "_less(.*)")) {
                        tutorialList.add(new Tutorial(f.getInt(null), norm,
                                getDescriptionStrings("desc_array_" + property + "_less_" + i)));
                        i++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        adapter = new TutorialRecyclerAdapter(tutorialList);
        recyclerView.setAdapter(adapter);
    }

    private String getDescriptionStrings(String arrayName) {
        String currentString = "";
        try {
            String[] strings = getResources().getStringArray(this.getResources().getIdentifier(arrayName, "array",
                    this.getPackageName()));
            for (String s : strings) {
                currentString += s;
            }
            return currentString;
        } catch (Exception e) {
            e.printStackTrace();
            return currentString;
        }

    }

    private void setColor(int deviation) {
        CircleImageView currentValueCircle = (CircleImageView) findViewById(R.id.show_current_value_circle);
        if (deviation <= 10) {
            currentValueCircle.setImageResource(R.drawable.circle_10);
        } else if (deviation <= 20) {
            currentValueCircle.setImageResource(R.drawable.circle_20);
        } else if (deviation <= 30) {
            currentValueCircle.setImageResource(R.drawable.circle_30);
        } else if (deviation <= 40) {
            currentValueCircle.setImageResource(R.drawable.circle_40);
        } else {
            currentValueCircle.setImageResource(R.drawable.circle_50);
        }
    }

    private void speak(ImageButton currentEar, final String currentValue, final String property) {
        currentEar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speaker.speak(getString(R.string.show_current_value_of) + property + getString(R.string.show_current_value_is)
                        + currentValue, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (speaker != null) {
            speaker.stop();
            speaker.shutdown();
        }
        super.onDestroy();
    }
}
