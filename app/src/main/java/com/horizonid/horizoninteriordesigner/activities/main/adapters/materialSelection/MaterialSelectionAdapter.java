package com.horizonid.horizoninteriordesigner.activities.main.adapters.materialSelection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.horizonid.horizoninteriordesigner.R;
import com.horizonid.horizoninteriordesigner.models.Material;

import java.util.ArrayList;


/**
 * Manages the item data model and adapts each item entry.
 */
public class MaterialSelectionAdapter extends RecyclerView.Adapter<MaterialSelectionAdapter.MaterialViewHolder> {

    private Context context;
    private ArrayList<Material> materialArrayList; // List of materials that could be selected
    private final ItemClickListener onItemClickListener; // onClick listener for when a material has been selected


    /**
     * Interface to handle onClick event for each material.
     */
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    public MaterialSelectionAdapter(Context context, ItemClickListener onItemClickListener, ArrayList<Material> materialArrayList) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.materialArrayList = materialArrayList;
    }


    /**
     * Inflates the layout to be used for each item.
     * @param parent
     * @param viewType
     * @return Contains references to each filled item entry. Used to access elements in the layout.
     */
    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View materialView = LayoutInflater.from(context).inflate(R.layout.card_material, parent, false);

        MaterialViewHolder materialViewHolder = new MaterialViewHolder(materialView);

        return materialViewHolder;
    }


    /**
     * Assigns values to each item using the inflated view from #onCreateViewHolder
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        Material currentMaterial = materialArrayList.get(position);

        // Load image using Glide
        Glide.with(context).load(currentMaterial.getMaterialUrl()).into(holder.materialIV);
    }


    /**
     * Gets the total number of items in the recycler view.
     * @return total number of items in array list.
     */
    @Override
    public int getItemCount() {
        return materialArrayList.size();
    }


    /**
     * Sets up view type that each item has.
     */
    public class MaterialViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView materialIV;


        public MaterialViewHolder(@NonNull View materialView) {
            super(materialView);

            materialIV = materialView.findViewById(R.id.iv_material);

            materialView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            onItemClickListener.onItemClick(v, position);
        }
    }
}

