package com.example.horizoninteriordesigner.models;

/**
 * Item class containing all the properties associated with a furniture item
 */
public class Item {
    private final String id;
    private final String name;
    private final String uri;

    public Item(String id, String name, String uri) {
        this.id = id;
        this.name = name;
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }
}
