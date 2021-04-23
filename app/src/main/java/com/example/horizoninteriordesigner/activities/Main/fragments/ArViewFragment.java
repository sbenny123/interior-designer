package com.example.horizoninteriordesigner.activities.Main.fragments;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.horizoninteriordesigner.R;
import com.example.horizoninteriordesigner.activities.Main.MainActivity;
import com.example.horizoninteriordesigner.models.Item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.BaseTransformableNode;
import com.google.ar.sceneform.ux.TransformableNode;


import java.util.List;

import static com.example.horizoninteriordesigner.activities.Main.MainActivity.ITEM_SELECT_TAG;


public class ArViewFragment extends Fragment implements BaseArFragment.OnTapArPlaneListener {
    private SceneformFragment sceneformFragment;
    private Renderable renderable;
    private Item item;

    private AnchorNode currentAnchorNode;

    public ArViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sceneformFragment = new SceneformFragment();
        sceneformFragment.setOnTapArPlaneListener(this::onTapPlane);

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
                currentAnchorNode = null;
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

    @Override
    public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        Log.i("ArViewFragment", "Tapped on plane!");

        Renderable renderable = ((MainActivity)getActivity()).getRenderable();

        if (renderable == null) {
            Toast.makeText(getActivity(), "Select a model", Toast.LENGTH_SHORT).show();
            return;
        }

        Anchor anchor = hitResult.createAnchor();

        if (currentAnchorNode != null) {
            currentAnchorNode.setAnchor(anchor);

        } else {
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(sceneformFragment.getArSceneView().getScene());

            // Create the transformable model and add it to the anchor.
            TransformableNode model = new TransformableNode(sceneformFragment.getTransformationSystem());
            model.setParent(anchorNode);
            model.setRenderable(renderable);
            model.select();
            
            currentAnchorNode = anchorNode;
        }
    }
}