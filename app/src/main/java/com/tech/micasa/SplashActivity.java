package com.tech.micasa;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.tech.micasa.constants.Constant;
import com.tech.micasa.utility.SharedPreferenceUtility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private ImageView iv_Logo;
    boolean isalreadylogin = false;
    Context mContext = this;
    private boolean isUserLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.tech.micasa",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        // binding= DataBindingUtil.setContentView(this,R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        isUserLoggedIn = SharedPreferenceUtility.getInstance(SplashActivity.this).getBoolean(Constant.IS_USER_LOGGED_IN);

        boolean val =  SharedPreferenceUtility.getInstance(SplashActivity.this).getBoolean(Constant.SELECTED_LANGUAGE);

        if(!val)
        {
            updateResources(SplashActivity.this,"en");
        }else
        {
            updateResources(SplashActivity.this,"es");


        }

        finds();


/*

        String lang= PrefManager.get(mContext,"lang");
        Log.e("lang",lang);

        if (lang!=null){
            updateResources(mContext,lang);
        }else
        {
            updateResources(mContext,"en");
        }

*/
    }
       private void finds() {

       iv_Logo = findViewById(R.id.iv_Logo);

       new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                  if (isUserLoggedIn) {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, ChooseLanguage.class));
                finish();
            }

            }
        },3000);
    }


    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

}