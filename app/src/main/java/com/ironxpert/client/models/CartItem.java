package com.ironxpert.client.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String id;
    private int quantity;
    private ServiceItem serviceItem;
    private String serviceItemId;
    private int totalPrice;

    public CartItem() {}

    public CartItem(String id, int quantity, ServiceItem serviceItem, String serviceItemId, int totalPrice) {
        this.id = id;
        this.quantity = quantity;
        this.serviceItem = serviceItem;
        this.serviceItemId = serviceItemId;
        this.totalPrice = totalPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ServiceItem getServiceItem() {
        return serviceItem;
    }

    public void setServiceItem(ServiceItem serviceItem) {
        this.serviceItem = serviceItem;
    }

    public String getServiceItemId() {
        return serviceItemId;
    }

    public void setServiceItemId(String serviceItemId) {
        this.serviceItemId = serviceItemId;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
