package com.example.chinyao.mow.mowtube;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by chinyao on 7/28/2016.
 */
public class MowtubeSplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // splash shows up even before this line
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MowtubeActivity.class);
        startActivity(intent);
        finish();
    }
}
