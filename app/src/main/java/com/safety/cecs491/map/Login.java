package com.safety.cecs491.map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    ProgressDialog progressDialog;
    Button bLogin, bNewUser, bNewAdmin;
    EditText etUsername, etPassword;
    TextView tvUsername;
    CheckBox cbCheckbox;
    UserLocalStore userLocalStore;
    AdminLocalStore adminLocalStore;
    RequestQueue requestQueue;
    String showUrl = "http://cecs491a.comlu.com/showUsers.php";
    String showUrl2 = "http://cecs491a.comlu.com/showAdmin.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bLogin = (Button) findViewById(R.id.bLogin);
        bNewUser = (Button) findViewById(R.id.bNewUser);
        bNewAdmin = (Button) findViewById(R.id.bNewAdmin);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        cbCheckbox = (CheckBox) findViewById(R.id.cbCheckbox);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

//        bLogin.setOnClickListener(this);
//        bNewUser.setOnClickListener(this);
//        bNewAdmin.setOnClickListener(this);

        userLocalStore = new UserLocalStore(this);
        adminLocalStore = new AdminLocalStore(this);


//        listener for checkbox
//        cbCheckbox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(cbCheckbox.isChecked())
//                    etUsername.setText("check");
//                if(cbCheckbox.isChecked() == false)
//                    etUsername.setText("not");
//            }
//        });

        // listener for login button
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbCheckbox.isChecked())
                    adminCheck();
                else
                    userCheck();
            }
        });

        // listener for new user button
        bNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(Login.this, Register.class);
                startActivity(loginIntent);
            }
        });

        // listener for new admin button
        bNewAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(Login.this, Register2.class);
                startActivity(loginIntent);
            }
        });

    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Login.this);
        dialogBuilder.setMessage("Incorrect user/admin details");
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Login.this.finish();
                startActivity (new Intent(Login.this, Login.class));
            }
        });
        dialogBuilder.show();
    }

    private void startMap() {
        Intent loginIntent = new Intent(Login.this, MapsActivity.class);
        startActivity(loginIntent);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.welcome);
        mp.start();
    }

    private void startMap2() {
        Intent loginIntent = new Intent(Login.this, MapsActivity2.class);
        startActivity(loginIntent);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.welcome);
        mp.start();
    }

    private void logUserIn(User returnedUser) {
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);

        startMap();
    }

    private void logAdminIn(Admin returnedAdmin) {
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        adminLocalStore.storeAdminData(returnedAdmin);
        adminLocalStore.setAdminLoggedIn(true);

        startMap2();
    }

    private void userCheck() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                showUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray users = response.getJSONArray("users");
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject user = users.getJSONObject(i);
                        String userName = user.getString("userName");
                        String password = user.getString("password");
                        if(etUsername.getText().toString().equals(userName)  && etPassword.getText().toString().equals(password)) {
                            User u = new User(userName, password);
                            logUserIn(u);
                        }
//                                else {
//                                    showErrorMessage();
//                                }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                if(userLocalStore.getUserLoggedIn() == false)
                    showErrorMessage();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void adminCheck() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                showUrl2, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray admins = response.getJSONArray("admins");
                    for (int i = 0; i < admins.length(); i++) {
                        JSONObject admin = admins.getJSONObject(i);
                        String userName = admin.getString("userName");
                        String password = admin.getString("password");
                        if(etUsername.getText().toString().equals(userName)  && etPassword.getText().toString().equals(password)) {
                            Admin u = new Admin(userName, password);
                            logAdminIn(u);
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                if(adminLocalStore.getAdminLoggedIn() == false)
                    showErrorMessage();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
