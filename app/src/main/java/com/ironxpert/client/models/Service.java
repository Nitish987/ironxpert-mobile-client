package com.ironxpert.client.models;

import java.io.Serializable;

public class Service implements Serializable {
    private boolean available;
    private String by;
    private String desc;
    private int discount;
    private String name;
    private String photo;
    private int price;
    private double rating;
    private String serviceId;

    public Service() {}

    public Service(boolean available, String by, String desc, int discount, String name, String photo, int price, double rating, String serviceId) {
        this.available = available;
        this.by = by;
        this.desc = desc;
        this.discount = discount;
        this.name = name;
        this.photo = photo;
        this.price = price;
        this.rating = rating;
        this.serviceId = serviceId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
