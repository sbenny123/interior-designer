package com.horizonid.horizoninteriordesigner.activities.main.fragments.helpGuide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.horizonid.horizoninteriordesigner.R;


public class HelpGuideSlideArView3Fragment extends Fragment {

    public HelpGuideSlideArView3Fragment() {
        // Required empty public constructor.
    }


    /**
     * Factory method to create a new instance of HelpGuideSlideArView3Fragment.
     * @return a new instance of fragment HelpGuideSlideArView3Fragment.
     */
    public static HelpGuideSlideArView3Fragment newInstance() {
        return new HelpGuideSlideArView3Fragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_guide_slide_ar_view_3, container, false);
    }
}
