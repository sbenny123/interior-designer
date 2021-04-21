package com.example.horizoninteriordesigner.activities.Main.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.horizoninteriordesigner.R;
import com.example.horizoninteriordesigner.models.Item;

import java.util.ArrayList;

/**
 * Manages the item data model and adapts each item entry.
 */
public class ItemSelectionAdapter extends RecyclerView.Adapter<ItemSelectionAdapter.ItemViewHolder> {
    private Context context;
    private ArrayList<Item> itemArrayList; // List of item models that can be used

    private final ItemClickListener onItemClickListener; // onClick listener for when an item has
                                                         // been pressed


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

   /* public ItemSelectionAdapter(Context context, ArrayList<Item> itemArrayList) {
        this.context = context;
        this.itemArrayList = itemArrayList;
    }*/

    /**
     * Inflates the layout to be used for each item.
     * @param parent
     * @param viewType
     * @return Contains references to each filled item entry. Used to access elements in the layout.
     */
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);

        ItemViewHolder itemViewHolder = new ItemViewHolder(itemView);

        return itemViewHolder;
    }

    /**
     * Assigns values to each item using the inflated view from #onCreateViewHolder
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemArrayList.get(position);

        holder.itemIV.setImageResource(item.getResId());
        holder.itemTV.setText(item.getName());
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
     *
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView itemIV;
        private TextView itemTV;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemIV = itemView.findViewById(R.id.iv_item);
            itemTV = itemView.findViewById(R.id.tv_item);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            onItemClickListener.onItemClick(v, position);
        }
    }

}

