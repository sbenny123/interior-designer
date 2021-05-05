package com.example.horizoninteriordesigner.activities.Main.fragments.itemSelection;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.horizoninteriordesigner.R;

/**
 *
 */
public class NewItemSelectionFragment extends Fragment {

    public NewItemSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("NewItemSelect", "Reached onCreateView");
        return inflater.inflate(R.layout.fragment_new_item_selection, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i("NewItemSelect", "Reached onViewCreated");

        TextView t = (TextView) view.findViewById(R.id.textView);
        t.setText("here");

    }
}