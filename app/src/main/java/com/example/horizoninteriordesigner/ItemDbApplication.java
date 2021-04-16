package com.example.horizoninteriordesigner;

import android.app.Application;

import com.example.horizoninteriordesigner.models.ItemDB;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Allows item database to be shared amongst all components including activities.
 */
public class ItemDbApplication extends Application {
    private ArFragment arFragment;
    private ItemDB itemDB;
    private List<AnchorNode> modelList;
    private List<Anchor> anchorList;


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

    public List<Anchor> getAnchors() {
        if (anchorList == null) {
            anchorList = new ArrayList<Anchor>();
        }

        return anchorList;
    }

    public ArFragment getFragment() {
        return arFragment;
    }
}
