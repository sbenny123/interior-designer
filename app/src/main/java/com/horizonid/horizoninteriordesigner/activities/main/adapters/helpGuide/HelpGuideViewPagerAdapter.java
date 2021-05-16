package com.horizonid.horizoninteriordesigner.activities.main.adapters.helpGuide;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.horizonid.horizoninteriordesigner.activities.main.fragments.helpGuide.HelpGuideSlideArView1Fragment;
import com.horizonid.horizoninteriordesigner.activities.main.fragments.helpGuide.HelpGuideSlideArView2Fragment;
import com.horizonid.horizoninteriordesigner.activities.main.fragments.helpGuide.HelpGuideSlideArView3Fragment;
import com.horizonid.horizoninteriordesigner.activities.main.fragments.helpGuide.HelpGuideSlideEndFragment;
import com.horizonid.horizoninteriordesigner.activities.main.fragments.helpGuide.HelpGuideSlideItemSelectFragment;
import com.horizonid.horizoninteriordesigner.activities.main.fragments.helpGuide.HelpGuideSlideStartFragment;


public class HelpGuideViewPagerAdapter extends FragmentStateAdapter {

    private Fragment[] fragments; // Total number of pages in the guide


    public HelpGuideViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);

        fragments = new Fragment[]{
                HelpGuideSlideStartFragment.newInstance(),
                HelpGuideSlideItemSelectFragment.newInstance(),
                HelpGuideSlideArView1Fragment.newInstance(),
                HelpGuideSlideArView2Fragment.newInstance(),
                HelpGuideSlideArView3Fragment.newInstance(),
                HelpGuideSlideEndFragment.newInstance()
        };
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position > fragments.length ) {
            position = fragments.length - 1;
        }

        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }
}
