package com.example.chinyao.mow.mowtweebook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.codepath.oauth.OAuthLoginActionBarActivity;

/**
 * Created by chinyao on 7/28/2016.
 */
public class MowtweebookSplashActivity extends OAuthLoginActionBarActivity<MowtweebookRestClient> {
    private Handler handler = null;
    private Runnable runnable = null; // remember to new Handler(), onDestroy(), removeCallbacksAndMessages()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // splash shows up even before this line by SplashTheme
        super.onCreate(savedInstanceState);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                loginToRest(null);
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    // OAuth authenticated successfully, launch primary authenticated activity
    // i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
        if (handler != null) {
            // remove all the callbacks
            handler.removeCallbacksAndMessages(null);
        }
        // Toast.makeText(this, "Success!", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, MowtweebookActivity.class);
        startActivity(i);
    }

    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToRest(View view) {
        getClient().connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // avoid memory leak
        // https://techblog.badoo.com/blog/2014/08/28/android-handler-memory-leaks/
        // http://stackoverflow.com/questions/8430805/clicking-the-back-button-twice-to-exit-an-activity
        if (handler != null) {
            // remove all the callbacks
            handler.removeCallbacksAndMessages(null);
        }
    }
}
