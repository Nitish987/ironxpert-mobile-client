package com.ironxpert.client.models;

import java.io.Serializable;

public class Address implements Serializable {
    private String address;

    private Address() {}

    public Address(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
