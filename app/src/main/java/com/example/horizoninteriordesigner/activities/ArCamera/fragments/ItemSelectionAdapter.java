package com.example.horizoninteriordesigner.activities.ArCamera.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.horizoninteriordesigner.R;
import com.example.horizoninteriordesigner.models.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemSelectionAdapter extends RecyclerView.Adapter<ItemSelectionAdapter.ItemViewHolder> {
    private Context context;
    private ArrayList<Item> itemArrayList;

    public ItemSelectionAdapter(@NonNull Context context, ArrayList<Item> itemArrayList) {
        this.context = context;
        this.itemArrayList = itemArrayList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(View view) {
            super(view);


        }
    }

}

