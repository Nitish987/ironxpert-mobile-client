package com.ironxpert.client.models;

import java.io.Serializable;

public class Service implements Serializable {
    private int index;
    private String name, desc;
    private int drawableId;

    public Service(int index, String name, String desc, int drawableId) {
        this.index = index;
        this.name = name;
        this.desc = desc;
        this.drawableId = drawableId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
