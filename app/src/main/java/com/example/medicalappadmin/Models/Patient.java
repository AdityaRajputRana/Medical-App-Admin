package com.example.medicalappadmin.Models;

public class Patient {
    String _id;

    Long mobileNumber;
    String fullName;
    String email;
    String gender;
    String name;
    String age = "Age Not Specified";

    public String getAge() {
        return age;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Patient() {

    }

    public String getFullName() {
        if (fullName != null) return fullName;
        if (name != null) return name;
        return "Unregistered Patient";
    }

    public String getFullNameAsItIs() {
        if (fullName != null) return fullName;
        if (name != null) return name;
        return "";
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public String getGenderFull(){
        if (gender == null) return "Not Entered";
        if (gender.equals("M")) return "Male";
        if (gender.equals("F")) return "Female";
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
