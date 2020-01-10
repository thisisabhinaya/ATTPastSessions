package com.example.anytimetutor.SupportFiles;

public class User {

    private String id;
    private String username, email, gender;

    public User(String sapid, String firstname, String email) {
        this.id = sapid;
        this.username = firstname;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
