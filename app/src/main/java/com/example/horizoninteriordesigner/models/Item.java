package com.example.horizoninteriordesigner.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;


/**
 * Item class containing all the properties associated with a furniture item
 */
public class Item implements Parcelable {

    private String id;
    private final String name;
    private final Uri uri;
    private final int resId;


    public Item(String name, Uri uri, int resId) {
        this.name = name;
        this.uri = uri;
        this.resId = resId;
        setId(); // Creates a unique id
    }


    public String getId() {
        return id;
    }

    private void setId() {
        String randomId = UUID.randomUUID().toString();
        this.id = randomId;
    }

    public String getName() {
        return name;
    }

    public Uri getUri() {
        return uri;
    }
    public int getResId() { return resId; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
