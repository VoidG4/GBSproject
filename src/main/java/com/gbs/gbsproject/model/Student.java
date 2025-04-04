package com.gbs.gbsproject.model;

public class Student extends User {
    private String surname;
    private String username;
    private String password;
    private String email;

    // Constructor with id and name (inherited from User)
    public Student(int id, String name) {
        super(id, name);
    }

    // Constructor with all fields
    public Student(int id, String name, String surname, String username, String password, String email) {
        super(id, name);
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters and Setters
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

