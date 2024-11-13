package com.example.myapplication;

public class User {
    private String id;
    private String email;
    private String name;
    private String password;
    private String sdt;


    public User(String id, String email, String name, String password, String sdt) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.sdt = sdt;
        this.email = email;
    }
    public User(){
        this.id = "";
        this.name = "";
        this.password = "";
        this.sdt = "";
        this.email = "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
    public String getName() {
        return name;
    }
    public String getPassword() {
        return password;
    }
    public String getSDT() {
        return sdt;
    }



}
