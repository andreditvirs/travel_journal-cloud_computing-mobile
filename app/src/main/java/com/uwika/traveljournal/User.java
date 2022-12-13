package com.uwika.traveljournal;

import java.util.HashMap;

public class User {
    public String name,email,username,password,birthdate;
    public Object journals;

    public User(){

    }

    public User(String name, String username, String email,String password,String birthdate, Object journals){
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.birthdate = birthdate;
        this.journals = journals;
    }
}
