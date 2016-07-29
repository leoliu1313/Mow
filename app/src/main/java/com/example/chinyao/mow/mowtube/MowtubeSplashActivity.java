package com.example.chinyao.mow.mowtube;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by chinyao on 7/28/2016.
 */
public class MowtubeSplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // splash shows up even before this line by SplashTheme
        super.onCreate(savedInstanceState);

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // nothing
            }
            @Override
            public void onFinish() {
                Intent intent = new Intent(MowtubeSplashActivity.this, MowtubeActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
