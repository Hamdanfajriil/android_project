package com.example.lumicotelecommunication.models;

public class User {
    private String name;
    private String email;
    private String posisi;

    public User() {
        // Diperlukan untuk Firebase
    }

    public User(String name, String email, String posisi) {
        this.name = name;
        this.email = email;
        this.posisi = posisi;
    }

    // Getter dan Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosisi() {
        return posisi;
    }

    public void setPosisi(String latitude) {
        this.posisi = posisi;
    }
}
