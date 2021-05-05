package com.example.horizoninteriordesigner.models;

public class ItemCategory {
    private Integer tabPosition;
    private String catKey;
    private String catTitle;


    public ItemCategory(Integer tabPosition, String catKey, String catTitle) {
        this.tabPosition = tabPosition;
        this.catKey = catKey;
        this.catTitle = catTitle;
    }

    public Integer getTabPosition() {
        return tabPosition;
    }

    public String getCatKey() {
        return catKey;
    }

    public String getCatTitle() {
        return catTitle;
    }

}
