package com.safety.cecs491.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor ;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MapsActivity extends AppCompatActivity{

    private GoogleMap mMap;
    RequestQueue requestQueue;

    RadioButton rbPing, rbSettings, rbLogout, rbView, rbRefresh;
    UserLocalStore userLocalStore;
    String details;
    int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        rbPing = (RadioButton) findViewById(R.id.rbPing);
        rbSettings = (RadioButton) findViewById(R.id.rbSettings);
        rbLogout = (RadioButton) findViewById(R.id.rbLogout);
        rbView = (RadioButton) findViewById(R.id.rbView);
        rbRefresh = (RadioButton) findViewById(R.id.rbRefresh);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);

        rbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener();
            }
        });

        rbSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener();
            }
        });

        rbRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener();
            }
        });

        rbLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener();
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);
                startActivity(new Intent(MapsActivity.this, Login.class));
            }
        });

        rbPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ping();
            }
        });
    }

    protected void onStart() {
        super.onStart();
        if (authenticate() == false) {
            startActivity(new Intent(this, Login.class));
        }
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

    private void ping() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng position) {
                Context context = MapsActivity.this;
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                dialogBuilder.setTitle("Describe and Rate the Danger");
                final EditText etDetails = new EditText(context);
                etDetails.setSingleLine(false);
                layout.addView(etDetails);
                final NumberPicker rbLevel = new NumberPicker(context);
                rbLevel.setMinValue(1);
                rbLevel.setMaxValue(5);
                layout.addView(rbLevel);
                dialogBuilder.setView(layout);
                dialogBuilder.setPositiveButton("Ping", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String details = etDetails.getText().toString();
                        int level = rbLevel.getValue();
                        drawMarker(position, details, level);
                    }
                });
                dialogBuilder.setNegativeButton("Cancel", null);
                dialogBuilder.show();
            }
        });
    }

    private void removeListener() {
        mMap.setOnMapClickListener(null);
    }

    private void drawMarker(LatLng position, String details, int level) {
        SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
        String currentDateTime = date.format(new Date());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        switch (level) {
            case 1: markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                break;
            case 2: markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                break;
            case 3: markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                break;
            case 4: markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                break;
            default:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                break;
        }
        markerOptions.title(details + " " + currentDateTime + " " + userLocalStore.getLoggedInUser().userName);
        mMap.addMarker(markerOptions);
    }
    private void setUpMap() {
        SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
        String currentDateTime = date.format(new Date());
        // Add a marker to a location
        LatLng danger = new LatLng(33.783743, -118.113929);
        //mMap.addMarker(new MarkerOptions().position(danger).title("Danger " + currentDateTime));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(danger, 14.9f));
    }

    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
    }


//    public void onClick(View v) {
//        switch(v.getId()){
//            case R.id.rbPing:
//                //what happens when ping button is clicked
//                ping();
//                //startActivity(new Intent(this, MapsActivity.class));
//                break;
//
//            case R.id.rbView:
//                removeListener();
//                //what happens when ping button is clicked
//                //startActivity(new Intent(this, MapsActivity.class));
//                break;
//
//            case R.id.rbSettings:
//                removeListener();
//                //what happens when settings button is clicked
//                //startActivity(new Intent(this, MapsActivity.class));
//                break;
//
//            case R.id.rbRefresh:
//                removeListener();
//                //what happens when refresh button is clicked
//                //startActivity(new Intent(this, MapsActivity.class));
//                break;
//
//            case R.id.rbLogout:
//                //what happens when logout button is clicked
//                removeListener();
//                userLocalStore.clearUserData();
//                //adminLocalStore.clearadminData();
//                userLocalStore.setUserLoggedIn(false);
//                startActivity(new Intent(this, Login.class));
//                break;
//        }
//    }

}
