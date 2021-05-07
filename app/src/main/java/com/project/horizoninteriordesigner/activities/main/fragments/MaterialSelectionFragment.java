package com.project.horizoninteriordesigner.activities.main.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.horizoninteriordesigner.R;
import com.project.horizoninteriordesigner.activities.main.MainActivity;
import com.project.horizoninteriordesigner.activities.main.adapters.MaterialSelectionAdapter;
import com.project.horizoninteriordesigner.activities.main.viewModels.ItemViewModel;
import com.project.horizoninteriordesigner.models.Material;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.project.horizoninteriordesigner.activities.main.MainActivity.AR_VIEW_TAG;


/**
 *
 */
public class MaterialSelectionFragment extends Fragment implements MaterialSelectionAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private MaterialSelectionAdapter adapter;
    private ArrayList<Material> materialArrayList;
    private ItemViewModel itemViewModel;
    private FirebaseFirestore db;

    public MaterialSelectionFragment() {
        // Required empty public constructor
    }


    /**
     * Inflates the fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_material_selection, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up configuration for recycler view
        recyclerView = view.findViewById(R.id.rv_materials);
        setUpRecyclerView();

        itemViewModel = new ViewModelProvider(getActivity()).get(ItemViewModel.class);
        String itemId = itemViewModel.getItemId();

        db = FirebaseFirestore.getInstance();

        // Set material data in array list for a given item and creates adapter
        getMaterialsById(itemId);
    }


    @Override
    public void onItemClick(View view, int position) {
        Material selectedMaterial = materialArrayList.get(position);
        AtomicReference<TransformableNode> selectedModel = new AtomicReference<>();

        itemViewModel.getSelectedModelNode().observe(getViewLifecycleOwner(), transformableNode -> {
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

                    ((MainActivity) getActivity()).manageFragmentTransaction(AR_VIEW_TAG);
                });
    }


    private void getMaterialsById(String selectedItemId) {


        DocumentReference docRef = db.collection("models").document(selectedItemId);


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists() && document.contains("materials")) {
                        Map documentData = document.getData();
                        ArrayList materialNmes = (ArrayList) documentData.get("materials");

                        getSelectedMaterials(materialNmes);
                    }
                }
            }
        });
    }

    /**
     * Retrieves item documents from Firestore and maps to Item model and adds to array list.
     * Adapter is set once all documents are added.
     */
    private void getSelectedMaterials(ArrayList materialsToRetrieve) {
        materialArrayList = new ArrayList<>();

        db.collection("materials")
                .whereIn("materialName", materialsToRetrieve)
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
                            }

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