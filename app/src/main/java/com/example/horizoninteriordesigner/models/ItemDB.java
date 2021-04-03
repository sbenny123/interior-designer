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
        itemArrayList.add(new Item("Wooden chair", Uri.parse("file:///android_asset/CHAHIN_WOODEN_CHAIR.gltf"), R.drawable.wooden_chair));
        itemArrayList.add(new Item("Office chair", Uri.parse("file:///android_asset/model.gltf"), R.drawable.office_chair));
        itemArrayList.add(new Item("Lamp", Uri.parse("file:///android_asset/Standing_lamp_01.gltf"), R.drawable.standing_lamp));
        itemArrayList.add(new Item("Table", Uri.parse("file:///android_asset/Table_Large_Rectangular_01.gltf"), R.drawable.rectangle_table));
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
