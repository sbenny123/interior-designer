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
        itemArrayList.add(new Item("Desk", Uri.parse("file:///android_asset/Desk_01.obj"), R.drawable.desk_icon));
        itemArrayList.add(new Item("Office chair", Uri.parse("file:///android_asset/item_office_chair.obj"), R.drawable.item_office_chair));
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
