package com.safety.cecs491.map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

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
                Context context = MapsActivity.this;
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                dialogBuilder.setTitle("Describe and Rate the Danger");
                TextView tv = new TextView(context);
                tv.setText("\nDescribe the danger:");
                layout.addView(tv);
                final EditText etDetails = new EditText(context);
                etDetails.setSingleLine(true);
                etDetails.setImeOptions(EditorInfo.IME_ACTION_DONE);
                layout.addView(etDetails);
                TextView tv2 = new TextView(context);
                tv2.setText("\nRate the danger:");
                layout.addView(tv2);
                final NumberPicker rbLevel = new NumberPicker(context);
                rbLevel.setMinValue(1);
                rbLevel.setMaxValue(3);
                rbLevel.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
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
        SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy hh:mma");
        String currentDateTime = date.format(new Date());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        switch (level) {
            case 1: markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                break;
            case 2:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                break;
            default:
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                break;
        }
        String user = userLocalStore.getLoggedInUser().userName;
        markerOptions.title(details + " " + currentDateTime + " " + user);
        mMap.addMarker(markerOptions);
        storePing(position, details, level, user, currentDateTime);
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

    private void storePing(final LatLng position, final String details, final int level, final String user, final String currentDateTime) {
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
                    for (int i = 0; i < pings.length(); i++) {
                        JSONObject ping = pings.getJSONObject(i);
                        String latTemp = ping.getString("lat");
                        String lngTemp = ping.getString("lng");
                        String user = ping.getString("userName");
                        String currentDateTime = ping.getString("time");
                        String details = ping.getString("details");
                        String levelTemp = ping.getString("level");
                        double lat = Double.parseDouble(latTemp);
                        double lng = Double.parseDouble(lngTemp);
                        int level = Integer.parseInt(levelTemp);
                        LatLng position = new LatLng(lat, lng);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(position);
                        switch (level) {
                            case 1: markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                break;
                            case 2: markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                break;
                            default:
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                break;
                        }
                        markerOptions.title(details + " " + currentDateTime + " " + user);
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
