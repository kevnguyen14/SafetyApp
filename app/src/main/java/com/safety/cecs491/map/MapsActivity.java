package com.safety.cecs491.map;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor ;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MapsActivity extends AppCompatActivity implements View.OnClickListener{

    private GoogleMap mMap;
    RadioButton rbPing, rbSettings, rbLogout;
    UserLocalStore userLocalStore;
    //AdminLocalStore adminLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        rbPing = (RadioButton) findViewById(R.id.rbPing);
        //rbRemovePing = (RadioButton) findViewById(R.id.rbRemovePing);
        rbSettings = (RadioButton) findViewById(R.id.rbSettings);
        rbLogout = (RadioButton) findViewById(R.id.rbLogout);
        rbPing.setOnClickListener(this);
        //rbRemovePing.setOnClickListener(this);
        rbSettings.setOnClickListener(this);
        rbLogout.setOnClickListener(this);

        userLocalStore = new UserLocalStore(this);
        //adminLocalStore = new AdminLocalStore(this);
    }

    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    public void setUpMapIfNeeded() {
        // null check to confirm if we have not already instantiated the map
        if (mMap ==null) {
            // try to obtain the map from the supportmapfragment
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // check if we were successful in obtaining map
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
        String currentDateTime = date.format(new Date());
        // Add a marker to a location
        LatLng danger = new LatLng(33.783743, -118.113929);
        mMap.addMarker(new MarkerOptions().position(danger).title("Danger "+ currentDateTime));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(danger, 14.9f));
    }

    protected void onStart() {
        super.onStart();
        if (authenticate() == false) {
            startActivity(new Intent(this, Login.class));
        }
    }

    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rbPing:
                //what happens when ping button is clicked
                //startActivity(new Intent(this, MapsActivity.class));
                break;

            case R.id.rbSettings:
                //what happens when settings button is clicked
                //startActivity(new Intent(this, MapsActivity.class));
                break;

            case R.id.rbLogout:
                //what happens when logout button is clicked
                userLocalStore.clearUserData();
                //adminLocalStore.clearadminData();
                userLocalStore.setUserLoggedIn(false);
                startActivity(new Intent(this, Login.class));
                break;
        }
    }

}
