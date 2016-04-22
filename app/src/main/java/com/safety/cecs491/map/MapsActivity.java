package com.safety.cecs491.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity{

    private GoogleMap mMap;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    RadioButton rbPing, rbSettings, rbLogout, rbView, rbRefresh;
    RadioButton rbNormal, rbSatellite, rbHybrid, rbTerrain;
    UserLocalStore userLocalStore;
    String insertPing = "http://cecs491a.comlu.com/insertPing.php";
    String showPing = "http://cecs491a.comlu.com/showPings.php";
    int pingCounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        rbPing = (RadioButton) findViewById(R.id.rbPing);
        rbSettings = (RadioButton) findViewById(R.id.rbSettings);
        rbLogout = (RadioButton) findViewById(R.id.rbLogout);
        rbView = (RadioButton) findViewById(R.id.rbView);
        rbView.toggle();
        rbRefresh = (RadioButton) findViewById(R.id.rbRefresh);
        rbNormal = (RadioButton) findViewById(R.id.rbNormal);
        rbHybrid = (RadioButton) findViewById(R.id.rbHybrid);
        rbSatellite = (RadioButton) findViewById(R.id.rbSatellite);
        rbTerrain = (RadioButton) findViewById(R.id.rbTerrain);
        rbTerrain.toggle();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);
        loadPings();
        /**
         * Listener for view
         */
        rbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener();
            }
        });

        /**
         * Listener for settings
         */
        rbSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener();
                startActivity(new Intent(MapsActivity.this, EmergencyContacts.class));
            }
        });

        /**
         * Listener for Refresh
         */
        rbRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener();
                loadPings();
            }
        });

        /**
         * Listener for logout
         */
        rbLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener();
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);
                startActivity(new Intent(MapsActivity.this, Login.class));
            }
        });

        /**
         * Listener for ping
         */
        rbPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ping();
            }
        });

        /**
         * Listener for normal view
         */
        rbNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        /**
         * Listener for satellite view
         */
        rbSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        /**
         * Listener for hybrid view
         */
        rbHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        /**
         * Listener for terrain view
         */
        rbTerrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
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
                Toast.makeText(getApplicationContext()
                        , "Scroll down for Key list"
                        , Toast.LENGTH_SHORT).show();
                Context context = MapsActivity.this;
                ScrollView scrollView = new ScrollView(context);
                scrollView.setScrollbarFadingEnabled(false);
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                dialogBuilder.setTitle("Describe and Rate the Danger");
                TextView tv = new TextView(context);
                tv.setText("\nDescribe the danger:");
                tv.setTypeface(null, Typeface.BOLD);
                layout.addView(tv);
                final EditText etDetails = new EditText(context);
                etDetails.setSingleLine(true);
                layout.addView(etDetails);

                TextView tv2 = new TextView(context);
                tv2.setText("\nSelect danger:");
                tv2.setTypeface(null, Typeface.BOLD);
                layout.addView(tv2);
                final NumberPicker rbLevel = new NumberPicker(context);
                rbLevel.setMinValue(1);
                rbLevel.setMaxValue(7);
                String dangers[] = {"Misc", "Fire", "Kidnapping", "Shooting", "Fight", "Biohazard", "Crash"};
                rbLevel.setDisplayedValues(dangers);
                rbLevel.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                rbLevel.setWrapSelectorWheel(false);
                layout.addView(rbLevel);

                TextView tv3 = new TextView(context);
                tv3.setText("\nRate danger: (1 Lowest, 2 Moderate, 3 Highest)");
                tv3.setTypeface(null, Typeface.BOLD);
                layout.addView(tv3);
                final NumberPicker rbLevel2 = new NumberPicker(context);
                rbLevel2.setMinValue(1);
                rbLevel2.setMaxValue(3);
                String ratings[] = {"1 - Yellow", "2 - Orange", "3 - Red"};
                rbLevel2.setDisplayedValues(ratings);
                rbLevel2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                rbLevel2.setWrapSelectorWheel(false);
                layout.addView(rbLevel2);

                TextView danger = new TextView(context);
                danger.setText("\nDanger Key:");
                danger.setTypeface(null, Typeface.BOLD);
                layout.addView(danger);
                TextView misc = new TextView(context);
                misc.setText("\nMisc: ");
                misc.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.miscdefault, 0);
                layout.addView(misc);
                TextView fire = new TextView(context);
                fire.setText("\nFire: ");
                fire.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.firedefault, 0);
                layout.addView(fire);
                TextView kidnapping = new TextView(context);
                kidnapping.setText("\nKidnapping: ");
                kidnapping.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ninjadefault, 0);
                layout.addView(kidnapping);
                TextView shooting = new TextView(context);
                shooting.setText("\nShooting: ");
                shooting.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.shootingdefault, 0);
                layout.addView(shooting);
                TextView fight = new TextView(context);
                fight.setText("\nFight: ");
                fight.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.fightdefault, 0);
                layout.addView(fight);
                TextView biohazard = new TextView(context);
                biohazard.setText("\nBiohazard: ");
                biohazard.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.biohazarddefault, 0);
                layout.addView(biohazard);
                TextView crash = new TextView(context);
                crash.setText("\nCrash: ");
                crash.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.crashdefault, 0);
                layout.addView(crash);
                scrollView.addView(layout);
                dialogBuilder.setView(scrollView);
                dialogBuilder.setPositiveButton("Ping", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String details = etDetails.getText().toString();
                        int level = rbLevel.getValue();
                        int rating = rbLevel2.getValue();
                        drawMarker(position, details, level, rating);
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

    private void drawMarker(LatLng position, String details, int level, int rating) {
        SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy hh:mma");
        String currentDateTime = date.format(new Date());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        String levelType = null;
        switch (level) {
            case 1:
                if(rating == 1)
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.misc));
                else if(rating == 2)
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.misc2));
                else
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.misc3));
                levelType = "Miscellaneous";
                break;
            case 2:
                if(rating == 1)
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fire));
                else if(rating == 2)
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fire2));
                else
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fire3));
                levelType = "Fire";
                break;
            case 3:
                if(rating == 1)
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ninja));
                else if(rating == 2)
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ninja2));
                else
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ninja3));
                levelType = "Kidnapping";
                break;
            case 4:
                if(rating == 1)
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.shooting));
                else if(rating == 2)
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.shooting2));
                else
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.shooting3));
                levelType = "Shooting";
                break;
            case 5:
                if(rating == 1)
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fight));
                else if(rating == 2)
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fight2));
                else
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fight3));
                levelType = "Fight";
                break;
            case 6:
                if(rating == 1)
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.biohazard));
                else if(rating == 2)
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.biohazard2));
                else
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.biohazard3));
                levelType = "Biohazard";
                break;
            case 7:
                if(rating == 1)
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.crash));
                else if(rating == 2)
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.crash2));
                else
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.crash3));
                levelType = "Crash";
                break;
        }
        String user = userLocalStore.getLoggedInUser().userName;
        markerOptions.title(details + " -" + user);
        markerOptions.snippet(currentDateTime);
        mMap.addMarker(markerOptions);
        Toast.makeText(getApplicationContext()
                , "You have completed a Ping!"
                , Toast.LENGTH_SHORT).show();
        storePing(position, details, level, user, currentDateTime, rating);
        textAdmin(position, details, levelType, user, currentDateTime, rating);
    }

    private void textAdmin(LatLng position, String details, String level, String user, String currentDateTime, int rating) {
        Log.i("Send SMS", "");
        String phoneNo []= {"3106342798"};
        String message = "User: " + user + "\n\n" + details + "\n\n" + level + " level " + rating + "severity at " + position.toString() + ". \n\n" + currentDateTime +".";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (int i = 0; i < phoneNo.length; i++) {
                smsManager.sendTextMessage(phoneNo[i], null, message, null, null);
            }
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void setUpMap() {
        SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
        String currentDateTime = date.format(new Date());
        // Add a marker to a location
        LatLng danger = new LatLng(33.783743, -118.113929);
        //mMap.addMarker(new MarkerOptions().position(danger).title("Danger " + currentDateTime));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(danger, 14.9f));
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    private boolean authenticate() {
        return userLocalStore.getUserLoggedIn();
    }

    private void storePing(final LatLng position, final String details, final int level, final String user, final String currentDateTime, final int rating) {
        StringRequest request = new StringRequest(Request.Method.POST, insertPing, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("lat", Double.toString(position.latitude));
                parameters.put("lng", Double.toString(position.longitude));
                parameters.put("userName", user);
                parameters.put("time", currentDateTime);
                parameters.put("details", details);
                parameters.put("level", Integer.toString(level));
                parameters.put("rating", Integer.toString(rating));
                return parameters;
            }
        };
        requestQueue.add(request);
    }

    private void loadPings() {
        mMap.clear();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                showPing, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray pings = response.getJSONArray("pings");
                    pingCounts = pings.length();
                    for (int i = 0; i < pings.length(); i++) {
                        JSONObject ping = pings.getJSONObject(i);
                        String latTemp = ping.getString("lat");
                        String lngTemp = ping.getString("lng");
                        String user = ping.getString("userName");
                        String currentDateTime = ping.getString("time");
                        String details = ping.getString("details");
                        String levelTemp = ping.getString("level");
                        String ratingTemp = ping.getString("rating");
                        double lat = Double.parseDouble(latTemp);
                        double lng = Double.parseDouble(lngTemp);
                        int level = Integer.parseInt(levelTemp);
                        int rating = Integer.parseInt(ratingTemp);
                        LatLng position = new LatLng(lat, lng);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(position);
                        switch (level) {
                            case 1:
                                if(rating == 1)
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.misc));
                                else if(rating == 2)
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.misc2));
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.misc3));
                                break;
                            case 2:
                                if(rating == 1)
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fire));
                                else if(rating == 2)
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fire2));
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fire3));
                                break;
                            case 3:
                                if(rating == 1)
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ninja));
                                else if(rating == 2)
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ninja2));
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ninja3));
                                break;
                            case 4:
                                if(rating == 1)
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.shooting));
                                else if(rating == 2)
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.shooting2));
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.shooting3));
                                break;
                            case 5:
                                if(rating == 1)
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fight));
                                else if(rating == 2)
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fight2));
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fight3));
                                break;
                            case 6:
                                if(rating == 1)
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.biohazard));
                                else if(rating == 2)
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.biohazard2));
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.biohazard3));
                                break;
                            case 7:
                                if(rating == 1)
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.crash));
                                else if(rating == 2)
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.crash2));
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.crash3));
                                break;
                        }
                        markerOptions.title(details + " -" + user);
                        markerOptions.snippet(currentDateTime);
                        mMap.addMarker(markerOptions);
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);

    }
}
