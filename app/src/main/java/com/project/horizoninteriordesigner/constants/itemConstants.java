package com.project.horizoninteriordesigner.constants;

import com.project.horizoninteriordesigner.models.ItemCategory;

import java.util.ArrayList;


/**
 * Constant data values relating to the items that are used throughout the application. Includes:
 *   - List of item categories with keys in the database and the name to show in the UI
 */
public class itemConstants {
    private static ArrayList<ItemCategory> itemCategories;

    public static ArrayList<ItemCategory> getItemCategories() {
        itemCategories = new ArrayList<>();

        itemCategories.add(new ItemCategory(0, "", "All"));
        itemCategories.add(new ItemCategory(1, "bed", "Beds"));
        itemCategories.add(new ItemCategory(2, "chair", "Chairs"));
        itemCategories.add(new ItemCategory(3, "sofa", "Sofas and Divans"));
        itemCategories.add(new ItemCategory(4, "table", "Tables"));
        itemCategories.add(new ItemCategory(5, "wardrobe", "Wardrobes and cabinets"));

        return itemCategories;
    }
}
