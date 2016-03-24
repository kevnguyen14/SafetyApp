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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class EmergencyContacts extends AppCompatActivity {
    EditText etFirstName, etLastName, etRelation, etPhone;
    Button bSave, bBack;
    String insertContact = "http://cecs491a.comlu.com/insertContact.php";
    ProgressDialog progressDialog;
    UserLocalStore userLocalStore;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etFirstName.setSingleLine(true);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etLastName.setSingleLine(true);
        etRelation = (EditText) findViewById(R.id.etRelation);
        etRelation.setSingleLine(true);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etPhone.setSingleLine(true);
        bSave = (Button) findViewById(R.id.bSave);
        bBack = (Button) findViewById(R.id.bBack);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        userLocalStore = new UserLocalStore(this);

        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmergencyContacts.this, MapsActivity.class));
            }
        });

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                progressDialog = new ProgressDialog(EmergencyContacts.this);
//                progressDialog.setCancelable(false);
//                progressDialog.setTitle("Processing...");
//                progressDialog.setMessage("Please wait...");
//                progressDialog.show();
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String relation = etRelation.getText().toString();
                String phone = etPhone.getText().toString();

                if (!firstName.isEmpty() && !lastName.isEmpty() && !relation.isEmpty() && phone.length() >= 9) {
                    StringRequest request = new StringRequest(Request.Method.POST, insertContact, new Response.Listener<String>() {
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
                            parameters.put("userName", userLocalStore.getLoggedInUser().userName);
                            parameters.put("firstName", etFirstName.getText().toString());
                            parameters.put("lastName", etLastName.getText().toString());
                            parameters.put("relation", etRelation.getText().toString());
                            parameters.put("phone", etPhone.getText().toString());

                            return parameters;
                        }
                    };
                    requestQueue.add(request);
                    startActivity(new Intent(EmergencyContacts.this, MapsActivity.class));
                }
                else {
                    showErrorMessage();
                }
            }
        });
    }


    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EmergencyContacts.this);
        dialogBuilder.setMessage("Incorrect emergency contact details, please try again.");
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EmergencyContacts.this.finish();
                startActivity(new Intent(EmergencyContacts.this, EmergencyContacts.class));
            }
        });
        dialogBuilder.show();
    }
}
