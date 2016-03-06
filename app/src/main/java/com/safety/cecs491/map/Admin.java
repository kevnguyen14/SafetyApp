package com.safety.cecs491.map;

/**
 * Created by Kevin on 3/2/2016.
 */
public class Admin {
    String firstName, lastName, userName, password, key;
    public Admin(String firstName, String lastName, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        key = "1234";
    }
    public Admin(String userName, String password) {
        firstName = "";
        lastName = "";
        this.userName = userName;
        this.password = password;
        key = "1234";
    }
}
