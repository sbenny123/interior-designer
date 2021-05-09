package com.project.horizoninteriordesigner.activities.main.fragments.helpGuide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.project.horizoninteriordesigner.R;


public class HelpGuideSlideArView1Fragment extends Fragment {

    public HelpGuideSlideArView1Fragment() {
        // Required empty public constructor.
    }


    /**
     * Factory method to create a new instance of HelpGuideSlideArView1Fragment.
     * @return a new instance of fragment HelpGuideSlideArView1Fragment.
     */
    public static HelpGuideSlideArView1Fragment newInstance() {
        return new HelpGuideSlideArView1Fragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_guide_slide_ar_view_1, container, false);
    }
}
