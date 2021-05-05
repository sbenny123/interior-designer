package com.example.horizoninteriordesigner.activities.Main.fragments.itemSelection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.horizoninteriordesigner.R;
import com.example.horizoninteriordesigner.activities.Main.adapters.itemSelection.ItemViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import static com.example.horizoninteriordesigner.constants.itemConstants.getItemCategories;


/**
 * Tabbed item selection fragment
 * Handles provision of tabs for each item category and calling the fragment to show the correct items.
 */
public class ItemViewPagerFragment extends Fragment {

    private ItemViewPagerAdapter adapter; // Handles calling of itemSelection fragment to get items
    private TabLayout tabLayout; // For tabs
    private ViewPager2 viewPager; // For section showing the items


    /**
     * Inflates the fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_view_pager, container, false);
    }


    /**
     *  Initialises the tabs to show the different item categories.
     *  Initialises the view page to show the items to select for that tab.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);

        adapter = new ItemViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Creates tab for each item category and gives it a title
        // Each category is of model ItemCategory
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(getItemCategories().get(position).getCatTitle())
        ).attach();
    }
}

