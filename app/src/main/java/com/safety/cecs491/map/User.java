package com.safety.cecs491.map;

/**
 * Created by Kevin on 3/2/2016.
 */
public class User {
    String firstName, lastName, email, password;
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
    public User(String email, String password) {
        firstName = "";
        lastName = "";
        this.email = email;
        this.password = password;
    }
}
