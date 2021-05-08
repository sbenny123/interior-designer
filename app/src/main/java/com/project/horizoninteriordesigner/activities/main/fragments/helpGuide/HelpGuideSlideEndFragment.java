package com.project.horizoninteriordesigner.activities.main.fragments.helpGuide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.project.horizoninteriordesigner.R;


public class HelpGuideSlideEndFragment extends Fragment {

    public static HelpGuideSlideEndFragment newInstance() {
        HelpGuideSlideEndFragment fragment = new HelpGuideSlideEndFragment();

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_guide_slide_end, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
