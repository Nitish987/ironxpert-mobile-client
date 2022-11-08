package com.ironxpert.client.models;

import java.io.Serializable;

public class ServiceItem implements Serializable {
    private boolean available;
    private String by;
    private String category;
    private int discount;
    private String id;
    private String name;
    private String photo;
    private int price;
    private int service;

    public ServiceItem() {}

    public ServiceItem(boolean available, String by, String category, int discount, String id, String name, String photo, int price, int service) {
        this.available = available;
        this.by = by;
        this.category = category;
        this.discount = discount;
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.price = price;
        this.service = service;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }
}
