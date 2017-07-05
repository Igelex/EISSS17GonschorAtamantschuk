package com.example.android.harvesthand;

import android.graphics.Color;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Sergej on 13.06.2017.
 */

public class Contracts {
    public static final String URL_PROTOCOL = "http://";
    public static final String URL_PORT = ":3001/";
    public static String URL_IP = "";
    public static final String URL_IP_BASE = "192.168..";
    public static String BASE_URL ;

    public static final String URL_BASE_USERS = "users/";
    public static final String URL_BASE_SIGNIN = "signin";
    public static final String URL_BASE_SIGNUP = "signup";
    public static final String URL_BASE_TUTORIAL = "/tutorials/";
    public static final String URL_BASE_ENTRIES = "entries/";
    public static final String URL_BASE_WEATHER = "weather/";

    public static final String URL_PARAMS_OWNER_ID = "owner_id";
    public static final String URL_PARAMS_COLLAB_ID = "collab_id";
    public static final String URL_PARAMS_PHONE_NUMBER = "phone_number";

    public static final String USER_SHARED_PREFS = "user";
    public static final String USER_SHARED_PREFS_ID = "user_id";
    public static final String USER_SHARED_PREFS_TYPE = "user_type";
    public static final int USER_TYPE_ILLITERATE = 1;
    public static final int USER_TYPE_LITERATE = 0;
    public static final String USER_SHARED_PREFS_NUMBER = "phone_number";
    /*
    Id und Telefonnummer des Benutzers werden global gespeichert
     */
    public static String USER_ID = "";
    public static String USER_NUMBER = "";


    public static final String IP_ADDRESS_SHARED_PREFS = "ipaddress";
    public static final String IP_SP_IP = "ip";

    /*
    * Arten der Pflanzen nach ID intialisiert, für CropSpinner
    * */
    public static final int DEFAULT_SELECTION = 0;
    public static final int CROP_ID_CAFFE = 1;
    public static final int CROP_ID_CACAO = 2;
    public static final int CROP_ID_BANANA = 3;/*...*/

    /*
    * Bodenarten nach ID intialisiert, für SoilSpinner
    * */
    public static final int SOIL_ID_SAND = 1;
    public static final int SOIL_ID_CLAY= 2;/*...*/
    public static final int SOIL_ID_HUMUS= 3;/*...*/

    /*
    * Id der Eigenschaft für Tutorial
    * */
    public static final int PROPERTY_SOIL_MOISTURE = 1;
    public static final int PROPERTY_AIR_TEMP = 2;
    public static final int PROPERTY_SOIL_TEMP = 3;
    public static final int PROPERTY_AIR_HUMIDITY = 4;
    public static final int PROPERTY_PH = 5;
    public static final int PROPERTY_SOIL_TYPE = 6;
    public static final int PROPERTY_HEIGHT= 7;
    public static final int PROPERTY_GENERAL= 8;

    private TextToSpeech speaker;

    public Contracts(TextToSpeech speaker) {
        this.speaker = speaker;
    }

    //Snackbar zum Anzeigen der System-Messages, universell für alle Klassen
    public void showSnackbar(View view, String msg, Boolean error, Boolean success){
        if(speaker != null){
            speak(msg);
        }
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView snackBarText = (snackbarView.findViewById(android.support.design.R.id.snackbar_text));
        if(error) {
            snackBarText.setTextColor(Color.rgb(253, 86, 86));
        }
        if(success) {
            snackBarText.setTextColor(Color.rgb(71, 151, 75));
        }
        snackbar.show();
    }

    public void speak(String textToSpeak){
        //Speed
        speaker.setSpeechRate((float)0.8);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            speaker.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            speaker.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void setSpeaker(TextToSpeech speaker) {
        this.speaker = speaker;
    }

}
