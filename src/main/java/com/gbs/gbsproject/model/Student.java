package com.gbs.gbsproject.model;

public class Student extends User {

    public Student() {
        super();
    }

    // Constructor with id and name (inherited from User)
    public Student(int id, String name) {
        super(id, name, "",  "",  "",  "");
    }

    // Constructor with all fields
    public Student(int id, String name, String surname, String username, String password, String email) {
        super(id, name, surname,  username,  password,  email);
    }
}

