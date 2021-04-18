package com.example.horizoninteriordesigner.activities.ArCamera.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.horizoninteriordesigner.R;
import com.example.horizoninteriordesigner.models.Item;

import java.util.ArrayList;


public class ItemSelectionFragment extends Fragment {
    private RecyclerView recyclerView;
    private ItemSelectionAdapter adapter;
    private ArrayList<Item> itemArrayList;

    public ItemSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_selection, container, false);

        /*if (getActivity() != null) {
            // Initialises a version of the default items "database"
            ItemDbApplication itemDbApplication = (ItemDbApplication) requireActivity().getApplication();
            itemArrayList = itemDbApplication.getItemDB().getItems();
        } else {
            itemArrayList = new ArrayList<Item>();
        }


        recyclerView = view.findViewById(R.id.rv_items);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        adapter = new ItemSelectionAdapter(requireContext(), itemArrayList);
        recyclerView.setAdapter(adapter);*/

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}