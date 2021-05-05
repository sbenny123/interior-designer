package com.example.horizoninteriordesigner.activities.Main.adapters.itemSelection;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.horizoninteriordesigner.activities.Main.fragments.itemSelection.ItemSelectionFragment;

import static com.example.horizoninteriordesigner.constants.itemConstants.getItemCategories;


/**
 * Used to select which fragment to view when a tab has been selected.
 * ItemSelectionFragment is always used but its category key is changed.
 */
public class ItemViewPagerAdapter extends FragmentStateAdapter {

    public ItemViewPagerAdapter(Fragment fragment) {
        super(fragment);
    }


    /**
     * Gets the item selection fragment which shows the items.
     * @param position index of selected tab
     * @return Fragment to show in viewpager
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {

        String catKey = getItemCategories().get(position).getCatKey();

        return new ItemSelectionFragment(catKey);
    }


    /**
     * @return Total number of tabulated categories
     */
    @Override
    public int getItemCount() {
        return getItemCategories().size();
    }
}
