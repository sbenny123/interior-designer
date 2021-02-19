package com.example.horizoninteriordesigner.activities.ItemSelection;

import android.net.Uri;
import android.os.Bundle;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.horizoninteriordesigner.*;
import com.example.horizoninteriordesigner.activities.ItemSelection.adapters.ItemSelectionAdapter;
import com.example.horizoninteriordesigner.models.Item;

import java.util.ArrayList;


public class ItemSelectionActivity extends AppCompatActivity {
    GridView itemsGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_selection);

        itemsGridView = findViewById(R.id.grid_view_items);

        ArrayList<Item> itemArrayList = new ArrayList<Item>();
        itemArrayList.add(new Item("1", "Desk", Uri.parse("file:///android_asset/Desk_01.obj"), R.drawable.desk_icon));
        itemArrayList.add(new Item("2", "Office chair", Uri.parse("file:///android_asset/item_office_chair.obj"), R.drawable.item_office_chair));
        
        ItemSelectionAdapter itemSelectionAdapter = new ItemSelectionAdapter(this, itemArrayList);
        itemsGridView.setAdapter(itemSelectionAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}