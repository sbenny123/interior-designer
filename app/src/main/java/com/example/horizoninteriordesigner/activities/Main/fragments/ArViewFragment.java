package com.example.horizoninteriordesigner.activities.Main.fragments;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.example.horizoninteriordesigner.R;
import com.example.horizoninteriordesigner.activities.Main.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import static com.example.horizoninteriordesigner.activities.Main.MainActivity.ITEM_SELECT_TAG;


public class ArViewFragment extends Fragment {

    public ArViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SceneformFragment sceneformFragment = new SceneformFragment();
        //sceneformFragment.setOnTapArPlaneListener();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_ar, sceneformFragment).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ar_view, container, false);

        initialiseButtons(view);

        return view;
    }

    private void initialiseButtons(View view) {
        FloatingActionButton selectItemsBtn = view.findViewById(R.id.btn_select_items);
        FloatingActionButton takePhotoBtn = view.findViewById(R.id.btn_take_photo);

        selectItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showItemSelectionFragment();
            }
        });

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    /**
     * Opens the item selection page.
     */
    private void showItemSelectionFragment() {
        ((MainActivity) getActivity()).manageFragmentTransaction(ITEM_SELECT_TAG);
    }
}