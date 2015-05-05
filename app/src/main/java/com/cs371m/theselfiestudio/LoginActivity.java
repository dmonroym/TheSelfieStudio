package com.cs371m.theselfiestudio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.cs371m.theselfiestudio",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        //uncomment next line in order to skip the facebook login activity
        //startActivity(intent_main);

        setContentView(R.layout.activity_login);

//        LoginButton loginButton = (LoginButton) this.findViewById(R.id.login_button);
//        loginButton.setReadPermissions("user_photos");


//        LoginManager.getInstance().logInWithReadPermissions(
//                this,
//                Arrays.asList("user_photos"));
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

        //determine if the user has already logged in
        boolean loggedIn = AccessToken.getCurrentAccessToken() != null;
        if(loggedIn)
        {
            startActivity(intent_main);
            finish();
        }

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.cs371m.theselfiestudio",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

//    @Override
//    public View onCreateView(String name, Context context, AttributeSet attrs)
//    {
//        LoginButton authButton = (LoginButton)this.findViewById(R.id.login_button);
//        authButton.setReadPermissions(Arrays.asList("user_photos"));
//
//        return super.onCreateView(name, context, attrs);
//    }
}
