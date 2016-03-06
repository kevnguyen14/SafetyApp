package com.safety.cecs491.map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity{

    Button bRegister;
    EditText etFirstName, etLastName, etUsername, etPassword, etConfirmPassword;
    RequestQueue requestQueue;
    String insertUrl = "http://cecs491a.comlu.com/insertUser.php";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        bRegister = (Button) findViewById(R.id.bRegister);
        //result = (TextView) findViewById(R.id.textView);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();

                if(userName.length()>3 && password.length()>3 && password.equals(confirmPassword) && !firstName.isEmpty() && !lastName.isEmpty()) {
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
                    progressDialog = new ProgressDialog(Register.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setTitle("Processing...");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                    Intent loginIntent = new Intent(Register.this, Login.class);
                    startActivity(loginIntent);
                }
                else {
                    showErrorMessage();}
            };
        });
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Register.this);
        dialogBuilder.setMessage("Incorrect user details, please try again.");
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Register.this.finish();
                startActivity (new Intent(Register.this, Register.class));
            }
        });
        dialogBuilder.show();
    }
}
