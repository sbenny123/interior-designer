package com.example.horizoninteriordesigner.activities.Main.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.horizoninteriordesigner.R;
import com.example.horizoninteriordesigner.activities.Main.MainActivity;
import com.example.horizoninteriordesigner.activities.Main.adapters.MaterialSelectionAdapter;
import com.example.horizoninteriordesigner.activities.Main.viewModels.ItemViewModel;
import com.example.horizoninteriordesigner.models.Material;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static com.example.horizoninteriordesigner.activities.Main.MainActivity.AR_VIEW_TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MaterialSelectionFragment extends Fragment implements MaterialSelectionAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private MaterialSelectionAdapter adapter;
    private ArrayList<Material> materialArrayList;
    private ItemViewModel itemViewModel;

    public MaterialSelectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_material_selection, container, false);

        recyclerView = view.findViewById(R.id.rv_materials);

        // Set up configuration for recycler view
        setUpRecyclerView();

        // Set material data in array list for a given item and creates adapter
        getMaterials();

        itemViewModel = new ViewModelProvider(getActivity()).get(ItemViewModel.class);

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Material selectedMaterial = materialArrayList.get(position);
        AtomicReference<TransformableNode> selectedModel = null;

        itemViewModel.getModelNode().observe(getViewLifecycleOwner(), transformableNode -> {
            selectedModel.set(transformableNode);

            updateModelMaterial(selectedModel.get(), selectedMaterial);
        });
    }

    private void updateModelMaterial(TransformableNode selectedModel, Material updatedMaterial) {
        Uri materialUri = Uri.parse(updatedMaterial.getMaterialUrl());

        Texture.builder()
                .setSource(getActivity(), materialUri)
                .build()
                .thenAccept(texture -> {
                    selectedModel.getRenderable().getMaterial().setTexture("baseColorMap", texture);

                    Toast.makeText(getContext(), "Texture applied", Toast.LENGTH_SHORT);

                    ((MainActivity) getActivity()).manageFragmentTransaction(AR_VIEW_TAG);
                });
    }


    /**
     * Retrieves item documents from Firestore and maps to Item model and adds to array list.
     * Adapter is set once all documents are added.
     */
    private void getMaterials() {
        materialArrayList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("materials")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String materialId = document.getId();
                                String materialName = document.get("materialName") + "";
                                String materialUrl = document.get("materialUrl") + "";

                                materialArrayList.add(new Material(materialId, materialName, materialUrl));

                                Log.i("MaterialSelectionFragment", materialName);
                            }

                            Log.i("MaterialSelectionFragment", "Material array is " + materialArrayList.size());

                            // Create adapter for materials view - bridges recyclerview and materials (data source)
                            adapter = new MaterialSelectionAdapter(getContext(),
                                    MaterialSelectionFragment.this::onItemClick, materialArrayList);

                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
    }

    /**
     *
     */
    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }
}