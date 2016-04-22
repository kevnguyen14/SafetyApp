package com.safety.cecs491.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

public class MapsActivity2 extends FragmentActivity {

    private GoogleMap mMap;
    RequestQueue requestQueue;
    RadioButton rbRemovePing, rbLogout, rbView, rbRefresh;
    RadioButton rbNormal, rbSatellite, rbHybrid, rbTerrain;
    AdminLocalStore adminLocalStore;
    String showPing = "http://cecs491a.comlu.com/showPings.php";
    String removePings = "http://cecs491a.comlu.com/deletePing.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        setUpMapIfNeeded();
        rbRemovePing = (RadioButton) findViewById(R.id.rbRemovePing);
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
        adminLocalStore = new AdminLocalStore(this);
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
         * Listener for remove ping
         */

        rbRemovePing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MapsActivity2.this);
                        dialogBuilder.setMessage("Are you sure you want to remove ping? ");
                        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                marker.remove();
                                MediaPlayer mp = MediaPlayer.create(MapsActivity2.this, R.raw.poof);
                                mp.start();
                                removePing(marker.getPosition());
                                Toast.makeText(getApplicationContext()
                                        , "Removed Ping "
                                        , Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialogBuilder.setNegativeButton("Cancel", null);
                        dialogBuilder.show();
                        return true;
                    }
                });
            }
        });

        /**
         * Listener for Refresh
         */
        rbRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeListener();
//                markers.clear();
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
                adminLocalStore.clearAdminData();
                adminLocalStore.setAdminLoggedIn(false);
                startActivity(new Intent(MapsActivity2.this, Login.class));
                MediaPlayer mp = MediaPlayer.create(MapsActivity2.this, R.raw.goodbye);
                mp.start();
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

    private void removeListener() {
        mMap.setOnMapClickListener(null);
        mMap.setOnMarkerClickListener(null);
    }

    private void setUpMap() {
        // Add a marker to a location
        LatLng danger = new LatLng(33.783743, -118.113929);
        //mMap.addMarker(new MarkerOptions().position(danger).title("Danger " + currentDateTime));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(danger, 14.9f));
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    private boolean authenticate() {
        return adminLocalStore.getAdminLoggedIn();
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
                            case 1:markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.misc));
                                break;
                            case 2:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fire));
                                break;
                            case 3:markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ninja));
                                break;
                            case 4:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.shooting));
                                break;
                            case 5:markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fight));
                                break;
                            case 6:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.biohazard));
                                break;
                            case 7:
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.crash));
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

    private void removePing(final LatLng latLng) {
        StringRequest request = new StringRequest(Request.Method.POST, removePings, new Response.Listener<String>() {
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
                parameters.put("lat", Double.toString(latLng.latitude));
                parameters.put("lng", Double.toString(latLng.longitude));
                return parameters;
            }
        };
        requestQueue.add(request);
    }

}
