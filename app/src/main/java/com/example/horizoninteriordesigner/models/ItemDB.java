package com.example.horizoninteriordesigner.models;

import android.net.Uri;

import com.example.horizoninteriordesigner.*;

import java.util.ArrayList;



public class ItemDB {
    private ArrayList<Item> itemArrayList = new ArrayList<Item>(); // List of furniture items

    public ItemDB() {
        setItems(); // Adds the items to the ArrayList
    }

    public ArrayList<Item> getItems() { return itemArrayList; }

    private void setItems() {
        String assestPrefix = "file:///android_asset/models/";

        itemArrayList.add(new Item("Cube", Uri.parse(assestPrefix + "model.glb"), R.drawable.wooden_chair));
        itemArrayList.add(new Item("Sofa", Uri.parse(assestPrefix + "sofa.glb"), R.drawable.office_chair));
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
