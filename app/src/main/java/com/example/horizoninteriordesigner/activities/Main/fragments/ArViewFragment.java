package com.example.horizoninteriordesigner.activities.Main.fragments;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.horizoninteriordesigner.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.ux.ArFragment;


public class ArViewFragment extends Fragment {

    public ArViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SceneformFragment sceneformFragment = new SceneformFragment();

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
        NavController navController = Navigation.findNavController(getActivity(), R.id.fragment_main_nav_host);

       /* if (navController.popBackStack(R.id.itemSelectionFragment, false)) {
            Log.i("Fragment", "Found itemSelection in backStack");
        } else {
            navController.navigate(R.id.itemSelectionFragment);
        }*/

        navController.navigate(R.id.action_arViewFragment_to_itemSelectionFragment);
    }
}