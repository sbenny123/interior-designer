package com.example.horizoninteriordesigner.activities.ItemSelection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.horizoninteriordesigner.*;
import com.example.horizoninteriordesigner.models.Item;

import java.util.ArrayList;


public class ItemSelectionAdapter extends ArrayAdapter<Item> {

    public ItemSelectionAdapter(@NonNull Context context, ArrayList<Item> itemArrayList) {
        super(context, 0, itemArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;

        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.item_card, parent, false);
        }

        Item item = getItem(position);
        ImageView itemImageView = listitemView.findViewById(R.id.iv_item);
        TextView itemTextView = listitemView.findViewById(R.id.tv_item);

        itemImageView.setImageResource(item.getResId());
        itemTextView.setText(item.getName());

        return listitemView;
    }
}
