package com.example.horizoninteriordesigner.models;

/**
 * Item class containing all the properties associated with a furniture item
 */
public class Item {
    public final String name;
    public final String uri;

    public Item(String name, String uri){
        this.name = name;
        this.uri = uri;
    }
}
