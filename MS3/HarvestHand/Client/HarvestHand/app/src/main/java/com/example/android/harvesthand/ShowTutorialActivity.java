package com.example.android.harvesthand;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.android.harvesthand.Contracts.CROP_ID_BANANA;
import static com.example.android.harvesthand.Contracts.CROP_ID_CACAO;
import static com.example.android.harvesthand.Contracts.CROP_ID_CAFFE;
import static com.example.android.harvesthand.Contracts.PROPERTY_AIR_TEMP;
import static com.example.android.harvesthand.Contracts.PROPERTY_GENERAL;
import static com.example.android.harvesthand.Contracts.PROPERTY_SOIL_MOISTURE;

public class ShowTutorialActivity extends AppCompatActivity {
    private String norm, cropName;
    private int status;
    private int waterReq;
    private int propertyId;
    private int currentValue;
    private int cropId;
    private static final int GREATER = 2;
    TextView currentValueText;
    private ArrayList<Tutorial> tutorialList = new ArrayList<>();
    private RecyclerView recyclerView;
    TutorialRecyclerAdapter adapter;
    TextToSpeech speaker;
    ImageButton currentValueEar;
    InitTTS tts = new InitTTS(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tutorial);

        speaker = tts.initSpeaker();
        Contracts contracts = new Contracts(speaker);
        LinearLayout container = (LinearLayout) findViewById(R.id.show_container);

        currentValueEar = (ImageButton) findViewById(R.id.show_current_value_ear);
        CircleImageView currentValueCircleImg = (CircleImageView) findViewById(R.id.show_current_value_circle);
        currentValueText = (TextView) findViewById(R.id.show_current_value);
        View divider = findViewById(R.id.show_divider);

        // Lese daten von der EntryTutorialActivity
        Intent intent = getIntent();
        if (intent != null) {
            try {
                norm = intent.getStringExtra("norm");
                status = intent.getIntExtra("status", -1);
                currentValue = intent.getIntExtra("currentValue", -1);
                propertyId = intent.getIntExtra("property", -1);
                int deviation = intent.getIntExtra("deviation", -1);
                if (intent.hasExtra("water_require")) {
                    waterReq = intent.getIntExtra("water_require", 0);
                }
                if (intent.hasExtra("crop_id")) {
                    cropId = intent.getIntExtra("crop_id", 0);
                    cropName = intent.getStringExtra("crop_name");
                    currentValueEar.setVisibility(View.GONE);
                    currentValueCircleImg.setVisibility(View.GONE);
                    currentValueText.setVisibility(View.GONE);
                    divider.setVisibility(View.GONE);
                }
                //Liste, in der die Tutorials generiert werden
                recyclerView = (RecyclerView) findViewById(R.id.show_list);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);

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

    /**
     * Als erstet wird nach der @propertyId entschieden, Anbauempfehlung (Tutorial) zu welcher Eigenschaft angezeigt
     * werden muss
     */
    private void switchProperty() {
        switch (propertyId) {
            case PROPERTY_SOIL_MOISTURE:
                switchStatus("soilmoisture");
                speak(currentValueEar, String.valueOf(currentValue), getString(R.string.property_soilmoisture));
                break;
            case PROPERTY_AIR_TEMP:
                switchStatus("airtemp");
                speak(currentValueEar, String.valueOf(currentValue), getString(R.string.property_airtemp));
                break;
            /*Werden nicht implementiert, nur zwei obere Beispiele
            case PROPERTY_SOIL_TEMP:
                switchStatus("soilttemp");
                speak();
                break;
            case PROPERTY_AIR_HUMIDITY:
                switchStatus("airhumidity");
                speak();
                break;
            case PROPERTY_PH:
                switchStatus("ph");
                speak();
                break;
            case PROPERTY_SOIL_TYPE:
                switchStatus("soiltype");
                speak();
                break;
            case PROPERTY_HEIGHT:
                switchStatus("height");
                speak();
                break;*/
            case PROPERTY_GENERAL:
                //Allgemeien Anbauempfehlung zur bestimmten Pflanze
                switchStatus(cropName);
                break;
        }
    }

    /**
     * in switchStatus werden die passenden Images in drawables abhängig vom @status gesucht.
     *@param property - ist die Eigenschaft eines Eintrags, für die ein Tutorial erstellt werden muss(z.B. airtemp)
     * in drawables wird nach dem vorher definiertem Namen des Images gesucht.
     *Alle Namen müssen nach bestimtem Muster gebildet werden(z.B "tutorial_soilmoisture_less_2);
     * Zur einer @property kann es mehrere Images/Animation/videos geben. Images/Animation/videos zur einem
     * @property werden am ende des Namens mit einem @i Identifikator bezeichnet.
     */
    private void switchStatus(String property) {
        //Allgemeien Anbauempfehlung zur bestimmten Pflanze
        if (cropId > 0){
            try {
                //Alle images aus drawable(nicht perfomat)
                Field[] drawables = R.drawable.class.getFields();
                int i = 1;//Identifikator
                //Images für @property nach Namen suchen
                for (Field f : drawables) {
                    if (f.getName().matches("tutorial_general_" + property + "_(.*)")) {
                        //Bei der Übereinstimmung Image und zugehörgie Description der Liste
                        //hinzufügen
                        tutorialList.add(new Tutorial(f.getInt(null), norm,
                                getDescriptionStrings("desc_array_general_" + property + "_" + i)));
                        i++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Für den Fall, dass der Wert die Norm übersteigt
        else if (status == GREATER) {
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

    /**
     * Sucht nach dem Array von Strings zu einem Image
     * @param arrayName - array-Name, wird von switchStatus() übergeben.
     * @return - string, description eines Images, wird vorgelesen.
     */
    private String getDescriptionStrings(String arrayName) {
        String water = "";
        String currentString = "";
        if (waterReq > 0){
            water = String.valueOf(waterReq/10);
        }
        try {
            //Suche String-array in /values/strings nach Namen
            String[] strings = getResources().getStringArray(this.getResources().getIdentifier(arrayName, "array",
                    this.getPackageName()));
            // Bilde ein String aus mehreren Strings
            for (String s : strings) {
                //Fals es um Bodenfeuchtigkeit geht, wird die empfohlene Wassermenge an String angehängt
                if (s.contains("pots")){
                    currentString = water + s;
                    return currentString;
                }
                currentString += s;
            }
            return currentString;
        } catch (Exception e) {
            e.printStackTrace();
            return currentString;
        }

    }

    /**
     * Visual Feedback zur dem aktuellen Wert der Eigenschaft
     * @param deviation - die Abweichnug des aktuellen Wertes von Norm in Prozetn
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
     * Vorlesefunktion, aktuelle Eigenschaft und derer Wert werden vorgelesen
     * @param currentEar   - ImageButton, der angeklickt wird
     * @param currentValue - aktueller Wert der Eigenschaft
     * @param property     - aktuelle Eigenschaft
     */
    private void speak(ImageButton currentEar, final String currentValue, final String property) {
        currentEar.setOnClickListener(new View.OnClickListener() {
            @Override
            //Vorlese Text wird zusammengesetzt(Z.b Current value of + Air Temp + is + 25)
            public void onClick(View view) {
                //Speed
                speaker.setSpeechRate((float)0.8);
                speaker.speak(getString(R.string.show_current_value_of) + property + getString(R.string.show_current_value_is)
                        + currentValue, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    @Override
    protected void onPause() {
        tts.stopSpeaker( );
        super.onPause();
    }
}
