package com.project.horizoninteriordesigner.activities.main.adapters.helpGuide;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.project.horizoninteriordesigner.activities.main.fragments.helpGuide.HelpGuideSlideEndFragment;
import com.project.horizoninteriordesigner.activities.main.fragments.helpGuide.HelpGuideSlideStartFragment;


public class HelpGuideViewPagerAdapter extends FragmentStateAdapter {

    public HelpGuideViewPagerAdapter(@NonNull Fragment fragment) { super(fragment); }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return HelpGuideSlideStartFragment.newInstance();

            case 1:
                return HelpGuideSlideEndFragment.newInstance();

            default:
                return HelpGuideSlideStartFragment.newInstance();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
