package com.example.horizoninteriordesigner.activities.Main.adapters.itemSelection;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.horizoninteriordesigner.activities.Main.fragments.itemSelection.ItemSelectionFragment;

import static com.example.horizoninteriordesigner.constants.itemConstants.getItemCategories;


public class ItemViewPagerAdapter extends FragmentStateAdapter {

    public ItemViewPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new ItemSelectionFragment();
    }

    @Override
    public int getItemCount() {
        return getItemCategories().size();
    }
}
