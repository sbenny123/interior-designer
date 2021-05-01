package com.example.horizoninteriordesigner.activities.Main.fragments;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.horizoninteriordesigner.activities.Main.MainActivity;
import com.example.horizoninteriordesigner.activities.Main.adapters.ItemSelectionAdapter;
import com.example.horizoninteriordesigner.R;
import com.example.horizoninteriordesigner.activities.Main.viewModels.ItemViewModel;
import com.example.horizoninteriordesigner.models.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.horizoninteriordesigner.activities.Main.MainActivity.AR_VIEW_TAG;

import java.util.ArrayList;


public class ItemSelectionFragment extends Fragment implements ItemSelectionAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private ItemSelectionAdapter adapter;
    private ArrayList<Item> itemArrayList;
    private ItemViewModel itemViewModel;
    private AlertDialog progressDialog;

    public ItemSelectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_selection, container, false);

        recyclerView = view.findViewById(R.id.rv_items);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.dialog_item_progress);

        progressDialog = builder.create();

        setUpRecyclerView(); // sets up configuration for recycler view
        getItems(); // Sets item data in array list and creates adapter

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemViewModel = new ViewModelProvider(getActivity()).get(ItemViewModel.class);
    }

    @Override
    public void onItemClick(View view, int position) {
        progressDialog.show();

        Item selectedItem = itemArrayList.get(position);

        buildModel(selectedItem);
    }

    private void buildModel(Item selectedItem) {
        Uri itemUri = Uri.parse(selectedItem.getModelUrl());

        ModelRenderable.builder()
                .setSource(getActivity(), itemUri)
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(renderable -> {
                    itemViewModel.setRenderable(renderable);

                    progressDialog.dismiss();

                    ((MainActivity) getActivity()).manageFragmentTransaction(AR_VIEW_TAG);

                })
                .exceptionally(
                        throwable -> {
                            Toast.makeText(getActivity(), "Unable to load renderable", Toast.LENGTH_LONG).show();
                            return null;
                        });
    }

    /**
     * Retrieves item documents from Firestore and maps to Item model and adds to array list.
     * Adapter is set once all documents are added.
     */
    private void getItems() {
        itemArrayList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("models")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String itemId = document.getId();
                                String imageName = document.get("imageName") + "";
                                String imageUrl = document.get("imageUrl") + "";
                                String modelUrl = document.get("modelUrl") + "";

                                itemArrayList.add(new Item(itemId, imageName, imageUrl, modelUrl));
                            }

                            adapter = new ItemSelectionAdapter(getActivity(), ItemSelectionFragment.this::onItemClick, itemArrayList);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });
    }

    /**
     *
     */
    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }
}