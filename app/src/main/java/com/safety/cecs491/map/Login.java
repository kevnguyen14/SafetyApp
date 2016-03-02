package com.safety.cecs491.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity implements View.OnClickListener{

    Button bLogin, bNewUser, bNewAdmin;
    EditText etEmail, etPassword;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bLogin = (Button) findViewById(R.id.bLogin);
        bNewUser = (Button) findViewById(R.id.bNewUser);
        bNewAdmin = (Button) findViewById(R.id.bNewAdmin);

        bLogin.setOnClickListener(this);
        bNewUser.setOnClickListener(this);
        bNewAdmin.setOnClickListener(this);

        userLocalStore = new UserLocalStore(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bLogin:
                //what happens when login button is clicked and successful
                User user = new User(null, null);
                userLocalStore.storeUserData(user);
                userLocalStore.setUserLoggedIn(true);
                startActivity(new Intent(this, MapsActivity.class));
                break;

            case R.id.bNewUser:
                //what happens when new user is clicked
                startActivity(new Intent(this, Register.class));
                break;

            case R.id.bNewAdmin:
                //what happens when new user is clicked
                startActivity(new Intent(this, Register2.class));
                break;
        }
    }
}
