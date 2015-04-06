package com.cs371m.theselfiestudio;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

public class LoginActivity extends FragmentActivity {

    private CallbackManager callbackManager;

    @Override
    protected void onResume() {
        super.onResume();

        // Call the 'activateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onResume methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("LoginActivity","onCreate");

        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        final Intent intent_main = new Intent(this, MainActivity.class);

        //uncomment next line in order to skip the facebook login activity
        // startActivity(intent_main);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        startActivity(intent_main);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                            showAlert();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                            showAlert();
                    }

                    private void showAlert() {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("CANCELLED")
                                .setMessage("Permission not granted")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                });

        setContentView(R.layout.activity_login);

        //determine if the user has already logged in
        boolean loggedIn = AccessToken.getCurrentAccessToken() != null;
        if(loggedIn)
        {
            startActivity(intent_main);
            finish();
        }
    }
}
