package com.project.horizoninteriordesigner.activities.main.fragments.helpGuide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.horizoninteriordesigner.R;
import com.project.horizoninteriordesigner.activities.main.MainActivity;
import com.project.horizoninteriordesigner.activities.main.adapters.helpGuide.HelpGuideViewPagerAdapter;


public class HelpGuideViewPagerFragment extends Fragment implements View.OnClickListener {

    // Fragment initialisation parameters.
    private static final String LAST_FRAGMENT_KEY = "fragmentTag";

    private HelpGuideViewPagerAdapter adapter;
    private Button nextPageBtn;
    private String previousFragment;
    private Button skipGuideBtn;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;


    public HelpGuideViewPagerFragment() {
        // Required empty public constructor.
    }


    /**
     * Factory method to create a new instance of HelpGuideViewPagerFragment.
     * @return a new instance of fragment HelpGuideViewPagerFragment
     */
    public static HelpGuideViewPagerFragment newInstance(String fragmentTag) {
        HelpGuideViewPagerFragment fragment = new HelpGuideViewPagerFragment();

        Bundle args = new Bundle();
        args.putString(LAST_FRAGMENT_KEY, fragmentTag);

        fragment.setArguments(args);

        return fragment;
    }


    /**
     * Initialises variables using the arguments passed in at instance creation.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            previousFragment = getArguments().getString(LAST_FRAGMENT_KEY);
        }
    }


    /**
     * Inflates the fragment's layout.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_guide_view_pager, container, false);
    }


    /**
     * Initialises the bottom dot navigation
     * Initialise the view page to show the current help guide page
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tab_layout_help);
        viewPager = view.findViewById(R.id.view_pager_help);

        adapter = new HelpGuideViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Creates bottom tab with dots for each page.
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();

        initialiseButtons(view);
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

    private void initialiseButtons(View view) {
        nextPageBtn = view.findViewById(R.id.btn_next_page);
        skipGuideBtn = view.findViewById(R.id.btn_skip_guide);

        nextPageBtn.setOnClickListener(this);
        skipGuideBtn.setOnClickListener(this);
    }

    private void showLastFragment() {
        ((MainActivity) getActivity()).manageFragmentTransaction(previousFragment);
    }
}
