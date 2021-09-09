package com.tech.micasa;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.tech.micasa.databinding.ActivityTurnOnGPSBinding;

public class TurnOnGPSActivity extends AppCompatActivity {

    ActivityTurnOnGPSBinding binding;

    String strName = "";
    LocationManager manager =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityTurnOnGPSBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            strName = extras.getString("name");
            //The key argument here must match that used in the other activity
        }

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
           // buildAlertMessageNoGps();
        }
        else {
            startActivity(new Intent(this,HomeActivity.class));
        }

        String wholeText = strName+" "+getString(R.string.hi_john_nwelcome_to);

        binding.tvLogin.setText(wholeText);

        binding.tvSkip.setOnClickListener(v -> {
            startActivity(new Intent(this,HomeActivity.class));
        });

        binding.btnOnGps.setOnClickListener(v ->
                {
                    if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                    else {
                        startActivity(new Intent(this,HomeActivity.class));
                    }
                }
                );

    }

    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
            checkSTatus();

        }
    }

    private void checkSTatus() {

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
       //     buildAlertMessageNoGps();
        }
        else {
        //    Toast.makeText(this,"Gps on successfully",Toast.LENGTH_SHORT).show();
           // startActivity(new Intent(this,SellBorowActivity.class));
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            //     buildAlertMessageNoGps();
        }
        else {
            Toast.makeText(this, ""+R.string.gps_on_successfully,Toast.LENGTH_SHORT).show();
             startActivity(new Intent(this,HomeActivity.class));
        }

    }

}