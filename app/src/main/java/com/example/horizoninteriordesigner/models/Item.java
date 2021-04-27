package com.example.horizoninteriordesigner.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;


/**
 * Item class containing all the properties associated with a furniture item
 */
public class Item {

    private String itemId;
    private String imageName;
    private String imageUrl;
    private String modelUrl;

    //private String id;
    //private final String name;
   // private final int resId;
   // private String url;
   // private Uri uri;


    public Item() {
        // Needed for Firebase
    }

    public Item(String itemId, String imageName, String imageUrl, String modelUrl) {
        this.itemId = itemId;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.modelUrl = modelUrl;
    }

    public String getItemId() {
        return itemId;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getModelUrl() {
        return modelUrl;
    }


    /*  public Item(String name, Uri uri, int resId) {
        this.name = name;
        this.uri = uri;
        this.resId = resId;
        setId(); // Creates a unique id
    }

    public Item(String name, int resId, String url) {
        this.name = name;
        this.url = url;
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

    public String getUrl() { return url; }*/
}
