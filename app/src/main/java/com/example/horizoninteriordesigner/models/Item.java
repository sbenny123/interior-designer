package com.example.horizoninteriordesigner.models;

import android.net.Uri;

/**
 * Item class containing all the properties associated with a furniture item
 */
public class Item {
    private final String id;
    private final String name;
    private final Uri uri;

    public Item(String id, String name, Uri uri) {
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

    public Uri getUri() {
        return uri;
    }
}
