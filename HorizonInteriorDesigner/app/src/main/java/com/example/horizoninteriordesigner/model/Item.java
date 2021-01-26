package com.example.horizoninteriordesigner.model;

/**
 * Item class containing all the properties associated with a furniture item
 */
public class Item {
    public final String name;
    public final String modelUri;

    public Item(String aName, String aModelUri){
        name = aName;
        modelUri = aModelUri;
    }
}
