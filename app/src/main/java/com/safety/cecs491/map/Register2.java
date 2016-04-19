
package com.safety.cecs491.map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import java.util.HashMap;
import java.util.Map;

public class Register2 extends AppCompatActivity implements View.OnClickListener{
/**
    Button bRegister;
    EditText etFirstName, etLastName, etUsername, etPassword, etConfirmPassword, etAdminKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        etAdminKey = (EditText) findViewById(R.id.etAdminKey);
        bRegister = (Button) findViewById(R.id.bRegister);

        bRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bRegister:
                //store data in database, and bring window back to login window
                startActivity(new Intent(this, Login.class));
                break;
        }
    }
*/

    Button bRegister, bBack;
    EditText etFirstName, etLastName, etUsername, etPassword, etConfirmPassword, etAdminKey;
    RequestQueue requestQueue;
    String insertUrl = "http://cecs491a.comlu.com/insertAdmin.php";
    String showKey = "http://cecs491a.comlu.com/showKeys.php";
    ProgressDialog progressDialog;
    AdminLocalStore adminLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        etAdminKey = (EditText) findViewById(R.id.etAdminKey);
        bRegister = (Button) findViewById(R.id.bRegister);
        bBack = (Button) findViewById(R.id.bBack);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        adminLocalStore = new AdminLocalStore(this);

        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register2.this, Login.class));
            }
        });

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                etFirstName.setText("1");

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        showKey, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray keys = response.getJSONArray("adminKeys");
                            for (int i = 0; i < keys.length(); i++) {
                                JSONObject key = keys.getJSONObject(i);
                                String keyID = key.getString("adminKey");
                                etFirstName.getText().toString().equals(keyID);
                                if (etAdminKey.getText().toString().equals(keyID)) {
                                    checkAdminDetails();
                                }
                                else {
                                    showErrorMessage();
                                }
                            }
                        } catch (JSONException e) {
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
        });
    }

    private void checkAdminDetails() {
        String userName = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();

        if(userName.length()>3&&password.length()>3&&password.equals(confirmPassword)&&!firstName.isEmpty()&&!lastName.isEmpty())

        {
            StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
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
                    parameters.put("firstName", etFirstName.getText().toString());
                    parameters.put("lastName", etLastName.getText().toString());
                    parameters.put("userName", etUsername.getText().toString());
                    parameters.put("password", etPassword.getText().toString());

                    return parameters;
                }
            };
            requestQueue.add(request);
            progressDialog = new ProgressDialog(Register2.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Processing...");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            Intent loginIntent = new Intent(Register2.this, Login.class);
            startActivity(loginIntent);
        }
        else
        {
            showErrorMessage();
        }
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Register2.this);
        dialogBuilder.setMessage("Incorrect user details, please try again.");
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Register2.this.finish();
                startActivity (new Intent(Register2.this, Register2.class));
            }
        });
        dialogBuilder.show();
    }



    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bRegister:
                //store data in database, and bring window back to login window
                startActivity(new Intent(this, Login.class));
                break;
        }
    }
}


