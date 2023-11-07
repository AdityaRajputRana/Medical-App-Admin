package com.example.medicalappadmin.Models;

public class User {
    String _id;
    String fullName;
    String firstName;
    String lastName;
    String mobileNumber;
    String email;
    String password;
    String displayPicture;
    String type;
    String title;

    public User(String fullName, String firstName, String lastName, String mobileNumber, String email, String password, String displayPicture, String type, String title, Hospital hospital) {
        this.fullName = fullName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.password = password;
        this.displayPicture = displayPicture;
        this.type = type;
        this.title = title;
        this.hospital = hospital;
    }

    public Hospital getHospital() {
        return hospital;
    }

    Hospital hospital;

    public String getFullName() {
        return fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public User() {
    }


    public String get_id() {
        return _id;
    }


    public String getEmail() {
        return email;
    }


    public String getDisplayPicture() {
        return displayPicture;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDisplayPicture(String displayPicture) {
        this.displayPicture = displayPicture;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
