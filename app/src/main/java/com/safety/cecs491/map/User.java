package com.safety.cecs491.map;

/**
 * Created by Kevin on 3/2/2016.
 */
public class User {
    String firstName, lastName, userName, password;
    public User(String firstName, String lastName, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
    }
    public User(String userName, String password) {
        firstName = "";
        lastName = "";
        this.userName = userName;
        this.password = password;
    }
}
