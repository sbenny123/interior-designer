package com.horizonid.horizoninteriordesigner.activities.main.adapters.itemSelection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.horizonid.horizoninteriordesigner.R;
import com.horizonid.horizoninteriordesigner.models.Item;

import java.util.ArrayList;

/**
 * Manages the item data model and sorts each item entry.
 */
public class ItemSelectionAdapter extends RecyclerView.Adapter<ItemSelectionAdapter.ItemViewHolder> {

    private Context context;
    private ArrayList<Item> itemArrayList; // List of item models that can be used
    private final ItemClickListener onItemClickListener; // onClick listener for when an item has been pressed


    /**
     * Interface to handle onClick event for each item.
     */
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


    public ItemSelectionAdapter(Context context, ItemClickListener onItemClickListener, ArrayList<Item> itemArrayList) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.itemArrayList = itemArrayList;
    }


    /**
     * Inflates the layout to be used for each item.
     * @return References to each filled item entry. Used to access elements in the layout.
     */
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);

        return new ItemViewHolder(itemView);
    }


    /**
     * Assigns values to each item using the inflated view from onCreateViewHolder
     * @param holder types of views the values can be assigned to e.g. imageView and textView.
     * @param position The index of the current card - used to retrieve its assocated value in itemArrayList.
     */
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item currentItem = itemArrayList.get(position);

        // Load image using Glide
        Glide.with(context).load(currentItem.getImageUrl()).into(holder.itemIV);
    }


    /**
     * Gets the total number of items in the recycler view.
     * @return total number of items in array list.
     */
    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }


    /**
     * Specifies what view types the values should be assigned to and the variables for the onClick
     * event for each item.
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView itemIV;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemIV = itemView.findViewById(R.id.iv_item);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            onItemClickListener.onItemClick(view, position);
        }
    }
}

