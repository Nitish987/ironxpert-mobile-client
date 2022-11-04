package com.ironxpert.client.models;

import java.io.Serializable;

public class User implements Serializable {
    private String email, msgToken, name, phone, photo, uid;

    public User() {}

    public User(String email, String msgToken, String name, String phone, String photo, String uid) {
        this.email = email;
        this.msgToken = msgToken;
        this.name = name;
        this.phone = phone;
        this.photo = photo;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMsgToken() {
        return msgToken;
    }

    public void setMsgToken(String msgToken) {
        this.msgToken = msgToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
