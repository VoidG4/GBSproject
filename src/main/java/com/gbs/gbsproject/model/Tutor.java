package com.gbs.gbsproject.model;

public class Tutor extends User {
    private String field;

    // Constructor with id and name (inherited from User)
    public Tutor(int id, String name) {
        super(id, name, "",  "",  "",  "");
    }

    // Constructor with all fields
    public Tutor(int id, String name, String surname, String username, String password, String email, String field) {
        super(id, name, surname,  username,  password,  email);
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
