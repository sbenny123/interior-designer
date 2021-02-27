package com.example.horizoninteriordesigner.models;

import android.net.Uri;


/**
 * 3D model using ViroCore including dragging, zoom etc.
 */
public class ItemModel {
    private final Uri modelUri;

    public ItemModel(Uri modelUri) {
        this.modelUri = modelUri;
    }
}
