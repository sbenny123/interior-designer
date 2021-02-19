package com.example.horizoninteriordesigner.models;

import android.net.Uri;

/**
 * Item class containing all the properties associated with a furniture item
 */
public class Item {
    private final String id;
    private final String name;
    private final Uri uri;
    private final int resId;

    public Item(String id, String name, Uri uri, int resId) {
        this.id = id;
        this.name = name;
        this.uri = uri;
        this.resId = resId;
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

    public int getResId() { return resId; }
}
