package com.example.anytimetutor.SupportFiles;

public class User {

    private String id, scan_status;
    private String username, email, gender;

    public User(String sapid, String firstname, String email, String scan_status) {
        this.id = sapid;
        this.username = firstname;
        this.email = email;
        this.scan_status = scan_status;
    }

    public String getId() {
        return id;
    }

    public String getScanStatus() {
        return scan_status;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

}
