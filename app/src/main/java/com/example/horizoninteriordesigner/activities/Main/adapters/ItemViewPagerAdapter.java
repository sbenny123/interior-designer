package com.example.horizoninteriordesigner.activities.Main.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.horizoninteriordesigner.activities.Main.fragments.itemSelection.NewItemSelectionFragment;

public class ItemViewPagerAdapter extends FragmentStateAdapter {

    public ItemViewPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new NewItemSelectionFragment();
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
