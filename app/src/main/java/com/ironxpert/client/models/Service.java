package com.ironxpert.client.models;

public class Service {
    private String name, desc;
    private int drawableId;

    public Service(String name, String desc, int drawableId) {
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
}
