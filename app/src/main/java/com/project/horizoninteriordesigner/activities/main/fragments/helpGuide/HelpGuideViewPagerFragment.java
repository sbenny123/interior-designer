package com.project.horizoninteriordesigner.activities.main.fragments.helpGuide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.horizoninteriordesigner.R;
import com.project.horizoninteriordesigner.activities.main.adapters.helpGuide.HelpGuideViewPagerAdapter;


public class HelpGuideViewPagerFragment extends Fragment implements View.OnClickListener {

    private HelpGuideViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;


    public HelpGuideViewPagerFragment() {
        // Required empty public constructor.
    }


    /**
     * Factory method to create a new instance of HelpGuideViewPagerFragment.
     * @return a new instance of fragment HelpGuideViewPagerFragment
     */
    public static HelpGuideViewPagerFragment newInstance() {
        return new HelpGuideViewPagerFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_guide_view_pager, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tab_layout_help);
        viewPager = view.findViewById(R.id.view_pager_help);

        adapter = new HelpGuideViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Creates bottom tab with dots for each page.
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_next_page) {
            int nextPage = (viewPager.getCurrentItem() + 1);

            // Not the last page yet, so move to next page.
            if (nextPage < adapter.getItemCount()) {
                viewPager.setCurrentItem(nextPage);

            } else {
                showLastFragment();
            }

        } else if (id == R.id.btn_skip_guide) {
            showLastFragment();
        }
    }


    private void showLastFragment() {

    }
}
