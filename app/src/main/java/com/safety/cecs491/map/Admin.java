package com.safety.cecs491.map;

/**
 * Created by Kevin on 3/2/2016.
 */
public class Admin {
    String firstName, lastName, email, password, key;
    public Admin(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        key = "1234";
    }
    public Admin(String email, String password) {
        firstName = "";
        lastName = "";
        this.email = email;
        this.password = password;
        key = "1234";
    }
}
