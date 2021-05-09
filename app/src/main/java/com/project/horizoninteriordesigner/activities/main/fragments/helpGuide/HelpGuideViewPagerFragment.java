package com.project.horizoninteriordesigner.activities.main.fragments.helpGuide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.project.horizoninteriordesigner.R;
import com.project.horizoninteriordesigner.activities.main.adapters.helpGuide.HelpGuideViewPagerAdapter;


public class HelpGuideViewPagerFragment extends Fragment {

    private HelpGuideViewPagerAdapter adapter;
    private ViewPager2 viewPager;


    public HelpGuideViewPagerFragment() {
        // Required empty public constructor.
    }


    /**
     * Factory method to create a new instance of HelpGuideViewPagerFragment.
     * @return a new instance of fragment HelpGuideViewPagerFragment
     */
    public static HelpGuideViewPagerFragment newInstance() {
        HelpGuideViewPagerFragment fragment = new HelpGuideViewPagerFragment();

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_guide_view_pager, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.view_pager_help);

        adapter = new HelpGuideViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
    }
}
