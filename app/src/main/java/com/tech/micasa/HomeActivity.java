package com.tech.micasa;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tech.micasa.constants.Constant;
import com.tech.micasa.databinding.ActivityHomeBinding;
import com.tech.micasa.utility.DataManager;
import com.tech.micasa.utility.GPSTracker;
import com.tech.micasa.utility.GpsUtils;
import com.tech.micasa.utility.SharedPreferenceUtility;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private Bundle intent;
    private String status ="";
    String chat ="";

    GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

       /* boolean val =  SharedPreferenceUtility.getInstance(HomeActivity.this).getBoolean(Constant.SELECTED_LANGUAGE);

        if(!val)
        {
            updateResources(getApplicationContext(),"en");
        }else
        {
            updateResources(getApplicationContext(),"es");

        }*/

        boolean val =  SharedPreferenceUtility.getInstance(HomeActivity.this).getBoolean(Constant.SELECTED_LANGUAGE);

        if(!val)
        {
            //   updateResources(getActivity().getApplicationContext(),"en");
            setLocale(HomeActivity.this,"en");

        }else
        {
            //    updateResources(getActivity().getApplicationContext(),"es");
            setLocale(HomeActivity.this,"es");
        }



        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_chatlist, R.id.navigation_add,R.id.navigation_notifications,R.id.myProfileFragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        intent =  getIntent().getExtras();

        if(intent!=null)
        {

            String key = intent.getString("key");

            status=  intent.getString("notification");
            chat = intent.getString("chat");

            if (key.equalsIgnoreCase("notification")){
                navController.navigateUp();
                String Goto = intent.getString("GoToTab3");
                Bundle bundle = new Bundle();
                bundle.putString("Goto",Goto);
                navController.navigate(R.id.navigation_chatlist,bundle);
            }else if (key.equalsIgnoreCase("chat")){
                String sellerId = intent.getString("seller_id");
                String itemId = intent.getString("item_id");
                Bundle bundle = new Bundle();
                bundle.putString("key","1");
                bundle.putString("seller_id",sellerId);
                bundle.putString("item_id",itemId);
                navController.navigateUp();
                navController.navigate(R.id.one2OneChatFragment,bundle);
            }else if (key.equalsIgnoreCase("AddItemScreen")){
                navController.navigateUp();
                navController.navigate(R.id.navigation_add);
            }

/*

            if (status.equalsIgnoreCase("notification")){
                navController.navigateUp();
                String Goto = intent.getString("GoToTab3");
                Bundle bundle = new Bundle();
                bundle.putString("Goto",Goto);
                navController.navigate(R.id.navigation_chatlist,bundle);
            }
            if (chat.equalsIgnoreCase("chat")){
                String sellerId = intent.getString("seller_id");
                String itemId = intent.getString("item_id");
                Bundle bundle = new Bundle();
                bundle.putString("key","1");
                bundle.putString("seller_id",sellerId);
                bundle.putString("item_id",itemId);
                navController.navigateUp();
                navController.navigate(R.id.one2OneChatFragment,bundle);
            }*/
/*
            if(key.equalsIgnoreCase("3"))
            {
                navController.navigateUp();
                navController.navigate(R.id.action_navigation_home_to_itemAllFragment);
            }*/

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }


    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

}