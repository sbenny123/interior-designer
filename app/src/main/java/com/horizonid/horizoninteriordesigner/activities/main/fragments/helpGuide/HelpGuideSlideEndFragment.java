package com.horizonid.horizoninteriordesigner.activities.main.fragments.helpGuide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.horizonid.horizoninteriordesigner.R;


public class HelpGuideSlideEndFragment extends Fragment {

    public HelpGuideSlideEndFragment() {
        // Required empty public constructor.
    }


    /**
     * Factory method to create a new instance of HelpGuideSlideEndFragment.
     * @return a new instance of fragment HelpGuideSlideEndFragment.
     */
    public static HelpGuideSlideEndFragment newInstance() {
        return new HelpGuideSlideEndFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_guide_slide_end, container, false);
    }
}
