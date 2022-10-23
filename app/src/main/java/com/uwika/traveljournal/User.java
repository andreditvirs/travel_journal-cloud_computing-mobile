package com.uwika.traveljournal;

public class User {
    public String name,email,username,password;

    public User(){

    }

    public User(String name, String username, String email,String password){
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
