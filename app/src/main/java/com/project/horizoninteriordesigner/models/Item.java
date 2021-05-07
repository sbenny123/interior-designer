package com.project.horizoninteriordesigner.models;


/**
 * Item class containing all the properties associated with a furniture item
 */
public class Item {

    private String itemId;
    private String imageName;
    private String imageUrl;
    private String modelUrl;

    
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
}
