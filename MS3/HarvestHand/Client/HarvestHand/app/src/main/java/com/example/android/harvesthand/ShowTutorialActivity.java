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

        InitTTS tts = new InitTTS(this);
        speaker = tts.initTTS();
        contracts = new Contracts(speaker);
        container = (LinearLayout) findViewById(R.id.show_container);

        // Lese daten von der EntryTutorialActivity
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
                switchStatus("soilmoisture", String.valueOf(waterReq));
                speak(currentValueEar, String.valueOf(currentValue), getString(R.string.property_soilmoisture));
                break;
            case 1:
                switchStatus("airtemp", "");
                speak(currentValueEar, String.valueOf(currentValue), getString(R.string.property_airtemp));
                break;
            /*Werden nicht implementiert, nur zwei obere Beispiele
            case 2:
                switchStatus("soilttemp");
                speak();
                break;
            case 3:
                switchStatus("airhumidity");
                speak();
                break;
            case 4:
                switchStatus("ph");
                speak();
                break;
            case 5:
                switchStatus("soiltype");
                speak();
                break;
            case 6:
                switchStatus("height");
                speak();
                break;*/
        }
    }

    /**
     * in switchStatus werden die passenden Images in drawables abhängig vom @status gesucht.
     *@param property - ist die Eigenschaft eines Eintrags, für die ein Tutorial erstellt werden muss.
     * in drawables wird nach dem vorher definiertem Namen dem Image gesucht.
     *Alle Namen müssen nach bestimtem Muster gebildet werden(z.B "tutorial_soilmoisture_less_1);
     * Zur eine @property kann es mehrere Images/Animation/videos geben. Images/Animation/videos zur einem
     * @property werden am ende des Namens mit einem @i Identifikator bezeichnet.
     */
    private void switchStatus(String property, String optional) {
        String water = "";
        if (status == GREATER) {
            try {
                //Alle images aus drawable(nicht perfomat)
                Field[] drawables = R.drawable.class.getFields();
                int i = 1;//Identifikator
                //Images für @property nach Namen suchen
                for (Field f : drawables) {
                    if (f.getName().matches("tutorial_" + property + "_greater(.*)")) {
                        //Bei der Übereinstimmung Image und zugehörgie Description der Liste
                        //hinzufügen
                        tutorialList.add(new Tutorial(f.getInt(null), norm,
                                getDescriptionStrings("desc_array_" + property + "_greater_" + i) + optional));
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
                                getDescriptionStrings("desc_array_" + property + "_less_" + i) + optional));
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

    /**
     * Sucht nach dem Array von Strings zur einem Image
     *
     * @param arrayName - array-Name, wird von switchStatus() übergeben.
     * @return - string, description eines Images, wird vorgelesen.
     */
    private String getDescriptionStrings(String arrayName) {
        String currentString = "";
        try {
            //Suche array nach Namen
            String[] strings = getResources().getStringArray(this.getResources().getIdentifier(arrayName, "array",
                    this.getPackageName()));
            // Bilde ein String aus mehreren Strings
            for (String s : strings) {
                currentString += s;
            }
            return currentString;
        } catch (Exception e) {
            e.printStackTrace();
            return currentString;
        }

    }

    /**
     * Visual Feedback zur dem aktuellen Werte der Eigenschaft
     * @param deviation - die abweichnug des aktuellen Wertes von Norm in Prozetn
     */
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

    /**
     * Vorlesefunktion, aktuelle Eigenschaft und derer Wert werden vorgelesen     *
     * @param currentEar   - ImageButton
     * @param currentValue - aktueller Wert der Eigenschaft
     * @param property     - aktuelle Eigenschaft
     */
    private void speak(ImageButton currentEar, final String currentValue, final String property) {
        currentEar.setOnClickListener(new View.OnClickListener() {
            @Override
            //Vorlese Text wird zusammengesetzt(Z.b Current value of + Air Temp + is + 25)
            public void onClick(View view) {
                speaker.speak(getString(R.string.show_current_value_of) + property + getString(R.string.show_current_value_is)
                        + currentValue, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    @Override
    protected void onStop() {
        if (speaker != null) {
            speaker.stop();
            speaker.shutdown();
        }
        super.onStop();
    }
}
