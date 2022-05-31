package com.codebrew.clikat.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.codebrew.clikat.modal.AppGlobal;
import com.codebrew.clikat.utils.StaticFunction;
import com.codebrew.clikat.utils.configurations.Configurations;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Arrays;


/*
 * Created by ber$erk-kAjU on 5/13/2015.
 */
public abstract class FacebookLoginActivity extends AppCompatActivity {

    public CallbackManager callbackManager;

    private int appBackground = Color.parseColor(Configurations.colors.appBackground);


    public static void printHashKey(Context pContext) {
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = pContext.getPackageManager()
                    .getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("Key Hash:", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (Exception e) {
            Log.e("Key Hash:", "printHashKey()", e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StaticFunction.INSTANCE.setStatusBarColor(this, appBackground);
        callbackManager = CallbackManager.Factory.create();
    }


    public void getUserProfile() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                (object, response) -> {

                    OnSuccess(object, response);
                    // Application code
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday,albums,age_range");
        request.setParameters(parameters);
        request.executeAsync();
    }


    //user_location,user_birthday,user_likes,user_photos,user_about_me : needs review from facebook
    public void performLogin() {
       /* LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("email", "public_profile","user_location","user_birthday"
        ,"user_friends","user_likes","user_photos","user_about_me"));*/
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                loginSuccess(loginResult);
            }

            @Override
            public void onCancel() {
                loginCancelled();
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                loginError(exception);
                // App code
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  AppEventsLogger.activateApp(this);
    }

    public abstract int getLayoutId();

    public abstract void loginSuccess(LoginResult result);


    public abstract void OnSuccess(
            JSONObject object,
            GraphResponse response);

    public abstract void loginCancelled();

    public abstract void loginError(FacebookException exception);

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(AppGlobal.Companion.getLocaleManager().setLocale(base));
    }
}