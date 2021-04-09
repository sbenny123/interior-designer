package com.example.horizoninteriordesigner;

import android.app.Application;

import com.example.horizoninteriordesigner.models.ItemDB;
import com.google.ar.sceneform.AnchorNode;

import java.util.ArrayList;
import java.util.List;


/**
 * Allows item database to be shared amongst all components including activities.
 */
public class ItemDbApplication extends Application {
    private ItemDB itemDB;
    private List<AnchorNode> modelList;


    /**
     * Create Itemdb if it hasn't been yet and return it.
     * @return 'Database' of items
     */
    public ItemDB getItemDB() {
        if (itemDB == null) {
            itemDB = new ItemDB();
        }

        return itemDB;
    }

    /**
     *
     * @return
     */
    public List<AnchorNode> getModels() {
        if (modelList == null) {
            modelList = new ArrayList<AnchorNode>();
        }

        return modelList;
    }
}
