package com.safety.cecs491.map;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kevin on 3/2/2016.
 */
public class UserLocalStore {

    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("firstName", user.firstName);
        spEditor.putString("lastName", user.lastName);
        spEditor.putString("userName", user.userName);
        spEditor.putString("password", user.password);
        spEditor.commit();
    }

    public User getLoggedInUser() {
        String firstName = userLocalDatabase.getString("firstName", "");
        String lastName = userLocalDatabase.getString("lastName", "");
        String userName = userLocalDatabase.getString("userName", "");
        String password = userLocalDatabase.getString("password", "");

        User storedUser = new User(firstName, lastName, userName, password);
        return storedUser;
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();
    }

    public boolean getUserLoggedIn() {
        if (userLocalDatabase.getBoolean("loggedIn", false) == true) {
            return true;
        }
        else {
            return false;
        }
    }

    public void clearUserData() {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

}
