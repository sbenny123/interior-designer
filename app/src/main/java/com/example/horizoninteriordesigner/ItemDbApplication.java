package com.example.horizoninteriordesigner;

import android.app.Application;

import com.example.horizoninteriordesigner.models.ItemDB;


/**
 * Allows item database to be shared amongst all components including activities.
 */
public class ItemDbApplication extends Application {
    private ItemDB itemDB;

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
}
