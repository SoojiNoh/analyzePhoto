package com.example.soojinoh.analyzeimage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class SplashActivity extends Activity {

    TextToSpeech t1;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }

        });

        t1.speak("Welcome to MarketIsee. Touch the top of the screen to detect object, or Touch the bottom of the screen to search product", TextToSpeech.QUEUE_FLUSH, null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler = new Handler();

        init();
    }

    private void init(){
        t1.speak("Welcome to MarketIsee. Touch the top of the screen to detect object, or Touch the bottom of the screen to search product", TextToSpeech.QUEUE_FLUSH, null);

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        }, 3000);// 3 초
    }

}

