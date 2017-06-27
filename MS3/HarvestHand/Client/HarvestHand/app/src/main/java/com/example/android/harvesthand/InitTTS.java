package com.example.android.harvesthand;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Pastuh on 27.06.2017.
 */

public class InitTTS {
    TextToSpeech speaker;
    Context context;

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
                Log.i("Speaker Status!!!!!!!:", "" + status);
                if (status != TextToSpeech.ERROR && status == TextToSpeech.SUCCESS) {
                    int lang = speaker.setLanguage(Locale.getDefault());
                    if (lang == TextToSpeech.LANG_MISSING_DATA || lang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }

                } else {
                    Log.i("Speaker!!!!!!!!!!!!!!: ", "Initialization failed");
                }
            }
        });
        return speaker;
    }
}
