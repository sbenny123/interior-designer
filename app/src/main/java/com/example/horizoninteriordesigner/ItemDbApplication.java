package com.example.horizoninteriordesigner;

import android.app.Application;

import com.example.horizoninteriordesigner.models.ItemDB;
import com.example.horizoninteriordesigner.models.ItemModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Allows item database to be shared amongst all components including activities.
 */
public class ItemDbApplication extends Application {
    private ItemDB itemDB;
    private List<ItemModel> itemModelsList;


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
    public List<ItemModel> getItemModelDB() {
        if (itemModelsList == null) {
            itemModelsList = new ArrayList<ItemModel>();
        }

        return itemModelsList;
    }

    /*public ARScene getArScene() {
        if (arScene == null) {
            arScene = new ARScene();

            AmbientLight ambientLight = new AmbientLight(); // Light to illuminate node containing item

            // Add lighting to scene to make 3D object appear
            ambientLight.setColor(Color.WHITE);
            ambientLight.setIntensity(400); // Measure of brightness, 1000 is default
            ambientLight.setInfluenceBitMask(3); // Used to make light apply to a specific node
            arScene.getRootNode().addLight(ambientLight);
        }

        return arScene;
    }

    public ViroView getViroView() {
        return viroView;
    }*/
}
