package com.example.horizoninteriordesigner.activities.Main.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.horizoninteriordesigner.ItemDbApplication;
import com.example.horizoninteriordesigner.activities.Main.MainActivity;
import com.example.horizoninteriordesigner.activities.Main.adapters.ItemSelectionAdapter;
import com.example.horizoninteriordesigner.R;
import com.example.horizoninteriordesigner.models.Item;

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

    public ItemSelectionFragment(ArrayList<Item> itemArrayList) {
        this.itemArrayList = itemArrayList;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        ItemDbApplication itemDbApplication = (ItemDbApplication) getActivity().getApplication();
        itemArrayList = itemDbApplication.getItemDB().getItems();

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
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        adapter = new ItemSelectionAdapter(getActivity(), this, itemArrayList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Item selectedItem = itemArrayList.get(position);
        sendFragmentListener.sendItem(selectedItem);

        ((MainActivity) getActivity()).manageFragmentTransaction(AR_VIEW_TAG);
    }
}