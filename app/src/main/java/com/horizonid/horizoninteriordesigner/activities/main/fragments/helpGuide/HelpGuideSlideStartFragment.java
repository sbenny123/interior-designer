package com.horizonid.horizoninteriordesigner.activities.main.fragments.helpGuide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.horizonid.horizoninteriordesigner.R;


public class HelpGuideSlideStartFragment extends Fragment {

    public HelpGuideSlideStartFragment() {
        // Required empty public constructor.
    }


    /**
     * Factory method to create a new instance of HelpGuideSlideStartFragment.
     * @return a new instance of fragment HelpGuideSlideStartFragment.
     */
    public static HelpGuideSlideStartFragment newInstance() {
        return new HelpGuideSlideStartFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_guide_slide_start, container, false);
    }
}
