package com.example.debug.model;

public class User {
    private final long id;
    private final String username;
    private final String password;
    private final int age;
    private final String role;
    private final boolean active;

    public User(long id, String username, String password, int age, String role, boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.age = age;
        this.role = role;
        this.active = active;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getAge() {
        return age;
    }

    public String getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }
}
