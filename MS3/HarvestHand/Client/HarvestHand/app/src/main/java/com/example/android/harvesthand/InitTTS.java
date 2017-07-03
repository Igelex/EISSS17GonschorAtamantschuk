package com.example.android.harvesthand;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Hier wird TextToSpeech initialisiert und in mehreren Klassen verwendet
 */

public class InitTTS {
    private TextToSpeech speaker;
    private Context context;

    public InitTTS(Context context) {
        this.context = context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public TextToSpeech initTTS() {
        speaker = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR && status == TextToSpeech.SUCCESS) {
                    //Systemsprache ermitteln
                    int lang = speaker.setLanguage(Locale.getDefault());
                    if (lang == TextToSpeech.LANG_MISSING_DATA || lang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("Speaker!!!!!!!!!!!!!!!!", "No LANG");
                    }
                } else {
                    Log.i("Speaker!!!!!!!!!!!!!!: ", "Initialization failed");
                }
            }
        });
        return speaker;
    }
}
