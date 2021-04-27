package com.example.horizoninteriordesigner.models;

import android.net.Uri;

import com.example.horizoninteriordesigner.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;



public class ItemDB {
    private ArrayList<Item> itemArrayList = new ArrayList<Item>(); // List of furniture items

    public ItemDB() {
        setItems(); // Adds the items to the ArrayList
    }

    public ArrayList<Item> getItems() { return itemArrayList; }

    private void setItems() {
        String assestPrefix = "file:///android_asset/models/";

       // FirebaseStorage storage = FirebaseStorage.getInstance();
       // StorageReference modelsRef = storage.getReference().child("models");
      //  StorageReference thumbnailRef = storage.getReference().child("thumbnails");


        //itemArrayList.add(new Item("Chair", ))
        itemArrayList.add(new Item("Chair", Uri.parse(assestPrefix + "chair_1.glb"), R.drawable.chair_1));
        itemArrayList.add(new Item("Chair 2", Uri.parse(assestPrefix + "chair_2.glb"), R.drawable.chair_2));
        itemArrayList.add(new Item("Sofa", Uri.parse(assestPrefix + "sofa_1.glb"), R.drawable.sofa_1));
        itemArrayList.add(new Item("Sofa 2", Uri.parse(assestPrefix + "sofa_2.glb"), R.drawable.sofa_2));
        itemArrayList.add(new Item("Sofa 3", Uri.parse(assestPrefix + "sofa_3.glb"), R.drawable.sofa_3));
        itemArrayList.add(new Item("Sofa 4", Uri.parse(assestPrefix + "sofa_4.glb"), R.drawable.sofa_4));
        itemArrayList.add(new Item("Sofa 5", Uri.parse(assestPrefix + "sofa_5.glb"), R.drawable.sofa_5));
        itemArrayList.add(new Item("Test sofa google", Uri.parse(assestPrefix + "test_sofa.glb"), R.drawable.couch_icon));
        itemArrayList.add(new Item("Test sofa 1", Uri.parse(assestPrefix + "test_sofa1.glb"), R.drawable.couch_icon));
        itemArrayList.add(new Item("Test sofa 2", Uri.parse(assestPrefix + "test_sofa2.glb"), R.drawable.couch_icon));
        itemArrayList.add(new Item("Test chair 1", Uri.parse(assestPrefix + "test_chair1.glb"), R.drawable.couch_icon));
        itemArrayList.add(new Item("Test wardrobe 1", Uri.parse(assestPrefix + "test_wardrobe1.glb"), R.drawable.couch_icon));
        // itemArrayList.add(new Item("Test chair 1", Uri.parse(assestPrefix + ".glb"), R.drawable.couch_icon));
        // itemArrayList.add(new Item("Test chair 1", Uri.parse(assestPrefix + ".glb"), R.drawable.couch_icon));
        //itemArrayList.add(new Item("", Uri.parse(assestPrefix + ".glb"), R.drawable.couch_icon));
    }

    /**
     * Compares the id value with each Item's id value. Returns the Item object if they match.
     * @param id id of Item to find
     * @return Object Item if ids match or null if not.
     */
    public Item getItemById (String id) {
        Item currentItem;

        for (int i = 0; i < itemArrayList.size(); i++) {
            currentItem = itemArrayList.get(i);

            if (currentItem.getId().equals(id)) {
                return currentItem;
            }
        }

        return null;
    }
}
