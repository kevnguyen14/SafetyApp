package com.safety.cecs491.map;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kevin on 3/2/2016.
 */
public class AdminLocalStore {

    public static final String SP_NAME = "adminDetails";
    SharedPreferences adminLocalDatabase;

    public AdminLocalStore(Context context) {
        adminLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeAdminData(Admin admin) {
        SharedPreferences.Editor spEditor = adminLocalDatabase.edit();
        spEditor.putString("first name", admin.firstName);
        spEditor.putString("last name", admin.lastName);
        spEditor.putString("email", admin.email);
        spEditor.putString("password", admin.password);
        spEditor.putString("key", admin.key);
        spEditor.commit();
    }

    public Admin getLoggedInAdmin() {
        String firstName = adminLocalDatabase.getString("first name", "");
        String lastName = adminLocalDatabase.getString("last name", "");
        String email = adminLocalDatabase.getString("email", "");
        String password = adminLocalDatabase.getString("password", "");

        Admin storedadmin = new Admin(firstName, lastName, email, password);
        return storedadmin;
    }

    public void setAdminLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor spEditor = adminLocalDatabase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();
    }

    public boolean getAdminLoggedIn() {
        if (adminLocalDatabase.getBoolean("loggedIn", false) == true) {
            return true;
        }
        else {
            return false;
        }
    }

    public void clearAdminData() {
        SharedPreferences.Editor spEditor = adminLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

}
