package com.horizonid.horizoninteriordesigner.activities.main.fragments.helpGuide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.horizonid.horizoninteriordesigner.R;


public class HelpGuideSlideArView2Fragment extends Fragment {

    public HelpGuideSlideArView2Fragment() {
        // Required empty public constructor.
    }


    /**
     * Factory method to create a new instance of HelpGuideSlideArView2Fragment.
     * @return a new instance of fragment HelpGuideSlideArView2Fragment.
     */
    public static HelpGuideSlideArView2Fragment newInstance() {
        return new HelpGuideSlideArView2Fragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_guide_slide_ar_view_2, container, false);
    }
}
