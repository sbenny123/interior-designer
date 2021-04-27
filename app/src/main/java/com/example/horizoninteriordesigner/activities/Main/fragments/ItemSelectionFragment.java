package com.example.horizoninteriordesigner.activities.Main.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.horizoninteriordesigner.ItemDbApplication;
import com.example.horizoninteriordesigner.activities.Main.MainActivity;
import com.example.horizoninteriordesigner.activities.Main.adapters.ItemSelectionAdapter;
import com.example.horizoninteriordesigner.R;
import com.example.horizoninteriordesigner.models.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.horizoninteriordesigner.activities.Main.MainActivity.AR_VIEW_TAG;

import java.util.ArrayList;


public class ItemSelectionFragment extends Fragment implements ItemSelectionAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private ItemSelectionAdapter adapter;
    private ArrayList<Item> itemArrayList;
    SendFragmentListener sendFragmentListener;


    public interface SendFragmentListener {
        void sendItem(Item item);
    }

    public ItemSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        sendFragmentListener = (SendFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        sendFragmentListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_selection, container, false);

        recyclerView = view.findViewById(R.id.rv_items);

        setUpRecyclerView(); // sets up configuration for recycler view
        getItems(); // Sets item data in array list and creates adapter

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Item selectedItem = itemArrayList.get(position);
        sendFragmentListener.sendItem(selectedItem);

        ((MainActivity) getActivity()).manageFragmentTransaction(AR_VIEW_TAG);
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